import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioClient {

    public static void main(String[] args) {
        try {
            //1、窗口客户端SocketChannel,绑定客户端本地地址(不选默认随机分配一个可用地址)
            SocketChannel socketChannel = SocketChannel.open();
            //2、设置非阻塞模式,
            socketChannel.configureBlocking(false);
            //3、创建Selector
            Selector selector = Selector.open();
            //3、创建Reactor线程
            new Thread(new SingleReactorClient(socketChannel,selector)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class SingleReactorClient implements  Runnable{
    private final SocketChannel socketChannel;
    private final Selector selector;

    public SingleReactorClient(SocketChannel socketChannel, Selector selector) {
        this.socketChannel = socketChannel;
        this.selector = selector;
    }

    public void run() {
        try {
            //连接服务端
            doConnect(socketChannel,selector);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        //5、多路复用器执行多路复用程序
        while (true) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    processKey(selectionKey);
                    iterator.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void doConnect(SocketChannel sc, Selector selector) throws IOException {
        System.out.println("客户端成功启动,开始连接服务端");
        //3、连接服务端
        boolean connect = sc.connect(new InetSocketAddress("127.0.0.1", 1145));
        //4、将socketChannel注册到selector并判断是否连接成功，连接成功监听读事件，没有继续监听连接事件
        System.out.println("connect="+connect);
        if (connect) {
            sc.register(selector, SelectionKey.OP_READ);
            System.out.println("客户端成功连上服务端,准备发送数据");
            //开始进行业务处理，向服务端发送数据
            doService(sc);
        }else {
            sc.register(selector,SelectionKey.OP_CONNECT);
        }
    }

    private void processKey(SelectionKey key) throws IOException {
        if (key.isValid()) {
            //6、根据准备就绪的事件类型分别处理
            if (key.isConnectable()) {//服务端可连接事件准备就绪
                SocketChannel sc = (SocketChannel) key.channel();
                if (sc.finishConnect()) {
                    //6.1、向selector注册可读事件(接收来自服务端的数据)
                    sc.register(selector,SelectionKey.OP_READ);
                    //6.2、处理业务 向服务端发送数据
                    doService(sc);
                }else {
                    //连接失败，退出
                    System.exit(1);
                }
            }

            if (key.isReadable()) {//读事件准备继续
                //6.1、读服务端返回的数据
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBufer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBufer);
                //前面设置过socketChannel是非阻塞的，故要通过返回值判断读取到的字节数
                if (readBytes > 0) {
                    readBufer.flip();//读写模式切换
                    byte[] bytes = new byte[readBufer.remaining()];
                    readBufer.get(bytes);
                    String msg = new String(bytes,"utf-8");
                    //接收到服务端返回的数据后进行相关操作
                    doService(msg);
                }else if (readBytes < 0) {
                    //值为-1表示链路通道已经关闭
                    key.cancel();
                    sc.close();
                }else {
                    //没读取到数据，忽略
                }
            }
        }
    }
    private static void doService(SocketChannel socketChannel) throws IOException {
        System.out.println("客户端开始向服务端发送数据:");
        //向服务端发送数据
        byte[] bytes = "hello nioServer,i am nioClient !".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();
        socketChannel.write(writeBuffer);
    }

    private String doService(String msg) {
        System.out.println("成功接收来自服务端响应的数据:"+msg);
        return "";
    }
}

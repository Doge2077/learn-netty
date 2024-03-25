import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NioServer {
    public static void main(String[] args) {
        try {
            //1、打开ServerSocketChannel,用于监听客户端的连接，它是所有客户端连接的父管道(代表客户端连接的管道都是通过它创建的)
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            //2、绑定监听端口，设置连接为非阻塞模式
            serverSocketChannel.socket().bind(new InetSocketAddress(1145));
            serverSocketChannel.configureBlocking(false);
            //3、创建多路复用器Selector
            Selector selector = Selector.open();
            //4、将ServlerSocketChannel注册到selector上，监听客户端连接事件ACCEPT
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            //5、创建 Reactor线程，让多路复用器在 Reactor 线程中执行多路复用程序
            new Thread(new SingleReactor(selector)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class SingleReactor implements  Runnable{

    private final Selector selector;
    public SingleReactor(Selector selector) {
        this.selector = selector;
    }

    public void run() {
        //6、selector轮询准备就绪的事件
        while (true) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    try {
                        processKey(selectionKey);
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (selectionKey !=null ) {
                            selectionKey.cancel();
                            SelectableChannel channel = selectionKey.channel();
                            if (channel !=null) {
                                channel.close();
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processKey(SelectionKey key) throws IOException {
        if (key.isValid()) {
            //7、根据准备就绪的事件类型分别处理
            if (key.isAcceptable()) {//客户端请求连接事件就绪
                //7.1、接收一个新的客户端连接，创建对应的SocketChannel,
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                SocketChannel socketChannel = serverSocketChannel.accept();
                //7.2、设置socketChannel的非阻塞模式，并将其注册到Selector上，监听读事件
                socketChannel.configureBlocking(false);
                socketChannel.register(this.selector,SelectionKey.OP_READ);
            }
            if (key.isReadable()) {//读事件准备继续
                //7.1、读客户端发送过来的数据
                SocketChannel socketChannel = (SocketChannel) key.channel();
                ByteBuffer readBufer = ByteBuffer.allocate(1024);
                int readBytes = socketChannel.read(readBufer);
                //前面设置过socketChannel是非阻塞的，故要通过返回值判断读取到的字节数
                if (readBytes > 0) {
                    readBufer.flip();//读写模式切换
                    byte[] bytes = new byte[readBufer.remaining()];
                    readBufer.get(bytes);
                    String msg = new String(bytes,"utf-8");
                    //进行业务处理
                    String response = doService(msg);
                    //给客户端响应数据
                    System.out.println("服务端开始向客户端响应数据");
                    byte[] responseBytes = response.getBytes();
                    ByteBuffer writeBuffer = ByteBuffer.allocate(responseBytes.length);
                    writeBuffer.put(responseBytes);
                    writeBuffer.flip();
                    socketChannel.write(writeBuffer);
                }else if (readBytes < 0) {
                    //值为-1表示链路通道已经关闭
                    key.cancel();
                    socketChannel.close();
                }else {
                    //没读取到数据，忽略
                }
            }
        }
    }

    private String doService(String msg) {
        System.out.println("成功接收来自客户端发送过来的数据:"+msg);
        return "hello client,i am server!";
    }

}

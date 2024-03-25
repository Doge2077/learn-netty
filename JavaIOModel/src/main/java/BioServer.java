import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {
    public static void main (String[] args) throws IOException {
        // BIO 模型的服务端要为每一个客户端建立一个对应的连接
        ServerSocket serverSocket = new ServerSocket(1145);
        while (true) {
            // 持续接受客户端的连接
            Socket accept = serverSocket.accept();
            // 为每一个客户端连接新开一个线程，执行对应的业务
            new Thread(new ClientService(accept)).start();
        }
    }

    static class ClientService implements Runnable {
        private Socket socket;
        public ClientService (Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("执行对应的业务操作：" + socket);
        }
    }
}

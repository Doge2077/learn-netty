import java.io.IOException;
import java.net.Socket;

public class BioClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 1145);
        System.out.println("建立连接：" + socket);
    }
}

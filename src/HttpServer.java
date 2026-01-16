import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
  private int port;

  public HttpServer(int port) {
    this.port = port;
  }

  public void run() {
    System.out.println("Server is running on port " + port);
    try {
      ServerSocket server = new ServerSocket(port);
      while (true) {
        Socket client = server.accept();
        System.out.println("Client accepted!");
        client.close();
      }
    } catch (Exception e) {
      System.out.println("Port occupied, try another one");
    }

  }
}
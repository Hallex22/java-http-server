import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
  private int port;

  public HttpServer(int port) {
    this.port = port;
  }

  public void run() {
    try {
      ServerSocket server = new ServerSocket(port);
      System.out.println("🚀 Server is running on port " + port);
      while (true) {
        Socket client = server.accept();

        String rawRequest = readAllFromClient(client);

        handleRequest(rawRequest);

        PrintWriter out = new PrintWriter(client.getOutputStream());
        out.write("HTTP/1.1 200 OK\r\n");
        out.write("Content-Type: text/plain\r\n");
        out.write("Content-Length: 2\r\n");
        out.write("\r\n");
        out.write("OK");
        out.flush();

        client.close();
      }
    } catch (Exception e) {
      System.out.println("❌ Port occupied, try another one");
    }

  }

  private String readAllFromClient(Socket client) {
    StringBuilder rawRequest = new StringBuilder();
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
      String line;
      while ((line = br.readLine()) != null) {
        if (line.isEmpty()) {
          break;
        }
        rawRequest.append(line).append("\r\n");
      }
    } catch (IOException e) {
      // TODO: handle exception
    }
    return rawRequest.toString();
  }

  private void handleRequest(String rawRequest) {
    // TODO: handle the user request
    System.out.println(rawRequest);
  }

}
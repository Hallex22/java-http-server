import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

  private int port;
  private Map<String, Map<String, HttpHandler>> routes = new HashMap<>();

  public HttpServer(int port) {
    this.port = port;
  }

  public void addRoute(String method, String path, HttpHandler handler) {
    routes.computeIfAbsent(method, k -> new HashMap<>());
    routes.get(method).put(path, handler);
  }

  public void run() {
    try {
      ServerSocket server = new ServerSocket(port);
      System.out.println("🚀 Server is running on port " + port);
      while (true) {

        Socket client = server.accept();

        String rawRequest = readAllFromClient(client);
        HttpRequestParser parser = new HttpRequestParser();
        HttpRequest req = parser.parseRequest(rawRequest);
        HttpResponse res = new HttpResponse();

        Map<String, HttpHandler> methodRoutes = routes.get(req.getMethod());
        if (methodRoutes != null && methodRoutes.containsKey(req.getPath())) {
          HttpHandler handler = methodRoutes.get(req.getPath());
          handler.handle(req, res);
        } else {
          res.setStatusCode(404);
          res.setStatusMessage("Not Found");
          res.setBody("Route not found");
        }

        // System.out.println("Response: " + res.serialize());
        PrintWriter out = new PrintWriter(client.getOutputStream());
        out.write(res.serialize());
        out.flush();
        client.close();
      }
    } catch (IOException e) {
      System.out.println("❌ Port ocuppied, try another one");
    } catch (IllegalArgumentException e) {
      System.out.println("Invalid request: " + e.getMessage());
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

}
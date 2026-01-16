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

  public void run() {
    try {
      ServerSocket server = new ServerSocket(port);
      System.out.println("🚀 Server is running on port " + port + " ...");
      while (true) {

        Socket client = server.accept();
        try {
          String rawRequest = readAllFromClient(client);
          HttpRequestParser parser = new HttpRequestParser();
          HttpRequest req = parser.parseRequest(rawRequest);
          HttpResponse res = new HttpResponse();

          Map<String, HttpHandler> methodRoutes = routes.get(req.getMethod());
          if (methodRoutes != null && methodRoutes.containsKey(req.getPath())) {
            HttpHandler handler = methodRoutes.get(req.getPath());
            handler.handle(req, res);
          } else {
            res.status(404).send("Route not found");
          }
          PrintWriter out = new PrintWriter(client.getOutputStream());
          out.write(res.serialize());
          out.flush();
        } catch (IllegalArgumentException e) {
          HttpResponse res = new HttpResponse();
          res.status(400).send("Bad Request");
          PrintWriter out = new PrintWriter(client.getOutputStream());
          out.write(res.serialize());
          out.flush();
        } catch (Exception e) {
          HttpResponse res = new HttpResponse();
          res.status(500).send("Internal Server error");
          PrintWriter out = new PrintWriter(client.getOutputStream());
          out.write(res.serialize());
          out.flush();
        } finally {
          client.close();
        }
      }
    } catch (IOException e) {
      System.out.println("Failed to start server: " + e.getMessage());
    }

  }

  // Server routing possible
  public void addRoute(String method, String path, HttpHandler handler) {
    routes.computeIfAbsent(method, k -> new HashMap<>());
    routes.get(method).put(path, handler);
  }

  public void get(String path, HttpHandler handler) {
    addRoute("GET", path, handler);
  }

  public void post(String path, HttpHandler handler) {
    addRoute("POST", path, handler);
  }

  public void put(String path, HttpHandler handler) {
    addRoute("PUT", path, handler);
  }

  public void patch(String path, HttpHandler handler) {
    addRoute("PATCH", path, handler);
  }

  public void delete(String path, HttpHandler handler) {
    addRoute("DELETE", path, handler);
  }

  public void options(String path, HttpHandler handler) {
    addRoute("OPTIONS", path, handler);
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
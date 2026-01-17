import java.io.IOException;
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
          HttpRequestParser parser = new HttpRequestParser(client);
          HttpRequest req = parser.parseRequest();

          System.out.println("[SERVER DEBUG]");
          System.out.println(req.serialize());

          HttpResponse res = new HttpResponse();

          Map<String, HttpHandler> methodRoutes = routes.get(req.getMethod());

          HttpHandler handler = null;
          Map<String, String> pathParams = null;

          if (methodRoutes != null) {
            for (String routePath : methodRoutes.keySet()) {
              // Compară ruta definită cu path-ul curat al request-ului
              Map<String, String> params = matchPathWithParams(routePath, req.getCleanPath());
              if (params != null) {
                handler = methodRoutes.get(routePath);
                pathParams = params;
                break;
              }
            }
          }
          if (handler != null) {
            req.setPathParams(pathParams);
            handler.handle(req, res);
          } else {
            res.status(404).json(Map.of("message", "Route not found"));
          }
          PrintWriter out = new PrintWriter(client.getOutputStream());
          out.write(res.serialize());
          out.flush();
        } catch (IllegalArgumentException e) {
          HttpResponse res = new HttpResponse();
          res.status(400).json(Map.of("message", "Bad Request"));
          PrintWriter out = new PrintWriter(client.getOutputStream());
          out.write(res.serialize());
          out.flush();
        } catch (Exception e) {
          HttpResponse res = new HttpResponse();
          res.status(500).json(Map.of("message", "Internal Server error"));
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

  private Map<String, String> matchPathWithParams(String routePath, String requestPath) {
    String[] routeParts = routePath.split("/");
    String[] reqParts = requestPath.split("/");

    if (routeParts.length != reqParts.length)
      return null;

    Map<String, String> params = new HashMap<>();
    for (int i = 0; i < routeParts.length; i++) {
      if (routeParts[i].startsWith(":")) {
        params.put(routeParts[i].substring(1), reqParts[i]);
      } else if (!routeParts[i].equals(reqParts[i])) {
        return null;
      }
    }
    return params;
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

}
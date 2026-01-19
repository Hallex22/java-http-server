package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServer {

  private int port;
  private boolean debug = true;
  private Map<String, List<Route>> routes = new HashMap<>();

  public HttpServer(int port) {
    this.port = port;
  }

  public HttpServer(int port, boolean debug) {
    this.port = port;
    this.debug = debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  private void logRequest(HttpRequest req) {
    if (!debug)
      return;
    System.out.println("\n[DEBUG] Incoming Request");
    System.out.println(req.serialize());
  }

  private void logResponse(HttpRequest req, HttpResponse res, long durationMs) {
    if (!debug)
      return;
    System.out.println("\n[DEBUG] Response");
    System.out.println(req.getMethod() + " " + req.getPath() + " -> " + res.getStatusCode()
        + " (" + durationMs + "ms)");
    System.out.println(res.serialize());
  }

  public void run() {
    try (ServerSocket server = new ServerSocket(port)) {

      System.out.println("🚀 Server is running on port " + port + " ...");

      while (true) {
        Socket client = server.accept();

        try {
          long startTime = System.currentTimeMillis();

          HttpRequestParser parser = new HttpRequestParser(client);
          HttpRequest req = parser.parseRequest();
          HttpResponse res = new HttpResponse();

          logRequest(req);

          Route matchedRoute = null;
          Map<String, String> pathParams = null;

          List<Route> methodRoutes = routes.get(req.getMethod());

          if (methodRoutes != null) {
            for (Route route : methodRoutes) {
              Map<String, String> params = matchPathWithParams(route.path, req.getCleanPath());

              if (params != null) {
                matchedRoute = route;
                pathParams = params;
                break;
              }
            }
          }

          if (matchedRoute != null) {
            req.setPathParams(pathParams);

            MiddlewareExecutor.execute(
                matchedRoute.middlewares,
                matchedRoute.handler,
                req,
                res);
          } else {
            res.status(404).json(Map.of("message", "Route not found"));
          }

          long duration = System.currentTimeMillis() - startTime;
          logResponse(req, res, duration);

          PrintWriter out = new PrintWriter(client.getOutputStream());
          out.write(res.serialize());
          out.flush();

        } catch (IllegalArgumentException e) {
          sendError(client, 400, "Bad Request");
        } catch (Exception e) {
          sendError(client, 500, "Internal Server Error");
        } finally {
          client.close();
        }
      }

    } catch (IOException e) {
      System.out.println("Failed to start server: " + e.getMessage());
    }
  }

  private void sendError(Socket client, int status, String message) {
    try {
      HttpResponse res = new HttpResponse();
      res.status(status).json(Map.of("message", message));
      PrintWriter out = new PrintWriter(client.getOutputStream());
      out.write(res.serialize());
      out.flush();
    } catch (IOException ignored) {
    }
  }

  // Server routing possible
  private void addRoute(String method, String path, Object... handlers) {

    List<Middleware> middlewares = new ArrayList<>();
    for (int i = 0; i < handlers.length - 1; i++) {
      if (!(handlers[i] instanceof Middleware)) {
        throw new IllegalArgumentException("All handlers except last must be Middleware");
      }
      middlewares.add((Middleware) handlers[i]);
    }

    Object last = handlers[handlers.length - 1];
    if (!(last instanceof HttpHandler)) {
      throw new IllegalArgumentException("Last argument must be HttpHandler");
    }
    HttpHandler handler = (HttpHandler) last;

    Route route = new Route(method, path, middlewares, handler);
    routes.computeIfAbsent(method, k -> new ArrayList<>()).add(route);
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

  // GET
  public void get(String path, Object... handlers) {
    addRoute("GET", path, handlers);
  }

  public void get(String path, HttpHandler handler) {
    addRoute("GET", path, handler);
  }

  // POST
  public void post(String path, Object... handlers) {
    addRoute("POST", path, handlers);
  }

  public void post(String path, HttpHandler handler) {
    addRoute("POST", path, handler);
  }

  // PUT
  public void put(String path, Object... handlers) {
    addRoute("PUT", path, handlers);
  }

  public void put(String path, HttpHandler handler) {
    addRoute("PUT", path, handler);
  }

  // PATCH
  public void patch(String path, Object... handlers) {
    addRoute("PATCH", path, handlers);
  }

  public void patch(String path, HttpHandler handler) {
    addRoute("PATCH", path, handler);
  }

  // DELETE
  public void delete(String path, Object... handlers) {
    addRoute("DELETE", path, handlers);
  }

  public void delete(String path, HttpHandler handler) {
    addRoute("DELETE", path, handler);
  }

  // OPTIONS
  public void options(String path, Object... handlers) {
    addRoute("OPTIONS", path, handlers);
  }

  public void options(String path, HttpHandler handler) {
    addRoute("OPTIONS", path, handler);
  }

}
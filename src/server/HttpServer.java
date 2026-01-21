package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpServer extends Router {

  private int port;
  private boolean debug = true;

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

  private Route findRoute(HttpRequest req) {
    List<Route> methodRoutes = this.routes.get(req.getMethod());
    if (methodRoutes == null)
      return null;

    for (Route route : methodRoutes) {
      // matchPathWithParams este tot din clasa părinte Router
      Map<String, String> params = matchPathWithParams(route.path, req.getCleanPath());
      if (params != null) {
        req.setPathParams(params);
        return route;
      }
    }
    return null;
  }

  public void run() {
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("🚀 Server is running on port " + port + " ...");
      while (true) {
        Socket client = serverSocket.accept();
        // TODO Multhi-Threading
        handleClient(client);
      }
    } catch (IOException e) {
      System.out.println("Failed to start server: " + e.getMessage());
    }
  }

  private void handleClient(Socket client) {
    try {
      long startTime = System.currentTimeMillis();

      HttpRequestParser parser = new HttpRequestParser(client);
      HttpRequest req = parser.parseRequest();
      HttpResponse res = new HttpResponse();

      logRequest(req);

      Route matchedRoute = findRoute(req);
      if (matchedRoute != null) {
        List<Middleware> effectiveMiddlewares = new ArrayList<>();
        for (GlobalMiddleware gm : this.middlewares) {
          if (gm.pathPrefix == null || req.getCleanPath().startsWith(gm.pathPrefix)) {
            effectiveMiddlewares.add(gm.handler);
          }
        }
        effectiveMiddlewares.addAll(matchedRoute.middlewares);

        MiddlewareExecutor.execute(effectiveMiddlewares, matchedRoute.handler, req, res);

      } else {
        res.status(404).json(Map.of("message", "Route not found"));
      }

      long duration = System.currentTimeMillis() - startTime;
      logResponse(req, res, duration);

      PrintWriter out = new PrintWriter(client.getOutputStream());
      out.write(res.serialize());
      out.flush();

    } catch (Exception e) {
      sendError(client, 500, "Internal Server Error");
    } finally {
      try {
        client.close();
      } catch (IOException ignored) {
      }
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

  public void printRouteTree() {
    System.out.println("\n===== HTTP ROUTE TREE =====");
    if (routes.isEmpty()) {
      System.out.println("No routes registered.");
      return;
    }

    // Grupăm rutele după path pentru a le afișa frumos
    Map<String, List<String>> tree = new HashMap<>();

    routes.forEach((method, routeList) -> {
      for (Route r : routeList) {
        tree.computeIfAbsent(r.path, k -> new ArrayList<>()).add(method);
      }
    });

    // Sortăm path-urile alfabetic pentru o listă ordonată
    List<String> sortedPaths = new ArrayList<>(tree.keySet());
    Collections.sort(sortedPaths);

    for (String path : sortedPaths) {
      List<String> methods = tree.get(path);
      // Afișăm path-ul și metodele suportate (ex: /cats [GET, POST])
      System.out.printf(" %-20s %s\n", path, methods.toString());

      // Dacă vrei să vezi și câte middleware-uri are fiecare
      for (Route r : getRoutesForPath(path)) {
        System.out.println("    └── " + r.method + ": " + r.middlewares.size() + " middlewares");
      }
    }
    System.out.println("===========================\n");
  }

  // Helper pentru a lua toate obiectele Route pentru un anumit path
  private List<Route> getRoutesForPath(String path) {
    List<Route> found = new ArrayList<>();
    routes.values().forEach(list -> {
      for (Route r : list) {
        if (r.path.equals(path))
          found.add(r);
      }
    });
    return found;
  }

}
package server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HttpServer extends Router {

  public int port = 8080;
  public String host;

  private volatile boolean isRunning = true;
  private ServerSocket serverSocket = null;
  private final ExecutorService threadPool = Executors.newFixedThreadPool(10);
  private boolean debug = false;

  public HttpServer(boolean debug) {
    this.debug = debug;
  }

  public HttpServer() {
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  // ---------------------------------------------------------
  // Public Methods
  public void listen(int port, String host, Runnable callback) {
    this.host = host;
    this.port = port;
    new Thread(() -> {
      this.run();
    }).start();

    // startConsoleListener
    new Thread(() -> {
      Scanner console = new Scanner(System.in);
      while (this.isRunning == true) {
        String command = console.nextLine();
        if (command.equals("stop") || command.equals("exit")) {
          System.out.println("Do you want to stop the server? y/n");
          String confirmation = console.nextLine();
          if (confirmation.equals("y")) {
            this.stop();
          } else {
            System.out.println("Server continues to run ...");
          }
        }
      }
    }).start();

    if (callback != null) {
      callback.run();
    }
  };

  public void listen() {
    this.listen(8080, "localhost", null);
  }

  public void listen(Runnable callback) {
    this.listen(8080, "localhost", callback);
  }

  public void listen(int port) {
    this.listen(port, "localhost", null);
  }

  public void listen(int port, Runnable callback) {
    this.listen(port, "localhost", callback);
  }

  public synchronized void stop() {
    if (!this.isRunning && serverSocket != null && serverSocket.isClosed()) {
      return;
    }
    try {
      this.isRunning = false;
      if (serverSocket != null && !serverSocket.isClosed()) {
        serverSocket.close();
      }
      threadPool.shutdown();
      if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
        threadPool.shutdownNow();
      }
      System.out.println("✅ Resources cleared and server stopped.");
    } catch (IOException e) {
      System.err.println("Error while stopping server: " + e.getMessage());
    } catch (InterruptedException e) {
      System.err.println("Error while stopping the threadPool: " + e.getMessage());
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

  // -------------------------------------------------------
  // Private methods

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

  private void run() {
    try {
      this.serverSocket = new ServerSocket(port);
      while (this.isRunning) {
        try {
          Socket client = serverSocket.accept();
          threadPool.execute(() -> handleClient(client));
        } catch (SocketException e) {
          if (!isRunning) {
            System.out.println("🛑 Server socket closed for shutdown.");
            break;
          } else {
            throw e;
          }
        }
      }
    } catch (IOException e) {
      if (isRunning) {
        System.err.println("❌ Server error: " + e.getMessage());
      }
    } finally {
      this.stop();
    }
  }

  private void handleClient(Socket client) {
    try {
      long startTime = System.currentTimeMillis();

      HttpRequestParser parser = new HttpRequestParser(client);
      HttpRequest req = parser.parseRequest();
      HttpResponse res = new HttpResponse();

      if (debug) {
        System.out.println("[THREAD] Cerere procesata de: " + Thread.currentThread().getName());
      }
      logRequest(req);

      // Static Files
      String path = req.getPath();
      boolean fileFound = false;
      for (String prefix : staticRoutes.keySet()) {
        if (path.startsWith(prefix)) {
          String folderPath = staticRoutes.get(prefix);
          String fileName = path.substring(prefix.length());
          File file = new File(folderPath + fileName);

          if (file.isDirectory()) {
            file = new File(file, "index.html");
          }

          if (file.exists() && !file.isDirectory()) {
            serveStaticFile(res, client, file);
            fileFound = true;
            break;
          }
        }
      }

      // Dinamyc route
      if (!fileFound) {
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

        PrintWriter out = new PrintWriter(client.getOutputStream());
        out.write(res.serialize());
        out.flush();
      }
      long duration = System.currentTimeMillis() - startTime;
      logResponse(req, res, duration);
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

  private void serveStaticFile(HttpResponse res, Socket client, File file) {
    try {
      OutputStream out = client.getOutputStream();
      PrintWriter writer = new PrintWriter(out, false);

      byte[] bodyBytes = Files.readAllBytes(file.toPath());
      String contentType = Files.probeContentType(file.toPath());
      if (contentType == null) {
        contentType = "application/octet-stream";
      }
      writer.println("HTTP/1.1 200 OK");
      writer.println("Content-Type: " + contentType);
      writer.println("Content-Length: " + bodyBytes.length);
      writer.println();
      writer.flush();
      out.write(bodyBytes);
      out.flush();

      res.status(200).setBody("[Static File: " + file.getName() + "]");

    } catch (IOException e) {
      // TODO Handle serverStaticFile IOException
    }
  }

}
package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Router {

  protected Map<String, List<Route>> routes = new HashMap<>();
  protected List<GlobalMiddleware> middlewares = new ArrayList<>();

  static class GlobalMiddleware {
    String pathPrefix;
    Middleware handler;

    GlobalMiddleware(String pathPrefix, Middleware handler) {
      this.pathPrefix = pathPrefix;
      this.handler = handler;
    }
  }

  protected void addRoute(String method, String path, Object... handlers) {

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

  protected Map<String, String> matchPathWithParams(String routePath, String requestPath) {
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

  public void use(Middleware handler) {
    middlewares.add(new GlobalMiddleware(null, handler));
  }

  public void use(String pathPrefix, Middleware handler) {
    middlewares.add(new GlobalMiddleware(pathPrefix, handler));
  }

  public void use(String prefix, Router subRouter) {
    subRouter.routes.forEach((method, routeList) -> {
      for (Route r : routeList) {
        String combinedPath = (prefix + "/" + r.path).replaceAll("/+", "/");
        if (combinedPath.endsWith("/") && combinedPath.length() > 1) {
          combinedPath = combinedPath.substring(0, combinedPath.length() - 1);
        }
        // 1. Colectăm totul într-o listă de Object
        List<Object> allHandlers = new ArrayList<>();
        // Adăugăm middleware-urile globale ale sub-routerului
        for (GlobalMiddleware gm : subRouter.middlewares) {
          allHandlers.add(gm.handler);
        }
        // Adăugăm middleware-urile specifice rutei
        allHandlers.addAll(r.middlewares);
        // Adăugăm handler-ul final (obligatoriu ultimul)
        allHandlers.add(r.handler);
        // 2. Apelăm addRoute folosind array-ul complet
        this.addRoute(method, combinedPath, allHandlers.toArray());
      }
    });
  }

}

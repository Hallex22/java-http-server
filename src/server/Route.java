package server;

import java.util.List;

public class Route {
  String method;
  String path;
  List<Middleware> middlewares;
  HttpHandler handler;

  public Route(String method, String path, List<Middleware> middlewares, HttpHandler httpHandler) {
    this.method = method;
    this.path = path;
    this.middlewares = middlewares;
    this.handler = httpHandler;
  }

}

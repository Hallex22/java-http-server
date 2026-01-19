package server;

@FunctionalInterface
public interface Middleware {
  void handle(HttpRequest req, HttpResponse res, Next next);
}

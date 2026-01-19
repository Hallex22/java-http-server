package server;

import java.util.List;

public class MiddlewareExecutor {

  public static void execute(
      List<Middleware> middlewares,
      HttpHandler finalHandler,
      HttpRequest req,
      HttpResponse res
  ) {
    executeAtIndex(0, middlewares, finalHandler, req, res);
  }

  private static void executeAtIndex(
      int index,
      List<Middleware> middlewares,
      HttpHandler finalHandler,
      HttpRequest req,
      HttpResponse res
  ) {
    // dacă am terminat middleware-urile → handlerul final
    if (index >= middlewares.size()) {
      finalHandler.handle(req, res);
      return;
    }

    Middleware current = middlewares.get(index);

    current.handle(req, res, () -> {
      executeAtIndex(index + 1, middlewares, finalHandler, req, res);
    });
  }
}

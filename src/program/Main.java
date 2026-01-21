package program;

import java.util.Map;

import server.HttpHandler;
import server.HttpServer;
import server.Router;
import fakeDB.CatsDB;

public class Main {

  public static void main(String[] args) {

    HttpServer server = new HttpServer(false);
    CatsDB db = new CatsDB("fakeDB/cats.json");

    server.use(UserMiddleware.test());

    // Routes
    server.get("/", (req, res) -> {
      res.status(200).json(Map.of("message", "Hello World!"));
    });

    // --------------------------------- Cats Routes ------------------
    Router catsRouter = new Router();

    catsRouter.get("/", UserMiddleware.tokenAuth(), (HttpHandler) (req, res) -> {
      res.status(200).json(Map.of("data", db.getAll()));
    });

    catsRouter.post("/", (req, res) -> {
      Map<String, Object> body = req.getBodyJson();
      int id = ((Number) body.get("id")).intValue();
      String name = (String) body.get("name");

      System.out.println("Req query params:" + req.getQueryParams());

      res.status(201).json(Map.of(
          "message", "Cat added",
          "name", name));
    });

    catsRouter.get("/:id", (req, res) -> {
      try {
        int id = Integer.parseInt(req.getPathParams().get("id"));
        Object cat = db.get(id);
        if (cat != null) {
          res.status(200).json(cat);
        } else {
          res.status(404).json(Map.of("message", "Cat not found"));
        }
      } catch (NumberFormatException e) {
        res.status(400).json(Map.of("message", "Invalid cat ID"));
      }
    });

    server.use("/cats", catsRouter);

    server.listen(8888, "localhost", () -> {
      System.out.println("🚀 Server running on http://" + server.host + ":" +
          server.port + " ...");
      server.printRouteTree();
    });

  }

}

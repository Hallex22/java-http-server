import java.util.Map;

import fakeDB.CatsDB;

public class Main {

  public static void main(String[] args) {

    HttpServer server = new HttpServer(8888);
    CatsDB db = new CatsDB("fakeDB/cats.json");

    // Routes
    server.get("/", (req, res) -> {
      res.status(200).json(Map.of("message", "Hello World!"));
    });

    server.get("/cats", (req, res) -> res.status(200).json(Map.of("data", db.getAll())));

    server.post("/cats", (req, res) -> {
      Map<String, Object> body = req.getBodyJson();
      int id = ((Number) body.get("id")).intValue();
      String name = (String) body.get("name");

      System.out.println("Req query params:" + req.getQueryParams());

      res.status(201).json(Map.of(
          "message", "Cat added",
          "name", name));
    });

    server.get("/cats/:id", (req, res) -> {
      try {
        // Extrage param-ul din path
        int id = Integer.parseInt(req.getPathParams().get("id"));

        // Caută pisica în DB
        Object cat = db.get(id); // db.get(int) returnează null dacă nu există

        if (cat != null) {
          // Returnează ca JSON
          res.status(200).json(cat);
        } else {
          // Pisica nu există → 404
          res.status(404).json(Map.of("message", "Cat not found"));
        }
      } catch (NumberFormatException e) {
        // id-ul nu era un număr valid
        res.status(400).json(Map.of("message", "Invalid cat ID"));
      }
    });

    server.run();

    // testHttpRequestParser();

  }

  // public static void testHttpRequestParser() {
  // HttpRequestParser urlParser = new HttpRequestParser();
  // String rawRequest = "GETT / HTTP/1.1\r\n";

  // try {
  // HttpRequest req = urlParser.parseRequest(rawRequest);
  // System.out.println("Req: " + req.serialize());
  // } catch (IllegalArgumentException e) {
  // System.out.println("Invalid request: " + e.getMessage());
  // }
  // }

}

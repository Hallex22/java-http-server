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

    server.run();

    // testHttpRequestParser();

  }

  // public static void testHttpRequestParser() {
  //   HttpRequestParser urlParser = new HttpRequestParser();
  //   String rawRequest = "GETT / HTTP/1.1\r\n";

  //   try {
  //     HttpRequest req = urlParser.parseRequest(rawRequest);
  //     System.out.println("Req: " + req.serialize());
  //   } catch (IllegalArgumentException e) {
  //     System.out.println("Invalid request: " + e.getMessage());
  //   }
  // }

}

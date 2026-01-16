import java.util.ArrayList;
import java.util.List;

public class Main {

  public static void main(String[] args) {

    HttpServer server = new HttpServer(8888);

    List<String> cats = new ArrayList<>();
    cats.add("Tigrutza");
    cats.add("Pufu");
    cats.add("Hera");
    cats.add("Ares");

    server.get("/", (req, res) -> {
      res.status(200).send("Hello World!");
      //res.setBody("Hello World!");
    });

    server.addRoute("GET", "/cats", (req, res) -> {
      res.setStatusCode(200);
      res.setStatusMessage("OK");
      // concatenăm lista într-un string
      StringBuilder body = new StringBuilder("Cats: ");
      for (String cat : cats) {
        body.append(cat).append(", ");
      }
      // ștergem ultima virgulă și spațiu
      if (!cats.isEmpty()) {
        body.setLength(body.length() - 2);
      }
      res.setBody(body.toString());
    });

    server.run();

    // testHttpRequestParser();

  }

  public static void testHttpRequestParser() {
    HttpRequestParser urlParser = new HttpRequestParser();
    String rawRequest = "GETT / HTTP/1.1\r\n";

    try {
      HttpRequest req = urlParser.parseRequest(rawRequest);
      System.out.println("Req: " + req.serialize());
    } catch (IllegalArgumentException e) {
      System.out.println("Invalid request: " + e.getMessage());
    }
  }

}

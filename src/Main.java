import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

  public static void main(String[] args) {

    HttpServer server = new HttpServer(8888);

    List<String> cats = new ArrayList<>();
    cats.add("Tigrutza");
    cats.add("Pufu");
    cats.add("Hera");
    cats.add("Ares");

    server.get("/", (req, res) -> {
      res.status(200).json(Map.of("message", "Hello World!"));
    });

    server.get("/cats", (req, res) -> {
      // concatenăm lista într-un string
      StringBuilder body = new StringBuilder("Cats: ");
      for (String cat : cats) {
        body.append(cat).append(", ");
      }
      // ștergem ultima virgulă și spațiu
      if (!cats.isEmpty()) {
        body.setLength(body.length() - 2);
      }
      // res.status(200).send(body.toString());
      res.status(200).json(Map.of(
          "success", true,
          "data", body.toString()));
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

public class Main {
  public static void main(String[] args) {

    // HttpServer server = new HttpServer(8888);
    // server.run();

    testHttpRequestParser();

  }

  public static void testHttpRequestParser() {
    HttpRequestParser urlParser = new HttpRequestParser();
    String rawRequest = "GETT / HTTP/1.1\r\n";

    try {
      HttpRequest req = urlParser.parseRequest(rawRequest);
      System.out.println(req.method);
      System.out.println(req.path);
      System.out.println(req.version);
    } catch (IllegalArgumentException e) {
      System.out.println("Invalid request: " + e.getMessage());
    }
  }

}

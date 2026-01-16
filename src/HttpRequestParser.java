import java.util.Set;

public class HttpRequestParser {

  static final Set<String> VALID_METHODS = Set.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");

  public HttpRequest parseRequest(String rawRequest) throws IllegalArgumentException {

    if (rawRequest == null || rawRequest.isEmpty()) {
      throw new IllegalArgumentException("Request is empty");
    }

    String[] lines = rawRequest.split("\r\n|\n");
    
    // 1st line parsing
    String firstLine = lines[0];
    String[] parts = firstLine.split(" ");
    if(parts.length != 3) {
      throw new IllegalArgumentException("Invalid Request line: should have 3 lines");
    }
    if(!VALID_METHODS.contains(parts[0])) {
      throw new IllegalArgumentException("Invalid HTTTP method: " + parts[0]);
    }
    if(!parts[1].startsWith("/")) {
      throw new IllegalArgumentException("Invalid path: " + parts[1]);
    }

    return new HttpRequest(parts[0], parts[1], parts[2]);
  }
  
}

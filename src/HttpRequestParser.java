import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HttpRequestParser {

  private Socket client;
  private BufferedReader in;
  private InputStream inputStream;

  static final Set<String> VALID_METHODS = Set.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
  static final int MAX_BODY_SIZE = 1024 * 1024;

  public HttpRequestParser(Socket client) throws IOException {
    this.client = client;
    this.inputStream = client.getInputStream();
  }

  public HttpRequest parseRequest() throws IllegalArgumentException, IOException {

    HttpRequest req = new HttpRequest();
    String[] firstLineParts = readRequestLine();
    req.setMethod(firstLineParts[0]);
    req.setPath(firstLineParts[1]);
    req.setVersion(firstLineParts[2]);

    Map<String, String> headers = readHeaders();
    req.setHeaders(headers);

    if (headers.containsKey("Content-Length")) {
      int length = Integer.parseInt(headers.get("Content-Length"));
      if (length < 0 || length > MAX_BODY_SIZE) {
        throw new IllegalArgumentException("Content-Length is too big");
      }
      String body = readBody(length);
      req.setBody(body);
    } else {
      req.setBody("");
    }

    return req;
  }

  // Reads request method, path and version + validation
  private String[] readRequestLine() throws IOException, IllegalArgumentException {
    String line = readLine(inputStream);

    if (line == null || line.isEmpty()) {
      throw new IllegalArgumentException("Empty request");
    }

    String[] parts = line.split(" ");
    if (parts.length != 3) {
      throw new IllegalArgumentException("Invalid Request line: should have 3 parts");
    }
    if (!VALID_METHODS.contains(parts[0])) {
      throw new IllegalArgumentException("Invalid HTTP method: " + parts[0]);
    }
    if (!parts[1].startsWith("/")) {
      throw new IllegalArgumentException("Invalid path: " + parts[1]);
    }
    return parts;

  }

  private Map<String, String> readHeaders() throws IOException {
    Map<String, String> headers = new HashMap<>();
    while (true) {
      String line = readLine(inputStream);
      if (line.isEmpty()) {
        break;
      }
      int colonIndex = line.indexOf(":");
      if (colonIndex == -1) {
        continue;
      }
      String name = line.substring(0, colonIndex).trim();
      String value = line.substring(colonIndex + 1).trim();
      headers.put(name, value);
    }
    return headers;
  }

  private String readBody(int contentLength) throws IOException {
    byte[] bodyByte = new byte[contentLength];
    int totalRead = 0;

    while (totalRead < contentLength) {
      int read = inputStream.read(bodyByte, totalRead, contentLength - totalRead);
      if (read == -1) {
        // clientul a închis conexiunea prea devreme
        throw new IOException("Unexpected end of stream while reading body");
      }
      totalRead += read;
    }

    return new String(bodyByte);
  }

  private String readLine(InputStream in) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int current;
    boolean seenCR = false;
    while ((current = in.read()) != -1) {
      if (seenCR && current == '\n') {
        break;
      }

      if (current == '\r') {
        seenCR = true;
      } else {
        if (seenCR) {
          buffer.write('\r');
          seenCR = false;
        }
        buffer.write(current);
      }
    }
    return buffer.toString(StandardCharsets.UTF_8);
  }

}

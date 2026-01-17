import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

  private String method;
  private String path;
  private String version;
  private Map<String, String> headers = new HashMap<>();
  private String body;

  public HttpRequest() {
  }

  public HttpRequest(String method, String path, String version) {
    this.method = method;
    this.path = path;
    this.version = version;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }

  public void addHeader(String key, String value) {
    headers.put(key, value);
  }

  public void removeHeader(String key) {
    headers.remove(key);
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String serialize() {
    StringBuilder sb = new StringBuilder();
    sb.append(method).append(" ").append(path).append(" ").append(version).append("\r\n");
    for (Map.Entry<String, String> header : headers.entrySet()) {
      sb.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
    }
    if (body != null && !body.isEmpty()) {
      sb.append("\r\n").append(body);
    }
    return sb.toString();
  }

}

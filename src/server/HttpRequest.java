import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class HttpRequest {

  private String method;
  private String path;
  private String version;
  private Map<String, String> headers = new HashMap<>();
  private String body;
  private Map<String, String> queryParams;
  private Map<String, String> pathParams;

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

  public String getCleanPath() {
    int qIndex = path.indexOf("?");
    return qIndex >= 0 ? path.substring(0, qIndex) : path;
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

  public Map<String, String> getHeaders() {
    return headers;
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

  public void setPathParams(Map<String, String> pathParams) {
    this.pathParams = pathParams;
  }

  public Map<String, String> getPathParams() {
    return pathParams != null ? pathParams : new HashMap<>();
  }

  public Map<String, Object> getBodyJson() throws IllegalStateException {
    String contentType = headers.get("Content-Type");
    if (contentType == null || !contentType.contains("application/json")) {
      throw new IllegalStateException("Request is not JSON");
    }
    Gson gson = new Gson();
    return gson.fromJson(this.body, Map.class);
  }

  public Map<String, String> getQueryParams() {
    if (queryParams != null)
      return queryParams;

    queryParams = new HashMap<>();

    int qIndex = path.indexOf("?");
    if (qIndex == -1)
      return queryParams;

    String query = path.substring(qIndex + 1);
    path = path.substring(0, qIndex);

    for (String pair : query.split("&")) {
      String[] kv = pair.split("=", 2);
      if (kv.length == 2) {
        queryParams.put(kv[0], kv[1]);
      }
    }

    return queryParams;
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

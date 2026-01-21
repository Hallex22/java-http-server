package server;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;

public class HttpResponse {

  private int statusCode;
  private String statusMessage;
  private Map<String, String> headers = new HashMap<>();
  private String body = "";

  public HttpResponse() {
    this.statusCode = 200;
    this.statusMessage = "OK";
  }

  public HttpResponse(int statusCode, String statusMessage, String body) {
    this.statusCode = statusCode;
    this.statusMessage = statusMessage;
    this.body = body;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public String getStatusMessage() {
    return statusMessage;
  }

  public void setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
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

  public String serialize() {
    StringBuilder sb = new StringBuilder();
    sb.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusMessage).append("\r\n");

    if (!headers.containsKey("Content-Type")) {
      headers.put("Content-Type", "text/plain; charset=UTF-8");
    }
    if (!headers.containsKey("Content-Length")) {
      headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }
    for (Map.Entry<String, String> header : headers.entrySet()) {
      sb.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
    }
    sb.append("\r\n");
    sb.append(body != null ? body : "");
    return sb.toString();
  }

  public HttpResponse status(int code) {
    this.statusCode = code;
    this.statusMessage = HttpStatus.getMessage(code);
    return this;
  }

  public HttpResponse send(String body) {
    this.body = body;
    return this;
  }

  public HttpResponse json(Object data) {
    this.addHeader("Content-Type", "application/json; charset=UTF-8");
    Gson gson = new Gson();
    String body = gson.toJson(data);
    this.body = body;
    return this;
  }

}

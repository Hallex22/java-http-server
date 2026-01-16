public class HttpRequest {

  String method;
  String path;
  String version;

  // public HttpRequest() {
  //   this.method = null;
  //   this.path = null;
  //   this.version = null;
  // }

  public HttpRequest(String method, String path, String version) {
    this.method = method;
    this.path = path;
    this.version = version;
  }

  public String getMethod() {
    return method;
  }

  public String getPath() {
    return path;
  }

  public String getVersion() {
    return version;
  }

}

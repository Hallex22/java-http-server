public class HttpServer {
    private int PORT;

    public HttpServer(int port) {
        this.PORT = port;
    }

    public void run() {
        System.out.println("Server is running on port " + PORT);
    }
}
package server;
import java.util.Map;

public class HttpStatus {
  public static final Map<Integer, String> STATUS_MESSAGES = Map.of(
    200, "OK",
    201, "Created",
    304, "Not Modified",
    400, "Bad Request",
    401, "Unauthorized",
    403, "Forbidden",
    500, "Internal Server Error"
  );

  public static String getMessage(int code) {
    return STATUS_MESSAGES.getOrDefault(code, "Unknown Status");
  }

  public static boolean isValid(int code) {
    return STATUS_MESSAGES.containsKey(code);
  }

}
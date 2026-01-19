package program;

import java.util.Map;

import server.Middleware;

public class UserMiddleware {


  public static Middleware tokenAuth() {
    return (req, res, next) -> {
      String token = req.getHeaders().get("Authorization");
      if (token != null && token.equals("LeBron")) {
        next.next();
      } else {
        res.status(401).json(Map.of("message", "Unauthorized"));
      }
    };
  }


}

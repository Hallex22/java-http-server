package program;

import java.util.Map;

import server.Middleware;

public class UserMiddleware {

  public static Middleware test() {
    return (req, res, next) -> {
      System.out.println("Global middleware used!");
      next.next();
    };
  }

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

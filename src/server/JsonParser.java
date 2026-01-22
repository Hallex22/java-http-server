package server;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class JsonParser implements Middleware {

  private final Gson gson = new Gson();

  @Override
  public void handle(HttpRequest req, HttpResponse res, Next next) {
    try {
      String contentType = req.getHeader("Content-Type");

      if (contentType == null || req.getBody() == null || req.getBody().isEmpty()) {
        next.next();
        return;
      }

      String bodyString = req.getBody();

      if (contentType.contains("application/json")) {
        try {
          // Încercăm parsarea JSON
          Map<String, Object> parsed = gson.fromJson(bodyString, Map.class);
          req.setParsedBody(parsed);
        } catch (Exception e) {
          // Dacă JSON-ul e prost, dăm 400 și oprim aici
          System.err.println("[BodyParser] Eroare JSON: " + e.getMessage());
          res.status(400).json(Map.of("message", "Invalid JSON format"));
          return;
        }
      } else if (contentType.contains("application/x-www-form-urlencoded")) {
        try {
          // Încercăm parsarea Form
          Map<String, Object> parsed = parseFormData(bodyString);
          req.setParsedBody(parsed);
        } catch (Exception e) {
          // Dacă form-ul dă eroare, logăm dar lăsăm cererea să treacă (va fi empty map)
          System.err.println("[BodyParser] Eroare Form: " + e.getMessage());
          e.printStackTrace(); // Asta îți va arăta LINIA exactă unde crapă
        }
      }

    } catch (Exception globalEx) {
      // "Plasa de siguranță" supremă să nu dea 500 serverul din cauza parserului
      System.err.println("[BodyParser] Eroare Critică: " + globalEx.getMessage());
      globalEx.printStackTrace();
    }

    // Indiferent ce s-a întâmplat, mergem mai departe
    next.next();
  }

  private Map<String, Object> parseFormData(String body) {
    if (body == null || body.isEmpty()) {
      return new HashMap<>();
    }

    Map<String, Object> result = new HashMap<>();
    String[] pairs = body.split("&");
    for (String pair : pairs) {
      String[] parts = pair.split("=", 2);
      if (parts.length == 2) {
        String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
        String value = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
        result.put(key, value);
      }
    }
    return result;
  }

}

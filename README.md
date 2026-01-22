## ⚡ Lightweight Java HTTP Server (Express-inspired)

A lightweight, educational HTTP server framework written **from scratch in Java**, inspired by **Node.js + Express**.

This project was built to deeply understand how HTTP servers work internally:
request parsing, routing, middleware execution, body parsing, and concurrency — **without relying on existing frameworks** like Spring or Netty.


## 🚀 Main Features

✅ Static file serving  
✅ Universal body parsing (via middleware)  
✅ JSON, form, and raw body support  
✅ Logging & debug mode  
✅ Modular router system  
✅ Middleware support (`use` + route-level middleware)  
✅ Multi-threaded request handling  
✅ Graceful shutdown  
✅ `.listen()` method (Express-style)  
✅ Path params (`/cats/:id`)  
✅ Query params (`/cats?age=3`)  


## 📦 Installation

Clone the repository:

```bash
git clone https://github.com/Hallex22/java-http-server.git
cd java-http-server
```

## 🔥 Basic Usage
```java
HttpServer server = new HttpServer();

server.use(new JsonParser());
server.use(AuthMiddleware.tokenAuth());

server.get("/", (req, res) -> {
  res.status(200).json(Map.of("message", "Hello World!"));
});

server.listen(8888, "localhost", () -> {
  System.out.println("🚀 Server running on " + server.host + ":" +
    server.port + " ...");
});
```

## 🧩 Routing
### Basic routes
```java
Router catsRouter = new Router();

catsRouter.get("/", CatsController.getAll());
catsRouter.post("/", CatsController.createCat());
catsRouter.put("/:id", CatsController.updateCat());
catsRouter.delete("/:id", CatsController.deleteCat());

server.use("/cats", catsRouter);
```

### Path Params
```java
catsRouter.get("/:id", (req, res) -> {
  try {
    // path params example: /cats/1
    int id = Integer.parseInt(req.getPathParam("id"));
    Cat cat = CatsRepository.findOne(id);
    if (cat != null) {
      res.status(200).json(cat);
    } else {
      res.status(404).json(Map.of("message", "Cat not found"));
    }
  } catch (Exception e) {
    res.status(400).json(Map.of("message", "Invalid cat ID"));
  }
});
```

### Query Params
```java
catsRouter.get("/", (req, res) -> {
  try {
    // query params example: /cats?age=3&color=black
    String age = req.getQueryParam("age");
    String color = req.getQueryParam("color");

    List<Cat> cats = CatsRepository.findAll(age, color);
    res.status(200).json(Map.of(
      "count", cats.size(),
      "data", cats
    ));

  } catch (Exception e) {
    res.status(400).json(Map.of("message", "Invalid request"));
  }
})
```

## 🔁 Middleware System (Express-style)
### Global middleware
```java
server.use((req, res, next) -> {
  System.out.println(req.getMethod() + " " + req.getPath());
  next.run();
});
```

### Route-level middleware
```java
catsRouter.get("/", AuthMiddleware.tokenAuth(), (req, res) -> {
  res.status(200).json(CatsRepository.findAll());
});
```

### Middleware behavior
- Middleware controls flow via `next.run()`
- If `next()` is NOT called, execution stops
- Perfect for auth, validation, logging, etc.

### Example auth middleware
```java
public class AuthMiddleware {

  public static Middleware tokenAuth() {
    return (req, res, next) -> {
      String token = req.getHeader("Authorization");
      if (!"TOP_SECRET".equals(token)) {
        res.status(401).json(Map.of("message", "Unauthorized"));
        return;
      }
      next.run();
    };
  }

}
```


## 🧠 Body Parsing (Universal)
Implemented via middleware, not hardcoded into the server.

Supported:
- `application/json`
- `application/x-www-form-urlencoded`

```java
server.use(new JsonParser());
```
It parses the **String body** received in **json**/**x-www-form-urlencoded** format into JSON format (Map<String, Object>)
```java
Map<String, Object> body = req.getParsedBody();
```


## 📂 Static Files
Serve static assets such as HTML, CSS, JavaScript, and images.
```java
server.staticFiles("/assets", "public");
```

Maps the `public` directory to the `/assets` URL prefix.

```text
public/
├── index.html
├── style.css
├── script.js
└── logo.png
```

Example: 
```http
GET /assets/logo.png → public/logo.png
```

### SPA Fallback
Unknown static paths can fallback to `index.html`, useful for Single Page Applications.

## 🧵 Multi-threading
Each client connection is handled by a worker thread.

The server uses a fixed-size thread pool (ExecutorService) to efficiently handle concurrent requests
without creating an unbounded number of threads.


## 🛑 Graceful Shutdown
- Supports clean shutdown (exit or stop command)
- Server closes sockets properly
- Optional confirmation before exit


## 🧪 Debug & Logging
```java
server.setDebug(true);
```
Logs:
- Incoming request
- Headers
- Body
- Response status
- Response time


## 🎯 Goals of This Project
- Learn how web frameworks work internally
- Understand HTTP deeply
- Build Express-like ergonomics in Java


## 🙌 Final Note
Thank you for reading all of this !! 

*P.S. make sure you never use this project in production* 🤣🤣🤣

# ⚡ Lightweight Java HTTP Server (Express-inspired)

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
server.use(AuthMiddleware.token());

server.get("/", (req, res) -> {
  res.status(200).json(Map.of("message", "Hello World!"));
});

server.listen(8888, "localhost", () -> {
  System.out.println("🚀 Server running on " + server.host + ":" +
    server.port + " ...");
});
```

## 🧩 Routing

Basic routes
```java
Router catsRouter = new Router();

catsRouter.get("/", CatsController.getAll());
catsRouter.post("/", CatsController.createCat());
catsRouter.put("/:id", CatsController.updateCat());
catsRouter.delete("/:id", CatsController.deleteCat());

server.use("/cats", catsRouter);
```




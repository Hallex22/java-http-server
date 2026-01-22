Alex PRV
hallex22
Online

457261 — 14.01.2026 12:34
https://www.youtube.com/watch?v=rqR7z2eHOBE
YouTube
Adam Something
The AI Endgame
Imagine
Alex PRV — 14.01.2026 13:12
ma duc si eu pana la baie
457261 — 14.01.2026 13:16
ok
acuma am revenit si eu
Alex PRV — 14.01.2026 21:40
cum a fost la prezentare?
457261 — 14.01.2026 21:40
ba a fost ok cu toate ca au aparut 2 probleme in timpul prezentarii
Alex PRV — 14.01.2026 21:40
cu hostul?
457261
 a început un apel care a durat 2 ore. — 14.01.2026 21:40
Alex PRV — 14.01.2026 21:53
Imagine
Alex PRV — 16.01.2026 01:29
deepseek s-a prins in mijlocul codului ca a gresit limbajul, ma inlocuieste :)))
Imagine
457261 — 16.01.2026 01:46
A facut si el o greseala na :))
Alex PRV — 16.01.2026 16:00
cf blud?
457261 — 16.01.2026 16:19
uite stau
a fost bine azi la examen
Alex PRV — 16.01.2026 16:19
examen azi?
457261 — 16.01.2026 16:19
si aparent l-am trecut si pe ala de sambata
cologviu dar practic examen
Alex PRV — 16.01.2026 16:21
a, atunci are sens
Alex PRV — 16.01.2026 16:21
felicitari
457261 — 16.01.2026 16:21
mersi
Alex PRV — 16.01.2026 16:21
voiam sa iti zic ca m-am apucat de un proiect care are durata aproximativa de 1 saptamana, care e interesant, propriul server http
457261 — 16.01.2026 16:22
ooo
Alex PRV — 16.01.2026 16:22
gen pe github public sa fie ca proiect la cv si sa invat mai multe despre http
457261 — 16.01.2026 16:22
fainut
Alex PRV — 16.01.2026 16:22
cu java ☠️
dar in vs code
457261 — 16.01.2026 16:22
💀
Alex PRV — 16.01.2026 16:24
se putea face si cu C dar nu mai vreau sa ma ating de C / C++ ca vreau sa fiu sanatos mental, python era prea pomana, si node.js era destul de simplut de facut
aici ai un balans in care chiar inveti chestii
ce smecher ca iti pune la exceptii TODO, si daca e scris asa apare si in consola aia cu probleme, ca sa tii minte sa vii sa remediezi
Imagine
Alex PRV — 16.01.2026 19:22
ghici cui ii merge requestul de baza si primeste un ok cu hello world, e hardcodat, dar merge
Imagine
Alex PRV — 16.01.2026 20:15
nu mai e hardcodat, pe ruta cats primesti pisicile ca o lista simpla din memorie
Imagine
Alex PRV — 17.01.2026 12:12
pentru mine
1️⃣ Body parsing pentru request (POST/PUT)

Scop: să poți citi req.body() ca Map<String, Object> sau POJO

Trebuie să:
Extinde
message.txt
3 KB
Alex PRV — 18.01.2026 22:50
asta da victorie
Imagine
vor patchui ei upgrade-urile dintre age-uri, ca fiindca am armata putin mai tehnologizata ii sparg efectiv
Alex PRV — 18.01.2026 22:59
aicia razboi de smecher
Imagine
457261 — 18.01.2026 23:07
nice
457261 — 19.01.2026 18:56
@Alex PRV
ba
ai cum sa ma ajuti si pe mine cu un mic edit?
(sa scoti fundalul de la o poza)
Alex PRV — 19.01.2026 19:34
Imagine
Uite ce sticker am de pe Shein
457261 — 19.01.2026 20:22
Aoleo
Alex PRV — 19.01.2026 20:22
asta e baza
Alex PRV — ieri la 20:23
cum e sesiunea?
457261 — ieri la 21:26
momentan ok
ramane de vazut ce nota am luat la cel de ieri
457261 — 00:56
RPReplay_Final1731336942.mov
Alex PRV — 14:08
nu si daca polonia era condusa de mine in eu5 si o duc pe culmele gloriei, ca fiind cel mai puternic stat din europa
Alex PRV — 19:21
https://github.com/Hallex22/java-http-server
imi dai si mie o stea pe github pentru acest proiect mirobolant?
GitHub
GitHub - Hallex22/java-http-server: Making my own simple version of...
Making my own simple version of an HTTTP 1.1 Server - Hallex22/java-http-server
GitHub - Hallex22/java-http-server: Making my own simple version of...
Alex PRV — 19:42
il trimit temporar
# ⚡ Lightweight Java HTTP Server (Express-inspired)

A lightweight, educational HTTP server framework written **from scratch in Java**, inspired by **Node.js + Express**.

This project was built to deeply understand how HTTP servers work internally:
request parsing, routing, middleware execution, body parsing, and concurrency — **without relying on existing frameworks** like Spring or Netty.
Extinde
README.md
6 KB
﻿
457261
alio5515
 
 
 
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

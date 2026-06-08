# Instrucciones para Diagramas de Actividad y Secuencia — Library Management System

Este documento describe, para cada endpoint del sistema, el flujo completo de información desde que el cliente envía la petición hasta que recibe la respuesta. Cada flujo está descrito en lenguaje natural con el nivel de detalle suficiente para que una inteligencia artificial genere los correspondientes diagramas de actividad UML y diagramas de secuencia UML en sintaxis Mermaid.

## Convenciones Generales

- **Autenticación:** Los endpoints marcados como "Auth: Sí" requieren un token JWT en el header `Authorization: Bearer <token>`. El token almacena el email del usuario como subject. Si el token falta o es inválido, el filtro `JwtFilter` intercepta la petición y devuelve `401 Unauthorized` antes de que llegue al controlador.
- **Endpoints públicos (GET):** Cualquier `GET` no requiere autenticación.
- **Endpoints privados (POST/PUT/DELETE):** Requieren autenticación. Tras pasar el filtro JWT, el `SecurityContext` contiene un `Authentication` cuyo `principal` es el email del usuario.
- **Base path:** Todos los endpoints cuelgan de `http://localhost:8080`.
- **Formato de respuesta:** Salvo que se indique lo contrario, las respuestas exitosas son `200 OK` con cuerpo JSON. Las respuestas de error son `400 Bad Request`, `401 Unauthorized`, `404 Not Found` o `409 Conflict` sin cuerpo o con un objeto `{ "error": "mensaje" }`.

---

# 1. Autenticación — `/auth`

## 1.1 POST /auth/login

**Auth:** No
**Propósito:** Autenticar a un usuario registrado y obtener un token JWT.

**Flujo:**
1. El cliente envía un `POST` a `/auth/login` con un cuerpo JSON que contiene `email` (string) y `password` (string).
2. El controlador `AuthController.login()` recibe el cuerpo y extrae ambos campos.
3. Llama a `UserService.getUserByEmail(email)`, que a su vez consulta `UserRepository.findByEmail(email)` en la base de datos.
4. Si no se encuentra un usuario con ese email, responde con `401 Unauthorized` y cuerpo `{ "exists": false }`. El flujo termina aquí.
5. Si el usuario existe, compara la contraseña recibida con `user.getPassword()` (comparación en texto plano, sin hash).
6. Si la contraseña no coincide, responde con `401 Unauthorized` y `{ "exists": false }`. El flujo termina aquí.
7. Si la contraseña coincide, llama a `JwtUtil.generateToken(email)` que construye un JWT firmado con el email como subject y una expiración configurable.
8. Responde con `200 OK` y cuerpo `{ "token": "jwt-string" }`.

**Actores involucrados:** Cliente, AuthController, UserService, UserRepository, JwtUtil, Base de datos MySQL.

**Flujo alternativo — token inválido/expirado:** El filtro JWT no interviene aquí porque el endpoint es público. No aplica.

**Flujo alternativo — error de base de datos:** Si la consulta falla, se propaga una excepción que Spring convierte en `500 Internal Server Error`.

---

## 1.2 POST /auth/register

**Auth:** No
**Propósito:** Registrar un nuevo usuario en el sistema y devolver un token JWT para que quede autenticado inmediatamente.

**Flujo:**
1. El cliente envía un `POST` a `/auth/register` con un cuerpo JSON que representa un objeto `User` completo: `name`, `email`, `password`, `phone`, `address`, `city`, `state`, `country`, `postalCode`, y un array `userTypes` (cada uno con al menos `id`).
2. El controlador `AuthController.register()` recibe el `User`.
3. Valida que `email` y `password` no sean nulos ni estén vacíos. Si lo están, responde `400 Bad Request` con `{ "error": "Email and password are required" }`.
4. Verifica si el email ya está registrado llamando a `UserService.getUserByEmail(email)` -> `UserRepository.findByEmail(email)`.
5. Si el email ya existe, responde `409 Conflict` con `{ "error": "Email already in use" }`. El flujo termina aquí.
6. Si el email es nuevo, procede a resolver los `UserType` del usuario:
   a. Si `user.getUserTypes()` no es nulo y tiene elementos, itera sobre ellos. Para cada tipo, extrae su `id` y llama a `UserTypeService.getUserType(id)` -> `UserTypeRepository.findById(id)`. Los tipos encontrados válidamente se añaden a un conjunto.
   b. Si tras la iteración el conjunto está vacío (ningún tipo válido o la lista era nula), usa el tipo por defecto con `id = 1L` (`UserTypeService.getUserType(1L)`).
   c. Asigna el conjunto de tipos resuelto al usuario mediante `user.setUserTypes(resolvedTypes)`.
7. Llama a `UserService.addUser(user)` -> `UserRepository.save(user)`, que persiste el nuevo usuario en la tabla `users` y las relaciones en `user_user_types`.
8. Genera un token JWT llamando a `JwtUtil.generateToken(email)`.
9. Responde con `200 OK` y cuerpo `{ "user": { ... }, "token": "jwt-string" }`, donde `user` es el objeto completo devuelto por JPA (con su `id` generado).

**Actores involucrados:** Cliente, AuthController, UserService, UserTypeService, UserRepository, UserTypeRepository, JwtUtil, Base de datos MySQL.

**Flujo alternativo — tipo de usuario inválido:** Si el `id` de un `UserType` no existe en base de datos, `UserTypeService.getUserType(id)` devuelve `Optional.empty()` y ese tipo se ignora. El conjunto final puede quedar vacío, en cuyo caso se usa el tipo por defecto.

---

# 2. Libros — `/books`

## 2.1 GET /books

**Auth:** No
**Propósito:** Obtener el catálogo completo de libros.

**Flujo:**
1. El cliente envía un `GET /books`.
2. El controlador `BookController.getBooks()` llama a `BookService.getBooks()`.
3. `BookService` delega en `BookRepository.findAll()`, que ejecuta `SELECT * FROM book`.
4. Responde con `200 OK` y un array JSON con todos los libros. Cada libro incluye sus relaciones (géneros, copias de préstamo) según la configuración de Jackson (\@JsonIgnore en las colecciones inversas).

**Actores involucrados:** Cliente, BookController, BookService, BookRepository, Base de datos.

---

## 2.2 GET /books/{isbn}

**Auth:** No
**Propósito:** Obtener un libro específico por su ISBN.

**Flujo:**
1. El cliente envía un `GET /books/{isbn}` donde `{isbn}` es un string con el ISBN del libro.
2. El controlador `BookController.getBook(id)` extrae el path variable y llama a `BookService.getBook(id)`.
3. `BookService` delega en `BookRepository.findById(id)`.
4. Si el libro existe, responde `200 OK` con el objeto JSON del libro.
5. Si no existe, responde `404 Not Found`.

**Actores involucrados:** Cliente, BookController, BookService, BookRepository, Base de datos.

**Flujo alternativo — libro no encontrado:** El repositorio devuelve `Optional.empty()`, el controlador responde `404`.

---

## 2.3 POST /books

**Auth:** Sí (requiere JWT)
**Propósito:** Añadir un nuevo libro al catálogo.

**Flujo:**
1. El cliente envía un `POST /books` con un cuerpo JSON que representa un `Book`: `isbn` (string), `title`, `author`, `price` (int), `stock` (int) y `genreo` (array de objetos `Genre` con al menos `id`).
2. El filtro JWT valida el token. Si es inválido o falta, responde `401` sin llegar al controlador.
3. El controlador `BookController.addBook(book)` recibe el libro y llama a `BookService.addBook(book)`.
4. `BookService` hace `BookRepository.save(book)`, que inserta el libro en la tabla `book` y las relaciones muchos-a-muchos en `book_genre`.
5. Responde `200 OK` con el objeto `Book` persistido (incluyendo sus relaciones).

**Actores involucrados:** Cliente, JwtFilter, BookController, BookService, BookRepository, Base de datos.

**Flujo alternativo — token inválido:** `JwtFilter` lanza excepción de autenticación, Spring Security devuelve `401 Unauthorized`.

---

## 2.4 PUT /books/{isbn}

**Auth:** Sí (requiere JWT)
**Propósito:** Actualizar un libro existente. Los campos se fusionan: el libro nuevo se aplica sobre el existente.

**Flujo:**
1. El cliente envía un `PUT /books/{isbn}` con un cuerpo JSON con los campos a actualizar del `Book`.
2. El filtro JWT valida el token.
3. El controlador `BookController.updateBook(id, book)` recibe el ISBN del path y el libro del cuerpo. Llama a `BookService.updateBook(id, book)`.
4. `BookService`:
   a. Busca el libro existente con `BookRepository.findById(id)`.
   b. Si no existe, devuelve `Optional.empty()` -> el controlador responde `404`.
   c. Si existe, fuerza el ISBN del path sobre el libro entrante (`newBook.setIsbn(id)`).
   d. Llama a `book.updateBook(newBook)`, método que copia los campos `author`, `isbn`, `price`, `stock`, `title` del nuevo sobre el existente (reescritura completa de esos campos).
   e. Persiste con `bookRepository.save(book)`.
5. Responde `200 OK` con el libro actualizado.

**Actores involucrados:** Cliente, JwtFilter, BookController, BookService, BookRepository, Base de datos.

---

## 2.5 DELETE /books/{isbn}

**Auth:** Sí (requiere JWT)
**Propósito:** Eliminar un libro del catálogo.

**Flujo:**
1. El cliente envía un `DELETE /books/{isbn}`.
2. El filtro JWT valida el token.
3. El controlador `BookController.deleteBook(id)` llama a `BookService.deleteBook(id)`.
4. `BookService`:
   a. Busca el libro con `BookRepository.findById(id)`.
   b. Si no existe, devuelve `false` -> el controlador responde `404 Not Found`.
   c. Si existe, lo elimina con `bookRepository.delete(book)`.
5. Responde `204 No Content` (sin cuerpo).

**Actores involucrados:** Cliente, JwtFilter, BookController, BookService, BookRepository, Base de datos.

---

## 2.6 GET /books/filter

**Auth:** No
**Propósito:** Buscar libros con filtros dinámicos (título, autor, género) y ordenación.

**Flujo:**
1. El cliente envía un `GET /books/filter` con query parameters opcionales: `title` (string, búsqueda parcial), `genre` (lista de IDs de género, separados por coma), `author` (string, búsqueda parcial), `sortBy` (string, default `"isbn"`), `order` (string, `"asc"` o `"desc"`, default `"asc"`).
2. El controlador `BookController.findWithFilter(...)` recibe los parámetros.
3. Convierte `order` en un `Sort.Direction` y construye un `Sort.by(direction, sortBy)`.
4. Llama a `BookService.getBooksFilter(title, genre, author, sort)`.
5. `BookService` construye una especificación JPA dinámica usando `BookSpecifications`:
   a. `hasTitle`: Filtro `LIKE %title%` (case-insensitive) sobre el campo `title`. Solo se aplica si `title` no es nulo.
   b. `hasAuthor`: Filtro `LIKE %author%` (case-insensitive) sobre el campo `author`. Solo si `author` no es nulo.
   c. `hasGenre`: Filtro con `INNER JOIN` a la tabla `genreo` donde `genre.id IN (lista de genres)`. Se aplica `distinct(true)` para evitar duplicados. Solo si `genre` no es nulo y no está vacío.
   d. Todas las specifications se combinan con `AND` lógico.
6. Ejecuta `BookRepository.findAll(specification, sort)` que usa `JpaSpecificationExecutor<Book>`.
7. Responde `200 OK` con un array JSON de libros filtrados y ordenados. Si no hay filtros, devuelve todos los libros ordenados.

**Actores involucrados:** Cliente, BookController, BookService, BookSpecifications, BookRepository, Base de datos.

**Flujo alternativo — sin parámetros:** Si no se envía ningún filtro, `title`, `genre` y `author` son nulos, la specification queda vacía y se devuelven todos los libros ordenados por `isbn` ascendente.

---

# 3. Usuarios — `/users`

## 3.1 GET /users

**Auth:** No
**Propósito:** Obtener la lista completa de usuarios registrados.

**Flujo:**
1. El cliente envía `GET /users`.
2. El controlador `UserController.getUsers()` llama a `UserService.listUsers()`.
3. `UserService` hace `UserRepository.findAll()`.
4. Responde `200 OK` con un array JSON de todos los usuarios.

**Actores involucrados:** Cliente, UserController, UserService, UserRepository, Base de datos.

---

## 3.2 GET /users/{id}

**Auth:** No
**Propósito:** Obtener un usuario específico por su ID.

**Flujo:**
1. El cliente envía `GET /users/{id}`.
2. El controlador `UserController.getUser(id)` llama a `UserService.getUser(id)`.
3. `UserService` hace `UserRepository.findById(id)`.
4. Si existe, responde `200 OK` con el usuario. Si no, `404`.

**Actores involucrados:** Cliente, UserController, UserService, UserRepository, Base de datos.

---

## 3.3 POST /users

**Auth:** Sí (requiere JWT)
**Propósito:** Crear un nuevo usuario (vía administrativa). Nótese que el registro público usa `POST /auth/register`.

**Flujo:**
1. El cliente envía `POST /users` con cuerpo JSON de `User`.
2. El filtro JWT valida el token.
3. El controlador `UserController.addUser(user)` llama a `UserService.addUser(user)`.
4. `UserService` hace `UserRepository.save(user)`.
5. Responde `200 OK` con el usuario creado.

**Actores involucrados:** Cliente, JwtFilter, UserController, UserService, UserRepository, Base de datos.

---

## 3.4 PUT /users/{id}

**Auth:** Sí (requiere JWT)
**Propósito:** Actualizar parcialmente un usuario. Solo se actualizan los campos no nulos del cuerpo.

**Flujo:**
1. El cliente envía `PUT /users/{id}` con cuerpo JSON parcial de `User`.
2. El filtro JWT valida el token.
3. El controlador `UserController.updateUser(id, user)` llama a `UserService.updateUser(id, user)`.
4. `UserService`:
   a. Busca el usuario existente con `UserRepository.findById(id)`.
   b. Si no existe, devuelve `Optional.empty()` -> `404`.
   c. Si existe, copia selectivamente del incoming al existing solo los campos que no sean nulos: `name`, `password` (solo si no nulo), `phone`, `address`, `city`, `state`, `country`, `postalCode`.
   d. Persiste con `userRepository.save(existing)`.
5. Responde `200 OK` con el usuario actualizado.

**Actores involucrados:** Cliente, JwtFilter, UserController, UserService, UserRepository, Base de datos.

---

## 3.5 DELETE /users/{id}

**Auth:** Sí (requiere JWT)
**Propósito:** Eliminar un usuario.

**Flujo:**
1. El cliente envía `DELETE /users/{id}`.
2. El filtro JWT valida el token.
3. El controlador `UserController.deleteUser(id)` llama a `UserService.deleteUser(id)`.
4. `UserService`:
   a. Busca con `UserRepository.findById(id)`.
   b. Si no existe, devuelve `false` -> `404`.
   c. Si existe, elimina con `userRepository.delete(user)`.
5. Responde `204 No Content`.

**Actores involucrados:** Cliente, JwtFilter, UserController, UserService, UserRepository, Base de datos.

---

# 4. Órdenes de Compra — `/orders`

## 4.1 GET /orders

**Auth:** No
**Propósito:** Obtener todas las órdenes de compra.

**Flujo:**
1. `GET /orders`.
2. `OrderController.getOrders()` -> `OrderService.getOrders()` -> `OrderRepository.findAll()`.
3. Responde `200 OK` con `List<Order>`.

**Actores:** Cliente, OrderController, OrderService, OrderRepository, BD.

---

## 4.2 GET /orders/{id}

**Auth:** No
**Propósito:** Obtener una orden específica.

**Flujo:**
1. `GET /orders/{id}`.
2. `OrderController.getOrder(id)` -> `OrderService.getOrder(id)` -> `OrderRepository.findById(id)`.
3. `200 OK` con la orden, o `404`.

**Actores:** Cliente, OrderController, OrderService, OrderRepository, BD.

---

## 4.3 POST /orders

**Auth:** Sí (requiere JWT)
**Propósito:** Crear una nueva orden de compra. El usuario autenticado se asigna automáticamente como propietario de la orden.

**Flujo:**
1. El cliente envía `POST /orders` con un cuerpo JSON que contiene al menos `orderDate` (Date), `state` (String: `PENDING`, `SENDED`, `IN_DISTIBUITION`, `DELIVERED`) y opcionalmente un array `buys`.
2. El filtro JWT valida el token y establece el `Authentication` en el contexto de seguridad.
3. El controlador `OrderController.addOrder(order, auth)`:
   a. Extrae el email del usuario autenticado mediante `auth.getName()`.
   b. Busca al usuario con `UserService.getUserByEmail(auth.getName())` -> `UserRepository.findByEmail(email)`.
   c. Asigna el usuario encontrado a la orden: `order.setUser(user)`.
   d. Llama a `OrderService.addOrder(order)` -> `OrderRepository.save(order)`.
4. Responde `200 OK` con la orden creada (incluyendo el usuario asignado y sus `buys` si se incluyeron en la petición).

**Actores involucrados:** Cliente, JwtFilter, OrderController, UserService, UserRepository, OrderService, OrderRepository, Base de datos.

**Flujo alternativo — usuario no encontrado:** Si `UserService.getUserByEmail` devuelve `Optional.empty()` (el email del token no corresponde a ningún usuario), se lanza una excepción que resulta en `500 Internal Server Error`.

---

## 4.4 PUT /orders/{id}

**Auth:** Sí (requiere JWT)
**Propósito:** Actualizar una orden existente (reemplazo completo).

**Flujo:**
1. El cliente envía `PUT /orders/{id}` con cuerpo JSON de `Order`.
2. El filtro JWT valida el token.
3. El controlador `OrderController.updateOrder(id, order)` llama a `OrderService.updateOrder(id, order)`.
4. `OrderService`:
   a. Verifica existencia con `OrderRepository.findById(id)`. Si no existe, `Optional.empty()` -> `404`.
   b. Fuerza el `id` del path sobre el objeto entrante (`order.setId(id)`).
   c. Persiste con `orderRepository.save(order)` (reescritura completa).
5. Responde `200 OK` con la orden actualizada.

**Actores:** Cliente, JwtFilter, OrderController, OrderService, OrderRepository, BD.

---

## 4.5 DELETE /orders/{id}

**Auth:** Sí (requiere JWT)
**Propósito:** Eliminar una orden.

**Flujo:**
1. `DELETE /orders/{id}`.
2. Filtro JWT válido.
3. `OrderController.deleteOrder(id)` -> `OrderService.deleteOrder(id)`.
4. `OrderService` verifica existencia con `OrderRepository.existsById(id)`. Si no existe, `false` -> `404`. Si existe, `orderRepository.deleteById(id)`.
5. `204 No Content`.

**Actores:** Cliente, JwtFilter, OrderController, OrderService, OrderRepository, BD.

---

# 5. Préstamos — `/borrowings`

## 5.1 GET /borrowings

**Auth:** No
**Propósito:** Listar todos los préstamos.

**Flujo:**
1. `GET /borrowings`.
2. `BorrowingController.getBorrowings()` -> `BorrowingService.getBorrowings()` -> `BorrowingRepository.findAll()`.
3. `200 OK` con `List<Borrowing>`.

**Actores:** Cliente, BorrowingController, BorrowingService, BorrowingRepository, BD.

---

## 5.2 GET /borrowings/{id}

**Auth:** No
**Propósito:** Obtener un préstamo específico.

**Flujo:**
1. `GET /borrowings/{id}`.
2. `BorrowingController.getBorrowing(id)` -> `BorrowingService.getBorrowing(id)` -> `BorrowingRepository.findById(id)`.
3. `200 OK` o `404`.

**Actores:** Cliente, BorrowingController, BorrowingService, BorrowingRepository, BD.

---

## 5.3 POST /borrowings

**Auth:** Sí (requiere JWT)
**Propósito:** Crear un nuevo préstamo. El usuario se resuelve del token JWT. La copia física (`BorrowingCopy`) se resuelve del cuerpo.

**Flujo:**
1. El cliente envía `POST /borrowings` con cuerpo JSON: `copy` (objeto con `id`), `borrowingDate` (LocalDate), `returnDate` (LocalDate).
2. El filtro JWT valida el token.
3. El controlador `BorrowingController.addBorrowing(borrowing, auth)`:
   a. Extrae el email del `Authentication` (`auth.getName()`).
   b. Busca al usuario: `UserService.getUserByEmail(email)` -> `UserRepository.findByEmail(email)`. Lo asigna al préstamo.
   c. Si `borrowing.getCopy()` no es nulo y tiene `id`, resuelve la `BorrowingCopy` llamando a `BorrowingCopyService.getBorrowingCopy(copyId)` -> `BorrowingCopyRepository.findById(copyId)`. La asigna al préstamo.
   d. Llama a `BorrowingService.addBorrowing(borrowing)` -> `BorrowingRepository.save(borrowing)`.
4. Responde `200 OK` con el préstamo creado.

**Actores involucrados:** Cliente, JwtFilter, BorrowingController, UserService, BorrowingCopyService, BorrowingService, UserRepository, BorrowingCopyRepository, BorrowingRepository, Base de datos.

**Flujo alternativo — copyId no existe:** Si `BorrowingCopyRepository.findById(copyId)` devuelve `Optional.empty()`, el controlador asigna `null` como copy del préstamo, lo que puede causar errores de integridad en la base de datos (no hay validación explícita).

---

## 5.4 PUT /borrowings/{id}

**Auth:** Sí (requiere JWT)
**Propósito:** Actualizar un préstamo (reemplazo completo).

**Flujo:**
1. `PUT /borrowings/{id}` con cuerpo JSON de `Borrowing`.
2. Filtro JWT válido.
3. `BorrowingController.updateBorrowing(id, borrowing)` -> `BorrowingService.updateBorrowing(id, borrowing)`.
4. `BorrowingService`: busca existencia con `BorrowingRepository.findById(id)`. Si no existe -> `404`. Fuerza `id` sobre el incoming. Guarda con `borrowingRepository.save(borrowing)`.
5. `200 OK` con el préstamo actualizado.

**Actores:** Cliente, JwtFilter, BorrowingController, BorrowingService, BorrowingRepository, BD.

---

## 5.5 DELETE /borrowings/{id}

**Auth:** Sí (requiere JWT)
**Propósito:** Eliminar un préstamo.

**Flujo:**
1. `DELETE /borrowings/{id}`.
2. Filtro JWT válido.
3. `BorrowingController.deleteBorrowing(id)` -> `BorrowingService.deleteBorrowing(id)`.
4. `BorrowingService`: verifica con `BorrowingRepository.existsById(id)`. Si no existe -> `404`. Si existe, `borrowingRepository.deleteById(id)`.
5. `204 No Content`.

**Actores:** Cliente, JwtFilter, BorrowingController, BorrowingService, BorrowingRepository, BD.

---

# 6. Compras (Line-Items) — `/buys`

## 6.1 GET /buys

**Auth:** No
**Propósito:** Obtener todas las líneas de compra.

**Flujo:**
1. `GET /buys`.
2. `BuyController.getBuys()` -> `BuyService.getBuys()` -> `BuyRepository.findAll()`.
3. `200 OK` con `List<Buy>`.

**Actores:** Cliente, BuyController, BuyService, BuyRepository, BD.

---

## 6.2 GET /buys/{id}

**Auth:** No
**Propósito:** Obtener una línea de compra específica.

**Flujo:**
1. `GET /buys/{id}`.
2. `BuyController.getBuy(id)` -> `BuyService.getBuy(id)` -> `BuyRepository.findById(id)`.
3. `200 OK` o `404`.

**Actores:** Cliente, BuyController, BuyService, BuyRepository, BD.

---

## 6.3 POST /buys

**Auth:** Sí (requiere JWT)
**Propósito:** Crear una línea de compra asociada a un libro y una orden.

**Flujo:**
1. El cliente envía `POST /buys` con cuerpo JSON: `book` (objeto con `isbn`), `order` (objeto con `id`), `quantity` (int), `price` (int), `discount` (int).
2. El filtro JWT valida el token.
3. El controlador `BuyController.addBuy(buy)` llama diretamente a `BuyService.addBuy(buy)` sin resolver usuario ni hacer validaciones adicionales.
4. `BuyService` hace `BuyRepository.save(buy)`.
5. Responde `200 OK` con la línea de compra creada.

**Actores involucrados:** Cliente, JwtFilter, BuyController, BuyService, BuyRepository, Base de datos.

---

## 6.4 PUT /buys/{id}

**Auth:** Sí (requiere JWT)
**Propósito:** Actualizar una línea de compra (reemplazo completo).

**Flujo:**
1. `PUT /buys/{id}` con cuerpo JSON de `Buy`.
2. Filtro JWT válido.
3. `BuyController.updateBuy(id, buy)` -> `BuyService.updateBuy(id, buy)`.
4. `BuyService`: busca con `BuyRepository.findById(id)`. Si no existe -> `404`. Fuerza `id` sobre incoming. Guarda con `buyRepository.save(buy)`.
5. `200 OK`.

**Actores:** Cliente, JwtFilter, BuyController, BuyService, BuyRepository, BD.

---

## 6.5 DELETE /buys/{id}

**Auth:** Sí (requiere JWT)
**Propósito:** Eliminar una línea de compra.

**Flujo:**
1. `DELETE /buys/{id}`.
2. Filtro JWT válido.
3. `BuyController.deleteBuy(id)` -> `BuyService.deleteBuy(id)`.
4. `BuyService`: verifica con `BuyRepository.existsById(id)`. Si no existe -> `404`. Si existe, `buyRepository.deleteById(id)`.
5. `204 No Content`.

**Actores:** Cliente, JwtFilter, BuyController, BuyService, BuyRepository, BD.

---

# 7. Copias de Préstamo — `/borrowing-copies`

## 7.1 GET /borrowing-copies

**Auth:** No
**Propósito:** Listar todas las copias físicas de libros disponibles para préstamo.

**Flujo:**
1. `GET /borrowing-copies`.
2. `BorrowingCopyController.getBorrowingCopies()` -> `BorrowingCopyService.getBorrowingCopies()` -> `BorrowingCopyRepository.findAll()`.
3. `200 OK` con `List<BorrowingCopy>`.

**Actores:** Cliente, BorrowingCopyController, BorrowingCopyService, BorrowingCopyRepository, BD.

---

## 7.2 GET /borrowing-copies/{id}

**Auth:** No
**Propósito:** Obtener una copia específica.

**Flujo:**
1. `GET /borrowing-copies/{id}`.
2. `BorrowingCopyController.getBorrowingCopy(id)` -> `BorrowingCopyService.getBorrowingCopy(id)` -> `BorrowingCopyRepository.findById(id)`.
3. `200 OK` o `404`.

**Actores:** Cliente, BorrowingCopyController, BorrowingCopyService, BorrowingCopyRepository, BD.

---

## 7.3 POST /borrowing-copies

**Auth:** Sí (requiere JWT)
**Propósito:** Crear una nueva copia física directamente (especificando libro y estado).

**Flujo:**
1. El cliente envía `POST /borrowing-copies` con cuerpo JSON: `book` (objeto con `isbn`), `avlbl` (string: `AVALIABLE`, `RESERVED`, `NOT_AVALIABLE`).
2. Filtro JWT válido.
3. `BorrowingCopyController.addBorrowingCopy(borrowingCopy)` -> `BorrowingCopyService.addBorrowingCopy(borrowingCopy)` -> `BorrowingCopyRepository.save(borrowingCopy)`.
4. `200 OK` con la copia creada.

**Actores:** Cliente, JwtFilter, BorrowingCopyController, BorrowingCopyService, BorrowingCopyRepository, BD.

---

## 7.4 POST /borrowing-copies/books/{isbn}

**Auth:** Sí (requiere JWT)
**Propósito:** Crear una copia de préstamo para un libro existente. La copia se crea automáticamente con estado `AVALIABLE`. Este es el endpoint que usa el frontend en la pantalla de administración.

**Flujo:**
1. El cliente envía `POST /borrowing-copies/books/{isbn}` (sin cuerpo).
2. Filtro JWT válido.
3. El controlador `BorrowingCopyController.addBorrowingCopyForBook(isbn)` llama a `BorrowingCopyService.addBorrowingCopyOfBook(isbn)`.
4. `BorrowingCopyService`:
   a. Busca el libro por ISBN: `BookRepository.findById(isbn)`.
   b. Si el libro no existe, devuelve `Optional.empty()` -> el controlador responde `404 Not Found`.
   c. Si existe, crea una nueva `BorrowingCopy` con `new BorrowingCopy(book, Avaliavility.AVALIABLE)`.
   d. Persiste con `BorrowingCopyRepository.save(borrowingCopy)`.
5. Responde `200 OK` con la copia creada.

**Actores involucrados:** Cliente, JwtFilter, BorrowingCopyController, BorrowingCopyService, BookRepository, BorrowingCopyRepository, Base de datos.

**Flujo alternativo — libro no encontrado:** Si `BookRepository.findById(isbn)` devuelve `Optional.empty()`, el servicio devuelve `Optional.empty()` y el controlador responde `404`.

---

## 7.5 PUT /borrowing-copies/{id}

**Auth:** Sí (requiere JWT)
**Propósito:** Actualizar una copia (reemplazo completo).

**Flujo:**
1. `PUT /borrowing-copies/{id}` con cuerpo JSON.
2. Filtro JWT válido.
3. `BorrowingCopyController.updateBorrowingCopy(id, borrowingCopy)` -> `BorrowingCopyService.updateBorrowingCopy(id, borrowingCopy)`.
4. `BorrowingCopyService`: busca con `BorrowingCopyRepository.findById(id)`. Si no existe -> `404`. Fuerza `id`. Guarda con `borrowingCopyRepository.save(borrowingCopy)`.
5. `200 OK`.

**Actores:** Cliente, JwtFilter, BorrowingCopyController, BorrowingCopyService, BorrowingCopyRepository, BD.

---

## 7.6 DELETE /borrowing-copies/{id}

**Auth:** Sí (requiere JWT)
**Propósito:** Eliminar una copia específica por su ID.

**Flujo:**
1. `DELETE /borrowing-copies/{id}`.
2. Filtro JWT válido.
3. `BorrowingCopyController.deleteBorrowingCopy(id)` -> `BorrowingCopyService.deleteBorrowingCopy(id)`.
4. `BorrowingCopyService`: verifica con `BorrowingCopyRepository.existsById(id)`. Si no existe -> `404`. Si existe, `borrowingCopyRepository.deleteById(id)`.
5. `204 No Content`.

**Actores:** Cliente, JwtFilter, BorrowingCopyController, BorrowingCopyService, BorrowingCopyRepository, BD.

---

## 7.7 DELETE /borrowing-copies/books/{isbn}/last

**Auth:** Sí (requiere JWT)
**Propósito:** Eliminar la última copia de préstamo asociada a un libro.

**Flujo:**
1. El cliente envía `DELETE /borrowing-copies/books/{isbn}/last` (sin cuerpo).
2. Filtro JWT válido.
3. El controlador `BorrowingCopyController.deleteLastBorrowingCopyOfBook(isbn)` llama a `BorrowingCopyService.deleteLastBorrowingCopyOfBook(isbn)`.
4. `BorrowingCopyService`:
   a. Busca todas las copias del libro: `BorrowingCopyRepository.findByBookIsbn(isbn)`.
   b. Si la lista está vacía, devuelve `false` -> el controlador responde `404 Not Found`.
   c. Si hay copias, obtiene la última con `copies.get(copies.size() - 1)` o `copies.getLast()` y la elimina con `borrowingCopyRepository.deleteById(lastCopy.getId())`.
5. Responde `204 No Content`.

**Actores involucrados:** Cliente, JwtFilter, BorrowingCopyController, BorrowingCopyService, BorrowingCopyRepository, Base de datos.

**Flujo alternativo — sin copias:** Si `findByBookIsbn` devuelve lista vacía, no hay nada que eliminar -> `404`.

---

# 8. Tipos de Usuario — `/user-types`

## 8.1 GET /user-types

**Auth:** No
**Propósito:** Obtener todos los tipos de usuario (roles).

**Flujo:**
1. `GET /user-types`.
2. `UserTypeController.getUserTypes()` -> `UserTypeService.getUserTypes()` -> `UserTypeRepository.findAll()`.
3. `200 OK` con `List<UserType>`.

**Actores:** Cliente, UserTypeController, UserTypeService, UserTypeRepository, BD.

---

## 8.2 GET /user-types/{id}

**Auth:** No
**Propósito:** Obtener un tipo de usuario específico.

**Flujo:**
1. `GET /user-types/{id}`.
2. `UserTypeController.getUserType(id)` -> `UserTypeService.getUserType(id)` -> `UserTypeRepository.findById(id)`.
3. `200 OK` o `404`.

**Actores:** Cliente, UserTypeController, UserTypeService, UserTypeRepository, BD.

---

## 8.3 POST /user-types

**Auth:** Sí (requiere JWT)
**Propósito:** Crear un nuevo tipo de usuario.

**Flujo:**
1. `POST /user-types` con cuerpo JSON: `{ "type": "string" }`.
2. Filtro JWT válido.
3. `UserTypeController.addUserType(userType)` -> `UserTypeService.addUserType(userType)` -> `UserTypeRepository.save(userType)`.
4. `200 OK`.

**Actores:** Cliente, JwtFilter, UserTypeController, UserTypeService, UserTypeRepository, BD.

---

## 8.4 PUT /user-types/{id}

**Auth:** Sí (requiere JWT)
**Propósito:** Actualizar un tipo de usuario.

**Flujo:**
1. `PUT /user-types/{id}` con cuerpo JSON.
2. Filtro JWT válido.
3. `UserTypeController.updateUserType(id, userType)` -> `UserTypeService.updateUserType(id, userType)`.
4. `UserTypeService`: busca con `UserTypeRepository.findById(id)`. Si no existe -> `404`. Fuerza `id`. Guarda.
5. `200 OK`.

**Actores:** Cliente, JwtFilter, UserTypeController, UserTypeService, UserTypeRepository, BD.

---

## 8.5 DELETE /user-types/{id}

**Auth:** Sí (requiere JWT)
**Propósito:** Eliminar un tipo de usuario.

**Flujo:**
1. `DELETE /user-types/{id}`.
2. Filtro JWT válido.
3. `UserTypeController.deleteUserType(id)` -> `UserTypeService.deleteUserType(id)`.
4. `UserTypeService`: verifica con `UserTypeRepository.existsById(id)`. Si no existe -> `404`. Si existe, `userTypeRepository.deleteById(id)`.
5. `204 No Content`.

**Actores:** Cliente, JwtFilter, UserTypeController, UserTypeService, UserTypeRepository, BD.

---

# Resumen de Patrones Comunes entre Flujos

Para facilitar la generación de diagramas, estos son los patrones que se repiten en múltiples endpoints:

## Patrón A — Lectura pública (GET sin filtros)
Secuencia: Cliente -> Controlador -> Servicio -> Repositorio -> BD -> Repositorio -> Servicio -> Controlador -> Cliente (200 con lista o entidad). Sin validación ni autenticación.

## Patrón B — Lectura por ID (GET /{id})
Secuencia: Cliente -> Controlador -> Servicio -> Repositorio.findById -> BD. Dos caminos: existe (200 con entidad) o no existe (404).

## Patrón C — Creación autenticada (POST con JWT)
Secuencia: Cliente -> JwtFilter (valida token) -> Controlador -> Servicio -> Repositorio.save -> BD -> 200 con entidad creada. Si el token falta o es inválido, se corta en JwtFilter con 401.

## Patrón D — Creación autenticada con resolución de entidad desde el token (POST /orders, POST /borrowings)
Secuencia: Cliente -> JwtFilter -> Controlador (extrae email del Authentication) -> UserService.getUserByEmail -> UserRepository.findByEmail -> Controlador asigna usuario a la entidad -> Servicio -> Repositorio.save -> BD -> 200.

## Patrón E — Actualización (PUT /{id})
Secuencia: Cliente -> JwtFilter -> Controlador -> Servicio.findById (si no existe -> 404) -> modificación -> save -> 200 con entidad actualizada.

## Patrón F — Eliminación (DELETE /{id})
Secuencia: Cliente -> JwtFilter -> Controlador -> Servicio.existsById (si no existe -> 404) -> deleteById -> 204.

## Patrón G — Filter con especificaciones dinámicas (GET /books/filter)
Secuencia: Cliente -> Controlador (parsea query params y construye Sort) -> Servicio (construye Specification compuesta con AND de hasTitle, hasAuthor, hasGenre) -> Repositorio.findAll(spec, sort) -> BD -> 200 con lista filtrada y ordenada.

## Flujo de seguridad transversal (JwtFilter)
Para cualquier endpoint privado (POST, PUT, DELETE):
1. JwtFilter recibe la petición HTTP.
2. Extrae el header `Authorization`.
3. Si no está presente o no empieza con `Bearer `, lanza excepción de autenticación -> 401.
4. Si está presente, extrae el token, llama a `JwtUtil.extractUserName(token)` para obtener el email.
5. Crea un `UsernamePasswordAuthenticationToken` con el email como principal y autoridad `ROLE_USER`.
6. Lo establece en el `SecurityContextHolder`.
7. La petición continúa al controlador.
8. En el controlador, se puede inyectar `Authentication auth` como parámetro para acceder al email del usuario autenticado.

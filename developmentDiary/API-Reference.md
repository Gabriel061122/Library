# API Reference

Todas las rutas parten de `http://localhost:8080`. Las rutas protegidas requieren header:

```
Authorization: Bearer <token>
```

## Autenticación (`/auth`)

### POST `/auth/register`
Registrar nuevo usuario.

```json
{
  "name": "Ana Garcia",
  "email": "ana@example.com",
  "password": "clave-segura",
  "phone": "600111222",
  "address": "Calle Mayor 1",
  "city": "Madrid",
  "state": "Madrid",
  "country": "Spain",
  "postalCode": "28001",
  "userTypes": [{ "id": 1 }]
}
```

**Respuesta:** `{ "user": {...}, "token": "jwt..." }`

### POST `/auth/login`
Iniciar sesión.

```json
{ "email": "ana@example.com", "password": "clave-segura" }
```

**Respuesta:** `{ "token": "jwt..." }`

---

## Usuarios (`/users`) — Protegido

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/users` | Listar todos |
| GET | `/users/{id}` | Obtener por ID |
| POST | `/users` | Crear |
| PUT | `/users/{id}` | Actualizar |
| DELETE | `/users/{id}` | Eliminar |

---

## Libros (`/books`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/books` | Listar todos |
| GET | `/books/{isbn}` | Obtener por ISBN |
| POST | `/books` | Crear |
| PUT | `/books/{isbn}` | Actualizar |
| DELETE | `/books/{isbn}` | Eliminar |
| GET | `/books/filter?title=&author=&genre=1,2&sortBy=title&order=asc` | Filtrar y ordenar |

---

## Órdenes (`/orders`) — Protegido

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/orders` | Listar todas |
| GET | `/orders/{id}` | Obtener por ID |
| POST | `/orders` | Crear (asigna usuario autenticado) |
| PUT | `/orders/{id}` | Actualizar |
| DELETE | `/orders/{id}` | Eliminar |

---

## Compras (`/buys`) — Protegido

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/buys` | Listar todas |
| GET | `/buys/{id}` | Obtener por ID |
| POST | `/buys` | Crear |
| PUT | `/buys/{id}` | Actualizar |
| DELETE | `/buys/{id}` | Eliminar |

---

## Préstamos (`/borrowings`) — Protegido

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/borrowings` | Listar todos |
| GET | `/borrowings/{id}` | Obtener por ID |
| POST | `/borrowings` | Crear (asigna usuario autenticado) |
| PUT | `/borrowings/{id}` | Actualizar |
| DELETE | `/borrowings/{id}` | Eliminar |

---

## Copias de préstamo (`/borrowing-copies`) — Protegido

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/borrowing-copies` | Listar todas |
| GET | `/borrowing-copies/{id}` | Obtener por ID |
| POST | `/borrowing-copies` | Crear |
| POST | `/borrowing-copies/books/{isbn}` | Crear copia para un libro |
| PUT | `/borrowing-copies/{id}` | Actualizar |
| DELETE | `/borrowing-copies/{id}` | Eliminar |
| DELETE | `/borrowing-copies/books/{isbn}/last` | Eliminar última copia de un libro |

---

## Tipos de usuario (`/user-types`) — Protegido

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/user-types` | Listar todos |
| GET | `/user-types/{id}` | Obtener por ID |
| POST | `/user-types` | Crear |
| PUT | `/user-types/{id}` | Actualizar |
| DELETE | `/user-types/{id}` | Eliminar |

---

## Estados de orden (`OrderState`)

```
PENDING → SENDED → IN_DISTRIBUTION → DELIVERED
```

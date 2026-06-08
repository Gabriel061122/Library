# Diagramas del Sistema — Librería

Este documento describe los diagramas UML generados para el proyecto **Library** (biblioteca-librería). Los diagramas están escritos en sintaxis [Mermaid](https://mermaid.js.org/) y se pueden visualizar en cualquier editor compatible (GitHub, Obsidian con plugin Mermaid, VS Code con extensión Mermaid, etc.).

---

## 1. Diagrama de Paquetes

**Archivo:** [`diagrama-de-paquetes.mmd`](diagrama-de-paquetes.mmd)

### Propósito

Mostrar la estructura de módulos/paquetes del proyecto y las dependencias entre ellos, tanto del backend (Java/Spring Boot) como del frontend (React).

### Paquetes del backend

| Paquete | Responsabilidad |
|---|---|
| `com.libreria.api` | Controladores REST. Punto de entrada HTTP. Cada controlador expone un conjunto de endpoints. |
| `com.libreria.service` | Lógica de negocio. Orquesta operaciones entre repositorios y entidades. |
| `com.libreria.model.book` | Entidades JPA del catálogo: `Book`, `Genre`, `BorrowingCopy`, enum `Avaliavility`. |
| `com.libreria.model.exchange` | Entidades JPA de transacciones: `Order`, `Buy`, `Borrowing`, enum `OrderState`. |
| `com.libreria.model.user` | Entidades JPA de usuarios: `User`, `UserType`, interfaz `UserAction`. |
| `com.libreria.model.keys` | Claves compuestas: `UserKey`. |
| `com.libreria.model.repositories` | Interfaces Spring Data JPA para acceso a datos. |
| `com.libreria.model.repositories.specification` | Especificaciones JPA para consultas dinámicas (`BookSpecifications`). |
| `com.libreria.security` | Seguridad JWT: generación/validación de tokens, filtro HTTP, configuración. |
| `com.libreria.exceptions` | Excepciones personalizadas del dominio. |

### Módulos del frontend

| Módulo | Responsabilidad |
|---|---|
| `api/api.js` | Cliente Axios con todas las funciones de llamada al backend. |
| `context/AuthContext.jsx` | Estado global de autenticación (login, logout, rol). |
| `components/` | Componentes reutilizables: `Navbar`, `PrivateRoute`, `AdminRoute`. |
| `pages/` | Pantallas completas: `Books`, `Login`, `Register`, `Orders`, `Borrowings`, `Admin`. |

### Flujo de dependencias

```
Frontend (React)  ──HTTP/JSON──►  Controladores (api)
                                       │
                                       ▼
                                  Services
                                       │
                                       ▼
                                  Repositories
                                       │
                                       ▼
                                  Entidades JPA
                                       │
                                       ▼
                                  Base de datos (MySQL)
```

Las flechas en el diagrama indican dependencia directa (la capa superior conoce e invoca a la inferior). El paquete `security` es usado por los controladores para autenticar peticiones.

---

## 2. Diagrama de Clases

**Archivo:** [`diagrama-de-clases.mmd`](diagrama-de-clases.mmd)

### Propósito

Modelar las entidades del dominio, sus atributos, métodos y relaciones (asociaciones, composiciones, herencia y enumeraciones).

### Entidades principales

#### `Book` (Libro)
Representa un libro del catálogo. Es la entidad central del sistema.
- **Clave primaria:** `isbn` (String)
- Se relaciona con `Genre` (many-to-many), `BorrowingCopy` (one-to-many) y `Buy` (one-to-many).

#### `User` (Usuario)
Representa un usuario registrado en el sistema. Puede ser CUSTOMER, LIBRARIAN o ADMIN según sus `UserType`.
- Se relaciona con `Order` (one-to-many) y `Borrowing` (one-to-many).

#### `Order` (Orden de compra)
Representa una compra de libros. Tiene un ciclo de vida de estado: `PENDING → SENDED → IN_DISTIBUITION → DELIVERED`.
- Contiene una lista de `Buy` (line-items), cada uno con cantidad, precio y descuento.

#### `Borrowing` (Préstamo)
Representa el préstamo de un ejemplar físico (`BorrowingCopy`) a un usuario por un período (fecha de préstamo y devolución).

#### `BorrowingCopy` (Copia de préstamo)
Ejemplar físico de un libro disponible para préstamo. Su estado (`Avaliavility`) puede ser `AVALIABLE`, `RESERVED` o `NOT_AVALIABLE`.

### Enumeraciones

| Enum | Valores | Uso |
|---|---|---|
| `Avaliavility` | AVALIABLE, RESERVED, NOT_AVALIABLE | Estado de `BorrowingCopy` |
| `OrderState` | PENDING, SENDED, IN_DISTIBUITION, DELIVERED | Ciclo de vida de `Order` |

### Interfaz `UserAction`

Define operaciones que un usuario puede realizar (`updateUser`, `addOrder`, `Borrow`, `finishBorrowing`, `reserve`, `endReserve`). Actualmente no está implementada por ninguna clase.

### Herencia de excepciones

```
InvalidOrderModification
       ↑
InvalidOrderStateModificationException
```

`InvalidOrderModification` es la excepción base; `InvalidOrderStateModificationException` la extiende para casos específicos de modificación de órdenes no pendientes.

### Relaciones clave

| Origen | Destino | Tipo | Descripción |
|---|---|---|---|
| `Book` | `BorrowingCopy` | 1 → * | Un libro tiene muchos ejemplares físicos |
| `Book` | `Buy` | 1 → * | Un libro aparece en muchas compras |
| `Book` | `Genre` | * ↔ * | Un libro tiene muchos géneros y viceversa |
| `User` | `Order` | 1 → * | Un usuario tiene muchas órdenes |
| `User` | `Borrowing` | 1 → * | Un usuario tiene muchos préstamos |
| `User` | `UserType` | * ↔ * | Un usuario tiene muchos roles y viceversa |
| `Order` | `Buy` | 1 → * | Una orden contiene muchas líneas de compra |
| `Borrowing` | `BorrowingCopy` | 1 → 1 | Cada préstamo está asociado a un único ejemplar |

---

## 3. Visualización

Para ver estos diagramas:

1. **GitHub:** Mermaid se renderiza automáticamente en archivos `.md` y `.mmd`.
2. **Obsidian:** Instalar el plugin "Mermaid" y abrir los archivos.
3. **VS Code:** Instalar la extensión "Markdown Preview Mermaid Support".
4. **Herramienta online:** Copiar el contenido en https://mermaid.live/ o https://mermaid.ink/.
5. **CLI:** Usar `npx @mermaid-js/mermaid-cli diagrama-de-clases.mmd` para generar PNG/SVG.

---

## 4. Notas sobre el código existente

- El servicio `BooksService` duplica parcialmente funcionalidad de `BookService` y `BorrowingCopyService`.
- El controlador `GenreController` y el servicio `GenreService` están vacíos (stubs).
- La interfaz `UserAction` está definida pero no implementada.
- La contraseña de `User` se almacena en texto plano (sin hash).
- Los enums tienen errores ortográficos: `AVALIABLE` (falta la `A`), `IN_DISTIBUITION` (debería ser `DISTRIBUTION`).

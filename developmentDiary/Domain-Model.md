# Domain Model

## Explicación sencilla

La app gestiona una **biblioteca-librería** donde:

1. **Libros** (`Book`) — el catálogo. Tienen ISBN, título, autor, precio y stock.
2. **Usuarios** (`User`) — clientes, bibliotecarios o admins. Tienen datos personales y tipos de usuario.
3. **Órdenes** (`Order`) — cuando alguien compra libros. Tiene estados: PENDING → SENDED → IN_DISTRIBUTION → DELIVERED.
4. **Compras** (`Buy`) — cada libro dentro de una orden (con cantidad, precio y descuento).
5. **Préstamos** (`Borrowing`) — cuando alguien toma prestado un libro físico por un período.
6. **Copias de préstamo** (`BorrowingCopy`) — ejemplares físicos de un libro que se pueden prestar. Tienen estado: AVAILABLE, RESERVED, NOT_AVAILABLE.
7. **Géneros** (`Genre`) — categorías de libros (Ficción, Ciencia, Historia...).
8. **Tipos de usuario** (`UserType`) — roles: CUSTOMER, LIBRARIAN, ADMIN.

### Relaciones clave

```
Book ──< Buy >── Order >── User
Book ──< BorrowingCopy >── Borrowing >── User
Book ──< Genre (many-to-many)
User ──< UserType (many-to-many)
```

## Entidades detalladas

### Book
| Campo | Tipo | Descripción |
|---|---|---|
| isbn | `String` (PK) | Identificador único del libro |
| title | `String` | Título |
| author | `String` | Autor |
| price | `int` | Precio en céntimos |
| stock | `int` | Unidades disponibles |
| genreo | `Set<Genre>` | Géneros del libro (M-M) |
| buyList | `List<Buy>` | Compras que incluyen este libro |
| copies | `List<BorrowingCopy>` | Ejemplares físicos para préstamo |

### User
| Campo | Tipo | Descripción |
|---|---|---|
| id | `Long` (PK) | Identificador único |
| email | `String` (unique) | Email de login |
| name | `String` | Nombre completo |
| password | `String` | Contraseña (sin hash) |
| phone, address, city, state, country, postalCode | `String` | Datos de contacto |
| orders | `List<Order>` | Órdenes del usuario |
| borrowings | `List<Borrowing>` | Préstamos del usuario |
| userTypes | `Set<UserType>` | Roles del usuario (M-M) |

### Order
| Campo | Tipo | Descripción |
|---|---|---|
| id | `Long` (PK) | Identificador |
| user | `User` (M-1) | Usuario que hizo la orden |
| orderDate | `Date` | Fecha de la orden |
| state | `OrderState` (enum) | PENDING, SENDED, IN_DISTRIBUTION, DELIVERED |
| buys | `List<Buy>` | Libros comprados en esta orden |

### Buy
| Campo | Tipo | Descripción |
|---|---|---|
| id | `Long` (PK) | Identificador |
| book | `Book` (M-1) | Libro comprado |
| order | `Order` (M-1) | Orden a la que pertenece |
| quantity | `int` | Cantidad |
| price | `int` | Precio unitario |
| discount | `int` | Descuento unitario |
| *getTotalPrice()* | — | `price * quantity - discount * quantity` |

### Borrowing
| Campo | Tipo | Descripción |
|---|---|---|
| id | `Long` (PK) | Identificador |
| user | `User` (M-1) | Usuario que toma prestado |
| copy | `BorrowingCopy` (1-1) | Ejemplar prestado |
| borrowingDate | `LocalDate` | Fecha de inicio |
| returnDate | `LocalDate` | Fecha de devolución |

### BorrowingCopy
| Campo | Tipo | Descripción |
|---|---|---|
| id | `Long` (PK) | Identificador |
| book | `Book` (M-1) | Libro al que pertenece |
| avlbl | `Availability` (enum) | AVAILABLE, RESERVED, NOT_AVAILABLE |

### Genre
| Campo | Tipo | Descripción |
|---|---|---|
| id | `Long` (PK) | Identificador |
| name | `String` (unique) | Nombre del género |
| books | `Set<Book>` | Libros de este género (M-M) |

### UserType
| Campo | Tipo | Descripción |
|---|---|---|
| id | `Long` (PK) | Identificador |
| type | `String` (unique) | CUSTOMER / LIBRARIAN / ADMIN |
| users | `Set<User>` | Usuarios con este rol (M-M) |

## Reglas de negocio principales

- Un usuario no puede tener más de un préstamo activo a la vez.
- Un usuario no puede pedir prestado si tiene un préstamo vencido.
- Una orden solo se puede modificar si está en estado PENDING.
- No se puede comprar sin stock suficiente.
- Los préstamos con fecha futura se tratan como reservas.

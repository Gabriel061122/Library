# Technical Architecture

## Resumen simple

La app tiene **dos partes** que se comunican entre sí:

```
Navegador (React)  ←→  Servidor (Spring Boot)  ←→  Base de datos (MySQL)
```

El frontend (React) muestra las pantallas. El backend (Spring Boot) procesa la lógica y guarda/lee datos. Usan JSON para hablar entre sí.

## Estructura del proyecto

```
Library/
├── src/main/java/com/libreria/   ← Código Java del backend
│   ├── LibreriaApplication.java  ← Punto de entrada
│   ├── api/                      ← Controladores (endpoints REST)
│   ├── service/                  ← Lógica de negocio
│   ├── model/                    ← Entidades y repositorios
│   ├── security/                 ← JWT y configuración de seguridad
│   └── exceptions/               ← Excepciones personalizadas
├── src/main/resources/
│   └── application.properties    ← Configuración (DB, JWT, puerto)
├── frontend/                     ← Código del frontend React
│   └── src/
│       ├── pages/                ← Pantallas de la app
│       ├── components/           ← Componentes reutilizables
│       ├── context/              ← Estado global (AuthContext)
│       └── api/                  ← Conexión con el backend
├── pom.xml                       ← Dependencias Java (Maven)
└── test-data.sql                 ← Datos de prueba
```

## Backend: Capas

Cada petición sigue este flujo:

```
Cliente → Controlador (api/) → Servicio (service/) → Repositorio (model/repositories/) → BD
```

| Capa | Rol | Ejemplo |
|---|---|---|
| **Controller** | Recibe la petición HTTP, valida datos básicos y delega | `BookController.java` |
| **Service** | Contiene la lógica de negocio | `BookService.java` |
| **Repository** | Habla con la base de datos (Spring Data JPA) | `BookRepository.java` |
| **Entity** | Representa una tabla de la BD en código | `Book.java` |

## Frontend: Flujo de navegación

```
App.jsx
├── "/" o "/books"        → Books.jsx (catálogo público)
├── "/login"              → Login.jsx
├── "/register"           → Register.jsx
├── "/orders"             → Orders.jsx (requiere login)
├── "/borrowings"         → Borrowings.jsx (requiere login)
└── "/admin"              → Admin.jsx (requiere admin)
```

- `PrivateRoute`: redirige a `/login` si no hay sesión.
- `AdminRoute`: redirige a `/` si no es administrador.

## Tecnologías clave

| Tecnología | Versión | Para qué sirve |
|---|---|---|
| Java 21 | 21 | Lenguaje del backend |
| Spring Boot | 4.0.5 | Framework web (REST, JPA, Security) |
| Spring Data JPA | — | Mapeo objeto-relacional (ORM) |
| Spring Security | — | Autenticación y autorización |
| JWT (jjwt) | 0.12.6 | Tokens de sesión |
| MySQL Connector | — | Driver de base de datos |
| Lombok | — | Menos código boilerplate |
| React | 19 | Biblioteca de UI del frontend |
| Vite | 8 | Bundler / dev server |
| React Router | 7.16 | Enrutamiento del frontend |
| Axios | 1.17 | Llamadas HTTP al backend |

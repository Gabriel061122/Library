# Frontend Structure

## Resumen simple

El frontend es una **Single Page Application (SPA)** con React. Solo hay una página HTML; React cambia el contenido según la ruta sin recargar el navegador.

## Árbol de componentes

```
main.jsx
└── App.jsx
    └── AuthProvider (context/AuthContext)
        ├── Navbar
        └── Routes
            ├── / → Books
            ├── /books → Books
            ├── /login → Login
            ├── /register → Register
            ├── /orders → PrivateRoute → Orders
            ├── /borrowings → PrivateRoute → Borrowings
            └── /admin → AdminRoute → Admin
```

## Cada archivo

| Archivo | Propósito |
|---|---|
| `main.jsx` | Punto de entrada, renderiza `<App />` |
| `App.jsx` | Define rutas y layout global |
| `api/api.js` | Cliente Axios con todas las llamadas al backend. Adjunta automáticamente el token JWT |
| `context/AuthContext.jsx` | Estado global de autenticación: login, logout, isAdmin |

### Páginas

| Archivo | Función |
|---|---|
| `Books.jsx` | Catálogo con filtros (título, autor, ordenamiento). Modal para pedir prestado o comprar |
| `Login.jsx` | Formulario de inicio de sesión |
| `Register.jsx` | Formulario de registro |
| `Orders.jsx` | Lista de órdenes del usuario con detalle de items |
| `Borrowings.jsx` | Lista de préstamos del usuario, marca los vencidos |
| `Admin.jsx` | Panel de administración: CRUD de libros, gestión de copias, ver tipos de usuario |

### Componentes

| Archivo | Función |
|---|---|
| `Navbar.jsx` | Barra de navegación superior. Muestra enlaces según si hay sesión y rol |
| `PrivateRoute.jsx` | Redirige a `/login` si no hay sesión |
| `AdminRoute.jsx` | Redirige a `/` si no es admin |

## Comunicación con el backend

`api/api.js` crea una instancia de Axios con:

- **Interceptor de request**: agrega `Authorization: Bearer <token>` desde localStorage.
- Funciones por cada endpoint (ej: `getBooks()`, `createOrder(data)`, `login(email, password)`).

El `Vite` proxy redirige las rutas `/auth`, `/users`, `/books`, `/orders`, `/buys`, `/borrowings`, `/borrowing-copies`, `/user-types` al backend en `localhost:8080`.

## Variables de entorno

- `VITE_API_URL` — opcional, URL base de la API (por defecto usa el proxy de Vite).

# Security

## Resumen simple

La app usa **tokens JWT** para mantener la sesión. Cuando te logueas, el backend te da un token. El frontend lo guarda en `localStorage` y lo envía en cada petición para demostrar quién eres.

## Flujo de autenticación

```
Login:  Cliente → POST /auth/login → Servidor verifica credenciales → Devuelve JWT
Request: Cliente → GET /orders (con header "Authorization: Bearer <jwt>") → Servidor valida JWT → Responde
```

## Componentes de seguridad

### SecurityConfig.java
Configura Spring Security:
- Desactiva CSRF (API REST, no usa cookies de sesión).
- Sesión STATELESS (no se guarda estado de sesión en el servidor).
- **Rutas públicas:** `POST /auth/login`, `POST /auth/register`, `GET /{anything}`.
- **Rutas protegidas:** todo lo demás requiere token JWT.
- Agrega `JwtFilter` antes de `UsernamePasswordAuthenticationFilter`.

### JwtFilter.java
Filtro que se ejecuta en cada petición:
1. Extrae el header `Authorization`.
2. Si empieza con `Bearer `, extrae el token.
3. Valida el token con `JwtUtil`.
4. Si es válido, establece la autenticación en el contexto de Spring Security.
5. Si no hay token o es inválido, deja pasar (las rutas GET son públicas, el resto dará 403).

### JwtUtil.java
Utilidades JWT:
- `generateToken(username)`: crea un token con el email como subject, fecha de emisión y expiración.
- `extractUserName(token)`: extrae el email del token.
- Usa HMAC-SHA256 con la clave secreta de `application.properties`.

## Configuración (`application.properties`)

```properties
jwt.secret=javaisaverygreatlanguaje430896@fg$%NtksP@
jwt.expiration=86400000
```

- `jwt.secret`: clave secreta para firmar/verificar tokens.
- `jwt.expiration`: tiempo de vida del token en milisegundos (86.400.000 = 24 horas).

## Cómo se usa en el frontend

1. **Login:** `AuthContext.login()` llama a `POST /auth/login`, recibe el token, lo guarda en `localStorage` y decodifica el payload para obtener el email del usuario, luego busca el usuario en `GET /users`.
2. **Peticiones:** `api.js` tiene un interceptor que agrega `Authorization: Bearer <token>` a cada request.
3. **Logout:** se elimina el token y el usuario de `localStorage`.
4. **Protección de rutas:** `PrivateRoute` y `AdminRoute` verifican si hay usuario en el contexto antes de mostrar el contenido.

## Roles de usuario (`UserType`)

| Tipo | Descripción |
|---|---|
| CUSTOMER (id: 1) | Usuario normal, puede comprar y pedir prestado |
| LIBRARIAN (id: 2) | Puede gestionar préstamos |
| ADMIN (id: 3) | Acceso al panel de administración |

Los roles se asignan como `Set<UserType>` en la entidad `User`. El frontend verifica si el usuario es admin mediante `user.userTypes.some(t => t.type === 'ADMIN')`.

**Nota:** Actualmente el backend no verifica roles en los endpoints (solo verifica que haya un token válido). El frontend usa `AdminRoute` para restringir la ruta `/admin` a nivel de UI.

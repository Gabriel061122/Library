# Library

Sistema de gestión bibliotecaria full-stack. Catálogo de libros, órdenes de compra, préstamos y administración de usuarios.

## Stack

- **Backend:** Java 21 + Spring Boot 4.0.5 + Maven
- **Frontend:** React 19 + Vite 8 + React Router 7
- **Base de datos:** MySQL 8
- **Auth:** JWT (jjwt 0.12.6)

## Prerrequisitos

- Java 21+
- Node.js 20+
- MySQL 8+
- Maven (incluido como wrapper: `./mvnw`)

## Configuración

### 1. Base de datos

```sql
CREATE DATABASE IF NOT EXISTS library;
```

Ajusta las credenciales en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library
spring.datasource.username=root
spring.datasource.password=1234
```

### 2. Backend

```bash
# Iniciar servidor (puerto 8080)
./mvnw spring-boot:run
```

La primera vez crea las tablas automáticamente (`ddl-auto=update`).

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Servidor de desarrollo en `http://localhost:3000`. Las peticiones se redirigen al backend en `http://localhost:8080`.

### 4. Datos de prueba (opcional)

Ejecuta `test-data.sql` contra la base de datos `library` para cargar datos iniciales (libros, usuarios, tipos de usuario, etc.).

## Uso rápido

| URL                     | Descripción             |
| ----------------------- | ----------------------- |
| `http://localhost:3000` | Catálogo de libros      |
| `http://localhost:3000/login` | Iniciar sesión    |
| `http://localhost:3000/register` | Registrarse    |
| `http://localhost:3000/admin` | Panel de admin    |

Usuarios de prueba (tras cargar `test-data.sql`):

| Email | Contraseña | Rol |
|---|---|---|
| `ana@example.com` | `password-claro-para-pruebas` | CUSTOMER |
| `bibliotecario@example.com` | `password-claro-para-pruebas` | CUSTOMER + LIBRARIAN |

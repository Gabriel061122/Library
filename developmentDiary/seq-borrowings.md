---
title: Diagramas de Secuencia — Préstamos (/borrowings)
---


## GET /borrowings — Listar todos los préstamos

```mermaid
sequenceDiagram
    participant Cliente
    participant BorrowingController
    participant BorrowingService
    participant BorrowingRepository
    participant DB as Base de Datos

    Cliente->>+BorrowingController: GET /borrowings
    BorrowingController->>+BorrowingService: getBorrowings()
    BorrowingService->>+BorrowingRepository: findAll()
    BorrowingRepository->>+DB: SELECT * FROM borrowing
    DB-->>-BorrowingRepository: List<Borrowing>
    BorrowingRepository-->>-BorrowingService: List<Borrowing>
    BorrowingService-->>-BorrowingController: List<Borrowing>
    BorrowingController-->>Cliente: 200 OK [ { id, borrowingDate, returnDate, ... } ]

```
## GET /borrowings/{id} — Obtener préstamo por ID

```mermaid
sequenceDiagram
    participant Cliente
    participant BorrowingController
    participant BorrowingService
    participant BorrowingRepository
    participant DB as Base de Datos

    Cliente->>+BorrowingController: GET /borrowings/{id}
    BorrowingController->>+BorrowingService: getBorrowing(id)
    BorrowingService->>+BorrowingRepository: findById(id)
    BorrowingRepository->>+DB: SELECT * FROM borrowing WHERE id = ?
    DB-->>-BorrowingRepository: Optional<Borrowing>
    BorrowingRepository-->>-BorrowingService: Optional<Borrowing>
    BorrowingService-->>-BorrowingController: Optional<Borrowing>

    alt Préstamo existe
        BorrowingController-->>Cliente: 200 OK { id, borrowingDate, returnDate, copy, user }
    else No encontrado
        BorrowingController-->>Cliente: 404 Not Found
    end

```
## POST /borrowings — Crear préstamo (autenticado, resuelve usuario + copia)

```mermaid
sequenceDiagram
    participant Cliente
    participant JwtFilter
    participant BorrowingController
    participant UserService
    participant UserRepository
    participant BorrowingCopyService
    participant BorrowingCopyRepository
    participant BorrowingService
    participant BorrowingRepository
    participant DB as Base de Datos

    Cliente->>+JwtFilter: POST /borrowings { copy: { id }, borrowingDate, returnDate }

    alt Token inválido
        JwtFilter-->>Cliente: 401 Unauthorized
    else Token válido
        JwtFilter->>+BorrowingController: reenvía con Authentication (email)
        BorrowingController->>BorrowingController: auth.getName() → email
        BorrowingController->>+UserService: getUserByEmail(email)
        UserService->>+UserRepository: findByEmail(email)
        UserRepository->>+DB: SELECT * FROM users WHERE email = ?
        DB-->>-UserRepository: Optional<User>
        UserRepository-->>-UserService: Optional<User>
        UserService-->>-BorrowingController: User
        BorrowingController->>BorrowingController: borrowing.setUser(user)

        opt copy.id presente
            BorrowingController->>+BorrowingCopyService: getBorrowingCopy(copyId)
            BorrowingCopyService->>+BorrowingCopyRepository: findById(copyId)
            BorrowingCopyRepository->>+DB: SELECT * FROM borrowing_copy WHERE id = ?
            DB-->>-BorrowingCopyRepository: Optional<BorrowingCopy>
            BorrowingCopyRepository-->>-BorrowingCopyService: Optional<BorrowingCopy>

            alt Copia existe
                BorrowingCopyService-->>-BorrowingController: BorrowingCopy
                BorrowingController->>BorrowingController: borrowing.setCopy(copy)
            else Copia no existe
                BorrowingCopyService-->>-BorrowingController: null
                BorrowingController->>BorrowingController: copy = null
            end
        end

        BorrowingController->>+BorrowingService: addBorrowing(borrowing)
        BorrowingService->>+BorrowingRepository: save(borrowing)
        BorrowingRepository->>+DB: INSERT INTO borrowing
        DB-->>-BorrowingRepository: Borrowing (persistido)
        BorrowingRepository-->>-BorrowingService: Borrowing
        BorrowingService-->>-BorrowingController: Borrowing
        BorrowingController-->>Cliente: 200 OK { id, borrowingDate, returnDate, copy, user }
    end

```
## PUT /borrowings/{id} — Actualizar préstamo (autenticado, reemplazo completo)

```mermaid
sequenceDiagram
    participant Cliente
    participant JwtFilter
    participant BorrowingController
    participant BorrowingService
    participant BorrowingRepository
    participant DB as Base de Datos

    Cliente->>+JwtFilter: PUT /borrowings/{id} { borrowingDate, returnDate, copy, ... }

    alt Token inválido
        JwtFilter-->>Cliente: 401 Unauthorized
    else Token válido
        JwtFilter->>BorrowingController: reenvía
        BorrowingController->>+BorrowingService: updateBorrowing(id, borrowing)
        BorrowingService->>+BorrowingRepository: findById(id)
        BorrowingRepository->>+DB: SELECT * FROM borrowing WHERE id = ?
        DB-->>-BorrowingRepository: Optional<Borrowing>
        BorrowingRepository-->>-BorrowingService: Optional<Borrowing>

        alt Préstamo no existe
            BorrowingService-->>BorrowingController: Optional.empty()
            BorrowingController-->>Cliente: 404 Not Found
        else Préstamo existe
            BorrowingService->>BorrowingService: borrowing.setId(id)
            BorrowingService->>+BorrowingRepository: save(borrowing)
            BorrowingRepository->>+DB: UPDATE borrowing SET ... WHERE id = ?
            DB-->>-BorrowingRepository: Borrowing (actualizado)
            BorrowingRepository-->>-BorrowingService: Borrowing
            BorrowingService-->>BorrowingController: Optional.of(updated)
            BorrowingController-->>Cliente: 200 OK { id, borrowingDate, returnDate, ... }
        end
    end

```
## DELETE /borrowings/{id} — Eliminar préstamo (autenticado)

```mermaid
sequenceDiagram
    participant Cliente
    participant JwtFilter
    participant BorrowingController
    participant BorrowingService
    participant BorrowingRepository
    participant DB as Base de Datos

    Cliente->>+JwtFilter: DELETE /borrowings/{id}

    alt Token inválido
        JwtFilter-->>Cliente: 401 Unauthorized
    else Token válido
        JwtFilter->>BorrowingController: reenvía
        BorrowingController->>+BorrowingService: deleteBorrowing(id)
        BorrowingService->>+BorrowingRepository: existsById(id)
        BorrowingRepository->>+DB: SELECT COUNT(*) FROM borrowing WHERE id = ?
        DB-->>-BorrowingRepository: true/false
        BorrowingRepository-->>-BorrowingService: boolean

        alt Préstamo no existe
            BorrowingService-->>BorrowingController: false
            BorrowingController-->>Cliente: 404 Not Found
        else Préstamo existe
            BorrowingService->>+BorrowingRepository: deleteById(id)
            BorrowingRepository->>+DB: DELETE FROM borrowing WHERE id = ?
            DB-->>-BorrowingRepository: OK
            BorrowingRepository-->>-BorrowingService: void
            BorrowingService-->>BorrowingController: true
            BorrowingController-->>Cliente: 204 No Content
        end
    end
```

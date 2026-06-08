---
title: Diagramas de Secuencia — Copias de Préstamo (/borrowing-copies)
---

## GET /borrowing-copies — Listar todas las copias

```mermaid
sequenceDiagram
    participant Cliente
    participant BCCtrl as BorrowingCopyController
    participant BCService as BorrowingCopyService
    participant BCRepo as BorrowingCopyRepository
    participant DB as Base de Datos

    Cliente->>+BCCtrl: GET /borrowing-copies
    BCCtrl->>+BCService: getBorrowingCopies()
    BCService->>+BCRepo: findAll()
    BCRepo->>+DB: SELECT * FROM borrowing_copy
    DB-->>-BCRepo: List<BorrowingCopy>
    BCRepo-->>-BCService: List<BorrowingCopy>
    BCService-->>-BCCtrl: List<BorrowingCopy>
    BCCtrl-->>Cliente: 200 OK [ { id, avlbl, book, ... } ]

```
## GET /borrowing-copies/{id} — Obtener copia por ID

```mermaid
sequenceDiagram
    participant Cliente
    participant BCCtrl as BorrowingCopyController
    participant BCService as BorrowingCopyService
    participant BCRepo as BorrowingCopyRepository
    participant DB as Base de Datos

    Cliente->>+BCCtrl: GET /borrowing-copies/{id}
    BCCtrl->>+BCService: getBorrowingCopy(id)
    BCService->>+BCRepo: findById(id)
    BCRepo->>+DB: SELECT * FROM borrowing_copy WHERE id = ?
    DB-->>-BCRepo: Optional<BorrowingCopy>
    BCRepo-->>-BCService: Optional<BorrowingCopy>
    BCService-->>-BCCtrl: Optional<BorrowingCopy>

    alt Copia existe
        BCCtrl-->>Cliente: 200 OK { id, avlbl, book }
    else No encontrada
        BCCtrl-->>Cliente: 404 Not Found
    end

```
## POST /borrowing-copies — Crear copia (autenticado)

```mermaid
sequenceDiagram
    participant Cliente
    participant JwtFilter
    participant BCCtrl as BorrowingCopyController
    participant BCService as BorrowingCopyService
    participant BCRepo as BorrowingCopyRepository
    participant DB as Base de Datos

    Cliente->>+JwtFilter: POST /borrowing-copies { book: { isbn }, avlbl }

    alt Token inválido
        JwtFilter-->>Cliente: 401 Unauthorized
    else Token válido
        JwtFilter->>BCCtrl: reenvía
        BCCtrl->>+BCService: addBorrowingCopy(borrowingCopy)
        BCService->>+BCRepo: save(borrowingCopy)
        BCRepo->>+DB: INSERT INTO borrowing_copy (book_id, avlbl)
        DB-->>-BCRepo: BorrowingCopy (persistido)
        BCRepo-->>-BCService: BorrowingCopy
        BCService-->>-BCCtrl: BorrowingCopy
        BCCtrl-->>Cliente: 200 OK { id, avlbl, book }
    end

```
## POST /borrowing-copies/books/{isbn} — Crear copia para libro (autenticado, AVALIABLE por defecto)

```mermaid
sequenceDiagram
    participant Cliente
    participant JwtFilter
    participant BCCtrl as BorrowingCopyController
    participant BCService as BorrowingCopyService
    participant BookRepo as BookRepository
    participant BCRepo as BorrowingCopyRepository
    participant DB as Base de Datos

    Cliente->>+JwtFilter: POST /borrowing-copies/books/{isbn}

    alt Token inválido
        JwtFilter-->>Cliente: 401 Unauthorized
    else Token válido
        JwtFilter->>+BCCtrl: reenvía
        BCCtrl->>+BCService: addBorrowingCopyOfBook(isbn)
        BCService->>+BookRepo: findById(isbn)
        BookRepo->>+DB: SELECT * FROM book WHERE isbn = ?
        DB-->>-BookRepo: Optional<Book>

        alt Libro no existe
            BookRepo-->>-BCService: Optional.empty()
            BCService-->>BCCtrl: Optional.empty()
            BCCtrl-->>Cliente: 404 Not Found
        else Libro existe
            BookRepo-->>-BCService: Book
            BCService->>BCService: new BorrowingCopy(book, AVALIABLE)
            BCService->>+BCRepo: save(borrowingCopy)
            BCRepo->>+DB: INSERT INTO borrowing_copy (book_id, avlbl)
            DB-->>-BCRepo: BorrowingCopy
            BCRepo-->>-BCService: BorrowingCopy
            BCService-->>BCCtrl: Optional.of(newCopy)
            BCCtrl-->>Cliente: 200 OK { id, avlbl: "AVALIABLE", book }
        end
    end

```
## PUT /borrowing-copies/{id} — Actualizar copia (autenticado, reemplazo completo)

```mermaid
sequenceDiagram
    participant Cliente
    participant JwtFilter
    participant BCCtrl as BorrowingCopyController
    participant BCService as BorrowingCopyService
    participant BCRepo as BorrowingCopyRepository
    participant DB as Base de Datos

    Cliente->>+JwtFilter: PUT /borrowing-copies/{id} { book, avlbl, ... }

    alt Token inválido
        JwtFilter-->>Cliente: 401 Unauthorized
    else Token válido
        JwtFilter->>BCCtrl: reenvía
        BCCtrl->>+BCService: updateBorrowingCopy(id, borrowingCopy)
        BCService->>+BCRepo: findById(id)
        BCRepo->>+DB: SELECT * FROM borrowing_copy WHERE id = ?
        DB-->>-BCRepo: Optional<BorrowingCopy>
        BCRepo-->>-BCService: Optional<BorrowingCopy>

        alt Copia no existe
            BCService-->>BCCtrl: Optional.empty()
            BCCtrl-->>Cliente: 404 Not Found
        else Copia existe
            BCService->>BCService: borrowingCopy.setId(id)
            BCService->>+BCRepo: save(borrowingCopy)
            BCRepo->>+DB: UPDATE borrowing_copy SET ... WHERE id = ?
            DB-->>-BCRepo: BorrowingCopy (actualizado)
            BCRepo-->>-BCService: BorrowingCopy
            BCService-->>BCCtrl: Optional.of(updated)
            BCCtrl-->>Cliente: 200 OK { id, avlbl, book }
        end
    end

```
## DELETE /borrowing-copies/{id} — Eliminar copia por ID (autenticado)

```mermaid
sequenceDiagram
    participant Cliente
    participant JwtFilter
    participant BCCtrl as BorrowingCopyController
    participant BCService as BorrowingCopyService
    participant BCRepo as BorrowingCopyRepository
    participant DB as Base de Datos

    Cliente->>+JwtFilter: DELETE /borrowing-copies/{id}

    alt Token inválido
        JwtFilter-->>Cliente: 401 Unauthorized
    else Token válido
        JwtFilter->>BCCtrl: reenvía
        BCCtrl->>+BCService: deleteBorrowingCopy(id)
        BCService->>+BCRepo: existsById(id)
        BCRepo->>+DB: SELECT COUNT(*) FROM borrowing_copy WHERE id = ?
        DB-->>-BCRepo: true/false
        BCRepo-->>-BCService: boolean

        alt Copia no existe
            BCService-->>BCCtrl: false
            BCCtrl-->>Cliente: 404 Not Found
        else Copia existe
            BCService->>+BCRepo: deleteById(id)
            BCRepo->>+DB: DELETE FROM borrowing_copy WHERE id = ?
            DB-->>-BCRepo: OK
            BCRepo-->>-BCService: void
            BCService-->>BCCtrl: true
            BCCtrl-->>Cliente: 204 No Content
        end
    end

```
## DELETE /borrowing-copies/books/{isbn}/last — Eliminar última copia de un libro (autenticado)

```mermaid
sequenceDiagram
    participant Cliente
    participant JwtFilter
    participant BCCtrl as BorrowingCopyController
    participant BCService as BorrowingCopyService
    participant BCRepo as BorrowingCopyRepository
    participant DB as Base de Datos

    Cliente->>+JwtFilter: DELETE /borrowing-copies/books/{isbn}/last

    alt Token inválido
        JwtFilter-->>Cliente: 401 Unauthorized
    else Token válido
        JwtFilter->>+BCCtrl: reenvía
        BCCtrl->>+BCService: deleteLastBorrowingCopyOfBook(isbn)
        BCService->>+BCRepo: findByBookIsbn(isbn)
        BCRepo->>+DB: SELECT * FROM borrowing_copy WHERE book_isbn = ?
        DB-->>-BCRepo: List<BorrowingCopy>
        BCRepo-->>-BCService: List<BorrowingCopy>

        alt Lista vacía (sin copias)
            BCService-->>BCCtrl: false
            BCCtrl-->>Cliente: 404 Not Found
        else Hay copias
            BCService->>BCService: lastCopy = copies.getLast()
            BCService->>+BCRepo: deleteById(lastCopy.getId())
            BCRepo->>+DB: DELETE FROM borrowing_copy WHERE id = ?
            DB-->>-BCRepo: OK
            BCRepo-->>-BCService: void
            BCService-->>BCCtrl: true
            BCCtrl-->>Cliente: 204 No Content
        end
    end
```

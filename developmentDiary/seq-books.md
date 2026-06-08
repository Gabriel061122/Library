---
title: Diagramas de Secuencia — Libros (/books)
---

## GET /books — Listar todos los libros

```mermaid
sequenceDiagram
    participant Cliente
    participant BookController
    participant BookService
    participant BookRepository
    participant DB as Base de Datos

    Cliente->>+BookController: GET /books
    BookController->>+BookService: getBooks()
    BookService->>+BookRepository: findAll()
    BookRepository->>+DB: SELECT * FROM book
    DB-->>-BookRepository: List<Book>
    BookRepository-->>-BookService: List<Book>
    BookService-->>-BookController: List<Book>
    BookController-->>Cliente: 200 OK { List<Book> }
```

## GET /books/{isbn} — Buscar libro por ISBN

```mermaid
sequenceDiagram
    participant Cliente
    participant BookController
    participant BookService
    participant BookRepository
    participant DB as Base de Datos

    Cliente->>+BookController: GET /books/{isbn}
    BookController->>+BookService: getBook(isbn)
    BookService->>+BookRepository: findById(isbn)
    BookRepository->>+DB: SELECT * FROM book WHERE isbn = ?
    DB-->>-BookRepository: Optional<Book>
    BookRepository-->>-BookService: Optional<Book>
    BookService-->>-BookController: Optional<Book>

    alt Libro encontrado
        BookController-->>Cliente: 200 OK { Book }
    else Libro no encontrado
        BookController-->>Cliente: 404 Not Found
    end
```

## POST /books — Agregar nuevo libro

```mermaid
sequenceDiagram
    participant Cliente
    participant BookController
    participant BookService
    participant BookRepository
    participant DB as Base de Datos

    Cliente->>+BookController: POST /books { Book }
    BookController->>+BookService: addBook(book)
    BookService->>+BookRepository: save(book)
    BookRepository->>+DB: INSERT INTO book
    DB-->>-BookRepository: Book (con isbn asignado)
    BookRepository-->>-BookService: Book
    BookService-->>-BookController: Book
    BookController-->>Cliente: 200 OK { Book }
```

## PUT /books/{isbn} — Actualizar libro

```mermaid
sequenceDiagram
    participant Cliente
    participant BookController
    participant BookService
    participant BookRepository
    participant DB as Base de Datos

    Cliente->>+BookController: PUT /books/{isbn} { Book }
    BookController->>+BookService: updateBook(isbn, book)
    BookService->>+BookRepository: findById(isbn)
    BookRepository->>+DB: SELECT * FROM book WHERE isbn = ?
    DB-->>-BookRepository: Optional<Book>

    alt Libro encontrado
        BookService->>BookService: actualizar datos
        BookService->>+BookRepository: save(book)
        BookRepository->>+DB: UPDATE book SET ... WHERE isbn = ?
        DB-->>-BookRepository: Book actualizado
        BookRepository-->>-BookService: Book
        BookService-->>-BookController: Optional<Book>
        BookController-->>Cliente: 200 OK { Book }
    else Libro no encontrado
        BookService-->>-BookController: Optional.empty()
        BookController-->>Cliente: 404 Not Found
    end
```

## DELETE /books/{isbn} — Eliminar libro

```mermaid
sequenceDiagram
    participant Cliente
    participant BookController
    participant BookService
    participant BookRepository
    participant DB as Base de Datos

    Cliente->>+BookController: DELETE /books/{isbn}
    BookController->>+BookService: deleteBook(isbn)
    BookService->>+BookRepository: findById(isbn)
    BookRepository->>+DB: SELECT * FROM book WHERE isbn = ?
    DB-->>-BookRepository: Optional<Book>

    alt Libro encontrado
        BookService->>+BookRepository: delete(book)
        BookRepository->>+DB: DELETE FROM book WHERE isbn = ?
        DB-->>-BookRepository: OK
        BookRepository-->>-BookService: void
        BookService-->>-BookController: true
        BookController-->>Cliente: 204 No Content
    else Libro no encontrado
        BookService-->>-BookController: false
        BookController-->>Cliente: 404 Not Found
    end
```

## GET /books/filter — Filtrar libros

```mermaid
sequenceDiagram
    participant Cliente
    participant BookController
    participant BookService
    participant BookRepository
    participant DB as Base de Datos

    Cliente->>+BookController: GET /books/filter?title=&genre=&author=&sortBy=&order=
    BookController->>BookController: Sort.Direction y Sort.by(dir, sortBy)
    BookController->>+BookService: getBooksFilter(title, genre, author, sort)
    BookService->>+BookRepository: findAll(spec, sort)
    BookRepository->>+DB: SELECT * FROM book WHERE (title LIKE ?) AND (genre_id IN ?) AND (author LIKE ?) ORDER BY ? ? 
    DB-->>-BookRepository: List<Book>
    BookRepository-->>-BookService: List<Book>
    BookService-->>-BookController: List<Book>
    BookController-->>Cliente: 200 OK { List<Book> }
```

package com.libreria.api;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.libreria.model.book.Book;
import com.libreria.model.book.BorrowingCopy;
import com.libreria.model.book.Genre;
import com.libreria.service.BooksService;

@RequestMapping("/books")
@RestController
public class BooksApi {
    
    private BooksService booksService;

    public BooksApi(BooksService booksService){
        this.booksService = booksService;
    }


    // Esto hay que controlar mejor las listas vacías
    @GetMapping()
    public ResponseEntity<List<Book>> getBooks(){
        List<Book> result = booksService.getBooks();
        return result.isEmpty() ?  ResponseEntity.notFound().build() : ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable String id) {
        return booksService.getBook(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Book savedBook = booksService.addBook(book);
        return ResponseEntity.ok(savedBook);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable String id, @RequestBody Book book) {
        return booksService.updateBook(id, book)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        boolean deleted = booksService.deleteBook(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/borrowingCopies")
    public ResponseEntity<BorrowingCopy> addBorrowingCopyOfABook(@PathVariable String id){
        BorrowingCopy borrowingCopy = booksService.addBorrowingCopy(id);
        return ResponseEntity.ok(borrowingCopy);
    }

    @DeleteMapping("/{id}/borrowingCopies")
    public ResponseEntity<Boolean> deleteBorrowingCopyOfABokk(@PathVariable String id){

        boolean exists = booksService.deleteBorrowingCopy();

        if (exists) {
            return ResponseEntity.ok(exists);
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Book>> findWithFilter (@RequestParam(required = false) String title,
		    				      @RequestParam(required = false) Genre genre,
						      @RequestParam(required = false) String author){
        List<Book> result = booksService.getBooksFilter(title, genre, author);
        return result.isEmpty() ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

}

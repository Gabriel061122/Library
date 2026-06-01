package com.libreria.api;

import com.libreria.model.book.BorrowingCopy;
import com.libreria.model.repositories.BorrowingCopyRepository;
import com.libreria.service.BooksService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/borrowing-copies")
@RestController
public class BorrowingCopyController {

    private final BorrowingCopyRepository borrowingCopyRepository;
    private final BooksService booksService;

    public BorrowingCopyController(BorrowingCopyRepository borrowingCopyRepository, BooksService booksService) {
        this.borrowingCopyRepository = borrowingCopyRepository;
        this.booksService = booksService;
    }

    @GetMapping
    public ResponseEntity<List<BorrowingCopy>> getBorrowingCopies() {
        return ResponseEntity.ok(borrowingCopyRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowingCopy> getBorrowingCopy(@PathVariable Long id) {
        return borrowingCopyRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BorrowingCopy> addBorrowingCopy(@RequestBody BorrowingCopy borrowingCopy) {
        return ResponseEntity.ok(borrowingCopyRepository.save(borrowingCopy));
    }

    @PostMapping("/books/{isbn}")
    public ResponseEntity<BorrowingCopy> addBorrowingCopyOfBook(@PathVariable String isbn) {
        return ResponseEntity.ok(booksService.addBorrowingCopy(isbn));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BorrowingCopy> updateBorrowingCopy(
            @PathVariable Long id,
            @RequestBody BorrowingCopy borrowingCopy
    ) {
        return borrowingCopyRepository.findById(id)
                .map(existing -> {
                    borrowingCopy.setId(id);
                    return ResponseEntity.ok(borrowingCopyRepository.save(borrowingCopy));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBorrowingCopy(@PathVariable Long id) {
        if (!borrowingCopyRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        borrowingCopyRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/books/{isbn}/last")
    public ResponseEntity<Void> deleteLastBorrowingCopyOfBook(@PathVariable String isbn) {
        if (booksService.deleteBorrowingCopy(isbn)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

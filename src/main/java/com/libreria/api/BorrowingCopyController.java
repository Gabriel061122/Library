package com.libreria.api;

import com.libreria.model.book.BorrowingCopy;
import com.libreria.service.BorrowingCopyService;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
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

    private final BorrowingCopyService borrowingCopyService;

    public BorrowingCopyController(BorrowingCopyService borrowingCopyService) {
        this.borrowingCopyService = borrowingCopyService;
    }

    @GetMapping
    public ResponseEntity<List<BorrowingCopy>> getBorrowingCopies() {
        return ResponseEntity.ok(borrowingCopyService.getBorrowingCopies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowingCopy> getBorrowingCopy(@PathVariable Long id) {
        return borrowingCopyService.getBorrowingCopy(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BorrowingCopy> addBorrowingCopy(@RequestBody BorrowingCopy borrowingCopy) {
        return ResponseEntity.ok(borrowingCopyService.addBorrowingCopy(borrowingCopy));
    }

    @PostMapping("/books/{isbn}")
    public ResponseEntity<BorrowingCopy> addBorrowingCopyOfBook(@PathVariable String isbn) {
        return borrowingCopyService.addBorrowingCopyOfBook(isbn)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BorrowingCopy> updateBorrowingCopy(
            @PathVariable Long id,
            @RequestBody BorrowingCopy borrowingCopy
    ) {
        return borrowingCopyService.updateBorrowingCopy(id, borrowingCopy)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBorrowingCopy(@PathVariable Long id) {
        if (borrowingCopyService.deleteBorrowingCopy(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/books/{isbn}/last")
    public ResponseEntity<Void> deleteLastBorrowingCopyOfBook(@PathVariable String isbn) {
        if (borrowingCopyService.deleteLastBorrowingCopyOfBook(isbn)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

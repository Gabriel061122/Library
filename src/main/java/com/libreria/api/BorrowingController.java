package com.libreria.api;

import com.libreria.model.exchange.Borrowing;
import com.libreria.model.repositories.BorrowingRepository;
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

@RequestMapping("/borrowings")
@RestController
public class BorrowingController {

    private final BorrowingRepository borrowingRepository;

    public BorrowingController(BorrowingRepository borrowingRepository) {
        this.borrowingRepository = borrowingRepository;
    }

    @GetMapping
    public ResponseEntity<List<Borrowing>> getBorrowings() {
        return ResponseEntity.ok(borrowingRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Borrowing> getBorrowing(@PathVariable Long id) {
        return borrowingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Borrowing> addBorrowing(@RequestBody Borrowing borrowing) {
        return ResponseEntity.ok(borrowingRepository.save(borrowing));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Borrowing> updateBorrowing(@PathVariable Long id, @RequestBody Borrowing borrowing) {
        return borrowingRepository.findById(id)
                .map(existing -> {
                    borrowing.setId(id);
                    return ResponseEntity.ok(borrowingRepository.save(borrowing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBorrowing(@PathVariable Long id) {
        if (!borrowingRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        borrowingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

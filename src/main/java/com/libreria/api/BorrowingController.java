package com.libreria.api;

import com.libreria.model.exchange.Borrowing;
import com.libreria.service.BorrowingCopyService;
import com.libreria.service.BorrowingService;
import com.libreria.service.UserService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    private final BorrowingService borrowingService;
    private final UserService userService;
    private final BorrowingCopyService borrowingCopyService;

    public BorrowingController(BorrowingService borrowingService, UserService userService, BorrowingCopyService borrowingCopyService) {
        this.borrowingService = borrowingService;
        this.userService = userService;
        this.borrowingCopyService = borrowingCopyService;
    }

    @GetMapping
    public ResponseEntity<List<Borrowing>> getBorrowings() {
        return ResponseEntity.ok(borrowingService.getBorrowings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Borrowing> getBorrowing(@PathVariable Long id) {
        return borrowingService.getBorrowing(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Borrowing> addBorrowing(@RequestBody Borrowing borrowing, Authentication auth) {
        userService.getUserByEmail(auth.getName()).ifPresent(borrowing::setUser);
        if (borrowing.getCopy() != null && borrowing.getCopy().getId() != null) {
            borrowingCopyService.getBorrowingCopy(borrowing.getCopy().getId())
                .ifPresent(borrowing::setCopy);
        }
        return ResponseEntity.ok(borrowingService.addBorrowing(borrowing));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Borrowing> updateBorrowing(@PathVariable Long id, @RequestBody Borrowing borrowing) {
        return borrowingService.updateBorrowing(id, borrowing)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBorrowing(@PathVariable Long id) {
        if (borrowingService.deleteBorrowing(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

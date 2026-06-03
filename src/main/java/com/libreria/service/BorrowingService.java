package com.libreria.service;

import com.libreria.model.exchange.Borrowing;
import com.libreria.model.repositories.BorrowingRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;

    public BorrowingService(BorrowingRepository borrowingRepository) {
        this.borrowingRepository = borrowingRepository;
    }

    public List<Borrowing> getBorrowings() {
        return borrowingRepository.findAll();
    }

    public Optional<Borrowing> getBorrowing(Long id) {
        return borrowingRepository.findById(id);
    }

    public Borrowing addBorrowing(Borrowing borrowing) {
        return borrowingRepository.save(borrowing);
    }

    public Optional<Borrowing> updateBorrowing(Long id, Borrowing borrowing) {
        return borrowingRepository.findById(id).map(existing -> {
            borrowing.setId(id);
            return borrowingRepository.save(borrowing);
        });
    }

    public boolean deleteBorrowing(Long id) {
        if (!borrowingRepository.existsById(id)) {
            return false;
        }
        borrowingRepository.deleteById(id);
        return true;
    }
}

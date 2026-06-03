package com.libreria.service;

import com.libreria.model.book.Avaliavility;
import com.libreria.model.book.Book;
import com.libreria.model.book.BorrowingCopy;
import com.libreria.model.repositories.BookRepository;
import com.libreria.model.repositories.BorrowingCopyRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class BorrowingCopyService {

    private final BorrowingCopyRepository borrowingCopyRepository;
    private final BookRepository bookRepository;

    public BorrowingCopyService(BorrowingCopyRepository borrowingCopyRepository, BookRepository bookRepository) {
        this.borrowingCopyRepository = borrowingCopyRepository;
        this.bookRepository = bookRepository;
    }

    public List<BorrowingCopy> getBorrowingCopies() {
        return borrowingCopyRepository.findAll();
    }

    public Optional<BorrowingCopy> getBorrowingCopy(Long id) {
        return borrowingCopyRepository.findById(id);
    }

    public BorrowingCopy addBorrowingCopy(BorrowingCopy borrowingCopy) {
        return borrowingCopyRepository.save(borrowingCopy);
    }

    public Optional<BorrowingCopy> addBorrowingCopyOfBook(String isbn) {
        Optional<Book> book = bookRepository.findById(isbn);
        if (book.isEmpty()) {
            return Optional.empty();
        }
        BorrowingCopy borrowingCopy = new BorrowingCopy(book.get(), Avaliavility.AVALIABLE);
        return Optional.of(borrowingCopyRepository.save(borrowingCopy));
    }

    public Optional<BorrowingCopy> updateBorrowingCopy(Long id, BorrowingCopy borrowingCopy) {
        return borrowingCopyRepository.findById(id).map(existing -> {
            borrowingCopy.setId(id);
            return borrowingCopyRepository.save(borrowingCopy);
        });
    }

    public boolean deleteBorrowingCopy(Long id) {
        if (!borrowingCopyRepository.existsById(id)) {
            return false;
        }
        borrowingCopyRepository.deleteById(id);
        return true;
    }

    public boolean deleteLastBorrowingCopyOfBook(String isbn) {
        List<BorrowingCopy> copies = borrowingCopyRepository.findByBookIsbn(isbn);
        if (copies.isEmpty()) {
            return false;
        }
        borrowingCopyRepository.deleteById(copies.getLast().getId());
        return true;
    }
}

package com.libreria.service;

import com.libreria.model.book.Book;
import com.libreria.model.book.Genre;
import com.libreria.model.repositories.BookRepository;
import com.libreria.model.repositories.specification.BookSpecifications;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Optional<Book> getBook(String id) {
        return bookRepository.findById(id);
    }

    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    public boolean deleteBook(String id) {
        return bookRepository.findById(id)
                .map(book -> {
                    bookRepository.delete(book);
                    return true;
                })
                .orElse(false);
    }

    public Optional<Book> updateBook(String id, Book newBook) {
        return bookRepository.findById(id).map(book -> {
            newBook.setIsbn(id);
            book.updateBook(newBook);
            return bookRepository.save(book);
        });
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> getBooksFilter(String title, Genre genre, String author, Sort sort) {
        Specification<Book> spec = Specification.where(BookSpecifications.hasTitle(title))
                .and(BookSpecifications.hasGenre(genre))
                .and(BookSpecifications.hasAuthor(author));
        return bookRepository.findAll(spec, sort);
    }
}

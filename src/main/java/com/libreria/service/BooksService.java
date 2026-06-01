package com.libreria.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.libreria.model.book.Avaliavility;
import com.libreria.model.book.Book;
import com.libreria.model.book.BorrowingCopy;
import com.libreria.model.repositories.BookRepository;
import com.libreria.model.repositories.BorrowingCopyRepository;
import com.libreria.model.repositories.specification.BookSpecifications;
import com.libreria.model.book.Genre;

@Service
public class BooksService {
    
    private BookRepository bookRepository;
    private BorrowingCopyRepository borrowingCopyRepository;

    public BooksService(BookRepository bookRepository, BorrowingCopyRepository borrowingCopyRepository){
        this.bookRepository = bookRepository;
        this.borrowingCopyRepository = borrowingCopyRepository;
    }

    public Optional<Book> getBook(String id){
        Optional<Book> book = bookRepository.findById(id);
        return book;

    }

    public List<Book> getBooks(){
        return bookRepository.findAll();
    }

    public boolean deleteBook(String id){

        boolean isPresent = false;

        Optional<Book> book = bookRepository.findById(id);

        if (book.isPresent()){
            bookRepository.delete(book.get());
            isPresent = true;
        } 

        return isPresent;
    }

    public Optional<Book> updateBook(String id, Book newBook){
        return bookRepository.findById(id).map(book -> {
            newBook.setIsbn(id);
            book.updateBook(newBook);
            return bookRepository.save(book);
        });
    }

    public Book addBook(Book book){
        bookRepository.save(book);
        return book;
    }

    public BorrowingCopy addBorrowingCopy(String isbn){
        Optional<Book> book = bookRepository.findById(isbn);
        if (book.isPresent()){
            BorrowingCopy newBorrowingCopy = new BorrowingCopy(book.get(), Avaliavility.AVALIABLE);
            borrowingCopyRepository.save(newBorrowingCopy);
            return newBorrowingCopy;
        }
        throw new RuntimeException("Libro no encontrado");
    }

    public boolean deleteBorrowingCopy(String isbn){

        List<BorrowingCopy> list = borrowingCopyRepository.findByBookIsbn(isbn);

        if (list.isEmpty()) {
            return false;
        } 

        borrowingCopyRepository.deleteById(list.getLast().getId( ));
        return true;
    }

    public List<Book> getBooksFilter(String title, Genre genre, String author, Sort sort){
	    Specification<Book> spec = Specification.where(BookSpecifications.hasTitle(title))
						    .and(BookSpecifications.hasGenre(genre))
						    .and(BookSpecifications.hasAuthor(author));
	    return bookRepository.findAll(spec, sort);
    }

}

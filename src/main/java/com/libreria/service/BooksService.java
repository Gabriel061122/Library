package com.libreria.service;

import java.util.List;
import java.util.Optional;

import com.libreria.model.book.Book;
import com.libreria.model.repositories.BookRepository;

public class BooksService {
    
    private BookRepository bookRepository;

    public BooksService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    public Optional<Book> getBook(Long id){
        Optional<Book> book = bookRepository.findById(id);
        return book;

    }

    public List<Book> getBooks(){
        return bookRepository.findAll();
    }

    public boolean deleteBook(Long id){

        boolean isPresent = false;

        Optional<Book> book = bookRepository.findById(id);

        if (book.isPresent()){
            bookRepository.delete(book.get());
            isPresent = true;
        } 

        return isPresent;
    }

    public Optional<Book> updateBook(Long id, Book newBook){
        return bookRepository.findById(id).map(book -> {return book.updateBook(newBook);});
    }

    public Book addBook(Book book){
        bookRepository.save(book);
        return book;
    }

}

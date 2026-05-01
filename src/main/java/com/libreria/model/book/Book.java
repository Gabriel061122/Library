package com.libreria.model.book;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;
import java.util.Objects;

import org.springframework.web.bind.annotation.Mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Book {
    @Id
    private String isbn;
    private String title;
    private String author;
    private int price;
    private int stock;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<BorrowingCopy> copies;

    public Book(
            String title,
            String author,
            String isbn,
            int price,
            int stock
    ) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.price = price;
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Book{" +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        if (o instanceof Book) {
            isEqual = this.isbn.equals(((Book) o).isbn);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    public Book updateBook(Book newBook){ 
        this.author = newBook.getAuthor();
        this.isbn = newBook.getIsbn();
        this.price = newBook.getPrice();
        this.stock = newBook.getStock();
        this.title = newBook.getTitle();
        return this;
    }

    
}

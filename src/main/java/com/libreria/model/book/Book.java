package com.libreria.model.book;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.libreria.model.exchange.Buy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    private String isbn;
    private String title;
    private String author;
    private int price;
    private int stock;
    private Genre genre;

    @OneToMany(mappedBy = "book")
    List<Buy> buyList;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    private List<BorrowingCopy> copies;

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
        this.genre = newBook.getGenre();
        return this;
    }

    
}

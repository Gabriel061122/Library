package com.libreria.model.exchange;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.Objects;
import java.util.Date;
import com.libreria.model.user.User;
import com.libreria.model.book.Book;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.JoinColumn;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    @JsonIgnore
    private Book book;


    private Date borrowingDate;
    private Date returnDate;

    public Borrowing(User user, Book book, Date borrowingDate, Date returnDate) {
        this.user = user;
        this.book = book;
        this.borrowingDate = borrowingDate;
        this.returnDate = returnDate;
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        if (o instanceof Borrowing) {
            isEqual = this.id.equals(((Borrowing) o).id);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
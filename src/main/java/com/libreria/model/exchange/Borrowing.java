package com.libreria.model.exchange;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.Objects;
import java.time.LocalDate;
import java.util.Date;
import com.libreria.model.user.User;
import com.libreria.model.book.Book;
import com.libreria.model.book.BorrowingCopy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;


@Entity
@Getter
@Setter
@AllArgsConstructor
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "user_id", referencedColumnName = "id"),
        @JoinColumn(name = "user_email", referencedColumnName = "email")
    })
    @JsonIgnore
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "copy_id")
    private BorrowingCopy copy;


    private LocalDate borrowingDate;
    private LocalDate returnDate; 

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
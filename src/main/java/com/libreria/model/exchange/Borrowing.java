package com.libreria.model.exchange;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.Objects;
import java.time.LocalDate;
import com.libreria.model.user.User;
import com.libreria.model.book.BorrowingCopy;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToOne
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

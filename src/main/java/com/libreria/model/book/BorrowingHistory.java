package com.libreria.modelo.book;

import com.libreria.model.User;
import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
public class BorrowingHistory {


    Long id;
    List<Borrowing> borrowings;

    public BorrowingHistory(List<Borrowing> borrowings) {
        this.borrowings = borrowings;
    }

    public List<Borrowing> getBorrowings() {
        return borrowings;
    }

    public void setBorrowings(List<Borrowing> borrowings) {
        this.borrowings = borrowings;
    }



}

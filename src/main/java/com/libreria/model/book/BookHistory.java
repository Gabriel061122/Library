package com.libreria.modelo.book;

import com.libreria.model.User;
import java.util.Date;

public class BookHistory {
    private Long id;
    private Book book;
    private User user;
    private Date loanDate;
    private Date returnDate;
    private int quantity;
    private int price;
    private int total;
    private int status;
}

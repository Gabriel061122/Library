package com.libreria.model.user;

import com.libreria.model.exchange.Borrowing;
import com.libreria.model.exchange.Order;

public interface UserAction {

    public User updateUser(User newUser);
    public void addOrder(Order order);
    public void Borrow(Borrowing borrowing);
    public void finishBorrowing();
    public void reserve(Borrowing reserve);
    public void endReserve();
    //public void grantPermissions();
} 

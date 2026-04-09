package com.libreria.model.exchange;

import jakarta.persistence.Entity; 
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.Objects;
import com.libreria.model.user.User;
import com.libreria.model.book.Book;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Buy {
    private Long id;
    private User user;
    private Book book;
    private int quantity;
    private int price;
    private int discount;


    public Buy(Long id, User user, Book book, int quantity, int price, int discount) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        if (o instanceof Buy) {
            isEqual = this.id.equals(((Buy) o).id);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getTotalPrice() {
        return price * quantity - getTotalDiscount();
    }

    public int getTotalDiscount() {
        return discount * quantity;
    }

    public int getTotalPriceWithoutDiscount() {
        return price * quantity;
    }

    public Buy updateBuy(Buy newBuy) {
        this.id = newBuy.id;
        this.user = newBuy.user;
        this.book = newBuy.book;
        this.quantity = newBuy.quantity;
        this.price = newBuy.price;
        this.discount = newBuy.discount;
        return this;
    }
}
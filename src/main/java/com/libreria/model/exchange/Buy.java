package com.libreria.model.exchange;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class Buy {
    @Id
    private Long id;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    private Order order;

    private int quantity;
    private int price;
    private int discount;

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

}

package com.libreria.model.exchange;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.libreria.model.exchange.OrderState.OrderState;
import com.libreria.model.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private Date orderDate;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private List<Buy> buys = new ArrayList<>();

    private OrderState state;

    public Order(User user, Date orderDate, List<Buy> buys) {
        this.user = user;
        this.orderDate = orderDate;
        this.buys = (buys == null) ? new ArrayList<>() : buys;
        this.state = OrderState.PENDING;
    }

    @Override
    public boolean equals(Object o) {
        boolean isEqual = false;
        if (o instanceof Order) {
            isEqual = Objects.equals(this.id, ((Order) o).id);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addBuy(Buy newBuy){
        if (this.state != OrderState.PENDING){
            
        }
        this.buys.add(newBuy);
    }
}

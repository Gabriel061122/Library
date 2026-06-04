package com.libreria.model.exchange;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.libreria.model.exchange.OrderState.OrderState;
import com.libreria.model.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private Date orderDate;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy="order")
    @JsonIgnore
    private List<Buy> buys;

    @Enumerated(EnumType.STRING)
    private OrderState state;

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


    public boolean isPending(){
	return this.state == OrderState.PENDING;
    }
}

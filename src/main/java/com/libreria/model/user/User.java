package com.libreria.model.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.libreria.model.exchange.Buy;
import com.libreria.model.exchange.Order;
import com.libreria.model.keys.UserKey;
import com.libreria.model.exchange.Borrowing;


@Entity
@Getter
@Setter
@NoArgsConstructor
@IdClass(UserKey.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Id
    private String email;
    private String name;
    private String password;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> orders;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<Borrowing> borrowings;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name= "user_user_types",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "user_type_id")
               )
    private List<UserType> userTypes;

    public User(
            String name,
            String email,
            String password,
            String role,
            String phone,
            String address,
            String city,
            String state,
            String country,
            String postalCode
    ) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;

        this.orders = new ArrayList<>();
        this.borrowings = new ArrayList<>();
        this.userTypes = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email); 
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void addOrder(Order order) {
        orders.add(order);
        order.setUser(this);
    }

    public void addBorrowing(Borrowing borrowing) {
        borrowings.add(borrowing);
        borrowing.setUser(this);
    }

    public void removeOrder(Order order) {
        orders.remove(order);
        order.setUser(null);
    }

    public void removeBorrowing(Borrowing borrowing) {
        borrowings.remove(borrowing);
        borrowing.setUser(null);
    }

    public void addUserType(UserType userType) {
        userTypes.add(userType);
        userType.addUser(this);
    }

    public void removeUserType(UserType userType) {
        userTypes.remove(userType);
        userType.removeUser(this);
    }
}

package com.libreria.model.user;

import java.util.Objects;
import java.util.Set;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import lombok.AllArgsConstructor;
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
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User /*implements UserAction */{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
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
    private List<Order> orders = new ArrayList<>();

    
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Borrowing> borrowings = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name= "user_user_types",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "user_type_id")
               )
    private Set<UserType> userTypes = new HashSet<>();

    //@OneToOne(cascade = CascadeType.ALL)
    //@JsonIgnore
    //private Borrowing reservation;

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


    public User updateUser(User newUser) {
        this.email = newUser.getEmail();
        this.name = newUser.getName();
        this.password = newUser.getPassword();
        this.phone = newUser.getPhone();
        this.address = newUser.getAddress();
        this.city = newUser.getCity();
        this.state = newUser.getState();
        this.country = newUser.getCountry();
        this.postalCode = newUser.getPostalCode();
        return this;
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


}

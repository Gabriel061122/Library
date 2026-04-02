package com.libreria.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_type")
@Getter
@NoArgsConstructor
public class UserType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Type type;

    public enum Type {
        ADMIN,
        CUSTOMER,
        LIBRARIAN
    }

    public UserType(Type type) {
        this.type = type;
    }
}

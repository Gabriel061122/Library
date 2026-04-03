package com.libreria.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.libreria.model.user.User;
import java.util.Objects;


@Entity
@Table(name = "user_type")
@Getter
@Setter
@NoArgsConstructor

public class UserType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String type;

    @ManyToMany(mappedBy = "userTypes", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users;

    public UserType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserType)) return false; 
        UserType userType = (UserType) o;
        return Objects.equals(id, userType.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    
}

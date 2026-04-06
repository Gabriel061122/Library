package com.libreria.model.repositories;

import com.libreria.model.keys.UserKey;
import com.libreria.model.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UserKey> {

    Optional<User> findFirstById(Long id);
}

package com.libreria.service;

import com.libreria.model.repositories.UserRepository;
import com.libreria.model.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> updateUser(Long id, User incoming) {
        return userRepository.findById(id).map(existing -> {
            existing.setName(incoming.getName());
            if (incoming.getPassword() != null) {
                existing.setPassword(incoming.getPassword());
            }
            existing.setPhone(incoming.getPhone());
            existing.setAddress(incoming.getAddress());
            existing.setCity(incoming.getCity());
            existing.setState(incoming.getState());
            existing.setCountry(incoming.getCountry());
            existing.setPostalCode(incoming.getPostalCode());
            return userRepository.save(existing);
        });
    }

    public boolean deleteUser(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                })
                .orElse(false);
    }
}

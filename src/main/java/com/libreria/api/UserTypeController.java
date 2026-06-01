package com.libreria.api;

import com.libreria.model.repositories.UserTypeRepository;
import com.libreria.model.user.UserType;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user-types")
@RestController
public class UserTypeController {

    private final UserTypeRepository userTypeRepository;

    public UserTypeController(UserTypeRepository userTypeRepository) {
        this.userTypeRepository = userTypeRepository;
    }

    @GetMapping
    public ResponseEntity<List<UserType>> getUserTypes() {
        return ResponseEntity.ok(userTypeRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserType> getUserType(@PathVariable Long id) {
        return userTypeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserType> addUserType(@RequestBody UserType userType) {
        return ResponseEntity.ok(userTypeRepository.save(userType));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserType> updateUserType(@PathVariable Long id, @RequestBody UserType userType) {
        return userTypeRepository.findById(id)
                .map(existing -> {
                    userType.setId(id);
                    return ResponseEntity.ok(userTypeRepository.save(userType));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserType(@PathVariable Long id) {
        if (!userTypeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userTypeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

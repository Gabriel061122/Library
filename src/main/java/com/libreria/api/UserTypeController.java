package com.libreria.api;

import com.libreria.model.user.UserType;
import com.libreria.service.UserTypeService;
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

    private final UserTypeService userTypeService;

    public UserTypeController(UserTypeService userTypeService) {
        this.userTypeService = userTypeService;
    }

    @GetMapping
    public ResponseEntity<List<UserType>> getUserTypes() {
        return ResponseEntity.ok(userTypeService.getUserTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserType> getUserType(@PathVariable Long id) {
        return userTypeService.getUserType(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserType> addUserType(@RequestBody UserType userType) {
        return ResponseEntity.ok(userTypeService.addUserType(userType));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserType> updateUserType(@PathVariable Long id, @RequestBody UserType userType) {
        return userTypeService.updateUserType(id, userType)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserType(@PathVariable Long id) {
        if (userTypeService.deleteUserType(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

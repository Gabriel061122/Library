package com.libreria.service;

import com.libreria.model.repositories.UserTypeRepository;
import com.libreria.model.user.UserType;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserTypeService {

    private final UserTypeRepository userTypeRepository;

    public UserTypeService(UserTypeRepository userTypeRepository) {
        this.userTypeRepository = userTypeRepository;
    }

    public List<UserType> getUserTypes() {
        return userTypeRepository.findAll();
    }

    public Optional<UserType> getUserType(Long id) {
        return userTypeRepository.findById(id);
    }

    public UserType addUserType(UserType userType) {
        return userTypeRepository.save(userType);
    }

    public Optional<UserType> updateUserType(Long id, UserType userType) {
        return userTypeRepository.findById(id).map(existing -> {
            userType.setId(id);
            return userTypeRepository.save(userType);
        });
    }

    public boolean deleteUserType(Long id) {
        if (!userTypeRepository.existsById(id)) {
            return false;
        }
        userTypeRepository.deleteById(id);
        return true;
    }
}

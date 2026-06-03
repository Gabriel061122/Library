package com.libreria.api;

import com.libreria.security.JwtUtil;
import com.libreria.model.user.User;
import com.libreria.model.user.UserType;
import com.libreria.service.UserService;
import com.libreria.service.UserTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    private final UserService userService;
    private final UserTypeService userTypeService;

    public AuthController(UserService userService, UserTypeService userTypeService, JwtUtil jwtUtil){
        this.userService = userService;
        this.userTypeService = userTypeService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials){

        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<User> user = userService.getUserByEmail(email);

        if(user.isEmpty() || !user.get().getPassword().equals(password)) return ResponseEntity.status(401).body(
                Map.of("exists", false)
        );

        return ResponseEntity.ok(Map.of("token", jwtUtil.generateToken(email)));

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){
        if(user.getEmail() == null || user.getPassword() == null){
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
        }

        if(userService.getUserByEmail(user.getEmail()).isPresent()){
            return ResponseEntity.status(409).body(Map.of("error", "Email already in use"));
        }

        Set<UserType> userTypes = new HashSet<>();
        if(user.getUserTypes() != null){
            user.getUserTypes().stream()
                    .map(UserType::getId)
                    .filter(id -> id != null)
                    .map(userTypeService::getUserType)
                    .flatMap(Optional::stream)
                    .forEach(userTypes::add);
        }

        if(userTypes.isEmpty()){
            userTypeService.getUserType(1L).ifPresent(userTypes::add);
        }
        user.setUserTypes(userTypes);

        User createdUser = userService.addUser(user);
        return ResponseEntity.ok(Map.of(
                "user", createdUser,
                "token", jwtUtil.generateToken(createdUser.getEmail())
        ));
    }

}

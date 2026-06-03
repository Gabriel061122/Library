package com.libreria.api;

import com.libreria.security.JwtUtil;
import com.libreria.model.user.User;
import com.libreria.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    private final UserService userService;

    public AuthController(UserService userService, JwtUtil jwtUtil){
        this.userService = userService;
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

}

package com.taskmanager.user_service.controller;

import com.taskmanager.user_service.exception.AuthenticationException;
import com.taskmanager.user_service.model.User;
import com.taskmanager.user_service.service.UserService;
import com.taskmanager.user_service.service.JwtTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtTokenService jwtTokenService;
    public AuthController(UserService userService, JwtTokenService jwtTokenService, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
    }

    // Регистрация нового пользователя
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        userService.createUser(user); // Здесь вызовите метод для регистрации
        return ResponseEntity.ok("User registered successfully");
    }

    // Логин (получение JWT токена)
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {

        User authenticatedUser = userService.getUserByUsername(user.getUsername())
                .orElseThrow(() -> new AuthenticationException("Invalid USERNAME or password"));
        if (!passwordEncoder.matches(user.getPassword(), authenticatedUser.getPassword())) {
            throw new AuthenticationException("Invalid username or PASSWORD");
        }

        String token = jwtTokenService.generateToken(authenticatedUser.getUsername(), authenticatedUser.getRole().toString());

        return ResponseEntity.ok(token);
    }
}

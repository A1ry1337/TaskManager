package com.example.task_service.controller;


import com.example.task_service.service.JwtTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwtController {

    private final JwtTokenService jwtTokenService;

    @Autowired
    public JwtController(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @GetMapping("/extract-token-info")
    public String extractTokenInfo(@RequestHeader("Authorization") String authHeader) {
        System.out.println(authHeader);
        // Ожидаем, что токен будет передан в заголовке Authorization как "Bearer <token>"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);  // Убираем "Bearer " из начала строки
            String username = jwtTokenService.extractUsername(token);
            String role = jwtTokenService.extractRole(token);

            return "Username: " + username + ", Role: " + role;
        }
        return "Token not found or invalid!";
    }
}


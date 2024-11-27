package com.taskmanager.user_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotBlank(message = "Username cannot be empty") // Валидация на пустое значение
    @Size(min = 3, max = 50, message = "Username should be between 3 and 50 characters") // Ограничение длины
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Email cannot be empty") // Валидация на пустое значение
    @Email(message = "Email should be valid") // Валидация на правильность формата email
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password cannot be empty") // Валидация на пустое значение
    @Size(min = 6, message = "Password should be at least 6 characters long") // Минимальная длина пароля
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}

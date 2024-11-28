package com.example.task_service.repository;

import com.example.task_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Найти пользователя по имени
    User findByUsername(String username);
}


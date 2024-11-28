package com.example.task_service.repository;

import com.example.task_service.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Можно добавить кастомные запросы, например, для поиска задач по пользователю
    // List<Task> findByCreatedBy(User createdBy);
}


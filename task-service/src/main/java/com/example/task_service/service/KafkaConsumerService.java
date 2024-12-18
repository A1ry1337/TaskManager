package com.example.task_service.service;

import com.example.task_service.model.Role;
import com.example.task_service.model.User;
import com.example.task_service.repository.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KafkaConsumerService {

    private final UserRepository userRepository;

    public KafkaConsumerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "user-events", groupId = "task-service-group")
    public void handleUserEvent(Map<String, Object> event) {
        System.out.println(event);
        String eventType = (String) event.get("eventType");
        Long id = Long.valueOf(event.get("id").toString());
        String username = (String) event.get("username");
        String role = (String) event.get("role");

        switch (eventType) {
            case "CREATE" -> createUser(id, username, role);
            case "UPDATE" -> updateUser(id, username, role);
            case "DELETE" -> deleteUser(id);
        }
    }

    private void createUser(Long id, String username, String role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setRole(Role.valueOf(role));
        userRepository.save(user);
    }

    private void updateUser(Long id, String username, String role) {
        userRepository.findById(id).ifPresent(user -> {
            user.setUsername(username);
            user.setRole(Role.valueOf(role));
            userRepository.save(user);
        });
    }

    private void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

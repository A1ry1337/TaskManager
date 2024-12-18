package com.taskmanager.user_service.service;

import com.taskmanager.user_service.dto.UserEvent;
import com.taskmanager.user_service.model.Role;
import com.taskmanager.user_service.model.User;
import com.taskmanager.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;;

    @Autowired
    public UserService(UserRepository userRepository, KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
        this.userRepository = userRepository;
    }

    public void createUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        User savedUser = userRepository.save(user);
        if (savedUser.getId() != null) {
            UserEvent event = new UserEvent(user, "CREATE");
            kafkaProducerService.sendUserEvent(event);
        }
    }

    // Получение всех пользователей
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Обновление пользователя
    public boolean updateUser(Long id, User user) {
        // Проверяем, существует ли пользователь
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            // Устанавливаем ID существующего пользователя
            user.setId(id);

            // Сохраняем обновлённого пользователя
            userRepository.save(user);

            // Отправка события в Kafka
            UserEvent event = new UserEvent(user, "UPDATE");
            kafkaProducerService.sendUserEvent(event);

            return true;
        }

        // Пользователь не найден
        return false;
    }


    // Удаление пользователя
    public boolean deleteUser(Long id) {
        // Проверяем, существует ли пользователь
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Удаляем пользователя
            userRepository.deleteById(id);

            // Отправка события в Kafka
            UserEvent event = new UserEvent(user, "DELETE");
            kafkaProducerService.sendUserEvent(event);

            return true;
        }

        // Пользователь не найден
        return false;
    }


    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

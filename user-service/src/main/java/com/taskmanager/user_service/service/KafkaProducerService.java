package com.taskmanager.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.user_service.dto.UserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserEvent(UserEvent event) { 
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Сериализация объекта в строку JSON
            String jsonString = objectMapper.writeValueAsString(event);

            // Отправка сообщения в Kafka
            kafkaTemplate.send("user-events", jsonString);
        } catch (JsonProcessingException e) {
            // Логирование ошибки или обработка исключения
            System.err.println("Ошибка сериализации объекта UserEvent в JSON: " + e.getMessage());
            // В зависимости от требований можно, например, выбросить RuntimeException
            throw new RuntimeException("Ошибка при отправке события пользователя", e);
        } catch (Exception e) {
            // Обработка других ошибок
            System.err.println("Произошла ошибка при отправке события в Kafka: " + e.getMessage());
            throw new RuntimeException("Ошибка при отправке события в Kafka", e);
        }
    }
}


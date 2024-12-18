package com.taskmanager.user_service.dto;

import com.taskmanager.user_service.model.User;
import lombok.Data;

@Data
public class UserEvent {
    private Long id;
    private String username;
    private String role;
    private String eventType; // "CREATE", "UPDATE", "DELETE"

    public UserEvent(User user, String eventType) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole().toString();
        this.eventType = eventType;
    }
}


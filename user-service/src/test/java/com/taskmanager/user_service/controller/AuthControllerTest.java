package com.taskmanager.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.user_service.config.TestSecurityConfig;
import com.taskmanager.user_service.model.User;
import com.taskmanager.user_service.service.JwtTokenService;
import com.taskmanager.user_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenService jwtTokenService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void register_Success() throws Exception {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");

        Mockito.doNothing().when(userService).createUser(any(User.class));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void login_Success() throws Exception {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String encodedPassword = passwordEncoder.encode(password);
        String token = "mockJwtToken";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        User existingUser = new User();
        existingUser.setUsername(username);
        existingUser.setPassword(encodedPassword);

        Mockito.when(userService.getUserByUsername(eq(username))).thenReturn(Optional.of(existingUser));
        Mockito.when(jwtTokenService.generateToken(eq(username))).thenReturn(token);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(token));
    }

    @Test
    void login_InvalidCredentials() throws Exception {
        // Arrange
        String username = "testuser";
        String password = "wrongPassword";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        User existingUser = new User();
        existingUser.setUsername(username);
        existingUser.setPassword(passwordEncoder.encode("password123"));

        Mockito.when(userService.getUserByUsername(eq(username))).thenReturn(Optional.of(existingUser));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_UserNotFound() throws Exception {
        // Arrange
        String username = "unknownuser";
        String password = "password123";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        Mockito.when(userService.getUserByUsername(eq(username))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());
    }
}


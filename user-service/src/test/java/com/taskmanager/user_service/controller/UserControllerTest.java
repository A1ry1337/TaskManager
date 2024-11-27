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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @MockBean
    private JwtTokenService jwtTokenService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void getAllUsers_Success() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@mail.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@mail.com");

        Mockito.when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    void getUserById_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setEmail("user1@mail.com");

        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"));
    }

    @Test
    void getUserById_NotFound() throws Exception {
        Mockito.when(userService.getUserById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("updatedUser");
        user.setEmail("updated@mail.com");

        Mockito.when(userService.updateUser(eq(1L), any(User.class))).thenReturn(true);

        mockMvc.perform(put("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated successfully"));
    }

    @Test
    void updateUser_NotFound() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("updatedUser");
        user.setEmail("updated@mail.com");

        Mockito.when(userService.updateUser(eq(1L), any(User.class))).thenReturn(false);

        mockMvc.perform(put("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_Success() throws Exception {
        Mockito.when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    @Test
    void deleteUser_NotFound() throws Exception {
        Mockito.when(userService.deleteUser(1L)).thenReturn(false);

        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isNotFound());
    }
}

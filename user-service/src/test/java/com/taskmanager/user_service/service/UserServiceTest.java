package com.taskmanager.user_service.service;

import com.taskmanager.user_service.model.Role;
import com.taskmanager.user_service.model.User;
import com.taskmanager.user_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Инициализация моков
        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password");
        user.setRole(Role.ROLE_USER);
    }

    @Test
    public void testCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.createUser(user);

        verify(userRepository, times(1)).save(any(User.class)); // Проверка, что save был вызван один раз
        assertNotNull(user.getPassword()); // Проверка, что пароль не равен null
    }

    @Test
    public void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        var users = userService.getAllUsers();
        assertEquals(1, users.size());
        assertEquals("testUser", users.get(0).getUsername());
    }

    @Test
    public void testGetUserById_UserExists() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getUsername());
    }

    @Test
    public void testGetUserById_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<User> foundUser = userService.getUserById(1L);

        assertFalse(foundUser.isPresent());
    }

    @Test
    public void testUpdateUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        boolean updated = userService.updateUser(1L, user);

        assertTrue(updated);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        boolean updated = userService.updateUser(1L, user);

        assertFalse(updated);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDeleteUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        boolean deleted = userService.deleteUser(1L);

        assertTrue(deleted);
        verify(userRepository, times(1)).deleteById(anyLong());
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        boolean deleted = userService.deleteUser(1L);

        assertFalse(deleted);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testGetUserByUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserByUsername("testUser");

        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getUsername());
    }
}

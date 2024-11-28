package com.example.task_service.security;

import com.example.task_service.service.JwtTokenService;
import com.example.task_service.service.UserService;
import com.example.task_service.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenService jwtTokenService, UserService userService) {
        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Извлекаем JWT токен из заголовка запроса
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new UnauthorizedException("JWT Token is missing or invalid"); // Если токен отсутствует или некорректен
            }

            // Извлекаем сам токен
            String jwt = authHeader.substring(7);
            String username = jwtTokenService.extractUsername(jwt);

            // Проверяем, есть ли токен и аутентификация не выполнена
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Загружаем пользователя по имени из базы данных
                var user = userService.getUserByUsername(username);

                if (user == null) {
                    throw new UnauthorizedException("User not found in the database"); // Если пользователь не найден
                }

                if (!jwtTokenService.validateToken(jwt, username)) {
                    throw new UnauthorizedException("Invalid JWT token"); // Если токен некорректен
                }

                // Используем строку роли напрямую (например, ROLE_USER, ROLE_ADMIN)
                String role = user.getRole().name(); // Преобразуем роль в строку (например, ROLE_USER)

                // Создаем объект SimpleGrantedAuthority для роли
                var authorities = Collections.singletonList(new SimpleGrantedAuthority(role));

                // Создаем объект аутентификации с ролью
                var authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Устанавливаем аутентификацию в контексте безопасности
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            // Пропускаем запрос
            filterChain.doFilter(request, response);

        } catch (UnauthorizedException ex) {
            // Устанавливаем HTTP-статус 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            // Указываем тип содержимого для ответа
            response.setContentType("text/plain");

            // Пишем сообщение об ошибке в виде строки
            response.getWriter().write(ex.getMessage());

            // Завершаем обработку запроса
        }
    }

}

package ru.wms.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.wms.backend.enums.Role;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    // JWT-токен для авторизации последующих запросов к складу
    private String token;

    // Вложенный объект с данными профиля для фронтенда
    private UserProfileDto user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserProfileDto {
        private String id;
        private String name;
        private String email;
        private Role role;
        private Long companyId;
        private String companyName;
    }
}

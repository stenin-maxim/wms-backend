package ru.wms.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.wms.backend.dto.AuthResponse;
import ru.wms.backend.dto.request.LoginRequest;
import ru.wms.backend.dto.request.RegisterCompanyRequest;
import ru.wms.backend.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    /**
     * Эндпоинт для регистрации новой компании и её администратора.
     * Доступен по адресу: POST http://localhost:5000/api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerCompany(@Valid @RequestBody RegisterCompanyRequest request) {
        AuthResponse response = authService.registerCompany(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Эндпоинт для входа сотрудников склада и менеджмента в систему.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}

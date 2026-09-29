package ru.wms.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import ru.wms.dto.request.LoginRequest;
import ru.wms.dto.request.RegisterCompanyRequest;
import ru.wms.dto.response.AuthResponse;
import ru.wms.enums.Role;
import ru.wms.enums.UserStatus;
import ru.wms.model.Company;
import ru.wms.model.User;
import ru.wms.repository.CompanyRepository;
import ru.wms.repository.UserRepository;
import ru.wms.security.JwtService;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class AuthServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegisterCompanyRequest registerRequest;
    private LoginRequest loginRequest;
    private Company mockCompany;
    private User mockUser;

    @BeforeEach
    void setUp() {
        // Инициализируем тестовые данные перед каждым запуском @Test метода
        registerRequest = new RegisterCompanyRequest();
        registerRequest.setCompanyName("Тест Склад");
        registerRequest.setAdminName("Максим");
        registerRequest.setAdminEmail("max@wms.ru");
        registerRequest.setPassword("raw_password");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("max@wms.ru");
        loginRequest.setPassword("raw_password");

        mockCompany = Company.builder()
            .id(1L)
            .name("Тест Склад")
            .email("max@wms.ru")
            .build();

        mockUser = User.builder()
            .id("01J69Z4Y8R")
            .name("Максим")
            .email("max@wms.ru")
            .password("encoded_password")
            .role(Role.ADMIN)
            .status(UserStatus.ACTIVE)
            .company(mockCompany)
            .build();        
    }

    // --- ТЕСТЫ НА РЕГИСТРАЦИЮ КОМПАНИИ ---

    @Test
    void registerCompany_Success_ReturnsAuthResponse() {
        // Обучаем заглушки (Mocks), как им отвечать на вызовы во время теста
        when(userRepository.existsByEmail(registerRequest.getAdminEmail())).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenReturn(mockCompany);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("valid_jwt_token");

        // Выполняем тестируемый метод сервиса
        AuthResponse response = authService.registerCompany(registerRequest);

        // Проверяем утверждения (Assertions) — совпадает ли результат с нашими требованиями
        assertNotNull(response);
        assertEquals("valid_jwt_token", response.getToken());
        assertEquals("max@wms.ru", response.getUser().getEmail());
        assertEquals(Role.ADMIN, response.getUser().getRole());

        // Проверяем, что методы сохранения в БД были вызваны ровно по одному разу
        verify(companyRepository, times(1)).save(any(Company.class));
        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void registerCompany_EmailAlreadyExists_ThrowsException() {
        // Симулируем ситуацию, когда email в системе уже занят
        when(userRepository.existsByEmail(registerRequest.getAdminEmail())).thenReturn(true);

        // Проверяем, что метод выбросит нужное нам исключение
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.registerCompany(registerRequest);
        });

        assertEquals("Пользователь с таким Email уже зарегистрирован в системе", exception.getMessage());
        
        // Убеждаемся, что база данных компаний не вызывалась на сохранение при ошибке
        verify(companyRepository, never()).save(any(Company.class));
    }

    // --- ТЕСТЫ НА ЛОГИН (ВХОД В СИСТЕМУ) ---

    @Test
    void login_Success_ReturnsAuthResponse() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken(mockUser)).thenReturn("valid_jwt_token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("valid_jwt_token", response.getToken());
        assertEquals("Максим", response.getUser().getName());
    }

    @Test
    void login_WrongPassword_ThrowsException() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        // Симулируем ввод неверного пароля
        when(passwordEncoder.matches(loginRequest.getPassword(), mockUser.getPassword())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Неверный Email или пароль", exception.getMessage());
    }

    @Test
    void login_UserBlocked_ThrowsException() {
        // Меняем статус нашего пользователя на заблокированный
        mockUser.setStatus(UserStatus.BLOCKED);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            authService.login(loginRequest);
        });

        assertTrue(exception.getMessage().contains("Ваш аккаунт заблокирован"));
    }

}

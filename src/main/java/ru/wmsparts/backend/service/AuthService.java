package ru.wmsparts.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.wmsparts.backend.dto.AuthResponse;
import ru.wmsparts.backend.dto.LoginRequest;
import ru.wmsparts.backend.dto.RegisterCompanyRequest;
import ru.wmsparts.backend.enums.Role;
import ru.wmsparts.backend.enums.UserStatus;
import ru.wmsparts.backend.model.Company;
import ru.wmsparts.backend.model.User;
import ru.wmsparts.backend.repository.CompanyRepository;
import ru.wmsparts.backend.repository.UserRepository;
import ru.wmsparts.backend.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Авторизация сотрудника склада (Вход в систему).
     */
    @Transactional(readOnly = true) // Оптимизируем транзакцию только на чтение для ускорения БД
    public AuthResponse login(LoginRequest request) {
        
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Неверный Email или пароль"));

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new IllegalStateException("Ваш аккаунт заблокирован. Обратитесь к администратору склада.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Неверный Email или пароль");
        }

        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
            .token(jwtToken)
            .user(AuthResponse.UserProfileDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .companyId(user.getCompany().getId())
                .companyName(user.getCompany().getName())
                .build())
            .build();
    }

    /**
     * Регистрация новой компании и автоматическое создание её администратора.
     * Аннотация @Transactional гарантирует, что обе сущности запишутся в БД вместе, либо не запишутся вообще.
     */
    @Transactional
    @SuppressWarnings("null") // Отключает строгий null-анализ компилятора для Builder-шаблонов Lombok
    public AuthResponse registerCompany(RegisterCompanyRequest request) {

        if (userRepository.existsByEmail(request.getAdminEmail())) {
            throw new IllegalArgumentException("Пользователь с таким Email уже зарегистрирован в системе");
        }

        Company company = Company.builder()
            .name(request.getCompanyName())
            .email(request.getAdminEmail())
            .build();

        Company savedCompany = java.util.Objects.requireNonNull(
            companyRepository.save(company),
            "Не удалось сохранить компанию в базу данных"
        );

        User adminUser = User.builder()
            .name(request.getAdminName())
            .email(request.getAdminEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.ADMIN)
            .status(UserStatus.ACTIVE)
            .company(savedCompany) 
            .build();

        User savedUser = java.util.Objects.requireNonNull(
            userRepository.save(adminUser), 
            "Не удалось сохранить администратора в базу данных"
        );

        String jwtToken = jwtService.generateToken(savedUser);

        return AuthResponse.builder()
            .token(jwtToken)
            .user(AuthResponse.UserProfileDto.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .companyId(savedCompany.getId())
                .companyName(savedCompany.getName())
                .build())
            .build();
    }
}
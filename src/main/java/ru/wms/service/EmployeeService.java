package ru.wms.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.wms.dto.request.employee.CreateEmployeeRequest;
import ru.wms.dto.response.AuthResponse.UserProfileDto;
import ru.wms.enums.Role;
import ru.wms.enums.UserStatus;
import ru.wms.model.Company;
import ru.wms.model.User;
import ru.wms.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Возвращает список всех сотрудников конкретной компании.
     */
    @Transactional(readOnly = true)
    public List<UserProfileDto> getEmployeesByCompany(Company company) {
        return userRepository.findByCompanyId(company.getId())
            .stream()
            .map(user -> UserProfileDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .companyId(company.getId())
                .companyName(company.getName())
                .build())
            .collect(Collectors.toList());
    }

    /**
     * Создание нового сотрудника внутри компании текущего авторизованного менеджера/админа.
     */
    @Transactional
    @SuppressWarnings("null")
    public UserProfileDto createEmployee(CreateEmployeeRequest request, Company company) {
        // Запрещаем создавать пользователей с ролью SUPER_ADMIN
        if (request.getRole() == Role.SUPER_ADMIN) {
            throw new IllegalArgumentException("Недопустимая роль для создания сотрудника");
        }

        // Проверяем уникальность Email в системе
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Сотрудник с таким Email уже зарегистрирован");
        }

        User employee = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .status(UserStatus.ACTIVE)
            .assignedEquipment(request.getAssignedEquipment())
            .company(company) // Принудительно привязываем к компании текущей сессии
            .build();

        User savedUser = Objects.requireNonNull(
            userRepository.save(employee),
            "Не удалось сохранить сотрудника на склад"
        );

        // Возвращаем безопасный DTO профиля для отображения в таблице на фронтенде
        return UserProfileDto.builder()
            .id(savedUser.getId())
            .name(savedUser.getName())
            .email(savedUser.getEmail())
            .role(savedUser.getRole())
            .companyId(company.getId())
            .companyName(company.getName())
            .build();
    }

    /**
     * Изменение статуса сотрудника (Блокировка / Разблокировка).
     * Безопасно проверяет принадлежность сотрудника к компании текущего администратора.
     */
    @Transactional
    public UserProfileDto toggleEmployeeStatus(String employeeId, String statusStr, User currentUser) {
        UserStatus newStatus;
        try {
            newStatus = UserStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Недопустимый статус сотрудника. Разрешены: ACTIVE, BLOCKED");
        }
        
        @SuppressWarnings("null")
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник с указанным ID не найден"));

        // ЗАЩИТА: Администратор не может заблокировать сам себя
        if (employee.getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Вы не можете изменить статус своего собственного аккаунта");
        }

        // SAAS-ИЗОЛЯЦИЯ: Проверяем, что увольняемый сотрудник работает именно в компании текущего админа
        if (!employee.getCompany().getId().equals(currentUser.getCompany().getId())) {
            throw new IllegalArgumentException("Доступ запрещен: этот сотрудник принадлежит другой организации");
        }

        // Меняем статус и сохраняем изменения
        employee.setStatus(newStatus);
        User updatedUser = userRepository.save(employee);

        return UserProfileDto.builder()
            .id(updatedUser.getId())
            .name(updatedUser.getName())
            .email(updatedUser.getEmail())
            .role(updatedUser.getRole())
            .companyId(currentUser.getCompany().getId())
            .companyName(currentUser.getCompany().getName())
            .build();
    }

}

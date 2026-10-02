package ru.wms.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.wms.dto.request.employee.CreateEmployeeRequest;
import ru.wms.dto.request.employee.UpdateEmployeeStatusRequest;
import ru.wms.dto.response.AuthResponse.UserProfileDto;
import ru.wms.model.User;
import ru.wms.service.EmployeeService;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Получить список всех сотрудников компании.
     */
    @GetMapping
    public ResponseEntity<List<UserProfileDto>> getMyEmployees(@AuthenticationPrincipal User currentUser) {
        // Из токена автоматически берется компания текущего менеджера/директора
        List<UserProfileDto> employees = employeeService.getEmployeesByCompany(currentUser.getCompany());
        return ResponseEntity.ok(employees);
    }

    /**
     * Добавить нового сотрудника (например, кладовщика STOREKEEPER или сборщика PICKER).
     */
    @PostMapping
    public ResponseEntity<UserProfileDto> addEmployee(
        @Valid @RequestBody CreateEmployeeRequest request,
        @AuthenticationPrincipal User currentUser
    ) {
        UserProfileDto response = employeeService.createEmployee(request, currentUser.getCompany());
        return ResponseEntity.ok(response);
    }

    /**
     * Изменить рабочий статус сотрудника (например, уволить/заблокировать).
     */
    @PatchMapping("/status")
    public ResponseEntity<UserProfileDto> changeEmployeeStatus(
        @Valid @RequestBody UpdateEmployeeStatusRequest request,
        @AuthenticationPrincipal User currentUser
    ) {
        UserProfileDto response = employeeService.toggleEmployeeStatus(
            request.getEmployeeId(),
            request.getStatus().toUpperCase(), 
            currentUser
        );
        
        return ResponseEntity.ok(response);
    }
}

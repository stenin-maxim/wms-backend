package ru.wmsparts.backend.enums;

public enum UserStatus {
    ACTIVE,  // Сотрудник активен, имеет доступ к складским операциям или ТСД
    BLOCKED  // Сотрудник заблокирован (уволен), доступ к системе аннулирован
}

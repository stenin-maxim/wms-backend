package ru.wms.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
     /**
     * Перехват ошибок бизнес-логики (неверный пароль, занятый email и т.д.)
     * Возвращает статус 400 Bad Request и понятный JSON текст ошибки.
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleBusinessExceptions(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Перехват ошибок автоматической валидации @Valid (кривой email, короткий ИНН)
     * Собирает все ошибки полей и возвращает список того, что заполнено неверно.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Перехват любых непредвиденных системных критических сбоев сервера (Error 500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalExceptions(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Внутренняя ошибка сервера. Попробуйте позже.");
        // Выводим реальную ошибку в консоль сервера для разработчика, чтобы её можно было дебажить
        ex.printStackTrace(); 
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

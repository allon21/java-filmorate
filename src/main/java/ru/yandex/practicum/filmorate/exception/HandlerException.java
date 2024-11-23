package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class HandlerException {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> validationException(ValidationException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("Ошибка валидации", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> notFoundException(NotFoundException e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("Объект не найден", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("Internal exception occured", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }
}

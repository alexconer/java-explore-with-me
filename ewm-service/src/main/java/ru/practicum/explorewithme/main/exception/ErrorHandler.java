package ru.practicum.explorewithme.main.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Map<String, String> handleBaseValidationException(MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        log.error("Ошибка валидации: {} ", errors);

        return makeError(HttpStatus.BAD_REQUEST.name(),  ex.getCause() != null ? ex.getCause().getMessage() : HttpStatus.BAD_REQUEST.getReasonPhrase(), errors.toString());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MissingRequestValueException.class})
    public Map<String, String> handleMissingRequestValueException(MissingRequestValueException ex) {

        log.error("Ошибка валидации: {} ", ex.getMessage());

        return makeError(HttpStatus.BAD_REQUEST.name(),  ex.getCause() != null ? ex.getCause().getMessage() : HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class})
    public Map<String, String> handleBadRequestException(BadRequestException ex) {
        log.error("Неверный запрос: {} ", ex.getMessage());
        return makeError(HttpStatus.BAD_REQUEST.name(),  ex.getCause() != null ? ex.getCause().getMessage() : HttpStatus.BAD_REQUEST.getReasonPhrase(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({DataIntegrityViolationException.class})
    public Map<String, String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Нарушение целостности данных: {} ", ex.getMessage());
        return makeError(HttpStatus.CONFLICT.name(),  ex.getCause() != null ? ex.getCause().getMessage() : HttpStatus.CONFLICT.getReasonPhrase(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({ConflictException.class})
    public Map<String, String> handleConflictException(ConflictException ex) {
        log.error("Нарушение целостности данных: {} ", ex.getMessage());
        return makeError(HttpStatus.CONFLICT.name(),  ex.getCause() != null ? ex.getCause().getMessage() : HttpStatus.CONFLICT.getReasonPhrase(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class})
    public Map<String, String> handleNotFoundException(NotFoundException ex) {
        log.error("Элемент не найден: {} ", ex.getMessage());
        return makeError(HttpStatus.NOT_FOUND.name(), ex.getCause() != null ? ex.getCause().getMessage() : HttpStatus.NOT_FOUND.getReasonPhrase(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Throwable.class})
    public Map<String, String> handleThrowable(Throwable throwable) {
        log.error("Ошибка сервера: {} ", throwable.getMessage());
        return makeError(HttpStatus.INTERNAL_SERVER_ERROR.name(), throwable.getCause() != null ? throwable.getCause().getMessage() : HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), throwable.getMessage());
    }

    private Map<String, String> makeError(String status, String reason, String message) {
        Map<String, String> errResponse = new HashMap<>();
        errResponse.put("status", status);
        errResponse.put("reason", reason);
        errResponse.put("message", message);
        errResponse.put("timestamp", String.valueOf(LocalDateTime.now()));
        return errResponse;
    }

}

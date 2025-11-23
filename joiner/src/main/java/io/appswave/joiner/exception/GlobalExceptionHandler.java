package io.appswave.joiner.exception;

import io.appswave.joiner.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    private String msg(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, null, code, locale);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(err -> {
            String messageKey = err.getDefaultMessage();
            String localized = msg(messageKey);
            errors.put(err.getField(), localized);
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<?> handleEmailAlreadyExists(EmailAlreadyExistsException ex, Locale locale) {
        String msg = messageSource.getMessage("user.exists", null, "Email already exists", locale);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(msg));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("user.unauthorized", null, "Access Denied", locale);
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        error.put("status", HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleEnumErrors(HttpMessageNotReadableException ex) {
        if (ex.getMessage().contains("Gender")) {
            return ResponseEntity.badRequest().body(msg("validation.gender.required"));
        }
        if (ex.getMessage().contains("MembershipType")) {
            return ResponseEntity.badRequest().body(msg("validation.membership.required"));
        }
        if (ex.getMessage().contains("PersonaType")) {
            return ResponseEntity.badRequest().body(msg("validation.persona.required"));
        }

        return ResponseEntity.badRequest().body(msg("bad.request"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {

        Map<String, String> error = new HashMap<>();

        String localized = msg(ex.getMessage());

        error.put("error", localized);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage("invalid.credentials", null, "Invalid credentials", locale);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        Locale locale = LocaleContextHolder.getLocale();
        String msg = messageSource.getMessage("error.internal", null, "An error occurred", locale);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", msg));
    }
}
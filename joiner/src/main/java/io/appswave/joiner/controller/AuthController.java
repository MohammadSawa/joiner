package io.appswave.joiner.controller;

import io.appswave.joiner.dto.request.LoginRequest;
import io.appswave.joiner.dto.request.SignupRequest;
import io.appswave.joiner.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MessageSource messageSource;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequest request, Locale locale) {
        authService.register(request);
        String msg = messageSource.getMessage("register.success", null, locale);
        return ResponseEntity.ok(msg);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest , Locale locale) {

        authService.login(request, httpRequest);
        String msg = messageSource.getMessage("login.success", null, locale);
        return ResponseEntity.ok(msg);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session,Locale locale) {
        authService.logout(session);
        String msg = messageSource.getMessage("logout.success", null, locale);

        return ResponseEntity.ok(msg);
    }
}

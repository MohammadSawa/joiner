package io.appswave.joiner.controller;

import io.appswave.joiner.dto.request.LoginRequest;
import io.appswave.joiner.dto.request.SignupRequest;
import io.appswave.joiner.dto.response.ApiResponse;
import io.appswave.joiner.dto.response.LoginResponse;
import io.appswave.joiner.dto.response.LogoutResponse;
import io.appswave.joiner.dto.response.RegisterResponse;
import io.appswave.joiner.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody SignupRequest request,
            Locale locale) {
        RegisterResponse registerResponse = authService.register(request);
        String msg = messageSource.getMessage("register.success", null, "User registered successfully", locale);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(msg, registerResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            Locale locale) {
        LoginResponse loginResponse = authService.login(request, httpRequest);
        String msg = messageSource.getMessage("login.success", null, "Login successful", locale);
        return ResponseEntity.ok(ApiResponse.success(msg, loginResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<LogoutResponse>> logout(
            HttpSession session,
            Locale locale) {
        authService.logout(session);
        String msg = messageSource.getMessage("logout.success", null, "Logout successful", locale);
        LogoutResponse logoutResponse = LogoutResponse.builder()
                .success(true)
                .message(msg)
                .build();
        return ResponseEntity.ok(ApiResponse.success(msg, logoutResponse));
    }
}

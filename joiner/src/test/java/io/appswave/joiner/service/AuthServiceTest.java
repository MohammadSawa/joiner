package io.appswave.joiner.service;

import io.appswave.joiner.dto.request.LoginRequest;
import io.appswave.joiner.dto.request.SignupRequest;
import io.appswave.joiner.dto.response.LoginResponse;
import io.appswave.joiner.dto.response.RegisterResponse;
import io.appswave.joiner.entity.User;
import io.appswave.joiner.enums.UserRole;
import io.appswave.joiner.exception.EmailAlreadyExistsException;
import io.appswave.joiner.exception.UserNotFoundException;
import io.appswave.joiner.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setEmail("john@example.com");
        signupRequest.setPassword("password123");

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("john@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setUserRole(UserRole.USER);
    }

    @Test
    void testRegisterSuccess() {
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        RegisterResponse response = authService.register(signupRequest);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.getUserId());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("John", response.getFirstName());
        assertTrue(response.isSuccess());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterWithDuplicateEmail() {
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> {
            authService.register(signupRequest);
        });
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(testUser);

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(httpRequest.getSession(true)).thenReturn(session);

        
        LoginResponse response = authService.login(loginRequest, httpRequest);

        
        assertNotNull(response);
        assertEquals(testUser.getId(), response.getUserId());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("USER", response.getRole());
        assertTrue(response.isAuthenticated());
        verify(userRepository, times(1)).findByEmail(loginRequest.getEmail());
    }

    @Test
    void testLoginUserNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("password123");

        HttpServletRequest httpRequest = mock(HttpServletRequest.class);

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            authService.login(loginRequest, httpRequest);
        });
    }

    @Test
    void testLogoutSuccess() {
        HttpSession session = mock(HttpSession.class);
        authService.logout(session);
        verify(session, times(1)).invalidate();
    }

    @Test
    void testLogoutWithNullSession() {
        assertDoesNotThrow(() -> {
            authService.logout(null);
        });
    }
}
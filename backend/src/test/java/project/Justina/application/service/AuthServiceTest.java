package project.Justina.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.Justina.domain.dto.AuthResponseDTO;
import project.Justina.domain.exception.AuthException;
import project.Justina.domain.exception.UserAlreadyExistsException;
import project.Justina.domain.model.User;
import project.Justina.domain.repository.UserRepository;
import project.Justina.infrastructure.security.JwtService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios para AuthService")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private UUID testUserId;
    private String testUsername;
    private String testPassword;
    private String encodedPassword;
    private String testToken;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUsername = "surgeon1";
        testPassword = "password123";
        encodedPassword = "$2a$10$encodedPasswordHash";
        testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";

        testUser = new User(testUserId, testUsername, encodedPassword, "ROLE_SURGEON");
    }

    @Test
    @DisplayName("login - Éxito con credenciales válidas")
    void login_Success_WhenValidCredentials() {
        // Given
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(testPassword, encodedPassword)).thenReturn(true);
        when(jwtService.createToken(testUserId, testUsername)).thenReturn(testToken);

        // When
        AuthResponseDTO result = authService.login(testUsername, testPassword);

        // Then
        assertNotNull(result);
        assertEquals(testToken, result.token());
        assertEquals(testUserId, result.userId());
        assertEquals(testUsername, result.username());
        assertEquals("Login exitoso", result.message());

        verify(userRepository).findByUsername(testUsername);
        verify(passwordEncoder).matches(testPassword, encodedPassword);
        verify(jwtService).createToken(testUserId, testUsername);
    }

    @Test
    @DisplayName("login - Lanza excepción cuando usuario no existe")
    void login_ThrowsException_WhenUserNotFound() {
        // Given
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authService.login(testUsername, testPassword)
        );

        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(userRepository).findByUsername(testUsername);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).createToken(any(), anyString());
    }

    @Test
    @DisplayName("login - Lanza excepción cuando contraseña es inválida")
    void login_ThrowsException_WhenInvalidPassword() {
        // Given
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(testPassword, encodedPassword)).thenReturn(false);

        // When & Then
        AuthException exception = assertThrows(
                AuthException.class,
                () -> authService.login(testUsername, testPassword)
        );

        assertEquals("Credenciales inválidas", exception.getMessage());

        verify(userRepository).findByUsername(testUsername);
        verify(passwordEncoder).matches(testPassword, encodedPassword);
        verify(jwtService, never()).createToken(any(), anyString());
    }

    @Test
    @DisplayName("register - Éxito con nuevo usuario")
    void register_Success_WhenNewUser() {
        // Given
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);

        // When
        assertDoesNotThrow(() -> authService.register(testUsername, testPassword));

        // Then
        verify(userRepository).findByUsername(testUsername);
        verify(passwordEncoder).encode(testPassword);
        verify(userRepository).save(argThat(user -> 
            user.getUsername().equals(testUsername) &&
            user.getPassword().equals(encodedPassword) &&
            user.getRole().equals("ROLE_SURGEON")
        ));
    }

    @Test
    @DisplayName("register - Lanza excepción cuando usuario ya existe")
    void register_ThrowsException_WhenUserExists() {
        // Given
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // When & Then
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(testUsername, testPassword)
        );

        assertEquals("El usuario ya existe", exception.getMessage());

        verify(userRepository).findByUsername(testUsername);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("registerSystemUser - No hace nada cuando usuario ya existe")
    void registerSystemUser_DoesNothing_WhenUserExists() {
        // Given
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // When
        assertDoesNotThrow(() -> authService.registerSystemUser(testUsername, testPassword, "ROLE_ADMIN"));

        // Then
        verify(userRepository).findByUsername(testUsername);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("registerSystemUser - Crea usuario cuando no existe")
    void registerSystemUser_CreatesUser_WhenNotExists() {
        // Given
        String role = "ROLE_ADMIN";
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testPassword)).thenReturn(encodedPassword);

        // When
        assertDoesNotThrow(() -> authService.registerSystemUser(testUsername, testPassword, role));

        // Then
        verify(userRepository).findByUsername(testUsername);
        verify(passwordEncoder).encode(testPassword);
        verify(userRepository).save(argThat(user -> 
            user.getUsername().equals(testUsername) &&
            user.getPassword().equals(encodedPassword) &&
            user.getRole().equals(role)
        ));
    }
}

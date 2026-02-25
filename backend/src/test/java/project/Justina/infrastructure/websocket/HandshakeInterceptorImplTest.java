package project.Justina.infrastructure.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;
import project.Justina.infrastructure.security.JwtService;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios para HandshakeInterceptorImpl")
class HandshakeInterceptorImplTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private WebSocketHandler wsHandler;

    @InjectMocks
    private HandshakeInterceptorImpl handshakeInterceptor;

    private String validToken;
    private String invalidToken;
    private String username;
    private UUID userId;

    @BeforeEach
    void setUp() {
        username = "test_surgeon";
        userId = UUID.randomUUID();
        validToken = "valid.jwt.token";
        invalidToken = "invalid.token";
    }

    @Test
    @DisplayName("beforeHandshake - Éxito cuando el token es válido")
    void beforeHandshake_Success_WithValidToken() {
        // Given
        when(request.getURI()).thenReturn(java.net.URI.create("ws://localhost:8080/ws/simulation?token=" + validToken));
        when(jwtService.extractUsername(validToken)).thenReturn(username);
        when(jwtService.extractUserId(validToken)).thenReturn(userId);

        Map<String, Object> attributes = new java.util.HashMap<>();

        // When
        boolean result = handshakeInterceptor.beforeHandshake(request, response, wsHandler, attributes);

        // Then
        assertTrue(result);
        assertEquals(userId, attributes.get("SURGEON_ID"));
        assertEquals(username, attributes.get("USERNAME"));

        verify(jwtService).extractUsername(validToken);
        verify(jwtService).extractUserId(validToken);
    }

    @Test
    @DisplayName("beforeHandshake - Falla cuando no hay token en la URL")
    void beforeHandshake_Fails_WhenNoToken() {
        // Given
        when(request.getURI()).thenReturn(java.net.URI.create("ws://localhost:8080/ws/simulation"));

        Map<String, Object> attributes = new java.util.HashMap<>();

        // When
        boolean result = handshakeInterceptor.beforeHandshake(request, response, wsHandler, attributes);

        // Then
        assertFalse(result);
        assertTrue(attributes.isEmpty());

        verify(jwtService, never()).extractUsername(anyString());
        verify(jwtService, never()).extractUserId(anyString());
    }

    @Test
    @DisplayName("beforeHandshake - Falla cuando el token es inválido")
    void beforeHandshake_Fails_WhenInvalidToken() {
        // Given
        when(request.getURI()).thenReturn(java.net.URI.create("ws://localhost:8080/ws/simulation?token=" + invalidToken));
        when(jwtService.extractUsername(invalidToken)).thenThrow(new RuntimeException("Token inválido"));

        Map<String, Object> attributes = new java.util.HashMap<>();

        // When
        boolean result = handshakeInterceptor.beforeHandshake(request, response, wsHandler, attributes);

        // Then
        assertFalse(result);
        assertTrue(attributes.isEmpty());

        verify(jwtService).extractUsername(invalidToken);
        verify(jwtService, never()).extractUserId(anyString());
    }

    @Test
    @DisplayName("beforeHandshake - Falla cuando el token está vacío")
    void beforeHandshake_Fails_WhenEmptyToken() {
        // Given
        when(request.getURI()).thenReturn(java.net.URI.create("ws://localhost:8080/ws/simulation?token="));

        Map<String, Object> attributes = new java.util.HashMap<>();

        // When
        boolean result = handshakeInterceptor.beforeHandshake(request, response, wsHandler, attributes);

        // Then
        assertFalse(result);
        assertTrue(attributes.isEmpty());

        verify(jwtService, never()).extractUsername(anyString());
        verify(jwtService, never()).extractUserId(anyString());
    }

    @Test
    @DisplayName("afterHandshake - No lanza excepciones")
    void afterHandshake_DoesNotThrowExceptions() {
        // Given & When & Then
        assertDoesNotThrow(() -> {
            handshakeInterceptor.afterHandshake(request, response, wsHandler, null);
        });

        assertDoesNotThrow(() -> {
            handshakeInterceptor.afterHandshake(request, response, wsHandler, new RuntimeException("Test exception"));
        });
    }
}
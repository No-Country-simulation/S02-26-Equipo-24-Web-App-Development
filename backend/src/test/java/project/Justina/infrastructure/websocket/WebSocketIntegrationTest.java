package project.Justina.infrastructure.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import project.Justina.application.service.AuthService;
import project.Justina.domain.dto.TelemetryDTO;
import project.Justina.domain.model.SurgeryEvent;
import project.Justina.domain.model.SurgerySession;
import project.Justina.domain.repository.SurgeryRepository;
import project.Justina.infrastructure.security.JwtService;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Integración para WebSocket")
class WebSocketIntegrationTest {

    @Autowired
    private SimulationWebSocketHandler simulationWebSocketHandler;

    @Autowired
    private HandshakeInterceptorImpl handshakeInterceptor;

    @MockitoBean
    private SurgeryRepository surgeryRepository;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Mock
    private WebSocketSession session;

    private ObjectMapper objectMapper;
    private String testUsername;
    private String testPassword;
    private String validToken;
    private UUID testSurgeonId;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        testUsername = "integration_test_surgeon";
        testPassword = "password123";
        testSurgeonId = UUID.randomUUID();
        validToken = "mock.jwt.token";
    }

    @Test
    @DisplayName("WebSocket Handshake - Autenticación exitosa con token válido")
    void websocketHandshake_Success_WithValidToken() throws Exception {
        // Given
        URI uri = new URI("ws://localhost:8080/ws/simulation?token=" + validToken);
        org.springframework.http.server.ServerHttpRequest request = mock(org.springframework.http.server.ServerHttpRequest.class);
        when(request.getURI()).thenReturn(uri);
        
        // Mock JWT service behavior
        when(jwtService.extractUsername(validToken)).thenReturn(testUsername);
        when(jwtService.extractUserId(validToken)).thenReturn(testSurgeonId);

        // When
        boolean result = handshakeInterceptor.beforeHandshake(
            request,
            mock(org.springframework.http.server.ServerHttpResponse.class),
            mock(org.springframework.web.socket.WebSocketHandler.class),
            new java.util.HashMap<>()
        );

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("WebSocket Handshake - Falla con token inválido")
    void websocketHandshake_Fails_WithInvalidToken() throws Exception {
        // Given
        String invalidToken = "invalid.token";
        URI uri = new URI("ws://localhost:8080/ws/simulation?token=" + invalidToken);
        org.springframework.http.server.ServerHttpRequest request = mock(org.springframework.http.server.ServerHttpRequest.class);
        when(request.getURI()).thenReturn(uri);
        
        // Mock JWT service to throw exception for invalid token
        when(jwtService.extractUsername(invalidToken)).thenThrow(new RuntimeException("Invalid token"));
        
        // When
        boolean result = handshakeInterceptor.beforeHandshake(
            request,
            mock(org.springframework.http.server.ServerHttpResponse.class),
            mock(org.springframework.web.socket.WebSocketHandler.class),
            new java.util.HashMap<>()
        );

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("WebSocket Message Processing - Procesa mensaje de telemetría válido")
    void websocketMessageProcessing_ProcessesValidTelemetry() throws Exception {
        // Given
        TelemetryDTO telemetryDTO = new TelemetryDTO(
            new double[]{1.0, 2.0, 3.0},
            SurgeryEvent.START,
            System.currentTimeMillis()
        );

        String jsonMessage = objectMapper.writeValueAsString(telemetryDTO);
        TextMessage textMessage = new TextMessage(jsonMessage);

        // Mock session attributes
        java.util.Map<String, Object> attributes = new java.util.HashMap<>();
        attributes.put("SURGEON_ID", testSurgeonId);
        attributes.put("USERNAME", testUsername);
        
        when(session.getAttributes()).thenReturn(attributes);
        when(session.getId()).thenReturn("test-session-id");

        // When
        simulationWebSocketHandler.handleTextMessage(session, textMessage);

        // Then
        verify(session, never()).close(any());
        verify(surgeryRepository, never()).save(any());
    }

    @Test
    @DisplayName("WebSocket Message Processing - Persiste cirugía con evento FINISH")
    void websocketMessageProcessing_PersistsSurgery_WithFinishEvent() throws Exception {
        // Given
        TelemetryDTO finishDTO = new TelemetryDTO(
            new double[]{5.0, 6.0, 7.0},
            SurgeryEvent.FINISH,
            System.currentTimeMillis()
        );

        String jsonMessage = objectMapper.writeValueAsString(finishDTO);
        TextMessage textMessage = new TextMessage(jsonMessage);

        // Mock session attributes
        java.util.Map<String, Object> attributes = new java.util.HashMap<>();
        attributes.put("SURGEON_ID", testSurgeonId);
        attributes.put("USERNAME", testUsername);
        
        when(session.getAttributes()).thenReturn(attributes);
        when(session.getId()).thenReturn("test-session-id");

        // When
        simulationWebSocketHandler.handleTextMessage(session, textMessage);

        // Then
        verify(surgeryRepository).save(any(SurgerySession.class));
        verify(session).sendMessage(any(TextMessage.class));
    }

    @Test
    @DisplayName("WebSocket Message Processing - Cierra conexión con datos inválidos")
    void websocketMessageProcessing_ClosesConnection_WithInvalidData() throws Exception {
        // Given
        TelemetryDTO invalidDTO = new TelemetryDTO(
            new double[]{}, // coordenadas vacías
            null, // evento nulo
            -1L // timestamp negativo
        );

        String jsonMessage = objectMapper.writeValueAsString(invalidDTO);
        TextMessage textMessage = new TextMessage(jsonMessage);

        // When
        simulationWebSocketHandler.handleTextMessage(session, textMessage);

        // Then
        verify(session).close(org.springframework.web.socket.CloseStatus.BAD_DATA);
        verify(surgeryRepository, never()).save(any());
    }

    @Test
    @DisplayName("WebSocket Message Processing - Cierra conexión sin SURGEON_ID")
    void websocketMessageProcessing_ClosesConnection_WithoutSurgeonId() throws Exception {
        // Given
        TelemetryDTO telemetryDTO = new TelemetryDTO(
            new double[]{1.0, 2.0, 3.0},
            SurgeryEvent.START,
            System.currentTimeMillis()
        );

        String jsonMessage = objectMapper.writeValueAsString(telemetryDTO);
        TextMessage textMessage = new TextMessage(jsonMessage);

        // Mock session attributes sin SURGEON_ID
        java.util.Map<String, Object> attributes = new java.util.HashMap<>();
        attributes.put("USERNAME", testUsername);
        
        when(session.getAttributes()).thenReturn(attributes);

        // When
        simulationWebSocketHandler.handleTextMessage(session, textMessage);

        // Then
        verify(session).close(org.springframework.web.socket.CloseStatus.POLICY_VIOLATION);
        verify(surgeryRepository, never()).save(any());
    }

    @Test
    @DisplayName("WebSocket Session Management - Maneja múltiples sesiones concurrentes")
    void websocketSessionManagement_HandlesMultipleSessions() throws Exception {
        // Given
        TelemetryDTO startDTO = new TelemetryDTO(
            new double[]{1.0, 2.0, 3.0},
            SurgeryEvent.START,
            System.currentTimeMillis()
        );

        TelemetryDTO finishDTO = new TelemetryDTO(
            new double[]{5.0, 6.0, 7.0},
            SurgeryEvent.FINISH,
            System.currentTimeMillis() + 1000
        );

        String startJson = objectMapper.writeValueAsString(startDTO);
        String finishJson = objectMapper.writeValueAsString(finishDTO);

        // Mock session 1
        java.util.Map<String, Object> attributes1 = new java.util.HashMap<>();
        attributes1.put("SURGEON_ID", testSurgeonId);
        attributes1.put("USERNAME", testUsername);
        
        WebSocketSession session1 = mock(WebSocketSession.class);
        when(session1.getAttributes()).thenReturn(attributes1);
        when(session1.getId()).thenReturn("session-1");

        // Mock session 2
        UUID differentSurgeonId = UUID.randomUUID();
        java.util.Map<String, Object> attributes2 = new java.util.HashMap<>();
        attributes2.put("SURGEON_ID", differentSurgeonId);
        attributes2.put("USERNAME", "different_surgeon");
        
        WebSocketSession session2 = mock(WebSocketSession.class);
        when(session2.getAttributes()).thenReturn(attributes2);
        when(session2.getId()).thenReturn("session-2");

        // When
        simulationWebSocketHandler.handleTextMessage(session1, new TextMessage(startJson));
        simulationWebSocketHandler.handleTextMessage(session2, new TextMessage(startJson));
        simulationWebSocketHandler.handleTextMessage(session1, new TextMessage(finishJson));

        // Then
        verify(surgeryRepository, times(1)).save(any(SurgerySession.class));
        verify(session1).sendMessage(any(TextMessage.class));
        verify(session2, never()).sendMessage(any(TextMessage.class));
    }
}

package project.Justina.infrastructure.websocket;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import project.Justina.domain.dto.TelemetryDTO;
import project.Justina.domain.model.Movement;
import project.Justina.domain.model.SurgeryEvent;
import project.Justina.domain.model.SurgerySession;
import project.Justina.domain.repository.SurgeryRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios para SimulationWebSocketHandler")
class SimulationWebSocketHandlerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SurgeryRepository surgeryRepository;

    @Mock
    private Validator validator;

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private SimulationWebSocketHandler webSocketHandler;

    @Captor
    private ArgumentCaptor<TextMessage> messageCaptor;

    private String validToken;
    private UUID surgeonId;
    private TelemetryDTO validTelemetryDTO;
    private TelemetryDTO invalidTelemetryDTO;
    private Movement validMovement;
    private SurgerySession surgerySession;

    @BeforeEach
    void setUp() {
        surgeonId = UUID.randomUUID();
        validToken = "valid.jwt.token";

        // Crear DTOs de prueba
        validTelemetryDTO = new TelemetryDTO(
            new double[]{1.0, 2.0, 3.0},
            SurgeryEvent.START,
            System.currentTimeMillis()
        );

        invalidTelemetryDTO = new TelemetryDTO(
            new double[]{}, // coordenadas vacías para fallar validación
            null, // evento nulo para fallar validación
            -1L // timestamp negativo para fallar validación
        );

        // Crear movimiento de prueba
        validMovement = new Movement(
            new double[]{1.0, 2.0, 3.0},
            SurgeryEvent.START,
            System.currentTimeMillis()
        );

        // Crear sesión de cirugía de prueba
        surgerySession = new SurgerySession(surgeonId);
    }

    @Test
    @DisplayName("handleTextMessage - Procesa mensaje válido y agrega movimiento")
    void handleTextMessage_ProcessesValidMessage() throws Exception {
        // Given
        String validJson = "{\"coordinates\":[1.0,2.0,3.0],\"event\":\"START\",\"timestamp\":1234567890}";
        
        // Configurar atributos de sesión
        Map<String, Object> sessionAttributes = new ConcurrentHashMap<>();
        sessionAttributes.put("SURGEON_ID", surgeonId);
        sessionAttributes.put("USERNAME", "test_surgeon");
        when(session.getAttributes()).thenReturn(sessionAttributes);
        when(session.getId()).thenReturn("test-session-id");
        
        when(objectMapper.readValue(validJson, TelemetryDTO.class)).thenReturn(validTelemetryDTO);
        when(validator.validate(validTelemetryDTO)).thenReturn(java.util.Set.of());

        // When
        webSocketHandler.handleTextMessage(session, new TextMessage(validJson));

        // Then
        verify(objectMapper).readValue(validJson, TelemetryDTO.class);
        verify(validator).validate(validTelemetryDTO);
        verify(session, never()).close(any(CloseStatus.class));
        verify(surgeryRepository, never()).save(any(SurgerySession.class));
    }

    @Test
    @DisplayName("handleTextMessage - Cierra conexión con BAD_DATA cuando el DTO es inválido")
    void handleTextMessage_ClosesWithBadData_WhenDtoInvalid() throws Exception {
        // Given
        String invalidJson = "{\"coordinates\":[],\"event\":null,\"timestamp\":-1}";
        
        when(objectMapper.readValue(invalidJson, TelemetryDTO.class)).thenReturn(invalidTelemetryDTO);
        when(validator.validate(invalidTelemetryDTO)).thenReturn(java.util.Set.of(
            mock(jakarta.validation.ConstraintViolation.class)
        ));

        // When
        webSocketHandler.handleTextMessage(session, new TextMessage(invalidJson));

        // Then
        verify(objectMapper).readValue(invalidJson, TelemetryDTO.class);
        verify(validator).validate(invalidTelemetryDTO);
        verify(session).close(CloseStatus.BAD_DATA);
        verify(surgeryRepository, never()).save(any(SurgerySession.class));
    }

    @Test
    @DisplayName("handleTextMessage - Cierra conexión con POLICY_VIOLATION cuando no hay SURGEON_ID")
    void handleTextMessage_ClosesWithPolicyViolation_WhenNoSurgeonId() throws Exception {
        // Given
        Map<String, Object> emptyAttributes = new ConcurrentHashMap<>();
        when(session.getAttributes()).thenReturn(emptyAttributes);
        
        String validJson = "{\"coordinates\":[1.0,2.0,3.0],\"event\":\"START\",\"timestamp\":1234567890}";
        when(objectMapper.readValue(validJson, TelemetryDTO.class)).thenReturn(validTelemetryDTO);
        when(validator.validate(validTelemetryDTO)).thenReturn(java.util.Set.of());

        // When
        webSocketHandler.handleTextMessage(session, new TextMessage(validJson));

        // Then
        verify(objectMapper).readValue(validJson, TelemetryDTO.class);
        verify(validator).validate(validTelemetryDTO);
        verify(session).close(CloseStatus.POLICY_VIOLATION);
        verify(surgeryRepository, never()).save(any(SurgerySession.class));
    }

    @Test
    @DisplayName("handleTextMessage - Persiste cirugía cuando recibe evento FINISH")
    void handleTextMessage_PersistsSurgery_WhenFinishEvent() throws Exception {
        // Given
        TelemetryDTO finishTelemetryDTO = new TelemetryDTO(
            new double[]{5.0, 6.0, 7.0},
            SurgeryEvent.FINISH,
            System.currentTimeMillis()
        );

        String finishJson = "{\"coordinates\":[5.0,6.0,7.0],\"event\":\"FINISH\",\"timestamp\":1234567890}";
        
        // Configurar atributos de sesión
        Map<String, Object> sessionAttributes = new ConcurrentHashMap<>();
        sessionAttributes.put("SURGEON_ID", surgeonId);
        when(session.getAttributes()).thenReturn(sessionAttributes);
        when(session.getId()).thenReturn("test-session-id");
        
        when(objectMapper.readValue(finishJson, TelemetryDTO.class)).thenReturn(finishTelemetryDTO);
        when(validator.validate(finishTelemetryDTO)).thenReturn(java.util.Set.of());

        // When
        webSocketHandler.handleTextMessage(session, new TextMessage(finishJson));

        // Then
        verify(objectMapper).readValue(finishJson, TelemetryDTO.class);
        verify(validator).validate(finishTelemetryDTO);
        verify(surgeryRepository).save(any(SurgerySession.class));
        verify(session).sendMessage(messageCaptor.capture());
        
        TextMessage responseMessage = messageCaptor.getValue();
        assertNotNull(responseMessage);
        assertTrue(responseMessage.getPayload().contains("\"status\":\"SAVED\""));
        assertTrue(responseMessage.getPayload().contains("\"surgeryId\""));
    }

    @Test
    @DisplayName("handleTextMessage - No persiste cirugía para eventos diferentes de FINISH")
    void handleTextMessage_DoesNotPersist_WhenNotFinishEvent() throws Exception {
        // Given
        TelemetryDTO startTelemetryDTO = new TelemetryDTO(
            new double[]{1.0, 2.0, 3.0},
            SurgeryEvent.START,
            System.currentTimeMillis()
        );

        String startJson = "{\"coordinates\":[1.0,2.0,3.0],\"event\":\"START\",\"timestamp\":1234567890}";
        
        // Configurar atributos de sesión
        Map<String, Object> sessionAttributes = new ConcurrentHashMap<>();
        sessionAttributes.put("SURGEON_ID", surgeonId);
        when(session.getAttributes()).thenReturn(sessionAttributes);
        when(session.getId()).thenReturn("test-session-id");
        
        when(objectMapper.readValue(startJson, TelemetryDTO.class)).thenReturn(startTelemetryDTO);
        when(validator.validate(startTelemetryDTO)).thenReturn(java.util.Set.of());

        // When
        webSocketHandler.handleTextMessage(session, new TextMessage(startJson));

        // Then
        verify(objectMapper).readValue(startJson, TelemetryDTO.class);
        verify(validator).validate(startTelemetryDTO);
        verify(surgeryRepository, never()).save(any(SurgerySession.class));
        verify(session, never()).sendMessage(any(TextMessage.class));
    }

    @Test
    @DisplayName("handleTextMessage - Maneja múltiples mensajes en la misma sesión")
    void handleTextMessage_HandlesMultipleMessages() throws Exception {
        // Given
        TelemetryDTO startDTO = new TelemetryDTO(
            new double[]{1.0, 2.0, 3.0},
            SurgeryEvent.START,
            System.currentTimeMillis()
        );
        
        TelemetryDTO touchDTO = new TelemetryDTO(
            new double[]{1.5, 2.5, 3.5},
            SurgeryEvent.TUMOR_REMOVAL,
            System.currentTimeMillis() + 1000
        );

        // Configurar atributos de sesión
        Map<String, Object> sessionAttributes = new ConcurrentHashMap<>();
        sessionAttributes.put("SURGEON_ID", surgeonId);
        when(session.getAttributes()).thenReturn(sessionAttributes);
        when(session.getId()).thenReturn("test-session-id");

        when(objectMapper.readValue(anyString(), eq(TelemetryDTO.class)))
            .thenReturn(startDTO)
            .thenReturn(touchDTO);
        when(validator.validate(any(TelemetryDTO.class))).thenReturn(java.util.Set.of());

        // When
        webSocketHandler.handleTextMessage(session, new TextMessage("{\"test\":\"start\"}"));
        webSocketHandler.handleTextMessage(session, new TextMessage("{\"test\":\"touch\"}"));

        // Then
        verify(objectMapper, times(2)).readValue(anyString(), eq(TelemetryDTO.class));
        verify(validator, times(2)).validate(any(TelemetryDTO.class));
        verify(surgeryRepository, never()).save(any(SurgerySession.class));
    }

    @Test
    @DisplayName("handleTextMessage - Lanza excepción al intentar parsear JSON inválido")
    void handleTextMessage_ThrowsException_WhenInvalidJson() throws Exception {
        // Given
        String invalidJson = "invalid json";
        when(objectMapper.readValue(invalidJson, TelemetryDTO.class))
            .thenThrow(new RuntimeException("Invalid JSON"));

        // When & Then
        assertThrows(Exception.class, () -> {
            webSocketHandler.handleTextMessage(session, new TextMessage(invalidJson));
        });

        verify(objectMapper).readValue(invalidJson, TelemetryDTO.class);
        verify(session, never()).close(any(CloseStatus.class));
    }
}

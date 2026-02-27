package project.Justina.infrastructure.websocket;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import project.Justina.domain.dto.TelemetryDTO;
import project.Justina.domain.model.Movement;
import project.Justina.domain.model.SurgeryEvent;
import project.Justina.domain.model.SurgerySession;
import project.Justina.domain.repository.SurgeryRepository;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SimulationWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper; //Convertir JSON  a objetos
    private final SurgeryRepository surgeryRepository;
    private final Validator validator;

    // Memoria temporal: Llave = ID de sesión de WebSocket, Valor = Objeto de Dominio
    private final Map<String, SurgerySession> activeSessions = new ConcurrentHashMap<>();


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        // Recibir el DTO (La entrada técnica)
        TelemetryDTO dto = objectMapper.readValue(message.getPayload(), TelemetryDTO.class);

        Set<ConstraintViolation<TelemetryDTO>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        // Mapeo Manual: Convertir DTO a Movement (Objeto de Dominio)
        // Se mapea manualmente para controlar la traducción
        Movement movement = new Movement(
                dto.coordinates(),
                dto.event(),
                dto.timestamp()
        );

        // Obtener el ID del cirujano de los atributos (Puesto ahí por el Interceptor)
        UUID surgeonId = (UUID) session.getAttributes().get("SURGEON_ID");

        // VALIDACIÓN DE SEGURIDAD:
        if (surgeonId == null) {
            session.close(CloseStatus.POLICY_VIOLATION); // Cerramos la conexión por falta de identidad
            return;
        }

        // 4. Obtener o crear la sesión de cirugía usando el ID REAL
        SurgerySession surgery = activeSessions.computeIfAbsent(session.getId(), k -> {
            return new SurgerySession(surgeonId);
        });

        // Agregar el movimiento al modelo de dominio
        surgery.addMovement(movement);

        // Si llega el evento FINISH, disparamos la persistencia de la HU-02
        // 6. Finalización y persistencia
        if (movement.event() == SurgeryEvent.FINISH) {
            surgery.endSurgery();
            surgeryRepository.save(surgery);
            activeSessions.remove(session.getId());

            String response = String.format("{\"status\":\"SAVED\", \"surgeryId\":\"%s\"}", surgery.getId());
            session.sendMessage(new TextMessage(response));
        }
    }
}

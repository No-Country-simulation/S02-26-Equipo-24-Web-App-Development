package project.Justina.infrastructure.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;



import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class AIWebSocketHandler extends TextWebSocketHandler {

    // Mapa de sesiones conectadas de IA (solo debería haber 1)
    private static final ConcurrentHashMap<String, WebSocketSession> aiSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Verificar que sea la IA (rol ROLE_AI en el token)
        String role = (String) session.getAttributes().get("ROLE");

        if (role != null && role.equals("ROLE_AI")) {
            aiSessions.put(session.getId(), session);
            System.out.println("🤖 IA conectada: " + session.getId());

            // Enviar confirmación
            session.sendMessage(new TextMessage("{\"status\":\"connected\",\"message\":\"IA conectada exitosamente\"}"));
        } else {
            session.close(CloseStatus.POLICY_VIOLATION);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // La IA puede enviar mensajes si es necesario (por ahora no hace nada)
        System.out.println("📩 Mensaje de IA: " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        aiSessions.remove(session.getId());
        System.out.println("🤖 IA desconectada: " + session.getId());
    }

    // Método estático para notificar a la IA desde cualquier parte del backend
    public static void notificarNuevaCirugia(UUID surgeryId) {
        aiSessions.values().forEach(session -> {
            try {
                String mensaje = String.format(
                        "{\"event\":\"NEW_SURGERY\",\"surgeryId\":\"%s\"}",
                        surgeryId
                );
                session.sendMessage(new TextMessage(mensaje));
                System.out.println("🔔 Notificación enviada a IA: " + surgeryId);
            } catch (Exception e) {
                System.err.println("❌ Error notificando a IA: " + e.getMessage());
            }
        });
    }
}
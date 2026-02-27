package project.Justina.infrastructure.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final SimulationWebSocketHandler simulationHandler;
    private final HandshakeInterceptorImpl handshakeInterceptor;
    private final AIWebSocketHandler aiHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(simulationHandler, "/ws/simulation")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("*");

        registry.addHandler(aiHandler, "/ws/ai")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("*");
    }
}

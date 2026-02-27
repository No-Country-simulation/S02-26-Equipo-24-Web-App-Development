package project.Justina.infrastructure.websocket;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;
import project.Justina.infrastructure.security.JwtService;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HandshakeInterceptorImpl implements HandshakeInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        String token = UriComponentsBuilder
                .fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst("token");

        if (token == null || token.isEmpty()) {
            return false;
        }

        try {
            String username = jwtService.extractUsername(token);
            UUID userId = jwtService.extractUserId(token);

            attributes.put("SURGEON_ID", userId);
            attributes.put("USERNAME", username);

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {

    }
}

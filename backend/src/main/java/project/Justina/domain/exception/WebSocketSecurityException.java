package project.Justina.domain.exception;

import org.springframework.http.HttpStatus;

public class WebSocketSecurityException extends JustinaException {
    public WebSocketSecurityException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}

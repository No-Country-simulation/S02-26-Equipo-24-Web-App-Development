package project.Justina.domain.exception;

import org.springframework.http.HttpStatus;

public class AuthException extends JustinaException {
    public AuthException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}

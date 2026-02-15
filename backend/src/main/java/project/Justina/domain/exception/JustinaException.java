package project.Justina.domain.exception;

import org.springframework.http.HttpStatus;

public class JustinaException extends RuntimeException {
    private final HttpStatus status;
    public JustinaException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    public HttpStatus getStatus() { return status; }
}

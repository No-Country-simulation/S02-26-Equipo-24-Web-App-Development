package project.Justina.domain.exception;

import org.springframework.http.HttpStatus;

public class InvalidTelemetryException extends JustinaException {
    public InvalidTelemetryException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

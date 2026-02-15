package project.Justina.domain.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenActionException extends JustinaException {
    public ForbiddenActionException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}

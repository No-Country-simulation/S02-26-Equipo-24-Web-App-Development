package project.Justina.domain.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends JustinaException {
    public UserAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}

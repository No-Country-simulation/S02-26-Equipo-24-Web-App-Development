package project.Justina.domain.exception;

import org.springframework.http.HttpStatus;

public class SurgeryNotFoundException extends JustinaException {
    public SurgeryNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}

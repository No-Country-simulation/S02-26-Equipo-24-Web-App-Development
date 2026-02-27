package project.Justina.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Excepciones para JustinaException")
class JustinaExceptionTest {

    @Test
    @DisplayName("Constructor - Crea excepción con mensaje y status")
    void constructor_CreatesException_WithMessageAndStatus() {
        // Given
        String message = "Error de prueba";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // When
        JustinaException exception = new JustinaException(message, status);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(status, exception.getStatus());
    }

    @Test
    @DisplayName("Constructor - Crea excepción con mensaje nulo")
    void constructor_CreatesException_WithNullMessage() {
        // Given
        String message = null;
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // When
        JustinaException exception = new JustinaException(message, status);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertEquals(status, exception.getStatus());
    }

    @Test
    @DisplayName("Constructor - Crea excepción con status nulo")
    void constructor_CreatesException_WithNullStatus() {
        // Given
        String message = "Mensaje de prueba";
        HttpStatus status = null;

        // When
        JustinaException exception = new JustinaException(message, status);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getStatus());
    }

    @Test
    @DisplayName("Constructor - Crea excepción con mensaje vacío")
    void constructor_CreatesException_WithEmptyMessage() {
        // Given
        String message = "";
        HttpStatus status = HttpStatus.NOT_FOUND;

        // When
        JustinaException exception = new JustinaException(message, status);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(status, exception.getStatus());
    }

    @Test
    @DisplayName("getStatus - Retorna status correcto")
    void getStatus_ReturnsCorrectStatus() {
        // Given
        HttpStatus[] statuses = {
            HttpStatus.OK,
            HttpStatus.BAD_REQUEST,
            HttpStatus.UNAUTHORIZED,
            HttpStatus.FORBIDDEN,
            HttpStatus.NOT_FOUND,
            HttpStatus.INTERNAL_SERVER_ERROR,
            HttpStatus.CONFLICT
        };

        // When & Then - Probar todos los status
        for (HttpStatus status : statuses) {
            JustinaException exception = new JustinaException("Test message", status);
            assertEquals(status, exception.getStatus());
        }
    }

    @Test
    @DisplayName("getMessage - Hereda de RuntimeException")
    void getMessage_InheritsFromRuntimeException() {
        // Given
        String message = "Mensaje de error personalizado";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // When
        JustinaException exception = new JustinaException(message, status);

        // Then
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("getCause - Hereda comportamiento de RuntimeException")
    void getCause_InheritsRuntimeExceptionBehavior() {
        // Given
        String message = "Error con causa";
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Throwable cause = new IllegalArgumentException("Causa raíz");

        // When
        JustinaException exception = new JustinaException(message, status);
        JustinaException exceptionWithCause = new JustinaException(message, status);

        // Then - Verificamos que podemos establecer causa (comportamiento de RuntimeException)
        exception.initCause(cause);
        assertEquals(cause, exception.getCause());
        assertNull(exceptionWithCause.getCause()); // Sin causa por defecto
    }

    @Test
    @DisplayName("JustinaException - Es RuntimeException")
    void justinaException_IsRuntimeException() {
        // Given
        JustinaException exception = new JustinaException("Test", HttpStatus.BAD_REQUEST);

        // When & Then
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    @DisplayName("JustinaException - Verifica herencia")
    void justinaException_VerifiesInheritance() {
        // Given
        JustinaException exception = new JustinaException("Test", HttpStatus.BAD_REQUEST);

        // When & Then
        assertEquals(RuntimeException.class, exception.getClass().getSuperclass());
    }

    @Test
    @DisplayName("JustinaException - Verifica toString()")
    void justinaException_VerifiesToString() {
        // Given
        String message = "Mensaje de prueba";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // When
        JustinaException exception = new JustinaException(message, status);
        String toString = exception.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("JustinaException"));
        assertTrue(toString.contains(message));
    }

    @Test
    @DisplayName("JustinaException - Maneja diferentes HTTP status")
    void justinaException_HandlesDifferentHttpStatus() {
        // Given
        String message = "Error";

        // When & Then - Probar diferentes códigos de estado
        assertDoesNotThrow(() -> new JustinaException(message, HttpStatus.valueOf(200)));
        assertDoesNotThrow(() -> new JustinaException(message, HttpStatus.valueOf(400)));
        assertDoesNotThrow(() -> new JustinaException(message, HttpStatus.valueOf(401)));
        assertDoesNotThrow(() -> new JustinaException(message, HttpStatus.valueOf(403)));
        assertDoesNotThrow(() -> new JustinaException(message, HttpStatus.valueOf(404)));
        assertDoesNotThrow(() -> new JustinaException(message, HttpStatus.valueOf(500)));
    }

    @Test
    @DisplayName("JustinaException - Verifica inmutabilidad")
    void justinaException_VerifiesImmutability() {
        // Given
        String originalMessage = "Mensaje original";
        HttpStatus originalStatus = HttpStatus.BAD_REQUEST;
        JustinaException exception = new JustinaException(originalMessage, originalStatus);

        // When - Intentar modificar referencias externas
        String externalMessage = exception.getMessage();
        HttpStatus externalStatus = exception.getStatus();

        // Then - Los valores originales no deben cambiar
        assertEquals(originalMessage, externalMessage);
        assertEquals(originalStatus, externalStatus);
        assertEquals(originalMessage, exception.getMessage());
        assertEquals(originalStatus, exception.getStatus());
    }

    @Test
    @DisplayName("JustinaException - Crea múltiples instancias independientes")
    void justinaException_CreatesMultipleIndependentInstances() {
        // Given
        String firstMessage = "Primer error";
        String secondMessage = "Segundo error";
        HttpStatus firstStatus = HttpStatus.BAD_REQUEST;
        HttpStatus secondStatus = HttpStatus.NOT_FOUND;

        // When
        JustinaException firstException = new JustinaException(firstMessage, firstStatus);
        JustinaException secondException = new JustinaException(secondMessage, secondStatus);

        // Then
        assertNotEquals(firstException.getMessage(), secondException.getMessage());
        assertNotEquals(firstException.getStatus(), secondException.getStatus());
        assertEquals(firstMessage, firstException.getMessage());
        assertEquals(secondMessage, secondException.getMessage());
        assertEquals(firstStatus, firstException.getStatus());
        assertEquals(secondStatus, secondException.getStatus());
    }
}

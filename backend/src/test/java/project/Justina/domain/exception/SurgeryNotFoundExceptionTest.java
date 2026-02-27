package project.Justina.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Excepciones para SurgeryNotFoundException")
class SurgeryNotFoundExceptionTest {

    @Test
    @DisplayName("Constructor - Crea excepción con mensaje")
    void constructor_CreatesException_WithMessage() {
        // Given
        String message = "Cirugía no encontrada";

        // When
        SurgeryNotFoundException exception = new SurgeryNotFoundException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Constructor - Crea excepción con mensaje vacío")
    void constructor_CreatesException_WithEmptyMessage() {
        // Given
        String message = "";

        // When
        SurgeryNotFoundException exception = new SurgeryNotFoundException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("Constructor - Crea excepción con mensaje nulo")
    void constructor_CreatesException_WithNullMessage() {
        // Given
        String message = null;

        // When
        SurgeryNotFoundException exception = new SurgeryNotFoundException(message);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("getStatus - Retorna siempre NOT_FOUND")
    void getStatus_AlwaysReturnsNotFound() {
        // Given
        String[] messages = {
            "Cirugía no encontrada",
            "Surgery not found",
            "No se encontró la sesión quirúrgica",
            "Surgery session not found",
            "",
            null
        };

        // When & Then
        for (String message : messages) {
            SurgeryNotFoundException exception = new SurgeryNotFoundException(message);
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        }
    }

    @Test
    @DisplayName("SurgeryNotFoundException - Es JustinaException")
    void surgeryNotFoundException_IsJustinaException() {
        // Given
        SurgeryNotFoundException exception = new SurgeryNotFoundException("Test message");

        // When & Then
        assertTrue(exception instanceof JustinaException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    @DisplayName("SurgeryNotFoundException - Verifica herencia")
    void surgeryNotFoundException_VerifiesInheritance() {
        // Given
        SurgeryNotFoundException exception = new SurgeryNotFoundException("Test");

        // When & Then
        assertEquals(JustinaException.class, exception.getClass().getSuperclass());
    }

    @Test
    @DisplayName("SurgeryNotFoundException - Verifica toString()")
    void surgeryNotFoundException_VerifiesToString() {
        // Given
        String message = "La cirugía con ID 12345 no fue encontrada";

        // When
        SurgeryNotFoundException exception = new SurgeryNotFoundException(message);
        String toString = exception.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("SurgeryNotFoundException"));
        assertTrue(toString.contains(message));
    }

    @Test
    @DisplayName("SurgeryNotFoundException - Maneja mensajes con UUID")
    void surgeryNotFoundException_HandlesMessagesWithUUID() {
        // Given
        UUID surgeryId = UUID.randomUUID();
        String message = "La cirugía con id " + surgeryId + " no existe";

        // When
        SurgeryNotFoundException exception = new SurgeryNotFoundException(message);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains(surgeryId.toString()));
    }

    @Test
    @DisplayName("SurgeryNotFoundException - Maneja mensajes específicos")
    void surgeryNotFoundException_HandlesSpecificMessages() {
        // Given
        String[] specificMessages = {
            "La cirugía no fue encontrada",
            "Surgery not found",
            "No se encontró la sesión quirúrgica",
            "Surgery session not found",
            "La cirugía solicitada no existe",
            "The requested surgery does not exist"
        };

        // When & Then
        for (String message : specificMessages) {
            SurgeryNotFoundException exception = new SurgeryNotFoundException(message);
            assertEquals(message, exception.getMessage());
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        }
    }

    @Test
    @DisplayName("SurgeryNotFoundException - Verifica consistencia de status")
    void surgeryNotFoundException_VerifiesStatusConsistency() {
        // Given
        SurgeryNotFoundException exception = new SurgeryNotFoundException("Test");

        // When & Then - El status siempre debe ser NOT_FOUND (404)
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(404, exception.getStatus().value());
        assertEquals("NOT_FOUND", exception.getStatus().name());
    }

    @Test
    @DisplayName("SurgeryNotFoundException - Crea múltiples instancias independientes")
    void surgeryNotFoundException_CreatesMultipleIndependentInstances() {
        // Given
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        String firstMessage = "La cirugía con id " + firstId + " no existe";
        String secondMessage = "La cirugía con id " + secondId + " no fue encontrada";

        // When
        SurgeryNotFoundException firstException = new SurgeryNotFoundException(firstMessage);
        SurgeryNotFoundException secondException = new SurgeryNotFoundException(secondMessage);

        // Then
        assertNotEquals(firstException.getMessage(), secondException.getMessage());
        assertEquals(firstException.getStatus(), secondException.getStatus()); // Mismo status
        assertEquals(HttpStatus.NOT_FOUND, firstException.getStatus());
        assertEquals(HttpStatus.NOT_FOUND, secondException.getStatus());
    }

    @Test
    @DisplayName("SurgeryNotFoundException - Verifica comportamiento con causa")
    void surgeryNotFoundException_VerifiesCauseBehavior() {
        // Given
        String message = "Error de búsqueda de cirugía con causa";
        Throwable cause = new IllegalArgumentException("ID de cirugía inválido");

        // When
        SurgeryNotFoundException exception = new SurgeryNotFoundException(message);
        exception.initCause(cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("SurgeryNotFoundException - Casos de uso típicos")
    void surgeryNotFoundException_TypicalUseCases() {
        // Given & When & Then - Simulamos casos de uso reales
        
        // Caso 1: Búsqueda por UUID
        UUID surgeryId = UUID.randomUUID();
        SurgeryNotFoundException notFoundById = new SurgeryNotFoundException("La cirugía con id " + surgeryId + " no existe");
        assertEquals("La cirugía con id " + surgeryId + " no existe", notFoundById.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, notFoundById.getStatus());

        // Caso 2: Mensaje genérico
        SurgeryNotFoundException genericMessage = new SurgeryNotFoundException("Cirugía no encontrada");
        assertEquals("Cirugía no encontrada", genericMessage.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, genericMessage.getStatus());

        // Caso 3: Mensaje en inglés
        SurgeryNotFoundException englishMessage = new SurgeryNotFoundException("Surgery not found");
        assertEquals("Surgery not found", englishMessage.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, englishMessage.getStatus());

        // Caso 4: Mensaje detallado
        SurgeryNotFoundException detailedMessage = new SurgeryNotFoundException("No se encontró la sesión quirúrgica solicitada por el cirujano");
        assertEquals("No se encontró la sesión quirúrgica solicitada por el cirujano", detailedMessage.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, detailedMessage.getStatus());
    }

    @Test
    @DisplayName("SurgeryNotFoundException - Verifica inmutabilidad")
    void surgeryNotFoundException_VerifiesImmutability() {
        // Given
        String originalMessage = "Mensaje original de cirugía no encontrada";
        SurgeryNotFoundException exception = new SurgeryNotFoundException(originalMessage);

        // When - Intentar modificar referencias externas
        String externalMessage = exception.getMessage();
        HttpStatus externalStatus = exception.getStatus();

        // Then - Los valores originales no deben cambiar
        assertEquals(originalMessage, externalMessage);
        assertEquals(HttpStatus.NOT_FOUND, externalStatus);
        assertEquals(originalMessage, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    @DisplayName("SurgeryNotFoundException - Maneja diferentes formatos de ID")
    void surgeryNotFoundException_HandlesDifferentIdFormats() {
        // Given
        UUID uuid = UUID.randomUUID();
        Long longId = 12345L;
        String stringId = "SURG-2024-001";

        // When & Then
        SurgeryNotFoundException uuidException = new SurgeryNotFoundException("Cirugía " + uuid + " no encontrada");
        SurgeryNotFoundException longException = new SurgeryNotFoundException("Cirugía " + longId + " no encontrada");
        SurgeryNotFoundException stringException = new SurgeryNotFoundException("Cirugía " + stringId + " no encontrada");

        assertEquals(HttpStatus.NOT_FOUND, uuidException.getStatus());
        assertEquals(HttpStatus.NOT_FOUND, longException.getStatus());
        assertEquals(HttpStatus.NOT_FOUND, stringException.getStatus());

        assertTrue(uuidException.getMessage().contains(uuid.toString()));
        assertTrue(longException.getMessage().contains(longId.toString()));
        assertTrue(stringException.getMessage().contains(stringId));
    }

    @Test
    @DisplayName("SurgeryNotFoundException - Maneja caracteres especiales")
    void surgeryNotFoundException_HandlesSpecialCharacters() {
        // Given
        String[] specialMessages = {
            "La cirugía ñoño no fue encontrada",
            "L'intervention 'test' n'a pas été trouvée",
            "A cirurgia 'teste' não foi encontrada",
            "手术'test'未找到",
            "La cirugía con espacios 'test surgery' no encontrada",
            "La cirugía con símbolos !@#$%^&*() no encontrada"
        };

        // When & Then
        for (String message : specialMessages) {
            SurgeryNotFoundException exception = new SurgeryNotFoundException(message);
            assertEquals(message, exception.getMessage());
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        }
    }
}

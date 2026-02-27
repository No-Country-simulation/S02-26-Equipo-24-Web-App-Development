package project.Justina.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Excepciones para ForbiddenActionException")
class ForbiddenActionExceptionTest {

    @Test
    @DisplayName("Constructor - Crea excepción con mensaje")
    void constructor_CreatesException_WithMessage() {
        // Given
        String message = "Acceso denegado";

        // When
        ForbiddenActionException exception = new ForbiddenActionException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    @DisplayName("Constructor - Crea excepción con mensaje vacío")
    void constructor_CreatesException_WithEmptyMessage() {
        // Given
        String message = "";

        // When
        ForbiddenActionException exception = new ForbiddenActionException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    @DisplayName("Constructor - Crea excepción con mensaje nulo")
    void constructor_CreatesException_WithNullMessage() {
        // Given
        String message = null;

        // When
        ForbiddenActionException exception = new ForbiddenActionException(message);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    @DisplayName("getStatus - Retorna siempre FORBIDDEN")
    void getStatus_AlwaysReturnsForbidden() {
        // Given
        String[] messages = {
            "Acceso denegado",
            "Forbidden action",
            "No tienes permiso para acceder",
            "Access denied",
            "",
            null
        };

        // When & Then
        for (String message : messages) {
            ForbiddenActionException exception = new ForbiddenActionException(message);
            assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        }
    }

    @Test
    @DisplayName("ForbiddenActionException - Es JustinaException")
    void forbiddenActionException_IsJustinaException() {
        // Given
        ForbiddenActionException exception = new ForbiddenActionException("Test message");

        // When & Then
        assertTrue(exception instanceof JustinaException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    @DisplayName("ForbiddenActionException - Verifica herencia")
    void forbiddenActionException_VerifiesInheritance() {
        // Given
        ForbiddenActionException exception = new ForbiddenActionException("Test");

        // When & Then
        assertEquals(JustinaException.class, exception.getClass().getSuperclass());
    }

    @Test
    @DisplayName("ForbiddenActionException - Verifica toString()")
    void forbiddenActionException_VerifiesToString() {
        // Given
        String message = "No tienes permiso para acceder a esta cirugía";

        // When
        ForbiddenActionException exception = new ForbiddenActionException(message);
        String toString = exception.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("ForbiddenActionException"));
        assertTrue(toString.contains(message));
    }

    @Test
    @DisplayName("ForbiddenActionException - Maneja mensajes con UUID")
    void forbiddenActionException_HandlesMessagesWithUUID() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID surgeryId = UUID.randomUUID();
        String message = "El usuario " + userId + " no tiene permiso para acceder a la cirugía " + surgeryId;

        // When
        ForbiddenActionException exception = new ForbiddenActionException(message);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertTrue(exception.getMessage().contains(userId.toString()));
        assertTrue(exception.getMessage().contains(surgeryId.toString()));
    }

    @Test
    @DisplayName("ForbiddenActionException - Maneja mensajes específicos")
    void forbiddenActionException_HandlesSpecificMessages() {
        // Given
        String[] specificMessages = {
            "No tienes permiso para acceder a esta cirugía",
            "Access denied",
            "Forbidden action",
            "No tienes permiso para realizar esta acción",
            "Access forbidden",
            "You don't have permission to access this resource"
        };

        // When & Then
        for (String message : specificMessages) {
            ForbiddenActionException exception = new ForbiddenActionException(message);
            assertEquals(message, exception.getMessage());
            assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        }
    }

    @Test
    @DisplayName("ForbiddenActionException - Verifica consistencia de status")
    void forbiddenActionException_VerifiesStatusConsistency() {
        // Given
        ForbiddenActionException exception = new ForbiddenActionException("Test");

        // When & Then - El status siempre debe ser FORBIDDEN (403)
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals(403, exception.getStatus().value());
        assertEquals("FORBIDDEN", exception.getStatus().name());
    }

    @Test
    @DisplayName("ForbiddenActionException - Crea múltiples instancias independientes")
    void forbiddenActionException_CreatesMultipleIndependentInstances() {
        // Given
        UUID userId = UUID.randomUUID();
        UUID surgeryId = UUID.randomUUID();
        String firstMessage = "El usuario " + userId + " no puede acceder a la cirugía " + surgeryId;
        String secondMessage = "Acceso denegado al recurso solicitado";

        // When
        ForbiddenActionException firstException = new ForbiddenActionException(firstMessage);
        ForbiddenActionException secondException = new ForbiddenActionException(secondMessage);

        // Then
        assertNotEquals(firstException.getMessage(), secondException.getMessage());
        assertEquals(firstException.getStatus(), secondException.getStatus()); // Mismo status
        assertEquals(HttpStatus.FORBIDDEN, firstException.getStatus());
        assertEquals(HttpStatus.FORBIDDEN, secondException.getStatus());
    }

    @Test
    @DisplayName("ForbiddenActionException - Verifica comportamiento con causa")
    void forbiddenActionException_VerifiesCauseBehavior() {
        // Given
        String message = "Error de permisos con causa";
        Throwable cause = new SecurityException("Violación de seguridad");

        // When
        ForbiddenActionException exception = new ForbiddenActionException(message);
        exception.initCause(cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("ForbiddenActionException - Casos de uso típicos")
    void forbiddenActionException_TypicalUseCases() {
        // Given & When & Then - Simulamos casos de uso reales
        
        // Caso 1: Acceso a cirugía de otro usuario
        UUID userId = UUID.randomUUID();
        UUID surgeryId = UUID.randomUUID();
        ForbiddenActionException surgeryAccess = new ForbiddenActionException(
            "No tienes permiso para acceder a esta cirugía");
        assertEquals("No tienes permiso para acceder a esta cirugía", surgeryAccess.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, surgeryAccess.getStatus());

        // Caso 2: Acción no permitida
        ForbiddenActionException actionNotAllowed = new ForbiddenActionException(
            "No tienes permiso para realizar esta acción");
        assertEquals("No tienes permiso para realizar esta acción", actionNotAllowed.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, actionNotAllowed.getStatus());

        // Caso 3: Mensaje en inglés
        ForbiddenActionException englishMessage = new ForbiddenActionException(
            "Access denied to this resource");
        assertEquals("Access denied to this resource", englishMessage.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, englishMessage.getStatus());

        // Caso 4: Mensaje detallado con IDs
        ForbiddenActionException detailedMessage = new ForbiddenActionException(
            "El usuario " + userId + " no tiene permiso para acceder a la cirugía " + surgeryId);
        assertTrue(detailedMessage.getMessage().contains(userId.toString()));
        assertTrue(detailedMessage.getMessage().contains(surgeryId.toString()));
        assertEquals(HttpStatus.FORBIDDEN, detailedMessage.getStatus());
    }

    @Test
    @DisplayName("ForbiddenActionException - Verifica inmutabilidad")
    void forbiddenActionException_VerifiesImmutability() {
        // Given
        String originalMessage = "Mensaje original de acceso denegado";
        ForbiddenActionException exception = new ForbiddenActionException(originalMessage);

        // When - Intentar modificar referencias externas
        String externalMessage = exception.getMessage();
        HttpStatus externalStatus = exception.getStatus();

        // Then - Los valores originales no deben cambiar
        assertEquals(originalMessage, externalMessage);
        assertEquals(HttpStatus.FORBIDDEN, externalStatus);
        assertEquals(originalMessage, exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    @DisplayName("ForbiddenActionException - Maneja diferentes escenarios de seguridad")
    void forbiddenActionException_HandlesDifferentSecurityScenarios() {
        // Given
        String[] securityScenarios = {
            "No tienes permiso para acceder a esta cirugía",
            "El usuario no es el dueño de esta sesión",
            "Acceso denegado: rol insuficiente",
            "No puedes modificar datos de otro cirujano",
            "Forbidden: access to surgery denied",
            "Security violation: unauthorized access attempt"
        };

        // When & Then
        for (String scenario : securityScenarios) {
            ForbiddenActionException exception = new ForbiddenActionException(scenario);
            assertEquals(scenario, exception.getMessage());
            assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        }
    }

    @Test
    @DisplayName("ForbiddenActionException - Maneja caracteres especiales")
    void forbiddenActionException_HandlesSpecialCharacters() {
        // Given
        String[] specialMessages = {
            "No tienes permiso para acceder a la cirugía ñoño",
            "L'accès 'test' est interdit",
            "O acesso 'teste' é proibido",
            "访问'test'被禁止",
            "No tienes permiso para acceder con espacios 'test surgery'",
            "No tienes permiso con símbolos !@#$%^&*()"
        };

        // When & Then
        for (String message : specialMessages) {
            ForbiddenActionException exception = new ForbiddenActionException(message);
            assertEquals(message, exception.getMessage());
            assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        }
    }

    @Test
    @DisplayName("ForbiddenActionException - Verifica contexto de autorización")
    void forbiddenActionException_VerifiesAuthorizationContext() {
        // Given
        UUID surgeonId = UUID.randomUUID();
        UUID resourceOwnerId = UUID.randomUUID();
        String resourceType = "cirugía";

        // When
        ForbiddenActionException exception = new ForbiddenActionException(
            "El cirujano " + surgeonId + " no tiene permiso para acceder a la " + resourceType + " del usuario " + resourceOwnerId);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertTrue(exception.getMessage().contains(surgeonId.toString()));
        assertTrue(exception.getMessage().contains(resourceOwnerId.toString()));
        assertTrue(exception.getMessage().contains(resourceType));
    }
}

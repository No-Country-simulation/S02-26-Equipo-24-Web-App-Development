package project.Justina.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Excepciones para UserAlreadyExistsException")
class UserAlreadyExistsExceptionTest {

    @Test
    @DisplayName("Constructor - Crea excepción con mensaje")
    void constructor_CreatesException_WithMessage() {
        // Given
        String message = "El usuario ya existe";

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    @DisplayName("Constructor - Crea excepción con mensaje vacío")
    void constructor_CreatesException_WithEmptyMessage() {
        // Given
        String message = "";

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    @DisplayName("Constructor - Crea excepción con mensaje nulo")
    void constructor_CreatesException_WithNullMessage() {
        // Given
        String message = null;

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(message);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    @DisplayName("getStatus - Retorna siempre CONFLICT")
    void getStatus_AlwaysReturnsConflict() {
        // Given
        String[] messages = {
            "El usuario ya existe",
            "Username already taken",
            "Email already registered",
            "User account already exists",
            "",
            null
        };

        // When & Then
        for (String message : messages) {
            UserAlreadyExistsException exception = new UserAlreadyExistsException(message);
            assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        }
    }

    @Test
    @DisplayName("UserAlreadyExistsException - Es JustinaException")
    void userAlreadyExistsException_IsJustinaException() {
        // Given
        UserAlreadyExistsException exception = new UserAlreadyExistsException("Test message");

        // When & Then
        assertTrue(exception instanceof JustinaException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    @DisplayName("UserAlreadyExistsException - Verifica herencia")
    void userAlreadyExistsException_VerifiesInheritance() {
        // Given
        UserAlreadyExistsException exception = new UserAlreadyExistsException("Test");

        // When & Then
        assertEquals(JustinaException.class, exception.getClass().getSuperclass());
    }

    @Test
    @DisplayName("UserAlreadyExistsException - Verifica toString()")
    void userAlreadyExistsException_VerifiesToString() {
        // Given
        String message = "El usuario test@example.com ya está registrado";

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(message);
        String toString = exception.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("UserAlreadyExistsException"));
        assertTrue(toString.contains(message));
    }

    @Test
    @DisplayName("UserAlreadyExistsException - Maneja mensajes específicos")
    void userAlreadyExistsException_HandlesSpecificMessages() {
        // Given
        String[] specificMessages = {
            "El usuario ya existe",
            "El nombre de usuario ya está en uso",
            "El email ya está registrado",
            "Username already taken",
            "Email already registered",
            "User account already exists",
            "Duplicate user found"
        };

        // When & Then
        for (String message : specificMessages) {
            UserAlreadyExistsException exception = new UserAlreadyExistsException(message);
            assertEquals(message, exception.getMessage());
            assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        }
    }

    @Test
    @DisplayName("UserAlreadyExistsException - Verifica consistencia de status")
    void userAlreadyExistsException_VerifiesStatusConsistency() {
        // Given
        UserAlreadyExistsException exception = new UserAlreadyExistsException("Test");

        // When & Then - El status siempre debe ser CONFLICT (409)
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals(409, exception.getStatus().value());
        assertEquals("CONFLICT", exception.getStatus().name());
    }

    @Test
    @DisplayName("UserAlreadyExistsException - Crea múltiples instancias independientes")
    void userAlreadyExistsException_CreatesMultipleIndependentInstances() {
        // Given
        String firstMessage = "El usuario john_doe ya existe";
        String secondMessage = "El email test@example.com ya está registrado";

        // When
        UserAlreadyExistsException firstException = new UserAlreadyExistsException(firstMessage);
        UserAlreadyExistsException secondException = new UserAlreadyExistsException(secondMessage);

        // Then
        assertNotEquals(firstException.getMessage(), secondException.getMessage());
        assertEquals(firstException.getStatus(), secondException.getStatus()); // Mismo status
        assertEquals(HttpStatus.CONFLICT, firstException.getStatus());
        assertEquals(HttpStatus.CONFLICT, secondException.getStatus());
    }

    @Test
    @DisplayName("UserAlreadyExistsException - Verifica comportamiento con causa")
    void userAlreadyExistsException_VerifiesCauseBehavior() {
        // Given
        String message = "Error de usuario duplicado con causa";
        Throwable cause = new IllegalArgumentException("Violación de constraint unique");

        // When
        UserAlreadyExistsException exception = new UserAlreadyExistsException(message);
        exception.initCause(cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("UserAlreadyExistsException - Casos de uso típicos")
    void userAlreadyExistsException_TypicalUseCases() {
        // Given & When & Then - Simulamos casos de uso reales
        
        // Caso 1: Registro con username duplicado
        UserAlreadyExistsException duplicateUsername = new UserAlreadyExistsException("El nombre de usuario 'john_doe' ya está en uso");
        assertEquals("El nombre de usuario 'john_doe' ya está en uso", duplicateUsername.getMessage());
        assertEquals(HttpStatus.CONFLICT, duplicateUsername.getStatus());

        // Caso 2: Registro con email duplicado
        UserAlreadyExistsException duplicateEmail = new UserAlreadyExistsException("El email 'test@example.com' ya está registrado");
        assertEquals("El email 'test@example.com' ya está registrado", duplicateEmail.getMessage());
        assertEquals(HttpStatus.CONFLICT, duplicateEmail.getStatus());

        // Caso 3: Mensaje genérico
        UserAlreadyExistsException genericMessage = new UserAlreadyExistsException("El usuario ya existe");
        assertEquals("El usuario ya existe", genericMessage.getMessage());
        assertEquals(HttpStatus.CONFLICT, genericMessage.getStatus());

        // Caso 4: Mensaje en inglés
        UserAlreadyExistsException englishMessage = new UserAlreadyExistsException("User account already exists");
        assertEquals("User account already exists", englishMessage.getMessage());
        assertEquals(HttpStatus.CONFLICT, englishMessage.getStatus());
    }

    @Test
    @DisplayName("UserAlreadyExistsException - Verifica inmutabilidad")
    void userAlreadyExistsException_VerifiesImmutability() {
        // Given
        String originalMessage = "Mensaje original de usuario duplicado";
        UserAlreadyExistsException exception = new UserAlreadyExistsException(originalMessage);

        // When - Intentar modificar referencias externas
        String externalMessage = exception.getMessage();
        HttpStatus externalStatus = exception.getStatus();

        // Then - Los valores originales no deben cambiar
        assertEquals(originalMessage, externalMessage);
        assertEquals(HttpStatus.CONFLICT, externalStatus);
        assertEquals(originalMessage, exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
    }

    @Test
    @DisplayName("UserAlreadyExistsException - Maneja caracteres especiales")
    void userAlreadyExistsException_HandlesSpecialCharacters() {
        // Given
        String[] specialMessages = {
            "El usuario ñoño@example.com ya existe",
            "L'username 'test_user' déjà utilisé",
            "O usuário 'joão' já existe",
            "用户'test'已存在",
            "El usuario con espacios 'test user' ya existe",
            "El usuario con símbolos !@#$%^&*() ya existe"
        };

        // When & Then
        for (String message : specialMessages) {
            UserAlreadyExistsException exception = new UserAlreadyExistsException(message);
            assertEquals(message, exception.getMessage());
            assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        }
    }
}

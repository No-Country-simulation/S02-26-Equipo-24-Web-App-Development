package project.Justina.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Excepciones para AuthException")
class AuthExceptionTest {

    @Test
    @DisplayName("Constructor - Crea excepción con mensaje")
    void constructor_CreatesException_WithMessage() {
        // Given
        String message = "Credenciales inválidas";

        // When
        AuthException exception = new AuthException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    @DisplayName("Constructor - Crea excepción con mensaje vacío")
    void constructor_CreatesException_WithEmptyMessage() {
        // Given
        String message = "";

        // When
        AuthException exception = new AuthException(message);

        // Then
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    @DisplayName("Constructor - Crea excepción con mensaje nulo")
    void constructor_CreatesException_WithNullMessage() {
        // Given
        String message = null;

        // When
        AuthException exception = new AuthException(message);

        // Then
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    @DisplayName("getStatus - Retorna siempre UNAUTHORIZED")
    void getStatus_AlwaysReturnsUnauthorized() {
        // Given
        String[] messages = {
            "Usuario no encontrado",
            "Contraseña incorrecta",
            "Token expirado",
            "Acceso denegado",
            "",
            null
        };

        // When & Then
        for (String message : messages) {
            AuthException exception = new AuthException(message);
            assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        }
    }

    @Test
    @DisplayName("AuthException - Es JustinaException")
    void authException_IsJustinaException() {
        // Given
        AuthException exception = new AuthException("Test message");

        // When & Then
        assertTrue(exception instanceof JustinaException);
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    @DisplayName("AuthException - Verifica herencia")
    void authException_VerifiesInheritance() {
        // Given
        AuthException exception = new AuthException("Test");

        // When & Then
        assertEquals(JustinaException.class, exception.getClass().getSuperclass());
    }

    @Test
    @DisplayName("AuthException - Verifica toString()")
    void authException_VerifiesToString() {
        // Given
        String message = "Error de autenticación";

        // When
        AuthException exception = new AuthException(message);
        String toString = exception.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("AuthException"));
        assertTrue(toString.contains(message));
    }

    @Test
    @DisplayName("AuthException - Maneja mensajes especiales")
    void authException_HandlesSpecialMessages() {
        // Given
        String[] specialMessages = {
            "Usuario o contraseña incorrectos",
            "El token ha expirado",
            "Acceso no autorizado",
            "Credenciales inválidas",
            "Sesión no válida",
            "Autenticación fallida"
        };

        // When & Then
        for (String message : specialMessages) {
            AuthException exception = new AuthException(message);
            assertEquals(message, exception.getMessage());
            assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        }
    }

    @Test
    @DisplayName("AuthException - Verifica consistencia de status")
    void authException_VerifiesStatusConsistency() {
        // Given
        AuthException exception = new AuthException("Test");

        // When & Then - El status siempre debe ser UNAUTHORIZED (401)
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals(401, exception.getStatus().value());
        assertEquals("UNAUTHORIZED", exception.getStatus().name());
    }

    @Test
    @DisplayName("AuthException - Crea múltiples instancias independientes")
    void authException_CreatesMultipleIndependentInstances() {
        // Given
        String firstMessage = "Primer error de auth";
        String secondMessage = "Segundo error de auth";

        // When
        AuthException firstException = new AuthException(firstMessage);
        AuthException secondException = new AuthException(secondMessage);

        // Then
        assertNotEquals(firstException.getMessage(), secondException.getMessage());
        assertEquals(firstException.getStatus(), secondException.getStatus()); // Mismo status
        assertEquals(HttpStatus.UNAUTHORIZED, firstException.getStatus());
        assertEquals(HttpStatus.UNAUTHORIZED, secondException.getStatus());
    }

    @Test
    @DisplayName("AuthException - Verifica comportamiento con causa")
    void authException_VerifiesCauseBehavior() {
        // Given
        String message = "Error de autenticación con causa";
        Throwable cause = new IllegalArgumentException("Causa raíz del error");

        // When
        AuthException exception = new AuthException(message);
        exception.initCause(cause);

        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("AuthException - Casos de uso típicos")
    void authException_TypicalUseCases() {
        // Given & When & Then - Simulamos casos de uso reales
        
        // Caso 1: Login fallido
        AuthException loginFailed = new AuthException("Usuario o contraseña incorrectos");
        assertEquals("Usuario o contraseña incorrectos", loginFailed.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, loginFailed.getStatus());

        // Caso 2: Token inválido
        AuthException invalidToken = new AuthException("Token de autenticación inválido");
        assertEquals("Token de autenticación inválido", invalidToken.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, invalidToken.getStatus());

        // Caso 3: Sesión expirada
        AuthException sessionExpired = new AuthException("La sesión ha expirado");
        assertEquals("La sesión ha expirado", sessionExpired.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, sessionExpired.getStatus());

        // Caso 4: Acceso denegado
        AuthException accessDenied = new AuthException("Acceso denegado al recurso");
        assertEquals("Acceso denegado al recurso", accessDenied.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, accessDenied.getStatus());
    }

    @Test
    @DisplayName("AuthException - Verifica inmutabilidad")
    void authException_VerifiesImmutability() {
        // Given
        String originalMessage = "Mensaje original de auth";
        AuthException exception = new AuthException(originalMessage);

        // When - Intentar modificar referencias externas
        String externalMessage = exception.getMessage();
        HttpStatus externalStatus = exception.getStatus();

        // Then - Los valores originales no deben cambiar
        assertEquals(originalMessage, externalMessage);
        assertEquals(HttpStatus.UNAUTHORIZED, externalStatus);
        assertEquals(originalMessage, exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }
}

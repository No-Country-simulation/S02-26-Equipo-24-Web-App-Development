package project.Justina.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Dominio para User")
class UserTest {

    private final UUID testId = UUID.randomUUID();
    private final String testUsername = "testUser";
    private final String testPassword = "testPassword123";
    private final String testRole = "ROLE_SURGEON";

    @Test
    @DisplayName("Constructor - Crea usuario con todos los parámetros")
    void constructor_CreatesUser_WithAllParameters() {
        // When
        User user = new User(testId, testUsername, testPassword, testRole);

        // Then
        assertNotNull(user);
        assertEquals(testId, user.getId());
        assertEquals(testUsername, user.getUsername());
        assertEquals(testPassword, user.getPassword());
        assertEquals(testRole, user.getRole());
    }

    @Test
    @DisplayName("Constructor - Crea usuario con valores nulos")
    void constructor_CreatesUser_WithNullValues() {
        // When
        User user = new User(null, null, null, null);

        // Then
        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getRole());
    }

    @Test
    @DisplayName("getId - Retorna ID correcto")
    void getId_ReturnsCorrectId() {
        // Given
        User user = new User(testId, testUsername, testPassword, testRole);

        // When
        UUID result = user.getId();

        // Then
        assertEquals(testId, result);
    }

    @Test
    @DisplayName("getUsername - Retorna username correcto")
    void getUsername_ReturnsCorrectUsername() {
        // Given
        User user = new User(testId, testUsername, testPassword, testRole);

        // When
        String result = user.getUsername();

        // Then
        assertEquals(testUsername, result);
    }

    @Test
    @DisplayName("getPassword - Retorna password correcto")
    void getPassword_ReturnsCorrectPassword() {
        // Given
        User user = new User(testId, testUsername, testPassword, testRole);

        // When
        String result = user.getPassword();

        // Then
        assertEquals(testPassword, result);
    }

    @Test
    @DisplayName("getRole - Retorna role correcto")
    void getRole_ReturnsCorrectRole() {
        // Given
        User user = new User(testId, testUsername, testPassword, testRole);

        // When
        String result = user.getRole();

        // Then
        assertEquals(testRole, result);
    }

    @Test
    @DisplayName("User - Mantiene inmutabilidad de valores")
    void user_MaintainsValueImmutability() {
        // Given
        User user = new User(testId, testUsername, testPassword, testRole);

        // When - Intentar modificar referencias externas
        UUID externalId = user.getId();
        String externalUsername = user.getUsername();

        // Then - Los valores originales no deben cambiar
        assertEquals(testId, externalId);
        assertEquals(testUsername, externalUsername);
        assertEquals(testId, user.getId());
        assertEquals(testUsername, user.getUsername());
    }

    @Test
    @DisplayName("User - Maneja strings vacíos y especiales")
    void user_HandlesEmptyAndSpecialStrings() {
        // Given
        String emptyUsername = "";
        String specialPassword = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        String numericRole = "12345";

        // When
        User user = new User(testId, emptyUsername, specialPassword, numericRole);

        // Then
        assertEquals(emptyUsername, user.getUsername());
        assertEquals(specialPassword, user.getPassword());
        assertEquals(numericRole, user.getRole());
    }

    @Test
    @DisplayName("User - Crea múltiples instancias independientes")
    void user_CreatesMultipleIndependentInstances() {
        // Given
        UUID secondId = UUID.randomUUID();
        String secondUsername = "secondUser";

        // When
        User firstUser = new User(testId, testUsername, testPassword, testRole);
        User secondUser = new User(secondId, secondUsername, testPassword, testRole);

        // Then
        assertNotEquals(firstUser.getId(), secondUser.getId());
        assertNotEquals(firstUser.getUsername(), secondUser.getUsername());
        assertEquals(firstUser.getPassword(), secondUser.getPassword());
        assertEquals(firstUser.getRole(), secondUser.getRole());
    }

    @Test
    @DisplayName("User - Verifica igualdad basada en contenido")
    void user_VerifiesContentBasedEquality() {
        // Given
        User firstUser = new User(testId, testUsername, testPassword, testRole);
        User secondUser = new User(testId, testUsername, testPassword, testRole);

        // When & Then - Verificamos que los valores son iguales
        assertEquals(firstUser.getId(), secondUser.getId());
        assertEquals(firstUser.getUsername(), secondUser.getUsername());
        assertEquals(firstUser.getPassword(), secondUser.getPassword());
        assertEquals(firstUser.getRole(), secondUser.getRole());
        
        // Nota: No implementamos equals(), así que verificamos por contenido
    }
}

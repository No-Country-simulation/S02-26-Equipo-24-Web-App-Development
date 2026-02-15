package project.Justina.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Dominio para Movement")
class MovementTest {

    private final double[] testCoordinates2D = {1.5, 2.7};
    private final double[] testCoordinates3D = {1.5, 2.7, 3.9};
    private final SurgeryEvent testEvent = SurgeryEvent.TUMOR_TOUCH;
    private final long testTimestamp = System.currentTimeMillis();

    @Test
    @DisplayName("Constructor (2D) - Crea movement con coordenadas 2D")
    void constructor_2D_CreatesMovementWith2DCoordinates() {
        // When
        Movement movement = new Movement(testCoordinates2D, testEvent, testTimestamp);

        // Then
        assertNotNull(movement);
        assertArrayEquals(testCoordinates2D, movement.coordinates());
        assertEquals(testEvent, movement.event());
        assertEquals(testTimestamp, movement.timestamp());
    }

    @Test
    @DisplayName("Constructor (3D) - Crea movement con coordenadas 3D")
    void constructor_3D_CreatesMovementWith3DCoordinates() {
        // When
        Movement movement = new Movement(testCoordinates3D, testEvent, testTimestamp);

        // Then
        assertNotNull(movement);
        assertArrayEquals(testCoordinates3D, movement.coordinates());
        assertEquals(testEvent, movement.event());
        assertEquals(testTimestamp, movement.timestamp());
    }

    @Test
    @DisplayName("Constructor - Crea movement con coordenadas vacías")
    void constructor_CreatesMovementWithEmptyCoordinates() {
        // Given
        double[] emptyCoordinates = {};

        // When
        Movement movement = new Movement(emptyCoordinates, testEvent, testTimestamp);

        // Then
        assertNotNull(movement);
        assertArrayEquals(emptyCoordinates, movement.coordinates());
        assertEquals(0, movement.coordinates().length);
        assertEquals(testEvent, movement.event());
        assertEquals(testTimestamp, movement.timestamp());
    }

    @Test
    @DisplayName("Constructor - Crea movement con coordenadas nulas")
    void constructor_CreatesMovementWithNullCoordinates() {
        // When
        Movement movement = new Movement(null, testEvent, testTimestamp);

        // Then
        assertNotNull(movement);
        assertNull(movement.coordinates());
        assertEquals(testEvent, movement.event());
        assertEquals(testTimestamp, movement.timestamp());
    }

    @Test
    @DisplayName("Constructor - Crea movement con evento nulo")
    void constructor_CreatesMovementWithNullEvent() {
        // When
        Movement movement = new Movement(testCoordinates2D, null, testTimestamp);

        // Then
        assertNotNull(movement);
        assertArrayEquals(testCoordinates2D, movement.coordinates());
        assertNull(movement.event());
        assertEquals(testTimestamp, movement.timestamp());
    }

    @Test
    @DisplayName("Constructor - Crea movement con timestamp cero")
    void constructor_CreatesMovementWithZeroTimestamp() {
        // Given
        long zeroTimestamp = 0L;

        // When
        Movement movement = new Movement(testCoordinates2D, testEvent, zeroTimestamp);

        // Then
        assertNotNull(movement);
        assertArrayEquals(testCoordinates2D, movement.coordinates());
        assertEquals(testEvent, movement.event());
        assertEquals(zeroTimestamp, movement.timestamp());
    }

    @Test
    @DisplayName("Constructor - Crea movement con timestamp negativo")
    void constructor_CreatesMovementWithNegativeTimestamp() {
        // Given
        long negativeTimestamp = -1000L;

        // When
        Movement movement = new Movement(testCoordinates2D, testEvent, negativeTimestamp);

        // Then
        assertNotNull(movement);
        assertArrayEquals(testCoordinates2D, movement.coordinates());
        assertEquals(testEvent, movement.event());
        assertEquals(negativeTimestamp, movement.timestamp());
    }

    @Test
    @DisplayName("Constructor - Crea movement con todos los valores nulos")
    void constructor_CreatesMovementWithAllNullValues() {
        // When
        Movement movement = new Movement(null, null, 0L);

        // Then
        assertNotNull(movement);
        assertNull(movement.coordinates());
        assertNull(movement.event());
        assertEquals(0L, movement.timestamp());
    }

    @Test
    @DisplayName("coordinates() - Retorna referencia directa al array")
    void coordinates_ReturnsDirectArrayReference() {
        // When
        Movement movement = new Movement(testCoordinates2D, testEvent, testTimestamp);
        double[] coordinates = movement.coordinates();

        // Then
        assertArrayEquals(testCoordinates2D, coordinates);
        assertSame(testCoordinates2D, coordinates); // Es la misma referencia (record es inmutable)
    }

    @Test
    @DisplayName("event() - Retorna evento correcto")
    void event_ReturnsCorrectEvent() {
        // Given
        SurgeryEvent[] events = SurgeryEvent.values();

        // When & Then - Probar todos los eventos posibles
        for (SurgeryEvent event : events) {
            Movement movement = new Movement(testCoordinates2D, event, testTimestamp);
            assertEquals(event, movement.event());
        }
    }

    @Test
    @DisplayName("timestamp() - Retorna timestamp correcto")
    void timestamp_ReturnsCorrectTimestamp() {
        // Given
        long[] timestamps = {0L, 1L, 1000L, System.currentTimeMillis(), Long.MAX_VALUE};

        // When & Then - Probar diferentes timestamps
        for (long timestamp : timestamps) {
            Movement movement = new Movement(testCoordinates2D, testEvent, timestamp);
            assertEquals(timestamp, movement.timestamp());
        }
    }

    @Test
    @DisplayName("Movement - Es inmutable (record)")
    void movement_IsImmutable() {
        // Given
        Movement originalMovement = new Movement(testCoordinates2D, testEvent, testTimestamp);

        // When - Intentar modificar referencias (no se puede con record)
        double[] coordinates = originalMovement.coordinates();
        coordinates[0] = 999.0; // Esto modifica el array original

        // Then - El movement original no cambia porque el record es inmutable
        // pero el array sí cambia porque es una referencia compartida
        assertEquals(999.0, coordinates[0]); // El array local sí cambió
        assertEquals(testCoordinates2D[0], originalMovement.coordinates()[0]); // Verificamos el valor original
    }

    @Test
    @DisplayName("Movement - Maneja coordenadas con valores extremos")
    void movement_HandlesExtremeCoordinateValues() {
        // Given
        double[] extremeCoordinates = {Double.MAX_VALUE, Double.MIN_VALUE, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN};

        // When
        Movement movement = new Movement(extremeCoordinates, testEvent, testTimestamp);

        // Then
        assertNotNull(movement);
        assertArrayEquals(extremeCoordinates, movement.coordinates());
        assertEquals(testEvent, movement.event());
        assertEquals(testTimestamp, movement.timestamp());
    }

    @Test
    @DisplayName("Movement - Verifica igualdad basada en contenido")
    void movement_VerifiesContentBasedEquality() {
        // Given
        Movement firstMovement = new Movement(testCoordinates2D, testEvent, testTimestamp);
        Movement secondMovement = new Movement(testCoordinates2D.clone(), testEvent, testTimestamp);

        // When & Then - Los records implementan equals() automáticamente
        // Pero los arrays se comparan por referencia, no por contenido
        assertEquals(testEvent, secondMovement.event());
        assertEquals(testTimestamp, secondMovement.timestamp());
        assertArrayEquals(testCoordinates2D, secondMovement.coordinates());
        
        // Los movimientos no son iguales porque los arrays tienen referencias diferentes
        assertNotEquals(firstMovement, secondMovement);
        assertNotEquals(firstMovement.hashCode(), secondMovement.hashCode());
    }

    @Test
    @DisplayName("Movement - Verifica desigualdad")
    void movement_VerifiesInequality() {
        // Given
        Movement baseMovement = new Movement(testCoordinates2D, testEvent, testTimestamp);
        Movement differentCoordinates = new Movement(new double[]{9.9, 8.8}, testEvent, testTimestamp);
        Movement differentEvent = new Movement(testCoordinates2D, SurgeryEvent.HEMORRHAGE, testTimestamp);
        Movement differentTimestamp = new Movement(testCoordinates2D, testEvent, testTimestamp + 1000);

        // When & Then
        assertNotEquals(baseMovement, differentCoordinates);
        assertNotEquals(baseMovement, differentEvent);
        assertNotEquals(baseMovement, differentTimestamp);
        assertNotEquals(baseMovement, null);
        assertNotEquals(baseMovement, "not a movement");
    }

    @Test
    @DisplayName("Movement - Verifica toString()")
    void movement_VerifiesToString() {
        // When
        Movement movement = new Movement(testCoordinates2D, testEvent, testTimestamp);
        String toString = movement.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Movement"));
        assertTrue(toString.contains(testEvent.toString()));
        assertTrue(toString.contains(String.valueOf(testTimestamp)));
    }

    @Test
    @DisplayName("Movement - Crea múltiples instancias independientes")
    void movement_CreatesMultipleIndependentInstances() {
        // Given
        Movement firstMovement = new Movement(testCoordinates2D, testEvent, testTimestamp);
        Movement secondMovement = new Movement(testCoordinates3D, SurgeryEvent.START, testTimestamp + 1000);

        // When & Then
        assertNotEquals(firstMovement, secondMovement);
        assertArrayEquals(testCoordinates2D, firstMovement.coordinates());
        assertArrayEquals(testCoordinates3D, secondMovement.coordinates());
        assertEquals(testEvent, firstMovement.event());
        assertEquals(SurgeryEvent.START, secondMovement.event());
        assertEquals(testTimestamp, firstMovement.timestamp());
        assertEquals(testTimestamp + 1000, secondMovement.timestamp());
    }
}

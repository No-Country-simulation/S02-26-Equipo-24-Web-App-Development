package project.Justina.domain.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.Justina.domain.model.SurgeryEvent;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests Unitarios para TelemetryDTO")
class TelemetryDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("TelemetryDTO - Creación válida con coordenadas 2D")
    void telemetryDTO_ValidCreation_With2DCoordinates() {
        // Given
        double[] coordinates = {1.0, 2.0};
        SurgeryEvent event = SurgeryEvent.START;
        long timestamp = System.currentTimeMillis();

        // When
        TelemetryDTO dto = new TelemetryDTO(coordinates, event, timestamp);

        // Then
        assertArrayEquals(coordinates, dto.coordinates());
        assertEquals(event, dto.event());
        assertEquals(timestamp, dto.timestamp());
    }

    @Test
    @DisplayName("TelemetryDTO - Creación válida con coordenadas 3D")
    void telemetryDTO_ValidCreation_With3DCoordinates() {
        // Given
        double[] coordinates = {1.0, 2.0, 3.0};
        SurgeryEvent event = SurgeryEvent.TUMOR_REMOVAL;
        long timestamp = System.currentTimeMillis();

        // When
        TelemetryDTO dto = new TelemetryDTO(coordinates, event, timestamp);

        // Then
        assertArrayEquals(coordinates, dto.coordinates());
        assertEquals(event, dto.event());
        assertEquals(timestamp, dto.timestamp());
    }

    @Test
    @DisplayName("TelemetryDTO - Validación falla con coordenadas nulas")
    void telemetryDTO_ValidationFails_WithNullCoordinates() {
        // Given
        TelemetryDTO dto = new TelemetryDTO(null, SurgeryEvent.START, System.currentTimeMillis());

        // When
        Set<ConstraintViolation<TelemetryDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
        
        ConstraintViolation<TelemetryDTO> violation = violations.iterator().next();
        assertEquals("coordinates", violation.getPropertyPath().toString());
        assertEquals("Las coordenadas son obligatorias", violation.getMessage());
    }

    @Test
    @DisplayName("TelemetryDTO - Validación falla con coordenadas vacías")
    void telemetryDTO_ValidationFails_WithEmptyCoordinates() {
        // Given
        double[] coordinates = {};
        TelemetryDTO dto = new TelemetryDTO(coordinates, SurgeryEvent.START, System.currentTimeMillis());

        // When
        Set<ConstraintViolation<TelemetryDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size());
        
        ConstraintViolation<TelemetryDTO> violation = violations.iterator().next();
        assertEquals("coordinates", violation.getPropertyPath().toString());
        assertEquals("Coordinates debe tener 2 o 3 valores", violation.getMessage());
    }

    @Test
    @DisplayName("TelemetryDTO - Validación falla con coordenadas de longitud 1")
    void telemetryDTO_ValidationFails_WithSingleCoordinate() {
        // Given
        double[] coordinates = {1.0};
        TelemetryDTO dto = new TelemetryDTO(coordinates, SurgeryEvent.START, System.currentTimeMillis());

        // When
        Set<ConstraintViolation<TelemetryDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
        ConstraintViolation<TelemetryDTO> violation = violations.iterator().next();
        assertEquals("coordinates", violation.getPropertyPath().toString());
        assertEquals("Coordinates debe tener 2 o 3 valores", violation.getMessage());
    }

    @Test
    @DisplayName("TelemetryDTO - Validación falla con coordenadas de longitud 4")
    void telemetryDTO_ValidationFails_WithFourCoordinates() {
        // Given
        double[] coordinates = {1.0, 2.0, 3.0, 4.0};
        TelemetryDTO dto = new TelemetryDTO(coordinates, SurgeryEvent.START, System.currentTimeMillis());

        // When
        Set<ConstraintViolation<TelemetryDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
        ConstraintViolation<TelemetryDTO> violation = violations.iterator().next();
        assertEquals("coordinates", violation.getPropertyPath().toString());
        assertEquals("Coordinates debe tener 2 o 3 valores", violation.getMessage());
    }

    @Test
    @DisplayName("TelemetryDTO - Validación falla con evento nulo")
    void telemetryDTO_ValidationFails_WithNullEvent() {
        // Given
        double[] coordinates = {1.0, 2.0};
        TelemetryDTO dto = new TelemetryDTO(coordinates, null, System.currentTimeMillis());

        // When
        Set<ConstraintViolation<TelemetryDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
        ConstraintViolation<TelemetryDTO> violation = violations.iterator().next();
        assertEquals("event", violation.getPropertyPath().toString());
        assertEquals("El evento es obligatorio", violation.getMessage());
    }

    @Test
    @DisplayName("TelemetryDTO - Validación falla con timestamp negativo")
    void telemetryDTO_ValidationFails_WithNegativeTimestamp() {
        // Given
        double[] coordinates = {1.0, 2.0};
        TelemetryDTO dto = new TelemetryDTO(coordinates, SurgeryEvent.START, -1L);

        // When
        Set<ConstraintViolation<TelemetryDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
        ConstraintViolation<TelemetryDTO> violation = violations.iterator().next();
        assertEquals("timestamp", violation.getPropertyPath().toString());
        assertEquals("El timestamp debe ser positivo", violation.getMessage());
    }

    @Test
    @DisplayName("TelemetryDTO - Validación falla con timestamp cero")
    void telemetryDTO_ValidationFails_WithZeroTimestamp() {
        // Given
        double[] coordinates = {1.0, 2.0};
        TelemetryDTO dto = new TelemetryDTO(coordinates, SurgeryEvent.START, 0L);

        // When
        Set<ConstraintViolation<TelemetryDTO>> violations = validator.validate(dto);

        // Then
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        
        ConstraintViolation<TelemetryDTO> violation = violations.iterator().next();
        assertEquals("timestamp", violation.getPropertyPath().toString());
        assertEquals("El timestamp debe ser positivo", violation.getMessage());
    }

    @Test
    @DisplayName("TelemetryDTO - Validación exitosa con todos los campos válidos")
    void telemetryDTO_ValidationSuccess_WithAllValidFields() {
        // Given
        double[] coordinates = {1.5, 2.5, 3.5};
        SurgeryEvent event = SurgeryEvent.HEMORRHAGE;
        long timestamp = System.currentTimeMillis();

        TelemetryDTO dto = new TelemetryDTO(coordinates, event, timestamp);

        // When
        Set<ConstraintViolation<TelemetryDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("TelemetryDTO - Validación exitosa con coordenadas 2D")
    void telemetryDTO_ValidationSuccess_With2DCoordinates() {
        // Given
        double[] coordinates = {0.0, 0.0};
        SurgeryEvent event = SurgeryEvent.NONE;
        long timestamp = 1000L;

        TelemetryDTO dto = new TelemetryDTO(coordinates, event, timestamp);

        // When
        Set<ConstraintViolation<TelemetryDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("TelemetryDTO - Validación exitosa con coordenadas 3D")
    void telemetryDTO_ValidationSuccess_With3DCoordinates() {
        // Given
        double[] coordinates = {-1.0, -2.0, -3.0};
        SurgeryEvent event = SurgeryEvent.FINISH;
        long timestamp = 999999999999L;

        TelemetryDTO dto = new TelemetryDTO(coordinates, event, timestamp);

        // When
        Set<ConstraintViolation<TelemetryDTO>> violations = validator.validate(dto);

        // Then
        assertTrue(violations.isEmpty());
    }
}
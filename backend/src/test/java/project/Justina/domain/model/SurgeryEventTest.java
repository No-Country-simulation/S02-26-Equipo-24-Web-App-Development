package project.Justina.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Dominio para SurgeryEvent")
class SurgeryEventTest {

    @Test
    @DisplayName("Enum - Contiene todos los valores esperados")
    void enum_ContainsAllExpectedValues() {
        // Given
        SurgeryEvent[] expectedEvents = {
            SurgeryEvent.NONE,
            SurgeryEvent.TUMOR_TOUCH,
            SurgeryEvent.HEMORRHAGE,
            SurgeryEvent.START,
            SurgeryEvent.FINISH
        };

        // When
        SurgeryEvent[] actualEvents = SurgeryEvent.values();

        // Then
        assertArrayEquals(expectedEvents, actualEvents);
        assertEquals(expectedEvents.length, actualEvents.length);
    }

    @Test
    @DisplayName("Enum - Verifica orden de los valores")
    void enum_VerifiesValueOrder() {
        // When
        SurgeryEvent[] events = SurgeryEvent.values();

        // Then - Verificamos el orden específico
        assertEquals(SurgeryEvent.NONE, events[0]);
        assertEquals(SurgeryEvent.TUMOR_TOUCH, events[1]);
        assertEquals(SurgeryEvent.HEMORRHAGE, events[2]);
        assertEquals(SurgeryEvent.START, events[3]);
        assertEquals(SurgeryEvent.FINISH, events[4]);
    }

    @Test
    @DisplayName("valueOf - Encuentra todos los valores por nombre")
    void valueOf_FindsAllValuesByName() {
        // Given & When & Then - Verificamos que cada valor se puede encontrar por su nombre
        assertEquals(SurgeryEvent.NONE, SurgeryEvent.valueOf("NONE"));
        assertEquals(SurgeryEvent.TUMOR_TOUCH, SurgeryEvent.valueOf("TUMOR_TOUCH"));
        assertEquals(SurgeryEvent.HEMORRHAGE, SurgeryEvent.valueOf("HEMORRHAGE"));
        assertEquals(SurgeryEvent.START, SurgeryEvent.valueOf("START"));
        assertEquals(SurgeryEvent.FINISH, SurgeryEvent.valueOf("FINISH"));
    }

    @Test
    @DisplayName("valueOf - Lanza excepción con nombre inválido")
    void valueOf_ThrowsExceptionWithInvalidName() {
        // Given
        String[] invalidNames = {
            "INVALID_EVENT",
            "none", // minúscula
            "None", // mixto
            " TUMOR_TOUCH ", // con espacios
            "",
            null
        };

        // When & Then - Todos deben lanzar excepción excepto null (que lanza NPE)
        for (String invalidName : invalidNames) {
            if (invalidName == null) {
                assertThrows(NullPointerException.class, () -> SurgeryEvent.valueOf(invalidName));
            } else {
                assertThrows(IllegalArgumentException.class, () -> SurgeryEvent.valueOf(invalidName));
            }
        }
    }

    @Test
    @DisplayName("name() - Retorna nombres correctos")
    void name_ReturnsCorrectNames() {
        // Given & When & Then
        assertEquals("NONE", SurgeryEvent.NONE.name());
        assertEquals("TUMOR_TOUCH", SurgeryEvent.TUMOR_TOUCH.name());
        assertEquals("HEMORRHAGE", SurgeryEvent.HEMORRHAGE.name());
        assertEquals("START", SurgeryEvent.START.name());
        assertEquals("FINISH", SurgeryEvent.FINISH.name());
    }

    @Test
    @DisplayName("toString() - Retorna representación string correcta")
    void toString_ReturnsCorrectStringRepresentation() {
        // Given & When & Then
        assertEquals("NONE", SurgeryEvent.NONE.toString());
        assertEquals("TUMOR_TOUCH", SurgeryEvent.TUMOR_TOUCH.toString());
        assertEquals("HEMORRHAGE", SurgeryEvent.HEMORRHAGE.toString());
        assertEquals("START", SurgeryEvent.START.toString());
        assertEquals("FINISH", SurgeryEvent.FINISH.toString());
    }

    @Test
    @DisplayName("ordinal() - Retorna posiciones correctas")
    void ordinal_ReturnsCorrectPositions() {
        // Given & When & Then
        assertEquals(0, SurgeryEvent.NONE.ordinal());
        assertEquals(1, SurgeryEvent.TUMOR_TOUCH.ordinal());
        assertEquals(2, SurgeryEvent.HEMORRHAGE.ordinal());
        assertEquals(3, SurgeryEvent.START.ordinal());
        assertEquals(4, SurgeryEvent.FINISH.ordinal());
    }

    @Test
    @DisplayName("Enum - Verifica igualdad")
    void enum_VerifiesEquality() {
        // Given
        SurgeryEvent none1 = SurgeryEvent.NONE;
        SurgeryEvent none2 = SurgeryEvent.valueOf("NONE");
        SurgeryEvent tumorTouch = SurgeryEvent.TUMOR_TOUCH;

        // When & Then
        assertEquals(none1, none2);
        assertEquals(none1.hashCode(), none2.hashCode());
        assertNotEquals(none1, tumorTouch);
        assertNotEquals(none1.hashCode(), tumorTouch.hashCode());
    }

    @Test
    @DisplayName("Enum - Verifica identidad")
    void enum_VerifiesIdentity() {
        // Given & When & Then - Los enums son singletons
        assertSame(SurgeryEvent.NONE, SurgeryEvent.valueOf("NONE"));
        assertSame(SurgeryEvent.TUMOR_TOUCH, SurgeryEvent.valueOf("TUMOR_TOUCH"));
        assertSame(SurgeryEvent.HEMORRHAGE, SurgeryEvent.valueOf("HEMORRHAGE"));
        assertSame(SurgeryEvent.START, SurgeryEvent.valueOf("START"));
        assertSame(SurgeryEvent.FINISH, SurgeryEvent.valueOf("FINISH"));
    }

    @Test
    @DisplayName("Enum - Verifica comparación")
    void enum_VerifiesComparison() {
        // Given & When & Then - Los enums son comparables por su ordinal
        assertTrue(SurgeryEvent.NONE.compareTo(SurgeryEvent.TUMOR_TOUCH) < 0);
        assertTrue(SurgeryEvent.TUMOR_TOUCH.compareTo(SurgeryEvent.HEMORRHAGE) < 0);
        assertTrue(SurgeryEvent.HEMORRHAGE.compareTo(SurgeryEvent.START) < 0);
        assertTrue(SurgeryEvent.START.compareTo(SurgeryEvent.FINISH) < 0);

        assertEquals(0, SurgeryEvent.NONE.compareTo(SurgeryEvent.NONE));
        assertTrue(SurgeryEvent.FINISH.compareTo(SurgeryEvent.NONE) > 0);
    }

    @Test
    @DisplayName("Enum - Verifica que es un enum válido")
    void enum_VerifiesIsValidEnum() {
        // Given & When & Then - Verificamos propiedades básicas de un enum
        assertTrue(SurgeryEvent.class.isEnum());
        assertFalse(SurgeryEvent.class.isInterface());
        assertFalse(SurgeryEvent.class.isAnnotation());
        
        // Verificamos que todos los valores son del tipo correcto
        for (SurgeryEvent event : SurgeryEvent.values()) {
            assertTrue(event instanceof Enum);
            assertTrue(event instanceof SurgeryEvent);
        }
    }

    @Test
    @DisplayName("Enum - Verifica casos de uso típicos")
    void enum_VerifiesTypicalUseCases() {
        // Given - Simulamos casos de uso reales
        
        // When & Then - Caso 1: Estado inicial
        SurgeryEvent currentState = SurgeryEvent.NONE;
        assertEquals(SurgeryEvent.NONE, currentState);
        
        // When & Then - Caso 2: Inicio de cirugía
        currentState = SurgeryEvent.START;
        assertEquals(SurgeryEvent.START, currentState);
        
        // When & Then - Caso 3: Evento crítico
        currentState = SurgeryEvent.TUMOR_TOUCH;
        assertEquals(SurgeryEvent.TUMOR_TOUCH, currentState);
        
        // When & Then - Caso 4: Complicación
        currentState = SurgeryEvent.HEMORRHAGE;
        assertEquals(SurgeryEvent.HEMORRHAGE, currentState);
        
        // When & Then - Caso 5: Fin de cirugía
        currentState = SurgeryEvent.FINISH;
        assertEquals(SurgeryEvent.FINISH, currentState);
    }

    @Test
    @DisplayName("Enum - Verifica uso en switch")
    void enum_VerifiesUsageInSwitch() {
        // Given
        SurgeryEvent[] events = SurgeryEvent.values();
        
        // When & Then - Verificamos que todos los eventos funcionen en switch
        for (SurgeryEvent event : events) {
            String result = switch (event) {
                case NONE -> "No event";
                case TUMOR_TOUCH -> "Tumor touched";
                case HEMORRHAGE -> "Hemorrhage detected";
                case START -> "Surgery started";
                case FINISH -> "Surgery finished";
            };
            
            assertNotNull(result);
            assertFalse(result.isEmpty());
        }
    }

    @Test
    @DisplayName("Enum - Verifica que no se pueden crear instancias directamente")
    void enum_VerifiesCannotCreateInstancesDirectly() {
        // Given & When & Then - Los enums no tienen constructores públicos
        // No podemos probar esto directamente, pero verificamos que no haya constructores públicos
        try {
            SurgeryEvent.class.getDeclaredConstructor();
            fail("Expected NoSuchMethodException");
        } catch (NoSuchMethodException e) {
            // Expected - no hay constructor sin parámetros
        }
        
        try {
            SurgeryEvent.class.getDeclaredConstructor(String.class, int.class);
            // Si existe, verificamos que no sea público
            var constructor = SurgeryEvent.class.getDeclaredConstructor(String.class, int.class);
            assertFalse(java.lang.reflect.Modifier.isPublic(constructor.getModifiers()));
        } catch (NoSuchMethodException e) {
            // También válido - podría no tener este constructor
        }
    }
}

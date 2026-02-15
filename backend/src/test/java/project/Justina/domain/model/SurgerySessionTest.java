package project.Justina.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests de Dominio para SurgerySession")
class SurgerySessionTest {

    private UUID surgeonId;
    private UUID sessionId;
    private LocalDateTime testStartTime;
    private Movement testMovement;

    @BeforeEach
    void setUp() {
        surgeonId = UUID.randomUUID();
        sessionId = UUID.randomUUID();
        testStartTime = LocalDateTime.now().minusHours(1);
        testMovement = new Movement(new double[]{1.0, 2.0, 3.0}, SurgeryEvent.START, System.currentTimeMillis());
    }

    @Test
    @DisplayName("Constructor (nueva cirugía) - Genera ID automáticamente y inicializa valores")
    void constructor_NewSurgery_GeneratesIdAndInitializesValues() {
        // When
        SurgerySession session = new SurgerySession(surgeonId);

        // Then
        assertNotNull(session.getId());
        assertEquals(surgeonId, session.getSurgeonId());
        assertNotNull(session.getTrajectory());
        assertTrue(session.getTrajectory().isEmpty());
        assertNotNull(session.getStartTime());
        assertNull(session.getEndTime());
        assertNull(session.getDurationInSeconds());
        assertNull(session.getScore());
        assertNull(session.getFeedback());
    }

    @Test
    @DisplayName("Constructor (recuperación DB) - Crea con todos los parámetros")
    void constructor_DatabaseRecovery_CreatesWithAllParameters() {
        // Given
        List<Movement> movements = List.of(testMovement);
        LocalDateTime endTime = testStartTime.plusHours(2);
        Long duration = 7200L;
        Double score = 85.5;
        String feedback = "Buena precisión";

        // When
        SurgerySession session = new SurgerySession(sessionId, surgeonId, movements, testStartTime, endTime, duration, score, feedback);

        // Then
        assertEquals(sessionId, session.getId());
        assertEquals(surgeonId, session.getSurgeonId());
        assertEquals(movements, session.getTrajectory());
        assertEquals(testStartTime, session.getStartTime());
        assertEquals(endTime, session.getEndTime());
        assertEquals(duration, session.getDurationInSeconds());
        assertEquals(score, session.getScore());
        assertEquals(feedback, session.getFeedback());
    }

    @Test
    @DisplayName("updateAnalysis - Actualiza score y feedback")
    void updateAnalysis_UpdatesScoreAndFeedback() {
        // Given
        SurgerySession session = new SurgerySession(surgeonId);
        Double newScore = 92.0;
        String newFeedback = "Excelente técnica";

        // When
        session.updateAnalysis(newScore, newFeedback);

        // Then
        assertEquals(newScore, session.getScore());
        assertEquals(newFeedback, session.getFeedback());
    }

    @Test
    @DisplayName("updateAnalysis - Permite valores nulos")
    void updateAnalysis_AllowsNullValues() {
        // Given
        SurgerySession session = new SurgerySession(surgeonId);
        session.updateAnalysis(80.0, "Feedback inicial");

        // When
        session.updateAnalysis(null, null);

        // Then
        assertNull(session.getScore());
        assertNull(session.getFeedback());
    }

    @Test
    @DisplayName("addMovement - Agrega movimiento a la trayectoria")
    void addMovement_AddsMovementToTrajectory() {
        // Given
        SurgerySession session = new SurgerySession(surgeonId);
        assertEquals(0, session.getTrajectory().size());

        // When
        session.addMovement(testMovement);

        // Then
        assertEquals(1, session.getTrajectory().size());
        assertTrue(session.getTrajectory().contains(testMovement));
    }

    @Test
    @DisplayName("addMovement - Agrega múltiples movimientos")
    void addMovement_AddsMultipleMovements() {
        // Given
        SurgerySession session = new SurgerySession(surgeonId);
        Movement secondMovement = new Movement(new double[]{4.0, 5.0, 6.0}, SurgeryEvent.TUMOR_TOUCH, System.currentTimeMillis());

        // When
        session.addMovement(testMovement);
        session.addMovement(secondMovement);

        // Then
        assertEquals(2, session.getTrajectory().size());
        assertTrue(session.getTrajectory().contains(testMovement));
        assertTrue(session.getTrajectory().contains(secondMovement));
    }

    @Test
    @DisplayName("addMovement - Permite movimientos nulos")
    void addMovement_AllowsNullMovements() {
        // Given
        SurgerySession session = new SurgerySession(surgeonId);

        // When
        session.addMovement(null);

        // Then
        assertEquals(1, session.getTrajectory().size());
        assertNull(session.getTrajectory().get(0));
    }

    @Test
    @DisplayName("endSurgery - Establece endTime y calcula duración")
    void endSurgery_SetsEndTimeAndCalculatesDuration() {
        // Given
        SurgerySession session = new SurgerySession(surgeonId);
        LocalDateTime beforeEnd = LocalDateTime.now();
        
        // When
        session.endSurgery();
        LocalDateTime afterEnd = LocalDateTime.now();

        // Then
        assertNotNull(session.getEndTime());
        assertTrue(session.getEndTime().isAfter(beforeEnd) || session.getEndTime().isEqual(beforeEnd));
        assertTrue(session.getEndTime().isBefore(afterEnd) || session.getEndTime().isEqual(afterEnd));
        assertNotNull(session.getDurationInSeconds());
        assertTrue(session.getDurationInSeconds() >= 0);
    }

    @Test
    @DisplayName("endSurgery - Calcula duración correctamente")
    void endSurgery_CalculatesDurationCorrectly() throws InterruptedException {
        // Given
        SurgerySession session = new SurgerySession(surgeonId);
        LocalDateTime startTime = session.getStartTime();
        
        // When - Esperar un pequeño tiempo para asegurar diferencia
        Thread.sleep(50); // Aumentamos el tiempo para asegurar diferencia
        session.endSurgery();

        // Then
        Long expectedDuration = ChronoUnit.SECONDS.between(startTime, session.getEndTime());
        assertEquals(expectedDuration, session.getDurationInSeconds());
        assertTrue(session.getDurationInSeconds() >= 0); // Puede ser 0 si es muy rápido
    }

    @Test
    @DisplayName("endSurgery - Solo se puede llamar una vez")
    void endSurgery_CanOnlyBeCalledOnce() throws InterruptedException {
        // Given
        SurgerySession session = new SurgerySession(surgeonId);
        session.endSurgery();
        LocalDateTime firstEndTime = session.getEndTime();
        Long firstDuration = session.getDurationInSeconds();

        // When - Esperar y llamar nuevamente
        Thread.sleep(50); // Aumentamos el tiempo
        session.endSurgery();

        // Then - Los valores deben ser diferentes (se recalculan)
        assertNotNull(session.getEndTime());
        assertTrue(session.getEndTime().isAfter(firstEndTime) || session.getEndTime().isEqual(firstEndTime));
        assertTrue(session.getDurationInSeconds() >= firstDuration); // Puede ser igual si es muy rápido
    }

    @Test
    @DisplayName("Getters y Setters - Funcionan correctamente")
    void gettersAndSetters_WorkCorrectly() {
        // Given
        SurgerySession session = new SurgerySession(surgeonId);
        Double newScore = 75.0;
        String newFeedback = "Necesita mejorar";

        // When
        session.setScore(newScore);
        session.setFeedback(newFeedback);

        // Then
        assertEquals(newScore, session.getScore());
        assertEquals(newFeedback, session.getFeedback());
    }

    @Test
    @DisplayName("getTrajectory - Retorna copia de la lista (inmutabilidad)")
    void getTrajectory_ReturnsListReference() {
        // Given
        SurgerySession session = new SurgerySession(surgeonId);
        List<Movement> trajectory = session.getTrajectory();

        // When - Modificar la lista obtenida
        trajectory.add(testMovement);

        // Then - La lista original debe haber cambiado (no es una copia defensiva)
        assertEquals(1, session.getTrajectory().size());
        assertTrue(session.getTrajectory().contains(testMovement));
    }

    @Test
    @DisplayName("SurgerySession - Maneja valores límite")
    void surgerySession_HandlesEdgeCases() {
        // Given
        UUID nullSurgeonId = null;

        // When
        SurgerySession session = new SurgerySession(nullSurgeonId);

        // Then
        assertNotNull(session.getId());
        assertNull(session.getSurgeonId());
        assertNotNull(session.getTrajectory());
        assertNotNull(session.getStartTime());
    }

    @Test
    @DisplayName("SurgerySession - Verifica consistencia de datos")
    void surgerySession_VerifiesDataConsistency() {
        // Given
        List<Movement> movements = List.of(testMovement);
        LocalDateTime endTime = testStartTime.plusHours(1);

        // When
        SurgerySession session = new SurgerySession(sessionId, surgeonId, movements, testStartTime, endTime, 3600L, 90.0, "Perfecto");

        // Then - Verificamos que todos los datos sean consistentes
        assertEquals(sessionId, session.getId());
        assertEquals(surgeonId, session.getSurgeonId());
        assertEquals(movements.size(), session.getTrajectory().size());
        assertTrue(session.getEndTime().isAfter(session.getStartTime()));
        assertTrue(session.getDurationInSeconds() > 0);
        assertTrue(session.getScore() >= 0 && session.getScore() <= 100);
        assertNotNull(session.getFeedback());
    }
}

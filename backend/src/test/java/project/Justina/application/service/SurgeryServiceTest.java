package project.Justina.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.Justina.domain.dto.AnalysisDTO;
import project.Justina.domain.dto.TrajectoryDTO;
import project.Justina.domain.exception.ForbiddenActionException;
import project.Justina.domain.exception.SurgeryNotFoundException;
import project.Justina.domain.model.Movement;
import project.Justina.domain.model.SurgeryEvent;
import project.Justina.domain.model.SurgerySession;
import project.Justina.domain.repository.SurgeryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios para SurgeryService")
class SurgeryServiceTest {

    @Mock
    private SurgeryRepository surgeryRepository;

    @InjectMocks
    private SurgeryService surgeryService;

    private SurgerySession testSurgerySession;
    private UUID testSurgeryId;
    private UUID testSurgeonId;
    private UUID differentSurgeonId;
    private List<Movement> testMovements;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        testSurgeryId = UUID.randomUUID();
        testSurgeonId = UUID.randomUUID();
        differentSurgeonId = UUID.randomUUID();
        startTime = LocalDateTime.now().minusHours(2);
        endTime = LocalDateTime.now().minusHours(1);

        // Crear movimientos de prueba
        testMovements = List.of(
            new Movement(new double[]{1.0, 2.0, 3.0}, SurgeryEvent.START, System.currentTimeMillis() - 7200000),
            new Movement(new double[]{1.5, 2.5, 3.5}, SurgeryEvent.TUMOR_TOUCH, System.currentTimeMillis() - 7000000),
            new Movement(new double[]{2.0, 3.0, 4.0}, SurgeryEvent.HEMORRHAGE, System.currentTimeMillis() - 6800000)
        );

        // Crear sesión de cirugía de prueba
        testSurgerySession = new SurgerySession(
            testSurgeryId,
            testSurgeonId,
            testMovements,
            startTime,
            endTime,
            3600L, // 1 hora en segundos
            85.5,
            "Buena precisión en los movimientos"
        );
    }

    @Test
    @DisplayName("getSurgeryTrajectory - Éxito cuando el cirujano dueño solicita su trayectoria")
    void getSurgeryTrajectory_Success_WhenOwnerRequests() {
        // Given
        when(surgeryRepository.findById(testSurgeryId)).thenReturn(Optional.of(testSurgerySession));

        // When
        TrajectoryDTO result = surgeryService.getSurgeryTrajectory(testSurgeryId, testSurgeonId);

        // Then
        assertNotNull(result);
        assertEquals(testSurgeryId, result.surgeryId());
        assertEquals(startTime, result.startTime());
        assertEquals(endTime, result.endTime());
        assertEquals(testMovements, result.movements());

        verify(surgeryRepository).findById(testSurgeryId);
    }

    @Test
    @DisplayName("getSurgeryTrajectory - Lanza ForbiddenException cuando cirujano diferente intenta acceder")
    void getSurgeryTrajectory_ThrowsForbidden_WhenDifferentSurgeon() {
        // Given
        when(surgeryRepository.findById(testSurgeryId)).thenReturn(Optional.of(testSurgerySession));

        // When & Then
        ForbiddenActionException exception = assertThrows(
                ForbiddenActionException.class,
                () -> surgeryService.getSurgeryTrajectory(testSurgeryId, differentSurgeonId)
        );

        assertEquals("No tienes permiso para acceder a esta cirugía.", exception.getMessage());

        verify(surgeryRepository).findById(testSurgeryId);
    }

    @Test
    @DisplayName("getSurgeryTrajectory - Lanza SurgeryNotFoundException cuando la cirugía no existe")
    void getSurgeryTrajectory_ThrowsNotFound_WhenSurgeryNotExists() {
        // Given
        UUID nonExistentSurgeryId = UUID.randomUUID();
        when(surgeryRepository.findById(nonExistentSurgeryId)).thenReturn(Optional.empty());

        // When & Then
        SurgeryNotFoundException exception = assertThrows(
                SurgeryNotFoundException.class,
                () -> surgeryService.getSurgeryTrajectory(nonExistentSurgeryId, testSurgeonId)
        );

        assertEquals("La cirugía con id " + nonExistentSurgeryId + " no existe.", exception.getMessage());

        verify(surgeryRepository).findById(nonExistentSurgeryId);
    }

    @Test
    @DisplayName("saveAiAnalysis - Éxito cuando la cirugía existe")
    void saveAiAnalysis_Success_WhenSurgeryExists() {
        // Given
        AnalysisDTO analysisDTO = new AnalysisDTO(92.5, "Excelente técnica quirúrgica");
        SurgerySession sessionToUpdate = new SurgerySession(
            testSurgeryId,
            testSurgeonId,
            testMovements,
            startTime,
            endTime,
            3600L,
            null, // score inicialmente null
            null  // feedback inicialmente null
        );

        when(surgeryRepository.findById(testSurgeryId)).thenReturn(Optional.of(sessionToUpdate));
        doNothing().when(surgeryRepository).save(any(SurgerySession.class));

        // When
        assertDoesNotThrow(() -> surgeryService.saveAiAnalysis(testSurgeryId, analysisDTO));

        // Then
        assertEquals(92.5, sessionToUpdate.getScore());
        assertEquals("Excelente técnica quirúrgica", sessionToUpdate.getFeedback());

        verify(surgeryRepository).findById(testSurgeryId);
        verify(surgeryRepository).save(sessionToUpdate);
    }

    @Test
    @DisplayName("saveAiAnalysis - Lanza SurgeryNotFoundException cuando la cirugía no existe")
    void saveAiAnalysis_ThrowsNotFound_WhenSurgeryNotExists() {
        // Given
        UUID nonExistentSurgeryId = UUID.randomUUID();
        AnalysisDTO analysisDTO = new AnalysisDTO(75.0, "Técnica mejorada");
        when(surgeryRepository.findById(nonExistentSurgeryId)).thenReturn(Optional.empty());

        // When & Then
        SurgeryNotFoundException exception = assertThrows(
                SurgeryNotFoundException.class,
                () -> surgeryService.saveAiAnalysis(nonExistentSurgeryId, analysisDTO)
        );

        assertEquals("La cirugía con id " + nonExistentSurgeryId + " no existe.", exception.getMessage());

        verify(surgeryRepository).findById(nonExistentSurgeryId);
        verify(surgeryRepository, never()).save(any());
    }

    @Test
    @DisplayName("saveAiAnalysis - Actualiza análisis existente cuando la cirugía ya tiene análisis previo")
    void saveAiAnalysis_UpdatesExistingAnalysis_WhenSurgeryHasPreviousAnalysis() {
        // Given
        AnalysisDTO newAnalysisDTO = new AnalysisDTO(95.0, "Técnica perfecta después de mejora");
        SurgerySession sessionWithExistingAnalysis = new SurgerySession(
            testSurgeryId,
            testSurgeonId,
            testMovements,
            startTime,
            endTime,
            3600L,
            80.0, // score existente
            "Buena técnica" // feedback existente
        );

        when(surgeryRepository.findById(testSurgeryId)).thenReturn(Optional.of(sessionWithExistingAnalysis));
        doNothing().when(surgeryRepository).save(any(SurgerySession.class));

        // When
        assertDoesNotThrow(() -> surgeryService.saveAiAnalysis(testSurgeryId, newAnalysisDTO));

        // Then
        assertEquals(95.0, sessionWithExistingAnalysis.getScore());
        assertEquals("Técnica perfecta después de mejora", sessionWithExistingAnalysis.getFeedback());

        verify(surgeryRepository).findById(testSurgeryId);
        verify(surgeryRepository).save(sessionWithExistingAnalysis);
    }
}

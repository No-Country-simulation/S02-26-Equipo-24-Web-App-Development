package project.Justina.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.Justina.domain.dto.AnalysisDTO;
import project.Justina.domain.dto.TrajectoryDTO;
import project.Justina.domain.exception.ForbiddenActionException;
import project.Justina.domain.exception.SurgeryNotFoundException;
import project.Justina.domain.model.SurgerySession;
import project.Justina.domain.repository.SurgeryRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SurgeryService {

    private final SurgeryRepository surgeryRepository;

    public TrajectoryDTO getSurgeryTrajectory(UUID surgeryId, UUID authenticatedSurgeonId) {
        // Buscar la cirugía o lanzar error 404 si no existe
        SurgerySession session = surgeryRepository.findById(surgeryId)
                .orElseThrow(() -> new SurgeryNotFoundException("La cirugía con id " + surgeryId + " no existe."));

        // 2. VALIDACIÓN REAL: Comparar UUIDs
        if (!session.getSurgeonId().equals(authenticatedSurgeonId)) {
            throw new ForbiddenActionException("No tienes permiso para acceder a esta cirugía.");
        }

        // Mapear al DTO "limpio" para la IA
        return new TrajectoryDTO(
                session.getId(),
                session.getStartTime(),
                session.getEndTime(),
                session.getTrajectory()
        );
    }

    public void saveAiAnalysis(UUID surgeryId, AnalysisDTO analysis) {
        // Buscamos la cirugía (Usamos tu SurgeryNotFoundException si no existe)
        SurgerySession session = surgeryRepository.findById(surgeryId)
                .orElseThrow(() -> new SurgeryNotFoundException("La cirugía con id " + surgeryId + " no existe."));

        // Actualizamos los datos
        session.updateAnalysis(analysis.score(), analysis.feedback());

        // Persistimos
        surgeryRepository.save(session);
    }
}

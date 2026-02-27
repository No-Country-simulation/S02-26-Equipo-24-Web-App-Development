package project.Justina.domain.repository;

import project.Justina.domain.model.SurgerySession;

import java.util.Optional;
import java.util.UUID;

public interface SurgeryRepository {
    void save(SurgerySession session);
    Optional<SurgerySession> findById(UUID id);
}

package project.Justina.infrastructure.adapter.mapper;

import org.springframework.stereotype.Component;
import project.Justina.domain.model.SurgerySession;
import project.Justina.infrastructure.adapter.entity.SurgerySessionEntity;

@Component
public class SurgeryMapper {
    // De Dominio a Entidad (Para Guardar)
    public SurgerySessionEntity toEntity(SurgerySession domain) {
        if (domain == null) return null;

        SurgerySessionEntity entity = new SurgerySessionEntity();
        entity.setId(domain.getId());
        entity.setSurgeonId(domain.getSurgeonId());
        entity.setTrajectory(domain.getTrajectory());
        entity.setStartTime(domain.getStartTime());
        entity.setEndTime(domain.getEndTime());
        entity.setDurationInSeconds(domain.getDurationInSeconds());
        entity.setScore(domain.getScore());
        entity.setFeedback(domain.getFeedback());
        return entity;
    }

    public SurgerySession toDomain(SurgerySessionEntity entity) {
        if (entity == null) return null;

        return new SurgerySession(
                entity.getId(),
                entity.getSurgeonId(),
                entity.getTrajectory(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getDurationInSeconds(),
                entity.getScore(),
                entity.getFeedback()
        );
    }
}

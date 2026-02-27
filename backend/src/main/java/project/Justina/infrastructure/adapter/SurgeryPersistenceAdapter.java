package project.Justina.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import project.Justina.domain.model.SurgerySession;
import project.Justina.domain.repository.SurgeryRepository;
import project.Justina.infrastructure.adapter.entity.SurgerySessionEntity;
import project.Justina.infrastructure.adapter.mapper.SurgeryMapper;
import project.Justina.infrastructure.adapter.repository.JpaSurgeryRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SurgeryPersistenceAdapter implements SurgeryRepository {
    private final JpaSurgeryRepository jpaRepository;
    private final SurgeryMapper mapper;

    @Override
    public void save(SurgerySession session) {
        SurgerySessionEntity entity = mapper.toEntity(session);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<SurgerySession> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
}

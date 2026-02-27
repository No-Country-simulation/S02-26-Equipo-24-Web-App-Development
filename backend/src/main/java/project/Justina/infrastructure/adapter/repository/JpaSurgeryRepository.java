package project.Justina.infrastructure.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.Justina.infrastructure.adapter.entity.SurgerySessionEntity;

import java.util.UUID;

@Repository
public interface JpaSurgeryRepository extends JpaRepository<SurgerySessionEntity, UUID> {
}

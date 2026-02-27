package project.Justina.infrastructure.adapter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import project.Justina.domain.model.Movement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "surgery_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurgerySessionEntity {
    @Id
    private UUID id;

    @Column(name = "surgeon_id")
    private UUID surgeonId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Movement> trajectory;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_seconds")
    private Long durationInSeconds;

    @Column()
    private Double score;

    @Column()
    private String feedback;
}
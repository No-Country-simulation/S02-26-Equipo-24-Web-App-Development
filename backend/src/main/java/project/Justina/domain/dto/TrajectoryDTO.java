package project.Justina.domain.dto;

import project.Justina.domain.model.Movement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TrajectoryDTO(
        UUID surgeryId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<Movement> movements
) {}

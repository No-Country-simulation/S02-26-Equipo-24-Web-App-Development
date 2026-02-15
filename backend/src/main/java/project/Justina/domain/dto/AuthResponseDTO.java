package project.Justina.domain.dto;

import java.util.UUID;

public record AuthResponseDTO (
        String token,
        UUID userId,
        String username,
        String message
) {
}

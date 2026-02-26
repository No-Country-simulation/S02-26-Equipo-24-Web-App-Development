package project.Justina.domain.dto;

import java.util.UUID;

public record UserResponseDTO(
    UUID id,
    String username,
    String role
) {}

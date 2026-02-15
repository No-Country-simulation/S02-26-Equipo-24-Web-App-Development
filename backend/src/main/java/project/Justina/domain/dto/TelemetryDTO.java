package project.Justina.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import project.Justina.domain.model.SurgeryEvent;

public record TelemetryDTO(

        @NotNull(message = "Las coordenadas son obligatorias")
        @NotEmpty(message = "Debe haber al menos una coordenada")
        @Size(min = 2, max = 3, message = "Coordinates debe tener 2 o 3 valores")
        double[] coordinates,

        @NotNull(message = "El evento es obligatorio")
        SurgeryEvent event,

        @Positive(message = "El timestamp debe ser positivo")
        long timestamp
) {}

package project.Justina.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AnalysisDTO (
        @Min(value = 0, message = "El score no puede ser menor a 0")
        @Max(value = 100, message = "El score no puede ser mayor a 100")
        double score,

        @NotBlank(message = "El feedback es obligatorio")
        String feedback
){
}

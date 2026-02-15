package project.Justina.domain.model;

public record Movement (
        double[] coordinates, // Puede ser [x, y] o [x, y, z]
        SurgeryEvent event,
        long timestamp
) {
}

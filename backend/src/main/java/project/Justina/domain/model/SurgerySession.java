package project.Justina.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SurgerySession {

    private UUID id;
    private UUID surgeonId;
    private List<Movement> trajectory;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationInSeconds;
    private Double score;
    private String feedback;

    public SurgerySession(UUID surgeonId) {
        this.id = UUID.randomUUID();
        this.surgeonId = surgeonId;
        this.trajectory = new ArrayList<>();
        this.startTime = LocalDateTime.now();
    }

    // Constructor para recuperar de DB (Mapper)
    public SurgerySession(UUID id, UUID surgeonId, List<Movement> trajectory,
                          LocalDateTime startTime, LocalDateTime endTime, Long durationInSeconds, Double score, String feedback) {
        this.id = id;
        this.surgeonId = surgeonId;
        this.trajectory = trajectory;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationInSeconds = durationInSeconds;
        this.score = score;
        this.feedback = feedback;
    }

    public void updateAnalysis(Double score, String feedback) {
        this.score = score;
        this.feedback = feedback;
    }

    // Método para ir agregando puntos durante la cirugía
    public void addMovement(Movement movement) {
        this.trajectory.add(movement);
    }

    //  Finalizar cirugía
    public void endSurgery() {
        this.endTime = LocalDateTime.now();
        this.durationInSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
    }

    public UUID getId() {
        return id;
    }

    public UUID getSurgeonId() {
        return surgeonId;
    }

    public List<Movement> getTrajectory() {
        return trajectory;
    }


    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Double getScore() {
        return score;
    }

    public String getFeedback() {
        return feedback;
    }

    public Long getDurationInSeconds() {
        return durationInSeconds;
    }
}

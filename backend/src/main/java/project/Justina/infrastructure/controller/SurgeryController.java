package project.Justina.infrastructure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import project.Justina.application.service.SurgeryService;
import project.Justina.domain.dto.AnalysisDTO;
import project.Justina.domain.dto.TrajectoryDTO;
import project.Justina.infrastructure.adapter.entity.UserEntity;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/surgeries")
@RequiredArgsConstructor
@Tag(name = "Surgeries", description = "Endpoints para extracción de telemetría y reportes")
public class SurgeryController {

    private final SurgeryService surgeryService;

    @GetMapping("/{id}/trajectory")
    @Operation(summary = "Obtener trayectoria para IA")
    public ResponseEntity<TrajectoryDTO> getTrajectory(@PathVariable UUID id) {

        // Extraemos el ID del cirujano del contexto de seguridad (JWT)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Cambiamos UserPrincipal por UserEntity que es lo que tienes
        UserEntity userDetails = (UserEntity) auth.getPrincipal();
        UUID authenticatedId = userDetails.getId();

        TrajectoryDTO trajectory = surgeryService.getSurgeryTrajectory(id, authenticatedId);
        return ResponseEntity.ok(trajectory);
    }

    @PostMapping("/{id}/analysis")
    @Operation(summary = "Recibir análisis de la IA")
    public ResponseEntity<Void> saveAnalysis(
            @PathVariable UUID id,
            @RequestBody @Valid AnalysisDTO analysis) {

        surgeryService.saveAiAnalysis(id, analysis);
        return ResponseEntity.noContent().build();
    }
}

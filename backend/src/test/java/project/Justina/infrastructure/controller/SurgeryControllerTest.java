package project.Justina.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import project.Justina.application.service.AuthService;
import project.Justina.application.service.SurgeryService;
import project.Justina.domain.dto.AnalysisDTO;
import project.Justina.domain.model.SurgerySession;
import project.Justina.domain.repository.SurgeryRepository;
import project.Justina.domain.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Tests de Integración para SurgeryController - CORREGIDOS")
class SurgeryControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AuthService authService;

    @Autowired
    private SurgeryService surgeryService;

    @Autowired
    private SurgeryRepository surgeryRepository;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private String testUsername;
    private String testPassword;
    private String validToken;
    private UUID testSurgeryId;
    private AnalysisDTO testAnalysisDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        objectMapper = new ObjectMapper();

        testUsername = "surgeon_" + System.currentTimeMillis();
        testPassword = "password123";
        testSurgeryId = UUID.randomUUID();

        testAnalysisDTO = new AnalysisDTO(85.5, "Buena precisión en los movimientos");

        // Registrar usuario y obtener token
        try {
            authService.register(testUsername, testPassword);
            validToken = authService.login(testUsername, testPassword).token();
        } catch (Exception e) {
            // Si el usuario ya existe, solo hacer login
            validToken = authService.login(testUsername, testPassword).token();
        }
    }

    @Test
    @DisplayName("getTrajectory - Retorna 200 con JWT válido y usuario dueño de la cirugía")
    void getTrajectory_Success_WithValidToken() throws Exception {
        // GIVEN: Necesitamos que la cirugía EXISTA en la DB antes de pedirla

        // Obtenemos el ID real del usuario que registramos en el setUp
        UUID realUserId = userRepository.findByUsername(testUsername).get().getId();

        // Creamos la sesión y la guardamos
        SurgerySession session = new SurgerySession(
                testSurgeryId,
                realUserId, // IMPORTANTE: El dueño debe ser el mismo del Token
                List.of(),  // Movimientos vacíos para el test
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now(),
                3600L,
                null,
                null
        );
        surgeryRepository.save(session);

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/surgeries/{id}/trajectory", testSurgeryId)
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.surgeryId").value(testSurgeryId.toString()));
    }

    @Test
    @DisplayName("getTrajectory - Retorna 403 cuando usuario no es dueño de la cirugía")
    void getTrajectory_Returns403_WithDifferentUser() throws Exception {
        // 1. GIVEN: Guardar la cirugía vinculada al USUARIO 1 (el del setUp)
        UUID ownerId = userRepository.findByUsername(testUsername).get().getId();

        SurgerySession session = new SurgerySession(
                testSurgeryId,
                ownerId, // El dueño es el cirujano 1
                List.of(),
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now(),
                3600L,
                null,
                null
        );
        surgeryRepository.save(session);

        // 2. Crear al USUARIO 2 y obtener su token
        String differentUsername = "surgeon2_" + System.currentTimeMillis();
        authService.register(differentUsername, "password456");
        String differentToken = authService.login(differentUsername, "password456").token();

        // 3. WHEN & THEN: El USUARIO 2 intenta acceder a la cirugía del USUARIO 1
        mockMvc.perform(get("/api/v1/surgeries/{id}/trajectory", testSurgeryId)
                        .header("Authorization", "Bearer " + differentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isForbidden()) // Ahora sí debería dar 403
                .andExpect(jsonPath("$.error").value("No tienes permiso para acceder a esta cirugía."));
    }

    @Test
    @DisplayName("getTrajectory - Retorna 401 sin token de autenticación")
    void getTrajectory_Returns401_WithoutToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/surgeries/{id}/trajectory", testSurgeryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("getTrajectory - Retorna 401 con token inválido")
    void getTrajectory_Returns401_WithInvalidToken() throws Exception {
        // Un token con formato legal (3 partes) pero contenido basura
        String malformedToken = "parte1.parte2.parte3";

        mockMvc.perform(get("/api/v1/surgeries/{id}/trajectory", testSurgeryId)
                        .header("Authorization", "Bearer " + malformedToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Token de autenticación inválido"));
    }

    @Test
    @DisplayName("saveAnalysis - Retorna 403 con ROLE_SURGEON (requiere ROLE_AI)")
    void saveAnalysis_Returns403_WithSurgeonRole() throws Exception {
        // When & Then - Este test debería fallar porque el endpoint requiere ROLE_AI
        mockMvc.perform(post("/api/v1/surgeries/{id}/analysis", testSurgeryId)
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAnalysisDTO))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("saveAnalysis - Retorna 401 sin token de autenticación")
    void saveAnalysis_Returns401_WithoutToken() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/surgeries/{id}/analysis", testSurgeryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAnalysisDTO))
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"AI"})
    @DisplayName("saveAnalysis - Retorna 204 con ROLE_AI válido")
    void saveAnalysis_Success_WithAIRole() throws Exception {
        // 1. GIVEN: Crear y guardar la cirugía en la DB para que el servicio la encuentre
        // No importa de quién sea la cirugía, la IA tiene permiso global para analizar
        UUID dummySurgeonId = UUID.randomUUID();

        SurgerySession session = new SurgerySession(
                testSurgeryId,
                dummySurgeonId,
                List.of(),
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now(),
                3600L,
                null,
                null
        );
        surgeryRepository.save(session);

        // 2. WHEN & THEN
        mockMvc.perform(post("/api/v1/surgeries/{id}/analysis", testSurgeryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAnalysisDTO))
                        .with(csrf()))
                .andExpect(status().isNoContent()); // Ahora sí encontrará la cirugía y devolverá 204
    }

    @Test
    @WithMockUser(roles = {"AI"}) // Usamos AI para que Spring nos deje pasar al controlador
    @DisplayName("saveAnalysis - Retorna 400 con JSON malformado")
    void saveAnalysis_Returns400_WithMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/surgeries/{id}/analysis", testSurgeryId)
                        // No necesitamos el header manual porque @WithMockUser ya nos autentica
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}")
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("JSON malformado o cuerpo de la petición inválido"));
    }

    @Test
    @DisplayName("getTrajectory - Retorna 404 con ID de cirugía inexistente")
    void getTrajectory_Returns404_WithNonExistentSurgery() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(get("/api/v1/surgeries/{id}/trajectory", nonExistentId)
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("La cirugía con id " + nonExistentId + " no existe."));
    }

    @Test
    @WithMockUser(roles = {"AI"})
    @DisplayName("saveAnalysis - Retorna 404 con ID de cirugía inexistente")
    void saveAnalysis_Returns404_WithNonExistentSurgery() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(post("/api/v1/surgeries/{id}/analysis", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testAnalysisDTO))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("La cirugía con id " + nonExistentId + " no existe."));
    }
}

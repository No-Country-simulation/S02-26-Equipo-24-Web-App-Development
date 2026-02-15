package project.Justina.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import project.Justina.application.service.AuthService;
import project.Justina.domain.dto.LoginRequestDTO;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Tests de Integración para AuthController")
class AuthControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AuthService authService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    private String testUsername;
    private String testPassword;
    private UUID testUserId;
    private String testToken;
    private LoginRequestDTO validLoginRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        
        testUsername = "surgeon1";
        testPassword = "password123";
        testUserId = UUID.randomUUID();
        testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
        
        validLoginRequest = new LoginRequestDTO(testUsername, testPassword);
    }

    @Test
    @DisplayName("login - Retorna 200 y AuthResponseDTO con credenciales válidas")
    void login_Success_WithValidCredentials() throws Exception {
        // Given - Primero registrar el usuario
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk());

        // When & Then - Luego intentar login
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Login exitoso"))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.username").value(testUsername));
    }

    @Test
    @DisplayName("login - Retorna 401 con credenciales inválidas")
    void login_Returns401_WithInvalidCredentials() throws Exception {
        // Given - Usar credenciales que no existen
        LoginRequestDTO invalidRequest = new LoginRequestDTO("nonexistent", "wrongpassword");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("register - Retorna 200 con nuevo usuario")
    void register_Success_WithNewUser() throws Exception {
        // Given - Usar un usuario único
        LoginRequestDTO newRegisterRequest = new LoginRequestDTO("newuser123", "newpassword123");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Usuario registrado con éxito"));
    }

    @Test
    @DisplayName("register - Retorna 400 cuando usuario ya existe")
    void register_Returns400_WithExistingUser() throws Exception {
        // Given - Intentar registrar el mismo usuario dos veces
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk());

        // When & Then - Segundo intento debe fallar
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("El usuario ya existe"));
    }

    @Test
    @DisplayName("login - Retorna 400 con datos inválidos (username vacío)")
    void login_Returns400_WithInvalidData_EmptyUsername() throws Exception {
        // Given
        LoginRequestDTO invalidRequest = new LoginRequestDTO("", testPassword);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("login - Retorna 400 con datos inválidos (password corta)")
    void login_Returns400_WithInvalidData_ShortPassword() throws Exception {
        // Given
        LoginRequestDTO invalidRequest = new LoginRequestDTO(testUsername, "123");

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("register - Retorna 400 con datos inválidos (username muy corto)")
    void register_Returns400_WithInvalidData_ShortUsername() throws Exception {
        // Given
        LoginRequestDTO invalidRequest = new LoginRequestDTO("abc", testPassword);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("login - Retorna 400 con JSON malformado")
    void login_Returns400_WithMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("register - Retorna 400 con JSON malformado")
    void register_Returns400_WithMalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }
}

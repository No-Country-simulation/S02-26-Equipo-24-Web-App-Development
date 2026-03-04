package project.Justina.infrastructure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.Justina.application.service.AuthService;
import project.Justina.domain.dto.AuthResponseDTO;
import project.Justina.domain.dto.LoginRequestDTO;
import project.Justina.domain.dto.UserResponseDTO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Inicio de sesión",
            description = "Autentica al usuario y devuelve un JWT válido para acceder al simulador"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticación exitosa",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o error de validación",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Credenciales incorrectas"
            )
    })
    public ResponseEntity<?> login(
            @RequestBody @Valid LoginRequestDTO request,
            HttpServletResponse response
    ) {
        AuthResponseDTO authResponse = authService.login(request.username(), request.password());
        

        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("jwt-token", authResponse.token());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400); // 24 horas
        cookie.setAttribute("SameSite", "None");
        
        response.addCookie(cookie);
        
        return ResponseEntity.ok(Map.of(
            "message", "Login exitoso",
            "userId", authResponse.userId(),
            "username", authResponse.username(),
            "token", authResponse.token()
        ));
    }


    @PostMapping("/register")
    @Operation(
            summary = "Registro de usuario",
            description = "Registra un nuevo usuario en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario registrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                {
                                  "message": "Usuario registrado con éxito"
                                }
                                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error en los datos enviados o usuario ya existente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                                {
                                  "error": "El usuario ya existe"
                                }
                                """)
                    )
            )
    })
    public ResponseEntity<?> register(
            @RequestBody @Valid LoginRequestDTO request
    ) {
        try {
            authService.register(request.username(), request.password());
            return ResponseEntity.ok(
                    Map.of("message", "Usuario registrado con éxito")
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    @Operation(
            summary = "Obtener datos del usuario autenticado",
            description = "Retorna la información del usuario actualmente autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Datos del usuario obtenidos exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Usuario no autenticado"
            )
    })
    public UserResponseDTO getCurrentUser() {
        return authService.getCurrentUser();
    }

}

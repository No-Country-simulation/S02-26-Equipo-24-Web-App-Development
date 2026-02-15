package project.Justina.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.Justina.domain.dto.AuthResponseDTO;
import project.Justina.domain.exception.AuthException;
import project.Justina.domain.exception.UserAlreadyExistsException;
import project.Justina.domain.model.User;
import project.Justina.domain.repository.UserRepository;
import project.Justina.infrastructure.security.JwtService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDTO login(String username, String password) {
        // 1. Buscar usuario
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // 2. Validar contraseña
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException("Credenciales inválidas");
        }

        // 3. Generar el Token
        String token = jwtService.createToken(user.getId(), user.getUsername());

        // 4. Retornar el DTO completo con los datos del usuario real
        return new AuthResponseDTO(
                token,
                user.getId(),
                user.getUsername(),
                "Login exitoso"
        );
    }

    public void register(String username, String password) {
        // 1. Validar si ya existe
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("El usuario ya existe");
        }

        // 2. Encriptar clave con BCrypt
        String encodedPassword = passwordEncoder.encode(password);

        // 3. Crear objeto de dominio con rol fijo para el MVP
        User newUser = new User(UUID.randomUUID(), username, encodedPassword, "ROLE_SURGEON");

        // 4. Guardar
        userRepository.save(newUser);
    }

    // Método nuevo para registros internos con rol específico
    public void registerSystemUser(String username, String password, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }

        String encodedPassword = passwordEncoder.encode(password);

        User newUser = new User(UUID.randomUUID(), username, encodedPassword, role);

        userRepository.save(newUser);
    }
}

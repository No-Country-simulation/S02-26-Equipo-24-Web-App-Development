package project.Justina.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import project.Justina.application.service.AuthService;
import project.Justina.domain.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        // 1. Creamos al Cirujano Master (para tus pruebas)
        authService.registerSystemUser("surgeon_master", "justina2024", "ROLE_SURGEON");

        // 2. Creamos a la IA de Justina (para la HU-06)
        authService.registerSystemUser("ia_justina", "ia_secret_2024", "ROLE_AI");

        System.out.println("✅ Usuarios de sistema verificados/creados.");
    }
}

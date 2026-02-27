package project.Justina.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.Justina.domain.repository.UserRepository;
import project.Justina.infrastructure.adapter.entity.UserEntity;
import project.Justina.infrastructure.adapter.mapper.UserMapper;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // 1. Buscar usuario en el dominio
            var domainUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            
            // 2. Convertir a UserEntity para Spring Security
            return userMapper.toEntity(domainUser);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

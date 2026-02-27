package project.Justina.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    private final String issuer = "Justina_Backend";

    public String createToken(UUID userId, String username) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(username)
                .withClaim("userId", userId.toString()) // Metemos el ID como "Claim"
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 86400000)) // 1 dia
                .sign(algorithm);
    }

    // Método para validar y sacar el username
    public String extractUsername(String token) {
        return JWT.require(Algorithm.HMAC256(secretKey))
                .withIssuer(issuer)
                .build()
                .verify(token)
                .getSubject();
    }

    public UUID extractUserId(String token) {
        String id = JWT.require(Algorithm.HMAC256(secretKey))
                .withIssuer(issuer)
                .build()
                .verify(token)
                .getClaim("userId")
                .asString();
        return UUID.fromString(id);
    }

    public boolean isTokenValid(String jwt, UserDetails userDetails) {
        try {
            String usernameFromToken = extractUsername(jwt);
            return (usernameFromToken.equals(userDetails.getUsername()));
        } catch (Exception e) {
            return false;
        }
    }
}

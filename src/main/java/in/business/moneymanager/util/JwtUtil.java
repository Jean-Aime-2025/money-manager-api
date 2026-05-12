package in.business.moneymanager.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;


@Component
public class JwtUtil {

    private final String SECRET_KEY;
    private final long EXPIRATION_TIME;

    public JwtUtil(
            @Value("${app.jwt-secret}") String secretKey,
            @Value("${app.jwt-expiration}") long expirationTime
    ) {
        this.SECRET_KEY = secretKey;
        this.EXPIRATION_TIME = expirationTime;
    }

    public String generateToken(String email) {
        Key hmacKey = new SecretKeySpec(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8),
                SignatureAlgorithm.HS256.getJcaName()
        );

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(hmacKey, SignatureAlgorithm.HS256)
                .compact();
    }


    public String extractUsername(String token) {
        try {
            Key hmacKey = new SecretKeySpec(
                    SECRET_KEY.getBytes(StandardCharsets.UTF_8),
                    SignatureAlgorithm.HS256.getJcaName()
            );

            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(hmacKey)
                    .build()
                    .parseClaimsJws(token);

            return claimsJws.getBody().getSubject(); // email stored as subject
        } catch (JwtException e) {
            // Token is invalid, expired, tampered, etc.
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
    private boolean isTokenExpired(String token) {
        try {
            Key hmacKey = new SecretKeySpec(
                    SECRET_KEY.getBytes(StandardCharsets.UTF_8),
                    SignatureAlgorithm.HS256.getJcaName()
            );
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(hmacKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}

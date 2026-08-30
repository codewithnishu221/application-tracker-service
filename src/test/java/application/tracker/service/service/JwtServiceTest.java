package application.tracker.service.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {


    private JwtService jwtService;
    private static final String SECRET = "5367566859703373367639792F423F4528482B4D6251655468576D5A71347437";
    private static final long EXPIRATION_MS = 3600000;

    @BeforeEach
    void setUp(){
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecretKey",
                SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration",EXPIRATION_MS );
    }

    private String createTestToken(String subjectEmail, Long userId, long ttlMillis){
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(subjectEmail)
                .claim("userId", userId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+ ttlMillis))
                .signWith(key)
                .compact();
    }
    @Test
    @DisplayName("Email extracted from token matches original email")
    void extractEmail_ReturnsCorrectEmail(){
        String token = createTestToken("nk@test.com", 42L, EXPIRATION_MS);
        String email = jwtService.extractEmail(token);
        assertEquals("nk@test.com", email);
    }

    @Test
    @DisplayName("extractUserId correctly extracts custom userId claim")
    void extractUserId_ReturnsCorrectUserId(){
        String token = createTestToken("nk@test.com", 42L, EXPIRATION_MS);
        Long userId = jwtService.extractUserId(token);
        assertEquals(42L, userId);
    }

    @Test
    @DisplayName("validateToken return true for valid token and matching email")
    void validateToken_ReturnsTrue_WhenTokenIsValidAndEmailMatches(){
        String token = createTestToken("nk@test.com", 42L, EXPIRATION_MS);
        boolean isValid = jwtService.validateToken(token, "nk@test.com");
        assertTrue(isValid);
    }

    @Test
    @DisplayName("extractEmail throws JwtException when token is expired")
    void extractEmail_ThrowsException_WhenTokenIsExpired(){
        String expiredToken = createTestToken("expired@domain.com", 5L, -1000);

        assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                ()-> jwtService.extractEmail(expiredToken));
    }

    @Test
    @DisplayName("extractEmail throws JwtException when token signature is invalid")
    void extractEmail_ThrowsException_WhenSignatureIsTampered(){
        String validToken = createTestToken("nk@test.com",1L, EXPIRATION_MS);
        String tamperedToken = validToken.substring(0, validToken.length() -6)+ "abcdef";
        assertThrows(io.jsonwebtoken.security.SignatureException.class, ()->
         jwtService.extractEmail(tamperedToken));
    }
}

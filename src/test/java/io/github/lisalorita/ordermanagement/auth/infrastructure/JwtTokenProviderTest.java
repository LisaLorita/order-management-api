package io.github.lisalorita.ordermanagement.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String SECRET_KEY = "357638792F423F4528482B4D6251655468576D5A7134743777217A25432A462D";

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", SECRET_KEY);
    }

    @Test
    @DisplayName("Should generate a valid token and extract username")
    void shouldGenerateAndExtract() {
        String email = "test@example.com";
        String token = jwtTokenProvider.generateToken(email);

        assertNotNull(token);
        assertEquals(email, jwtTokenProvider.extractUsername(token));
    }

    @Test
    @DisplayName("Should validate a correct token")
    void shouldValidateToken() {
        String email = "test@example.com";
        String token = jwtTokenProvider.generateToken(email);

        assertTrue(jwtTokenProvider.isTokenValid(token, email));
    }

    @Test
    @DisplayName("Should return false for invalid username")
    void shouldFailForWrongUser() {
        String email = "test@example.com";
        String token = jwtTokenProvider.generateToken(email);

        assertFalse(jwtTokenProvider.isTokenValid(token, "other@example.com"));
    }
}

package io.hexlet.spring.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class JWTUtilsTest {

    @Autowired
    private JWTUtils jwtUtils;

    @Test
    public void testGenerateToken() {
        String token = jwtUtils.generateToken("test@example.com");

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT состоит из 3 частей
    }

    @Test
    public void testGenerateTokenForDifferentUsers() {
        String token1 = jwtUtils.generateToken("user1@example.com");
        String token2 = jwtUtils.generateToken("user2@example.com");

        assertThat(token1).isNotEqualTo(token2);
    }
}

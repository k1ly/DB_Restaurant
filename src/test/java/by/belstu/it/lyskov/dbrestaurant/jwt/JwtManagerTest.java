package by.belstu.it.lyskov.dbrestaurant.jwt;

import by.belstu.it.lyskov.dbrestaurant.auth.JwtManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class JwtManagerTest {

    @Autowired
    private JwtManager jwtManager;

    @Test
    void shouldValidateTokenIfUserDetailsIsValid() {
//        UserDetails userDetails = new UserDetailsInfo(1L, "login", "secret", "", false, null);
//        String token = jwtManager.generateJwt(userDetails);
//        assertTrue(jwtManager.validateJwt(token, userDetails));
    }
}
package by.belstu.it.lyskov.dbrestaurant.util.validation;

import by.belstu.it.lyskov.dbrestaurant.controller.validation.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class PasswordValidatorTest {

    private PasswordValidator passwordValidator;

    @BeforeEach
    void init() {
        passwordValidator = new PasswordValidator();
    }

    @Test
    void shouldReturnFalseIfPasswordIsInvalid() {
        String invalidPassword = "this password is invalid";
        assertFalse(passwordValidator.isValid(invalidPassword, null));
    }
}
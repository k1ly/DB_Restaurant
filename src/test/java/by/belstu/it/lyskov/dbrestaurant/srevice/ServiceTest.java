package by.belstu.it.lyskov.dbrestaurant.srevice;

import by.belstu.it.lyskov.dbrestaurant.exception.UserNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void shouldThrowExceptionIfUserNotExists() {
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(0L));
    }
}

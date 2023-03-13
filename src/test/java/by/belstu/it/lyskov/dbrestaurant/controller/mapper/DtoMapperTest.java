package by.belstu.it.lyskov.dbrestaurant.controller.mapper;

import by.belstu.it.lyskov.dbrestaurant.dto.newdto.NewUserDto;
import by.belstu.it.lyskov.dbrestaurant.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class DtoMapperTest {

    @Autowired
    private DtoMapper dtoMapper;

    @Test
    void mappedUserFieldsShouldBeEqualToUserDtoFields() {
        NewUserDto userDto = new NewUserDto();
        userDto.setLogin("login");
        userDto.setName("test");
        userDto.setPassword("secret");
        User user = dtoMapper.map(userDto, User.class);
        assertEquals(user.getLogin(), userDto.getLogin());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getPassword(), userDto.getPassword());
    }
}
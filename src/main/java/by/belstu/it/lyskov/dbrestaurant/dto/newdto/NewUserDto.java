package by.belstu.it.lyskov.dbrestaurant.dto.newdto;

import by.belstu.it.lyskov.dbrestaurant.controller.validation.Password;
import by.belstu.it.lyskov.dbrestaurant.controller.validation.PasswordMatcher;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@PasswordMatcher
public class NewUserDto {

    @NotBlank
    private String login;

    @Password
    private String password;

    private String matchingPsw;

    @NotBlank
    private String name;

    private String email;

    private String phone;
}

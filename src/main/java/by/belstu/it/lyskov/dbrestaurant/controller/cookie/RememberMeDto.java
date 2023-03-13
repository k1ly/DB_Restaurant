package by.belstu.it.lyskov.dbrestaurant.controller.cookie;

import lombok.Data;

@Data
public class RememberMeDto {

    private String login;

    private String hash;
}

package by.belstu.it.lyskov.dbrestaurant.auth;

import by.belstu.it.lyskov.dbrestaurant.dto.OrderDto;
import by.belstu.it.lyskov.dbrestaurant.entity.Role;
import lombok.Data;

@Data
public class UserAuthDto {

    private long id;

    private String login;

    private String name;

    private String email;

    private String phone;

    private boolean blocked;

    private Role role;

    private OrderDto order;
}

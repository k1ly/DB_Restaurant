package by.belstu.it.lyskov.dbrestaurant.dto;

import by.belstu.it.lyskov.dbrestaurant.entity.Role;
import lombok.Data;

@Data
public class UserDto {

    private long id;

    private String name;

    private String email;

    private String phone;

    private boolean blocked;

    private Role role;

    private OrderDto order;
}

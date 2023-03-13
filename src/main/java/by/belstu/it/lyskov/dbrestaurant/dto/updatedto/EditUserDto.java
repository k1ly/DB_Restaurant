package by.belstu.it.lyskov.dbrestaurant.dto.updatedto;

import by.belstu.it.lyskov.dbrestaurant.entity.Role;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EditUserDto {

    @NotNull
    private boolean blocked;

    @NotNull
    private Role role;
}

package by.belstu.it.lyskov.dbrestaurant.dto.updatedto;

import by.belstu.it.lyskov.dbrestaurant.dto.UserDto;
import by.belstu.it.lyskov.dbrestaurant.entity.Status;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UpdateOrderDto {

    @NotNull
    private Status status;

    private UserDto manager;
}

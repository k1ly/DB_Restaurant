package by.belstu.it.lyskov.dbrestaurant.dto.newdto;

import by.belstu.it.lyskov.dbrestaurant.dto.DishDto;
import by.belstu.it.lyskov.dbrestaurant.dto.OrderDto;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
public class NewOrderItemDto {

    @PositiveOrZero
    private int quantity;

    @NotNull
    private DishDto dish;

    @NotNull
    private OrderDto order;
}

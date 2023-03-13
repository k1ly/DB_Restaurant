package by.belstu.it.lyskov.dbrestaurant.dto;

import lombok.Data;

import javax.validation.constraints.PositiveOrZero;

@Data
public class CookieCartItemDto {

    @PositiveOrZero
    private int quantity;

    private long dish;
}

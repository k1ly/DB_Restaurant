package by.belstu.it.lyskov.dbrestaurant.dto.updatedto;

import lombok.Data;

import javax.validation.constraints.PositiveOrZero;

@Data
public class UpdateOrderItemDto {

    @PositiveOrZero
    private int quantity;
}

package by.belstu.it.lyskov.dbrestaurant.dto.updatedto;

import by.belstu.it.lyskov.dbrestaurant.dto.AddressDto;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
public class ConfirmOrderDto {

    @PositiveOrZero
    private double price;

    @NotNull
    private LocalDateTime specifiedDate;

    @NotNull
    private AddressDto address;
}

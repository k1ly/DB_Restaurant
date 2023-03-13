package by.belstu.it.lyskov.dbrestaurant.dto.newdto;

import by.belstu.it.lyskov.dbrestaurant.dto.UserDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class NewAddressDto {

    @NotBlank
    private String country;

    @NotBlank
    private String locality;

    private String street;

    @NotBlank
    private String house;

    private String apartment;

    @NotNull
    private UserDto user;
}

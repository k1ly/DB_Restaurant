package by.belstu.it.lyskov.dbrestaurant.dto;

import lombok.Data;

@Data
public class AddressDto {

    private long id;

    private String country;

    private String locality;

    private String street;

    private String house;

    private String apartment;

    private UserDto user;
}

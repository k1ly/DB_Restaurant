package by.belstu.it.lyskov.dbrestaurant.dto.updatedto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateUserDto {

    @NotBlank
    private String name;

    private String email;

    private String phone;
}

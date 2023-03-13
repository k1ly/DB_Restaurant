package by.belstu.it.lyskov.dbrestaurant.dto.newdto;

import by.belstu.it.lyskov.dbrestaurant.dto.UserDto;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class NewReviewDto {

    @Min(1)
    @Max(5)
    private int grade;

    @NotBlank
    private String comment;

    @NotNull
    private UserDto user;
}

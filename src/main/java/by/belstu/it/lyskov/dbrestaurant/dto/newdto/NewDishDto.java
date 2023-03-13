package by.belstu.it.lyskov.dbrestaurant.dto.newdto;

import by.belstu.it.lyskov.dbrestaurant.dto.CategoryDto;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class NewDishDto {

    @NotBlank
    private String name;

    @NotNull
    private String description;

    private String imageUrl;

    @PositiveOrZero
    private int weight;

    @PositiveOrZero
    private double price;

    @Min(0)
    @Max(100)
    private int discount;

    @NotNull
    private CategoryDto category;
}

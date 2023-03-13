package by.belstu.it.lyskov.dbrestaurant.dto;

import lombok.Data;

@Data
public class DishDto {

    private long id;

    private String name;

    private String description;

    private String imageUrl;

    private int weight;

    private double price;

    private int discount;

    private CategoryDto category;
}

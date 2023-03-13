package by.belstu.it.lyskov.dbrestaurant.dto;

import lombok.Data;

@Data
public class OrderItemDto {

    private long id;

    private int quantity;

    private DishDto dish;

    private OrderDto order;
}

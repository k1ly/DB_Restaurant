package by.belstu.it.lyskov.dbrestaurant.dto;

import by.belstu.it.lyskov.dbrestaurant.entity.Status;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class OrderDto {

    private long id;

    private double price;

    private LocalDateTime specifiedDate;

    private Timestamp orderDate;

    private Timestamp deliveryDate;

    private AddressDto address;

    private Status status;

    private UserDto customer;

    private UserDto manager;
}

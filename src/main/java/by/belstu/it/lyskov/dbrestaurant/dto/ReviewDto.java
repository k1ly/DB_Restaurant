package by.belstu.it.lyskov.dbrestaurant.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ReviewDto {

    private long id;

    private int grade;

    private String comment;

    private Timestamp date;

    private UserDto user;
}

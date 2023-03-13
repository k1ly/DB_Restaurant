package by.belstu.it.lyskov.dbrestaurant.entity;

import by.belstu.it.lyskov.dbrestaurant.util.Column;
import by.belstu.it.lyskov.dbrestaurant.util.Id;
import by.belstu.it.lyskov.dbrestaurant.util.Relationship;
import by.belstu.it.lyskov.dbrestaurant.util.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(tableName = "dishes")
public class Dish implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnName = "id")
    private long id;

    @Column(columnName = "name")
    private String name;

    @Column(columnName = "description", isSortable = false)
    private String description;

    @Column(columnName = "image_url", isNullable = true, isSortable = false)
    private String imageUrl;

    @Column(columnName = "weight")
    private int weight;

    @Column(columnName = "price")
    private double price;

    @Column(columnName = "discount")
    private int discount;

    @Relationship(refColumnName = "category_id")
    private Category category;
}

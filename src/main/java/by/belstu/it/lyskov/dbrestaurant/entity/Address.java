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
@Table(tableName = "addresses")
public class Address implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnName = "id")
    private long id;

    @Column(columnName = "country")
    private String country;

    @Column(columnName = "locality")
    private String locality;

    @Column(columnName = "street", isNullable = true)
    private String street;

    @Column(columnName = "house")
    private String house;

    @Column(columnName = "apartment", isNullable = true)
    private String apartment;

    @Relationship(refColumnName = "user_id")
    private User user;
}

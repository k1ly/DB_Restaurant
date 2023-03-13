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
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(tableName = "reviews")
public class Review implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnName = "id")
    private long id;

    @Column(columnName = "grade")
    private int grade;

    @Column(columnName = "comment", isSortable = false)
    private String comment;

    @Column(columnName = "date")
    private Timestamp date;

    @Relationship(refColumnName = "user_id")
    private User user;
}

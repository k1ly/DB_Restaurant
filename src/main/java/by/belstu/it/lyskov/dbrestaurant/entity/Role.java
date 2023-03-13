package by.belstu.it.lyskov.dbrestaurant.entity;

import by.belstu.it.lyskov.dbrestaurant.util.Column;
import by.belstu.it.lyskov.dbrestaurant.util.Id;
import by.belstu.it.lyskov.dbrestaurant.util.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(tableName = "roles")
public class Role implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnName = "id")
    private long id;

    @Column(columnName = "name")
    private String name;
}

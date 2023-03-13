package by.belstu.it.lyskov.dbrestaurant.entity;

import by.belstu.it.lyskov.dbrestaurant.util.Column;
import by.belstu.it.lyskov.dbrestaurant.util.Relationship;
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
@Table(tableName = "users")
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnName = "id")
    private long id;

    @Column(columnName = "login")
    private String login;

    @Column(columnName = "password", isSortable = false)
    private String password;

    @Column(columnName = "name")
    private String name;

    @Column(columnName = "email", isNullable = true)
    private String email;

    @Column(columnName = "phone", isNullable = true)
    private String phone;

    @Column(columnName = "blocked")
    private boolean blocked;

    @Relationship(refColumnName = "role_id")
    private Role role;

    @Relationship(refColumnName = "order_id", isSortable = false)
    private Order order;
}

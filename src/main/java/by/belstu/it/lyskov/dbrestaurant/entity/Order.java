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
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(tableName = "orders")
public class Order implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnName = "id")
    private long id;

    @Column(columnName = "price")
    private Double price;

    @Column(columnName = "specified_date")
    private LocalDateTime specifiedDate;

    @Column(columnName = "order_date")
    private Timestamp orderDate;

    @Column(columnName = "delivery_date")
    private Timestamp deliveryDate;

    @Relationship(refColumnName = "address_id")
    private Address address;

    @Relationship(refColumnName = "status_id")
    private Status status;

    @Relationship(refColumnName = "customer_id")
    private User customer;

    @Relationship(refColumnName = "manager_id")
    private User manager;

    private List<OrderItem> orderItems;
}

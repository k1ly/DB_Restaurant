package by.belstu.it.lyskov.dbrestaurant.repository;

import by.belstu.it.lyskov.dbrestaurant.entity.OrderItem;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;

import java.util.Optional;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {

    Optional<OrderItem> findByOrderAndDish(Long orderId, Long dishId) throws RepositoryException;

    Page<OrderItem> findByOrder(Long orderId, Pageable pageable) throws RepositoryException;

    long countByOrder(Long orderId) throws RepositoryException;

    double countTotalPrice(Long orderId) throws RepositoryException;
}

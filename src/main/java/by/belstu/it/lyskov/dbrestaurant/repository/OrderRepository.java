package by.belstu.it.lyskov.dbrestaurant.repository;

import by.belstu.it.lyskov.dbrestaurant.entity.Order;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {

    Optional<Order> findByCustomer(Long customerId) throws RepositoryException;

    Page<Order> findByStatus(Long statusId, Pageable pageable) throws RepositoryException;

    long countByStatus(Long statusId) throws RepositoryException;

    Page<Order> findByStatusAndCustomer(Long statusId, Long customerId, Pageable pageable) throws RepositoryException;

    long countByStatusAndCustomer(Long statusId, Long customerId) throws RepositoryException;

    void confirm(Long id, Order order) throws RepositoryException;

    void cancel(Long id) throws RepositoryException;
}

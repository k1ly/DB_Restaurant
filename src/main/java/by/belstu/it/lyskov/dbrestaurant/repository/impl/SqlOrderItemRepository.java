package by.belstu.it.lyskov.dbrestaurant.repository.impl;

import by.belstu.it.lyskov.dbrestaurant.entity.OrderItem;
import by.belstu.it.lyskov.dbrestaurant.exception.EntityMappingException;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.repository.OrderItemRepository;
import by.belstu.it.lyskov.dbrestaurant.repository.ProcedureExecutor;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.PageImpl;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SqlOrderItemRepository implements OrderItemRepository {

    private final ProcedureExecutor executor;

    public SqlOrderItemRepository(ProcedureExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Optional<OrderItem> findByOrderAndDish(Long orderId, Long dishId) throws RepositoryException {
        try {
            List<Object> parameters = new ArrayList<>(List.of(orderId, dishId));
            return executor.executeSingleResultQuery("{call find_order_item_by_order_and_dish (?,?)}", OrderItem.class, parameters.toArray());
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public Page<OrderItem> findByOrder(Long orderId, Pageable pageable) throws RepositoryException {
        List<OrderItem> list;
        try {
            list = executor.executePage("{call find_order_items_by_order_paged (?,?,?,?,?)}", pageable, OrderItem.class, orderId);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
        return list.size() > 0 ? new PageImpl<>(list, pageable, countByOrder(orderId)) : Page.empty(pageable);
    }

    @Override
    public long countByOrder(Long orderId) throws RepositoryException {
        try {
            return (long) executor.executeScalar("{? = call count_order_items_by_order (?)}", orderId);
        } catch (SQLException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public double countTotalPrice(Long orderId) throws RepositoryException {
        try {
            return (double) executor.executeScalar("{? = call count_order_item_total_price (?)}", orderId);
        } catch (SQLException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public Optional<OrderItem> findById(Long id) throws RepositoryException {
        try {
            return executor.executeFindById(id, OrderItem.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public boolean existsById(Long id) throws RepositoryException {
        try {
            return executor.executeFindById(id, OrderItem.class).isPresent();
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void save(OrderItem orderItem) throws RepositoryException {
        try {
            executor.executeInsert(orderItem);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void update(Long id, OrderItem orderItem) throws RepositoryException {
        try {
            executor.executeUpdate(id, orderItem);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        try {
            executor.executeDelete(id, OrderItem.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public long count() throws RepositoryException {
        try {
            return executor.executeCount(OrderItem.class);
        } catch (SQLException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }
}

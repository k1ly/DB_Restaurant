package by.belstu.it.lyskov.dbrestaurant.repository.impl;

import by.belstu.it.lyskov.dbrestaurant.entity.Order;
import by.belstu.it.lyskov.dbrestaurant.exception.EntityMappingException;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.repository.OrderRepository;
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
public class SqlOrderRepository implements OrderRepository {

    private final ProcedureExecutor executor;

    public SqlOrderRepository(ProcedureExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Optional<Order> findByCustomer(Long customerId) throws RepositoryException {
        try {
            return executor.executeSingleResultQuery("{call find_order_by_customer (?)}", Order.class, customerId);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public Page<Order> findByStatus(Long statusId, Pageable pageable) throws RepositoryException {
        List<Order> list;
        try {
            list = executor.executePage("{call find_orders_by_status_paged (?,?,?,?,?)}", pageable, Order.class, statusId);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
        return list.size() > 0 ? new PageImpl<>(list, pageable, countByStatus(statusId)) : Page.empty(pageable);
    }

    @Override
    public long countByStatus(Long statusId) throws RepositoryException {
        try {
            return (long) executor.executeScalar("{? = call count_orders_by_status (?)}", statusId);
        } catch (SQLException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public Page<Order> findByStatusAndCustomer(Long statusId, Long customerId, Pageable pageable) throws RepositoryException {
        List<Order> list;
        try {
            list = executor.executePage("{call find_orders_by_status_and_customer_paged (?,?,?,?,?,?)}", pageable, Order.class, statusId, customerId);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
        return list.size() > 0 ? new PageImpl<>(list, pageable, countByStatusAndCustomer(statusId, customerId)) : Page.empty(pageable);
    }

    @Override
    public long countByStatusAndCustomer(Long statusId, Long customerId) throws RepositoryException {
        try {
            return (long) executor.executeScalar("{? = call count_orders_by_status_and_customer (?,?)}", statusId);
        } catch (SQLException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void confirm(Long id, Order order) throws RepositoryException {
        try {
            List<Object> parameters = new ArrayList<>(List.of(id));
            parameters.addAll(executor.takeFields(order).values());
            executor.executeUpdate("{call confirm_order (?,?,?,?)}", parameters.toArray());
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void cancel(Long id) throws RepositoryException {
        try {
            executor.executeUpdate("{call cancel_order (?)}", new ArrayList<>(List.of(id)).toArray());
        } catch (SQLException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public Optional<Order> findById(Long id) throws RepositoryException {
        try {
            return executor.executeFindById(id, Order.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public boolean existsById(Long id) throws RepositoryException {
        try {
            return executor.executeFindById(id, Order.class).isPresent();
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void save(Order order) throws RepositoryException {
        try {
            executor.executeInsert(order);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void update(Long id, Order order) throws RepositoryException {
        try {
            executor.executeUpdate(id, order);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        try {
            executor.executeDelete(id, Order.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public long count() throws RepositoryException {
        try {
            return executor.executeCount(Order.class);
        } catch (SQLException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }
}

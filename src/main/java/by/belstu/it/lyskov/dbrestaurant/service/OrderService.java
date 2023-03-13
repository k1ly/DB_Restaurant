package by.belstu.it.lyskov.dbrestaurant.service;

import by.belstu.it.lyskov.dbrestaurant.entity.Order;
import by.belstu.it.lyskov.dbrestaurant.exception.BadReferenceException;
import by.belstu.it.lyskov.dbrestaurant.exception.OrderNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;

public interface OrderService {

    Order findOrderById(Long id) throws OrderNotFoundException, ServiceException;

    Page<Order> findOrdersByStatus(Long statusId, Pageable pageable) throws BadReferenceException, ServiceException;

    Page<Order> findOrdersByStatusAndCustomer(Long statusId, Long customerId, Pageable pageable) throws BadReferenceException, ServiceException;

    void updateOrder(Long id, Order order) throws OrderNotFoundException, BadReferenceException, ServiceException;

    void confirmOrder(Long id, Order order) throws OrderNotFoundException, BadReferenceException, ServiceException;

    void cancelOrder(Long id) throws OrderNotFoundException, ServiceException;
}

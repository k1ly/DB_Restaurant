package by.belstu.it.lyskov.dbrestaurant.service;

import by.belstu.it.lyskov.dbrestaurant.entity.OrderItem;
import by.belstu.it.lyskov.dbrestaurant.exception.BadReferenceException;
import by.belstu.it.lyskov.dbrestaurant.exception.OrderItemNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;

public interface OrderItemService {

    OrderItem findOrderItemByOrderAndDish(Long orderId, Long dishId) throws OrderItemNotFoundException, BadReferenceException, ServiceException;

    Page<OrderItem> findOrderItemsByOrder(Long orderId, Pageable pageable) throws BadReferenceException, ServiceException;

    double countOrderItemTotalPrice(Long orderId) throws BadReferenceException, ServiceException;

    void addOrderItem(OrderItem orderItem) throws BadReferenceException, ServiceException;

    void updateOrderItem(Long id, OrderItem orderItem) throws OrderItemNotFoundException, ServiceException;

    void deleteOrderItem(Long id) throws OrderItemNotFoundException, ServiceException;
}

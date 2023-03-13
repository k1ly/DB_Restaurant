package by.belstu.it.lyskov.dbrestaurant.service.impl;

import by.belstu.it.lyskov.dbrestaurant.entity.OrderItem;
import by.belstu.it.lyskov.dbrestaurant.exception.BadReferenceException;
import by.belstu.it.lyskov.dbrestaurant.exception.OrderItemNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.repository.DishRepository;
import by.belstu.it.lyskov.dbrestaurant.repository.OrderItemRepository;
import by.belstu.it.lyskov.dbrestaurant.repository.OrderRepository;
import by.belstu.it.lyskov.dbrestaurant.service.OrderItemService;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, OrderRepository orderRepository, DishRepository dishRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.dishRepository = dishRepository;
    }

    @Override
    public OrderItem findOrderItemByOrderAndDish(Long orderId, Long dishId) throws OrderItemNotFoundException, BadReferenceException, ServiceException {
        try {
            if (!orderRepository.existsById(orderId))
                throw new BadReferenceException("Order with id \"" + orderId + "\" doesn't exist!");
            if (!dishRepository.existsById(dishId))
                throw new BadReferenceException("Dish with id \"" + dishId + "\" doesn't exist!");
            return orderItemRepository.findByOrderAndDish(orderId, dishId)
                    .orElseThrow(() -> new OrderItemNotFoundException("Order item with order id \"" + orderId + "\" and dish id \"" + dishId + "\" doesn't exist!"));
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to find order item by order and dish", e);
        }
    }

    @Override
    public Page<OrderItem> findOrderItemsByOrder(Long orderId, Pageable pageable) throws BadReferenceException, ServiceException {
        try {
            if (!orderRepository.existsById(orderId))
                throw new BadReferenceException("Order with id \"" + orderId + "\" doesn't exist!");
            return orderItemRepository.findByOrder(orderId, pageable);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to find order items by order", e);
        }
    }

    @Override
    public double countOrderItemTotalPrice(Long orderId) throws BadReferenceException, ServiceException {
        try {
            if (!orderRepository.existsById(orderId))
                throw new BadReferenceException("Order with id \"" + orderId + "\" doesn't exist!");
            return orderItemRepository.countTotalPrice(orderId);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to count order items total price", e);
        }
    }

    @Override
    public void addOrderItem(OrderItem orderItem) throws BadReferenceException, ServiceException {
        try {
            if (!orderRepository.existsById(orderItem.getOrder().getId()))
                throw new BadReferenceException("Order with id \"" + orderItem.getOrder().getId() + "\" doesn't exist!");
            if (!dishRepository.existsById(orderItem.getDish().getId()))
                throw new BadReferenceException("Dish with id \"" + orderItem.getDish().getId() + "\" doesn't exist!");
            orderItemRepository.save(orderItem);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to add order item", e);
        }
    }

    @Override
    public void updateOrderItem(Long id, OrderItem orderItem) throws OrderItemNotFoundException, ServiceException {
        try {
            if (!orderItemRepository.existsById(id))
                throw new OrderItemNotFoundException("Order item with id \"" + id + "\" doesn't exist!");
            orderItemRepository.update(id, orderItem);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to update order item", e);
        }
    }

    @Override
    public void deleteOrderItem(Long id) throws OrderItemNotFoundException, ServiceException {
        try {
            if (!orderItemRepository.existsById(id))
                throw new OrderItemNotFoundException("Order item with id \"" + id + "\" doesn't exist!");
            orderItemRepository.delete(id);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to delete order item", e);
        }
    }
}

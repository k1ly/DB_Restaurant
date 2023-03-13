package by.belstu.it.lyskov.dbrestaurant.service.impl;

import by.belstu.it.lyskov.dbrestaurant.entity.Order;
import by.belstu.it.lyskov.dbrestaurant.exception.BadReferenceException;
import by.belstu.it.lyskov.dbrestaurant.exception.OrderNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.repository.AddressRepository;
import by.belstu.it.lyskov.dbrestaurant.repository.OrderRepository;
import by.belstu.it.lyskov.dbrestaurant.repository.StatusRepository;
import by.belstu.it.lyskov.dbrestaurant.repository.UserRepository;
import by.belstu.it.lyskov.dbrestaurant.service.OrderService;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public OrderServiceImpl(OrderRepository orderRepository, StatusRepository statusRepository,
                            UserRepository userRepository, AddressRepository addressRepository) {
        this.orderRepository = orderRepository;
        this.statusRepository = statusRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public Order findOrderById(Long id) throws OrderNotFoundException, ServiceException {
        try {
            return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Order with id \"" + id + "\" doesn't exist!"));
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to find order by id", e);
        }
    }

    @Override
    public Page<Order> findOrdersByStatus(Long statusId, Pageable pageable) throws BadReferenceException, ServiceException {
        try {
            if (!statusRepository.existsById(statusId))
                throw new BadReferenceException("Status with id \"" + statusId + "\" doesn't exist!");
            return orderRepository.findByStatus(statusId, pageable);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to find orders by status", e);
        }
    }

    @Override
    public Page<Order> findOrdersByStatusAndCustomer(Long statusId, Long customerId, Pageable pageable) throws BadReferenceException, ServiceException {
        try {
            if (!statusRepository.existsById(statusId))
                throw new BadReferenceException("Status with id \"" + statusId + "\" doesn't exist!");
            if (!userRepository.existsById(customerId))
                throw new BadReferenceException("User with id \"" + customerId + "\" doesn't exist!");
            return orderRepository.findByStatusAndCustomer(statusId, customerId, pageable);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to find orders by status and customer", e);
        }
    }

    @Override
    public void updateOrder(Long id, Order order) throws OrderNotFoundException, BadReferenceException, ServiceException {
        try {
            if (!orderRepository.existsById(id))
                throw new OrderNotFoundException("Order with id \"" + id + "\" doesn't exist!");
            if (!statusRepository.existsById(order.getStatus().getId()))
                throw new BadReferenceException("Status with id \"" + order.getStatus().getId() + "\" doesn't exist!");
            if (order.getManager() != null && !userRepository.existsById(order.getManager().getId()))
                throw new BadReferenceException("User with id \"" + order.getManager().getId() + "\" doesn't exist!");
            orderRepository.update(id, order);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to update order", e);
        }
    }

    @Override
    public void confirmOrder(Long id, Order order) throws OrderNotFoundException, BadReferenceException, ServiceException {
        try {
            if (!orderRepository.existsById(id))
                throw new OrderNotFoundException("Order with id \"" + id + "\" doesn't exist!");
            if (order.getAddress() != null && !addressRepository.existsById(order.getAddress().getId()))
                throw new BadReferenceException("Address with id \"" + order.getAddress().getId() + "\" doesn't exist!");
            orderRepository.confirm(id, order);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to confirm order", e);
        }
    }

    @Override
    public void cancelOrder(Long id) throws OrderNotFoundException, ServiceException {
        try {
            if (!orderRepository.existsById(id))
                throw new OrderNotFoundException("Order with id \"" + id + "\" doesn't exist!");
            orderRepository.cancel(id);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to cancel order", e);
        }
    }
}

package by.belstu.it.lyskov.dbrestaurant.service.impl;

import by.belstu.it.lyskov.dbrestaurant.entity.*;
import by.belstu.it.lyskov.dbrestaurant.exception.*;
import by.belstu.it.lyskov.dbrestaurant.repository.*;
import by.belstu.it.lyskov.dbrestaurant.service.UserService;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository, OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public User findUserById(Long id) throws UserNotFoundException, ServiceException {
        try {
            return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id \"" + id + "\" doesn't exist!"));
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to find user by id", e);
        }
    }

    @Override
    public Page<User> getUsers(Pageable pageable) throws ServiceException {
        try {
            return userRepository.findAll(pageable);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to get users", e);
        }
    }

    @Override
    public Page<User> findUsersByFilter(String filter, Pageable pageable) throws ServiceException {
        try {
            return userRepository.findByFilter(filter, pageable);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to get users by filter", e);
        }
    }

    @Override
    public void registerUser(User user) throws UserAlreadyExistsException, ServiceException {
        try {
            if (userRepository.findByLogin(user.getLogin()).isPresent())
                throw new UserAlreadyExistsException("User already exists!");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            try {
                userRepository.register(user);
                User newUser = userRepository.findByLogin(user.getLogin()).orElseThrow(() ->
                        new UserNotFoundException("User with login \"" + user.getLogin() + "\" doesn't exist!"));
                Order order = orderRepository.findByCustomer(newUser.getId())
                        .orElseThrow(() -> new OrderNotFoundException("Order with customer id \"" + user.getId() + "\" doesn't exist!"));
                userRepository.beginTransaction();
                if (user.getOrder() != null && user.getOrder().getOrderItems() != null)
                    for (OrderItem orderItem : user.getOrder().getOrderItems()) {
                        orderItem.setOrder(order);
                        orderItemRepository.save(orderItem);
                    }
                userRepository.commitTransaction();
            } catch (RepositoryException | TransactionException |
                     UserNotFoundException | OrderNotFoundException e) {
                userRepository.rollbackTransaction();
                throw new ServiceException(e.getMessage(), e);
            }
        } catch (RepositoryException | TransactionException | ServiceException e) {
            throw new ServiceException("Failed to register user", e);
        }
    }

    @Override
    public void updateUser(Long id, User user) throws UserNotFoundException, ServiceException {
        try {
            if (!userRepository.existsById(id))
                throw new UserNotFoundException("User with id \"" + id + "\" doesn't exist!");
            userRepository.update(id, user);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to update user settings", e);
        }
    }

    @Override
    public void editUser(Long id, User user) throws UserNotFoundException, BadReferenceException, ServiceException {
        try {
            if (!userRepository.existsById(id))
                throw new UserNotFoundException("User with id \"" + id + "\" doesn't exist!");
            if (!roleRepository.existsById(user.getRole().getId()))
                throw new BadReferenceException("Role with name \"" + user.getRole().getId() + "\" doesn't exist!");
            userRepository.edit(id, user);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to edit user", e);
        }
    }
}

package by.belstu.it.lyskov.dbrestaurant.service;

import by.belstu.it.lyskov.dbrestaurant.entity.User;
import by.belstu.it.lyskov.dbrestaurant.exception.*;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;

public interface UserService {

    User findUserById(Long id) throws UserNotFoundException, ServiceException;

    Page<User> getUsers(Pageable pageable) throws ServiceException;

    Page<User> findUsersByFilter(String filter, Pageable pageable) throws ServiceException;

    void registerUser(User user) throws UserAlreadyExistsException, ServiceException;

    void updateUser(Long id, User user) throws UserNotFoundException, BadReferenceException, ServiceException;

    void editUser(Long id, User user) throws UserNotFoundException, BadReferenceException, ServiceException;
}

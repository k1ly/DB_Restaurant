package by.belstu.it.lyskov.dbrestaurant.service;

import by.belstu.it.lyskov.dbrestaurant.entity.Dish;
import by.belstu.it.lyskov.dbrestaurant.exception.BadReferenceException;
import by.belstu.it.lyskov.dbrestaurant.exception.DishNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;

public interface DishService {

    Dish findDishById(Long id) throws DishNotFoundException, ServiceException;

    Page<Dish> getDishes(Pageable pageable) throws ServiceException;

    Page<Dish> findDishesByFilter(String filter, Pageable pageable) throws ServiceException;

    Page<Dish> findDishesByCategory(Long categoryId, Pageable pageable) throws BadReferenceException, ServiceException;

    void addDish(Dish dish) throws BadReferenceException, ServiceException;

    void updateDish(Long id, Dish dish) throws DishNotFoundException, BadReferenceException, ServiceException;

    void deleteDish(Long id) throws DishNotFoundException, ServiceException;
}

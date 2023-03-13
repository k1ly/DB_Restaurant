package by.belstu.it.lyskov.dbrestaurant.service.impl;

import by.belstu.it.lyskov.dbrestaurant.entity.Dish;
import by.belstu.it.lyskov.dbrestaurant.exception.BadReferenceException;
import by.belstu.it.lyskov.dbrestaurant.exception.DishNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.repository.CategoryRepository;
import by.belstu.it.lyskov.dbrestaurant.repository.DishRepository;
import by.belstu.it.lyskov.dbrestaurant.service.DishService;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;

    public DishServiceImpl(DishRepository dishRepository, CategoryRepository categoryRepository) {
        this.dishRepository = dishRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Dish findDishById(Long id) throws DishNotFoundException, ServiceException {
        try {
            return dishRepository.findById(id).orElseThrow(() -> new DishNotFoundException("Dish with id \"" + id + "\" doesn't exist!"));
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to find dish by id", e);
        }
    }

    @Override
    public Page<Dish> getDishes(Pageable pageable) throws ServiceException {
        try {
            return dishRepository.findAll(pageable);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to get all dishes", e);
        }
    }

    @Override
    public Page<Dish> findDishesByFilter(String filter, Pageable pageable) throws ServiceException {
        try {
            return dishRepository.findByFilter(filter, pageable);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to find dishes by filter", e);
        }
    }

    @Override
    public Page<Dish> findDishesByCategory(Long categoryId, Pageable pageable) throws BadReferenceException, ServiceException {
        try {
            if (!categoryRepository.existsById(categoryId))
                throw new BadReferenceException("Category with id \"" + categoryId + "\" doesn't exist!");
            return dishRepository.findByCategory(categoryId, pageable);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to find dishes by category", e);
        }
    }

    @Override
    public void addDish(Dish dish) throws BadReferenceException, ServiceException {
        try {
            if (!categoryRepository.existsById(dish.getCategory().getId()))
                throw new BadReferenceException("Category with id \"" + dish.getCategory().getId() + "\" doesn't exist!");
            dishRepository.save(dish);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to add dish", e);
        }
    }

    @Override
    public void updateDish(Long id, Dish dish) throws DishNotFoundException, BadReferenceException, ServiceException {
        try {
            if (!dishRepository.existsById(id))
                throw new DishNotFoundException("Dish with id \"" + id + "\" doesn't exist!");
            if (!categoryRepository.existsById(dish.getCategory().getId()))
                throw new BadReferenceException("Category with id \"" + dish.getCategory().getId() + "\" doesn't exist!");
            dishRepository.update(id, dish);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to update dish", e);
        }
    }

    @Override
    public void deleteDish(Long id) throws DishNotFoundException, ServiceException {
        try {
            if (!dishRepository.existsById(id))
                throw new DishNotFoundException("Dish with id \"" + id + "\" doesn't exist!");
            dishRepository.delete(id);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to delete dish", e);
        }
    }
}

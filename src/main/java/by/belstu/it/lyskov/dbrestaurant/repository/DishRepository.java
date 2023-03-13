package by.belstu.it.lyskov.dbrestaurant.repository;

import by.belstu.it.lyskov.dbrestaurant.entity.Dish;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;

public interface DishRepository extends PagingAndSortingRepository<Dish, Long> {

    Page<Dish> findByFilter(String filter, Pageable pageable) throws RepositoryException;

    long countByFilter(String filter) throws RepositoryException;

    Page<Dish> findByCategory(Long categoryId, Pageable pageable) throws RepositoryException;

    long countByCategory(Long categoryId) throws RepositoryException;
}

package by.belstu.it.lyskov.dbrestaurant.repository;

import by.belstu.it.lyskov.dbrestaurant.entity.Category;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Long> {

    Page<Category> findByFilter(String filter, Pageable pageable) throws RepositoryException;

    long countByFilter(String filter) throws RepositoryException;
}

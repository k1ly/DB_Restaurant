package by.belstu.it.lyskov.dbrestaurant.service;

import by.belstu.it.lyskov.dbrestaurant.entity.Category;
import by.belstu.it.lyskov.dbrestaurant.exception.CategoryNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;
import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;

import java.util.List;

public interface CategoryService {

    Category findCategoryById(Long id) throws CategoryNotFoundException, ServiceException;

    List<Category> getCategories(Sort sort) throws ServiceException;

    Page<Category> getCategories(Pageable pageable) throws ServiceException;

    Page<Category> findCategoriesByFilter(String filter, Pageable pageable) throws ServiceException;

    void addCategory(Category category) throws ServiceException;

    void updateCategory(Long id, Category category) throws CategoryNotFoundException, ServiceException;

    void deleteCategory(Long id) throws CategoryNotFoundException, ServiceException;
}

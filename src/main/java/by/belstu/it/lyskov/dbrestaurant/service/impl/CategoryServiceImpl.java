package by.belstu.it.lyskov.dbrestaurant.service.impl;

import by.belstu.it.lyskov.dbrestaurant.entity.Category;
import by.belstu.it.lyskov.dbrestaurant.exception.CategoryNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.repository.CategoryRepository;
import by.belstu.it.lyskov.dbrestaurant.service.CategoryService;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;
import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category findCategoryById(Long id) throws CategoryNotFoundException, ServiceException {
        try {
            return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Category with id \"" + id + "\" doesn't exist!"));
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to find category by id", e);
        }
    }

    @Override
    public List<Category> getCategories(Sort sort) throws ServiceException {
        List<Category> list = new ArrayList<>();
        try {
            for (Category category : categoryRepository.findAll(sort)) {
                list.add(category);
            }
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to get categories", e);
        }
        return list;
    }

    @Override
    public Page<Category> getCategories(Pageable pageable) throws ServiceException {
        try {
            return categoryRepository.findAll(pageable);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to get categories", e);
        }
    }

    @Override
    public Page<Category> findCategoriesByFilter(String filter, Pageable pageable) throws ServiceException {
        try {
            return categoryRepository.findByFilter(filter, pageable);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to find categories by filter", e);
        }
    }

    @Override
    public void addCategory(Category category) throws ServiceException {
        try {
            categoryRepository.save(category);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to add category", e);
        }
    }

    @Override
    public void updateCategory(Long id, Category category) throws CategoryNotFoundException, ServiceException {
        try {
            if (!categoryRepository.existsById(id))
                throw new CategoryNotFoundException("Category with id \"" + id + "\" doesn't exist!");
            categoryRepository.update(id, category);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to update category", e);
        }
    }

    @Override
    public void deleteCategory(Long id) throws CategoryNotFoundException, ServiceException {
        try {
            if (!categoryRepository.existsById(id))
                throw new CategoryNotFoundException("Category with id \"" + id + "\" doesn't exist!");
            categoryRepository.delete(id);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to delete category", e);
        }
    }
}

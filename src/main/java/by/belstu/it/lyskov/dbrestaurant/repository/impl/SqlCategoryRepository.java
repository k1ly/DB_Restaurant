package by.belstu.it.lyskov.dbrestaurant.repository.impl;

import by.belstu.it.lyskov.dbrestaurant.entity.Category;
import by.belstu.it.lyskov.dbrestaurant.exception.EntityMappingException;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.repository.CategoryRepository;
import by.belstu.it.lyskov.dbrestaurant.repository.ProcedureExecutor;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.PageImpl;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;
import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class SqlCategoryRepository implements CategoryRepository {

    private final ProcedureExecutor executor;

    public SqlCategoryRepository(ProcedureExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Page<Category> findByFilter(String filter, Pageable pageable) throws RepositoryException {
        List<Category> list;
        try {
            list = executor.executePage("{call find_categories_by_filter_paged (?,?,?,?,?)}", pageable, Category.class, filter);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
        return list.size() > 0 ? new PageImpl<>(list, pageable, countByFilter(filter)) : Page.empty(pageable);
    }

    @Override
    public long countByFilter(String filter) throws RepositoryException {
        try {
            return (long) executor.executeScalar("{? = call count_categories_by_filter (?)}", filter);
        } catch (SQLException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public Iterable<Category> findAll(Sort sort) throws RepositoryException {
        try {
            return executor.executeSort(sort, Category.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public Page<Category> findAll(Pageable pageable) throws RepositoryException {
        List<Category> list;
        try {
            list = executor.executePage(pageable, Category.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
        return list.size() > 0 ? new PageImpl<>(list, pageable, count()) : Page.empty(pageable);
    }

    @Override
    public Optional<Category> findById(Long id) throws RepositoryException {
        try {
            return executor.executeFindById(id, Category.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public boolean existsById(Long id) throws RepositoryException {
        try {
            return executor.executeFindById(id, Category.class).isPresent();
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void save(Category category) throws RepositoryException {
        try {
            executor.executeInsert(category);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void update(Long id, Category category) throws RepositoryException {
        try {
            executor.executeUpdate(id, category);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        try {
            executor.executeDelete(id, Category.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public long count() throws RepositoryException {
        try {
            return executor.executeCount(Category.class);
        } catch (SQLException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }
}

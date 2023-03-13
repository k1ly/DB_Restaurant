package by.belstu.it.lyskov.dbrestaurant.repository.impl;

import by.belstu.it.lyskov.dbrestaurant.entity.Dish;
import by.belstu.it.lyskov.dbrestaurant.exception.EntityMappingException;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.repository.DishRepository;
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
public class SqlDishRepository implements DishRepository {

    private final ProcedureExecutor executor;

    public SqlDishRepository(ProcedureExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Page<Dish> findByFilter(String filter, Pageable pageable) throws RepositoryException {
        List<Dish> list;
        try {
            list = executor.executePage("{call find_dishes_by_filter_paged (?,?,?,?,?)}", pageable, Dish.class, filter);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
        return list.size() > 0 ? new PageImpl<>(list, pageable, countByFilter(filter)) : Page.empty(pageable);
    }

    @Override
    public long countByFilter(String filter) throws RepositoryException {
        try {
            return (long) executor.executeScalar("{? = call count_dishes_by_filter (?)}", filter);
        } catch (SQLException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public Page<Dish> findByCategory(Long categoryId, Pageable pageable) throws RepositoryException {
        List<Dish> list;
        try {
            list = executor.executePage("{call find_dishes_by_category_paged (?,?,?,?,?)}", pageable, Dish.class, categoryId);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
        return list.size() > 0 ? new PageImpl<>(list, pageable, countByCategory(categoryId)) : Page.empty(pageable);
    }

    @Override
    public long countByCategory(Long categoryId) throws RepositoryException {
        try {
            return (long) executor.executeScalar("{? = call count_dishes_by_category (?)}", categoryId);
        } catch (SQLException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public Iterable<Dish> findAll(Sort sort) throws RepositoryException {
        try {
            return executor.executeSort(sort, Dish.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public Page<Dish> findAll(Pageable pageable) throws RepositoryException {
        List<Dish> list;
        try {
            list = executor.executePage(pageable, Dish.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
        return list.size() > 0 ? new PageImpl<>(list, pageable, count()) : Page.empty(pageable);
    }

    @Override
    public Optional<Dish> findById(Long id) throws RepositoryException {
        try {
            return executor.executeFindById(id, Dish.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public boolean existsById(Long id) throws RepositoryException {
        try {
            return executor.executeFindById(id, Dish.class).isPresent();
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void save(Dish dish) throws RepositoryException {
        try {
            executor.executeInsert(dish);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void update(Long id, Dish dish) throws RepositoryException {
        try {
            executor.executeUpdate(id, dish);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        try {
            executor.executeDelete(id, Dish.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public long count() throws RepositoryException {
        try {
            return executor.executeCount(Dish.class);
        } catch (SQLException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }
}

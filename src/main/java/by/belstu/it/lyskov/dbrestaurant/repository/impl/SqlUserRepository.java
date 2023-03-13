package by.belstu.it.lyskov.dbrestaurant.repository.impl;

import by.belstu.it.lyskov.dbrestaurant.entity.User;
import by.belstu.it.lyskov.dbrestaurant.exception.EntityMappingException;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.exception.TransactionException;
import by.belstu.it.lyskov.dbrestaurant.repository.ProcedureExecutor;
import by.belstu.it.lyskov.dbrestaurant.repository.UserRepository;
import by.belstu.it.lyskov.dbrestaurant.util.page.PageImpl;
import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SqlUserRepository implements UserRepository {

    private final ProcedureExecutor executor;

    public SqlUserRepository(ProcedureExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void beginTransaction() throws TransactionException {
        executor.beginTransaction();
    }

    @Override
    public void commitTransaction() throws TransactionException {
        executor.commitTransaction();
    }

    @Override
    public void rollbackTransaction() throws TransactionException {
        executor.rollbackTransaction();
    }

    @Override
    public Optional<User> findByLogin(String login) throws RepositoryException {
        try {
            return executor.executeSingleResultQuery("{call find_user_by_login (?)}", User.class, login);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public Page<User> findByFilter(String filter, Pageable pageable) throws RepositoryException {
        List<User> list;
        try {
            list = executor.executePage("{call find_users_by_filter_paged (?,?,?,?,?)}", pageable, User.class, filter);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
        return list.size() > 0 ? new PageImpl<>(list, pageable, countByFilter(filter)) : Page.empty(pageable);
    }

    @Override
    public long countByFilter(String filter) throws RepositoryException {
        try {
            return (long) executor.executeScalar("{? = call count_users_by_filter (?)}", filter);
        } catch (SQLException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void register(User user) throws RepositoryException {
        try {
            executor.executeUpdate("{call register_user (?,?,?,?,?)}", executor.takeFields(user).values().toArray());
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void edit(Long id, User user) throws RepositoryException {
        try {
            List<Object> parameters = new ArrayList<>(List.of(id));
            parameters.addAll(executor.takeFields(user).values());
            executor.executeUpdate("{call edit_user (?,?,?)}", parameters.toArray());
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public Iterable<User> findAll(Sort sort) throws RepositoryException {
        try {
            return executor.executeSort(sort, User.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public Page<User> findAll(Pageable pageable) throws RepositoryException {
        List<User> list;
        try {
            list = executor.executePage(pageable, User.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
        return list.size() > 0 ? new PageImpl<>(list, pageable, count()) : Page.empty(pageable);
    }

    @Override
    public Optional<User> findById(Long id) throws RepositoryException {
        try {
            return executor.executeFindById(id, User.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public boolean existsById(Long id) throws RepositoryException {
        try {
            return executor.executeFindById(id, User.class).isPresent();
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void save(User user) throws RepositoryException {
        try {
            executor.executeInsert(user);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void update(Long id, User user) throws RepositoryException {
        try {
            executor.executeUpdate(id, user);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        try {
            executor.executeDelete(id, User.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public long count() throws RepositoryException {
        try {
            return executor.executeCount(User.class);
        } catch (SQLException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }
}

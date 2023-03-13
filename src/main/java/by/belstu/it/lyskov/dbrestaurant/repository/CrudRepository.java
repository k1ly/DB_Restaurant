package by.belstu.it.lyskov.dbrestaurant.repository;

import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.exception.TransactionException;

import java.util.Optional;

public interface CrudRepository<T, ID> {

    default void beginTransaction() throws TransactionException {
    }

    default void commitTransaction() throws TransactionException {
    }

    default void rollbackTransaction() throws TransactionException {
    }

    Optional<T> findById(ID id) throws RepositoryException;

    boolean existsById(ID id) throws RepositoryException;

    void save(T entity) throws RepositoryException;

    void update(ID id, T entity) throws RepositoryException;

    void delete(ID id) throws RepositoryException;

    long count() throws RepositoryException;
}

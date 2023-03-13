package by.belstu.it.lyskov.dbrestaurant.repository.impl;

import by.belstu.it.lyskov.dbrestaurant.entity.Status;
import by.belstu.it.lyskov.dbrestaurant.exception.EntityMappingException;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.repository.ProcedureExecutor;
import by.belstu.it.lyskov.dbrestaurant.repository.StatusRepository;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Optional;

@Repository
public class SqlStatusRepository implements StatusRepository {

    private final ProcedureExecutor executor;

    public SqlStatusRepository(ProcedureExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Optional<Status> findByName(String name) throws RepositoryException {
        try {
            return executor.executeSingleResultQuery("{call find_status_by_name (?)}", Status.class, name);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public Optional<Status> findById(Long id) throws RepositoryException {
        try {
            return executor.executeFindById(id, Status.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public boolean existsById(Long id) throws RepositoryException {
        try {
            return executor.executeFindById(id, Status.class).isPresent();
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void save(Status status) throws RepositoryException {
        try {
            executor.executeInsert(status);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void update(Long id, Status status) throws RepositoryException {
        try {
            executor.executeUpdate(id, status);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        try {
            executor.executeDelete(id, Status.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public long count() throws RepositoryException {
        try {
            return executor.executeCount(Status.class);
        } catch (SQLException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }
}

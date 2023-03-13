package by.belstu.it.lyskov.dbrestaurant.repository.impl;

import by.belstu.it.lyskov.dbrestaurant.entity.Address;
import by.belstu.it.lyskov.dbrestaurant.exception.EntityMappingException;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.repository.AddressRepository;
import by.belstu.it.lyskov.dbrestaurant.repository.ProcedureExecutor;
import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.Optional;

@Repository
public class SqlAddressRepository implements AddressRepository {

    private final ProcedureExecutor executor;

    public SqlAddressRepository(ProcedureExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Iterable<Address> findByUser(Long userId, Sort sort) throws RepositoryException {
        try {
            return executor.executeSort("{call find_addresses_by_user_sorted (?,?,?)}", sort, Address.class, userId);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public Optional<Address> findById(Long id) throws RepositoryException {
        try {
            return executor.executeFindById(id, Address.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public boolean existsById(Long id) throws RepositoryException {
        try {
            return executor.executeFindById(id, Address.class).isPresent();
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void save(Address address) throws RepositoryException {
        try {
            executor.executeInsert(address);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void update(Long id, Address address) throws RepositoryException {
        try {
            executor.executeUpdate(id, address);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public void delete(Long id) throws RepositoryException {
        try {
            executor.executeDelete(id, Address.class);
        } catch (SQLException | EntityMappingException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }

    @Override
    public long count() throws RepositoryException {
        try {
            return executor.executeCount(Address.class);
        } catch (SQLException e) {
            throw new RepositoryException("Error in procedure executor", e);
        }
    }
}

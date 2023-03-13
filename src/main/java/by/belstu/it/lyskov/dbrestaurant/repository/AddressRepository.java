package by.belstu.it.lyskov.dbrestaurant.repository;

import by.belstu.it.lyskov.dbrestaurant.entity.Address;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;

public interface AddressRepository extends CrudRepository<Address, Long> {

    Iterable<Address> findByUser(Long userId, Sort sort) throws RepositoryException;
}

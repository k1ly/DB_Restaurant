package by.belstu.it.lyskov.dbrestaurant.repository;

import by.belstu.it.lyskov.dbrestaurant.entity.Status;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;

import java.util.Optional;

public interface StatusRepository extends CrudRepository<Status, Long> {

    Optional<Status> findByName(String name) throws RepositoryException;
}

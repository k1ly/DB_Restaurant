package by.belstu.it.lyskov.dbrestaurant.repository;

import by.belstu.it.lyskov.dbrestaurant.entity.Role;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;

import java.util.Optional;

public interface RoleRepository extends PagingAndSortingRepository<Role, Long> {

    Optional<Role> findByName(String name) throws RepositoryException;
}

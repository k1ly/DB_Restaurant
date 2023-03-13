package by.belstu.it.lyskov.dbrestaurant.repository;

import by.belstu.it.lyskov.dbrestaurant.entity.User;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    Optional<User> findByLogin(String login) throws RepositoryException;

    Page<User> findByFilter(String filter, Pageable pageable) throws RepositoryException;

    long countByFilter(String filter) throws RepositoryException;

    void register(User user) throws RepositoryException;

    void edit(Long id, User user) throws RepositoryException;
}
package by.belstu.it.lyskov.dbrestaurant.repository;

import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.util.page.Page;
import by.belstu.it.lyskov.dbrestaurant.util.page.Pageable;
import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;

public interface PagingAndSortingRepository<T, ID> extends CrudRepository<T, ID> {

    Iterable<T> findAll(Sort sort) throws RepositoryException;

    Page<T> findAll(Pageable pageable) throws RepositoryException;

}

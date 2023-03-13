package by.belstu.it.lyskov.dbrestaurant.service;

import by.belstu.it.lyskov.dbrestaurant.entity.Role;
import by.belstu.it.lyskov.dbrestaurant.exception.RoleNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;

import java.util.List;

public interface RoleService {

    Role findRoleById(Long id) throws RoleNotFoundException, ServiceException;

    List<Role> getRoles(Sort sort) throws ServiceException;
}

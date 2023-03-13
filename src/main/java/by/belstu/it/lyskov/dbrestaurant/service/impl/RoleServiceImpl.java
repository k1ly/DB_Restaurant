package by.belstu.it.lyskov.dbrestaurant.service.impl;

import by.belstu.it.lyskov.dbrestaurant.entity.Role;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.exception.RoleNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.repository.RoleRepository;
import by.belstu.it.lyskov.dbrestaurant.service.RoleService;
import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    public final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findRoleById(Long id) throws RoleNotFoundException, ServiceException {
        try {
            return roleRepository.findById(id).orElseThrow(() -> new RoleNotFoundException("Role with id \"" + id + "\" doesn't exist!"));
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to find role by id", e);
        }
    }

    @Override
    public List<Role> getRoles(Sort sort) throws ServiceException {
        List<Role> list = new ArrayList<>();
        try {
            for (Role role : roleRepository.findAll(sort)) {
                list.add(role);
            }
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to get roles", e);
        }
        return list;
    }
}

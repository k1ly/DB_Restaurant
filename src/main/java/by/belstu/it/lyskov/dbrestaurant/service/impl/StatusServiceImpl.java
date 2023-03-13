package by.belstu.it.lyskov.dbrestaurant.service.impl;

import by.belstu.it.lyskov.dbrestaurant.entity.Status;
import by.belstu.it.lyskov.dbrestaurant.exception.RepositoryException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.exception.StatusNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.repository.StatusRepository;
import by.belstu.it.lyskov.dbrestaurant.service.StatusService;
import org.springframework.stereotype.Service;

@Service
public class StatusServiceImpl implements StatusService {

    public final StatusRepository statusRepository;

    public StatusServiceImpl(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @Override
    public Status findStatusByName(String name) throws StatusNotFoundException, ServiceException {
        try {
            return statusRepository.findByName(name).orElseThrow(() -> new StatusNotFoundException("Status with name \"" + name + "\" doesn't exist!"));
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to find status by name", e);
        }
    }
}

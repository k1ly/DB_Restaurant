package by.belstu.it.lyskov.dbrestaurant.service;

import by.belstu.it.lyskov.dbrestaurant.entity.Status;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.exception.StatusNotFoundException;

public interface StatusService {

    Status findStatusByName(String name) throws StatusNotFoundException, ServiceException;
}

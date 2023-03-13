package by.belstu.it.lyskov.dbrestaurant.service;

import by.belstu.it.lyskov.dbrestaurant.entity.Address;
import by.belstu.it.lyskov.dbrestaurant.exception.AddressNotFoundException;
import by.belstu.it.lyskov.dbrestaurant.exception.BadReferenceException;
import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;

import java.util.List;

public interface AddressService {

    Address findAddressById(Long id) throws AddressNotFoundException, ServiceException;

    List<Address> findAddressesByUser(Long userId, Sort sort) throws BadReferenceException, ServiceException;

    void addAddress(Address address) throws BadReferenceException, ServiceException;
}

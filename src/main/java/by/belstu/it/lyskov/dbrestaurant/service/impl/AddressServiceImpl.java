package by.belstu.it.lyskov.dbrestaurant.service.impl;

import by.belstu.it.lyskov.dbrestaurant.entity.Address;
import by.belstu.it.lyskov.dbrestaurant.exception.*;
import by.belstu.it.lyskov.dbrestaurant.repository.AddressRepository;
import by.belstu.it.lyskov.dbrestaurant.repository.UserRepository;
import by.belstu.it.lyskov.dbrestaurant.service.AddressService;
import by.belstu.it.lyskov.dbrestaurant.util.sort.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Address findAddressById(Long id) throws AddressNotFoundException, ServiceException {
        try {
            return addressRepository.findById(id).orElseThrow(() -> new AddressNotFoundException("Address with id \"" + id + "\" doesn't exist!"));
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to find address by id", e);
        }
    }

    @Override
    public List<Address> findAddressesByUser(Long userId, Sort sort) throws BadReferenceException, ServiceException {
        List<Address> list = new ArrayList<>();
        try {
            if (!userRepository.existsById(userId))
                throw new BadReferenceException("User with id \"" + userId + "\" doesn't exist!");
            for (Address address : addressRepository.findByUser(userId, sort)) {
                list.add(address);
            }
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to get addresses by user", e);
        }
        return list;
    }

    @Override
    public void addAddress(Address address) throws ServiceException, BadReferenceException {
        try {
            if (!userRepository.existsById(address.getUser().getId()))
                throw new BadReferenceException("User with id \"" + address.getUser().getId() + "\" doesn't exist!");
            addressRepository.save(address);
        } catch (RepositoryException e) {
            throw new ServiceException("Failed to add address", e);
        }
    }
}

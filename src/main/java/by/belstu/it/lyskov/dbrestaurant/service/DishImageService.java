package by.belstu.it.lyskov.dbrestaurant.service;

import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import org.springframework.web.multipart.MultipartFile;

public interface DishImageService {

    String saveFile(MultipartFile image) throws ServiceException;
}

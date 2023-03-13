package by.belstu.it.lyskov.dbrestaurant.service.impl;

import by.belstu.it.lyskov.dbrestaurant.exception.ServiceException;
import by.belstu.it.lyskov.dbrestaurant.service.DishImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DishImageServiceImpl implements DishImageService {

    @Value("${upload.dishes}")
    private String root;
    private Path directory;
    private int counter;

    @PostConstruct
    private void createDirectory() throws ServiceException {
        try {
            URL resource = getClass().getResource("/");
            if (resource == null)
                throw new IOException("Resource not found");
            directory = Paths.get(resource.toURI()).getParent().resolve("classes/static" + root);
            Files.createDirectories(directory);
            try (var files = Files.list(directory)) {
                counter = (int) (files.count() + 1);
            }
        } catch (IOException | URISyntaxException e) {
            throw new ServiceException("Failed to initialize directory for upload", e);
        }
    }

    @Override
    public String saveFile(MultipartFile image) throws ServiceException {
        try {
            Files.copy(image.getInputStream(), directory.resolve(counter + ".jpg"));
            counter++;
        } catch (IOException e) {
            throw new ServiceException("Failed to save file", e);
        }
        return Path.of(root).resolve(counter + ".jpg").toString().replace("\\", "/");
    }
}

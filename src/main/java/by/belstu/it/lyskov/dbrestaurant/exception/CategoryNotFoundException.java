package by.belstu.it.lyskov.dbrestaurant.exception;

public class CategoryNotFoundException extends Exception {

    public CategoryNotFoundException() {
        super();
    }

    public CategoryNotFoundException(String message) {
        super(message);
    }

    public CategoryNotFoundException(Throwable cause) {
        super(cause);
    }

    public CategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

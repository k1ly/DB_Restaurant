package by.belstu.it.lyskov.dbrestaurant.exception;

public class DishNotFoundException extends Exception {

    public DishNotFoundException() {
        super();
    }

    public DishNotFoundException(String message) {
        super(message);
    }

    public DishNotFoundException(Throwable cause) {
        super(cause);
    }

    public DishNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

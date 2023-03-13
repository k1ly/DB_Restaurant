package by.belstu.it.lyskov.dbrestaurant.exception;

public class OrderNotFoundException extends Exception {

    public OrderNotFoundException() {
        super();
    }

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(Throwable cause) {
        super(cause);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

package by.belstu.it.lyskov.dbrestaurant.exception;

public class OrderItemNotFoundException extends Exception {

    public OrderItemNotFoundException() {
        super();
    }

    public OrderItemNotFoundException(String message) {
        super(message);
    }

    public OrderItemNotFoundException(Throwable cause) {
        super(cause);
    }

    public OrderItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

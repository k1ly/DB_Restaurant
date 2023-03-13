package by.belstu.it.lyskov.dbrestaurant.exception;

public class CartItemNotFoundException extends Exception {

    public CartItemNotFoundException() {
        super();
    }

    public CartItemNotFoundException(String message) {
        super(message);
    }

    public CartItemNotFoundException(Throwable cause) {
        super(cause);
    }

    public CartItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

package by.belstu.it.lyskov.dbrestaurant.exception;

public class AddressNotFoundException extends Exception {

    public AddressNotFoundException() {
        super();
    }

    public AddressNotFoundException(String message) {
        super(message);
    }

    public AddressNotFoundException(Throwable cause) {
        super(cause);
    }

    public AddressNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

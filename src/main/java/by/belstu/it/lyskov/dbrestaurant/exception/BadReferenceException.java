package by.belstu.it.lyskov.dbrestaurant.exception;

public class BadReferenceException extends Exception {

    public BadReferenceException() {
        super();
    }

    public BadReferenceException(String message) {
        super(message);
    }

    public BadReferenceException(Throwable cause) {
        super(cause);
    }

    public BadReferenceException(String message, Throwable cause) {
        super(message, cause);
    }
}

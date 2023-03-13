package by.belstu.it.lyskov.dbrestaurant.exception;

public class ReviewNotFoundException extends Exception {

    public ReviewNotFoundException() {
        super();
    }

    public ReviewNotFoundException(String message) {
        super(message);
    }

    public ReviewNotFoundException(Throwable cause) {
        super(cause);
    }

    public ReviewNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

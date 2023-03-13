package by.belstu.it.lyskov.dbrestaurant.exception;

public class StatusNotFoundException extends Exception {

    public StatusNotFoundException() {
        super();
    }

    public StatusNotFoundException(String message) {
        super(message);
    }

    public StatusNotFoundException(Throwable cause) {
        super(cause);
    }

    public StatusNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

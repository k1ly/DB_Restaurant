package by.belstu.it.lyskov.dbrestaurant.exception;

public class RoleNotFoundException extends Exception {

    public RoleNotFoundException() {
        super();
    }

    public RoleNotFoundException(String message) {
        super(message);
    }

    public RoleNotFoundException(Throwable cause) {
        super(cause);
    }

    public RoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

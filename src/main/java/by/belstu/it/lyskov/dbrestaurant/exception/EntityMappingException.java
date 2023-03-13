package by.belstu.it.lyskov.dbrestaurant.exception;

public class EntityMappingException extends Exception {

    public EntityMappingException() {
        super();
    }

    public EntityMappingException(String message) {
        super(message);
    }

    public EntityMappingException(Throwable cause) {
        super(cause);
    }

    public EntityMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}

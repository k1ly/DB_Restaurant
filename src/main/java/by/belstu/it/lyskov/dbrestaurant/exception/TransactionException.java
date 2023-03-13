package by.belstu.it.lyskov.dbrestaurant.exception;

public class TransactionException extends Exception {

    public TransactionException() {
        super();
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}

package exceptions;

public class InsufficientInventoryException extends RuntimeException {

    public InsufficientInventoryException() {
    }

    public InsufficientInventoryException(String message) {
        super(message);
    }

    // Inherits method getMessage from the Exception class.
    // Returns specific information about the exception that was
    // included when an exception is created and thrown.

    // For this exception, the specific inventory item that there
    // was insufficient amount of is passed to the alternate constructor.
}
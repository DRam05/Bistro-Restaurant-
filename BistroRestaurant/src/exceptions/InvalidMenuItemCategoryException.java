package exceptions;

public class InvalidMenuItemCategoryException extends RuntimeException {
    

    public InvalidMenuItemCategoryException() {
    }

    public InvalidMenuItemCategoryException(String message) {
        super(message);
    }

    // Inherits method getMessage from the Exception class.
    // Returns specific information about the exception that was
    // included when an exception is created and thrown.

    // For this exception, the specific invalid menu item category
    // that was found is passed to the alternate constructor.

}
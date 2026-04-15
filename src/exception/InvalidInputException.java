package exception;

/*
Description:
Custom checked exception thrown when user-supplied input fails
validation (empty strings, negative IDs, malformed ISBN/email,
out-of-range numbers, etc.). Used throughout the CLI layer to
provide meaningful feedback instead of letting invalid data
propagate into the database.
 */
public class InvalidInputException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidInputException(String message) {
        super(message);
    }
}

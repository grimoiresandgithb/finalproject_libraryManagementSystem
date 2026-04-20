package exception;

/*Custom checked exception thrown when a user lookup by ID returns
no result. Used by UserManager and LibraryService during checkout and member-specific operations.*/
public class UserNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(int userId) {
        super("No user found with ID: " + userId);
    }
}

package exception;

/*Custom checked exception thrown when a member attempts to check out
an item that is already on loan. Enforces the business rule that
an item can only be borrowed by one member at a time.*/
public class ItemUnavailableException extends Exception {

    private static final long serialVersionUID = 1L;

    public ItemUnavailableException(String message) {
        super(message);
    }

    public ItemUnavailableException(int itemId, String title) {
        super("Item '" + title + "' (ID " + itemId + ") is currently on loan and unavailable.");
    }
}

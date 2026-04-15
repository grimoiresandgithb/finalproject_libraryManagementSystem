package exception;

/*Description:
Custom checked exception thrown when an item lookup by ID returns
no result. Using a checked exception (extending Exception, notRuntimeException) forces calling code to explicitly handle the
missing-item case, which is important for data integrity.
 */
public class ItemNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(int itemId) {
        super("No item found with ID: " + itemId);
    }
}

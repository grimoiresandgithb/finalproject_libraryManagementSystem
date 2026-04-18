package exception;

/*
 * Thrown when an attempt is made to check out an item that is already unavailable
 * 
 * That is a checked exception, forcing the caller to handle the error in LibraryService or the CLI
 */

public class ItemNotAvailableException extends Exception {

	private static final long serialVersionUID = 1L;
	private final int itemId;
    private final String itemTitle;

    public ItemNotAvailableException(int itemId, String itemTitle) {
        super("Item " + itemId + " (" + itemTitle + ") is not available for checkout.");
        this.itemId = itemId;
        this.itemTitle = itemTitle;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemTitle() {
        return itemTitle;
    }

}

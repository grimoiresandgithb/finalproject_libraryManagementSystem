package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import exception.ItemNotFoundException;
import interfaces.Manageable;
import interfaces.Searchable;
import model.Book;
import model.DVD;
import model.Item;


/**
 * Handles all CRUD operation for Item objects (Book + DVD)
 * Implements:
 * 	- Manageable<Item> 
 * 	- Searchable<Item>
 * 
 * Key responsibilities:
 * 	- Insert/update/delete rows in the items table
 * 	- Convert SQL rows into Book or DVD objects
 * 	- Perform keyword searches
 * 	- Filter available items
 */
public class ItemManager implements Manageable<Item>, Searchable<Item> {

    /**
	 * Dispatcher method to figure out  which type of item we're adding and call the correct helper method.
	 */
    @Override
    public void add(Item item) {
        if (item instanceof Book) {
            addBook((Book) item);
        } else if (item instanceof DVD) { //DVD specific field
            addDVD((DVD) item);
        } else {
            throw new IllegalArgumentException(
                    "Unknown item type: " + item.getClass().getSimpleName());
        }
    } // end add
    
    /**
	 * Adds a Book to the database
	 */
    //book specific methods - these are private because the public add() method 
    // dispatches to them based on the actual type of Item passed in. 
    // This keeps the public API clean and prevents misuse.
    private void addBook(Book book) {
        String sql = "INSERT INTO items "
                + "(title, available, item_type, author, isbn, publication_year) "
                + "VALUES (?, ?, 'book', ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, book.getTitle());
            ps.setBoolean(2, book.isAvailable());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getIsbn());
            ps.setInt(5, book.getPublicationYear());
            ps.executeUpdate();

            // Retrieve the auto-generated ID so the caller has it.
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    book.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add book: " + e.getMessage(), e);
        }
    } 
    /**
	 * Adds a DVD to the database
	 */
    //DVD specific methods - these are private because the 
    //public add() method dispatches to them based on the actual type of Item passed in. 
    // This keeps the public API clean and prevents misuse.
    private void addDVD(DVD dvd) {
        String sql = "INSERT INTO items "
                + "(title, available, item_type, director, runtime_minutes) "
                + "VALUES (?, ?, 'dvd', ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, dvd.getTitle());
            ps.setBoolean(2, dvd.isAvailable());
            ps.setString(3, dvd.getDirector());
            ps.setInt(4, dvd.getRuntimeMinutes());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    dvd.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add DVD: " + e.getMessage(), e);
        }
    }

    /**
	 * Retrieves an Item by ID
	 * Returns a Book or DVD depending on item_type
	 */
    @Override
    public Item getById(int id) throws ItemNotFoundException {
        String sql = "SELECT * FROM items WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToItem(rs);
                }
                throw new ItemNotFoundException(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch item: " + e.getMessage(), e);
        }
    } // end getById

    @Override
    /**
	 * Dispatcher method for updating an existing item
	 */
    public void update(Item item) {
        if (item instanceof Book) {
            updateBook((Book) item);
        } else if (item instanceof DVD) {
            updateDVD((DVD) item);
        } else {
            throw new IllegalArgumentException(
                    "Unknown item type: " + item.getClass().getSimpleName());
        }
    }
    /**
	 * Updates Book fields
	 */
    private void updateBook(Book book) {
        String sql = "UPDATE items SET title = ?, available = ?, "
                + "author = ?, isbn = ?, publication_year = ? "
                + "WHERE id = ? AND item_type = 'book'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setBoolean(2, book.isAvailable());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getIsbn());
            ps.setInt(5, book.getPublicationYear());
            ps.setInt(6, book.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update book: " + e.getMessage(), e);
        }
    } //end updateBook

    /**
	 * Updates DVD fields
	 */
    private void updateDVD(DVD dvd) {
        String sql = "UPDATE items SET title = ?, available = ?, "
                + "director = ?, runtime_minutes = ? "
                + "WHERE id = ? AND item_type = 'dvd'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dvd.getTitle());
            ps.setBoolean(2, dvd.isAvailable());
            ps.setString(3, dvd.getDirector());
            ps.setInt(4, dvd.getRuntimeMinutes());
            ps.setInt(5, dvd.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update DVD: " + e.getMessage(), e);
        }
    } //end updateDVD

   /**
	 * Deletes an item by ID
	 */
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM items WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete item: " + e.getMessage(), e);
        }
    } // end delete

    /**
	 * Returns all items in the database
	 */
    @Override
    public List<Item> getAll() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                items.add(mapRowToItem(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch items: " + e.getMessage(), e);
        }
        return items;
    } // end getAll

    /**
	 * Keyword search by title
	 */
    @Override
    public List<Item> search(String keyword) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items "
                + "WHERE LOWER(title) LIKE ? "
                + "   OR LOWER(author) LIKE ? "
                + "   OR LOWER(director) LIKE ? "
                + "ORDER BY id";

        String pattern = "%" + keyword.toLowerCase() + "%";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRowToItem(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search items: " + e.getMessage(), e);
        }
        return items;
    }

    /**
	 * Returns items where available = true
	 */ 
    public List<Item> getAvailableItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE available = TRUE ORDER BY id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                items.add(mapRowToItem(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch available items: " + e.getMessage(), e);
        }
        return items;
    } //end getAvailableItems

    /**
	 * Updates only the availability flag
	 */
    public void setAvailability(int itemId, boolean available) {
        String sql = "UPDATE items SET available = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, available);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update item availability: " + e.getMessage(), e);
        }
    }

    /**
	 * Helper method that converst a SQL row into either a Book or DVD object
	 */
    private Item mapRowToItem(ResultSet rs) throws SQLException {
        String type = rs.getString("item_type");

        if ("book".equalsIgnoreCase(type)) {
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setTitle(rs.getString("title"));
            book.setAvailable(rs.getBoolean("available"));
            book.setAuthor(rs.getString("author"));
            book.setIsbn(rs.getString("isbn"));
            book.setPublicationYear(rs.getInt("publication_year"));
            return book;
        } else if ("dvd".equalsIgnoreCase(type)) {
            DVD dvd = new DVD();
            dvd.setId(rs.getInt("id"));
            dvd.setTitle(rs.getString("title"));
            dvd.setAvailable(rs.getBoolean("available"));
            dvd.setDirector(rs.getString("director"));
            dvd.setRuntimeMinutes(rs.getInt("runtime_minutes"));
            return dvd;
        } else {
            throw new SQLException("Unknown item_type in database: " + type);
        }
    } //end class
}

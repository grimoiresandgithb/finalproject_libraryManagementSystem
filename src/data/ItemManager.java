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

/*
Description:
Data-access class for Item records (both Books and DVDs). Implements
Manageable<Item> for standard CRUD and Searchable<Item> for keyword
search. Uses PreparedStatements for every query to prevent SQL
injection. Maps ResultSet rows to concrete Book or DVD instances
based on the item_type column.

Inputs: Item (Book or DVD) instances or an item ID.
Processing: executes parameterized SQL against the items table.
Outputs: individual Items, lists of Items, or void for writes.
 */
public class ItemManager implements Manageable<Item>, Searchable<Item> {

    /*
     Insert a new item. Dispatches to the appropriate insert based on
     the concrete subclass (Book or DVD) so that type-specific columns
     are populated correctly.
     */
    @Override
    public void add(Item item) {
        if (item instanceof Book) {
            addBook((Book) item);
        } else if (item instanceof DVD) {
            addDVD((DVD) item);
        } else {
            throw new IllegalArgumentException(
                    "Unknown item type: " + item.getClass().getSimpleName());
        }
    }

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

    /*
     Retrieve an item by its primary key. Throws ItemNotFoundException
     if no row matches - callers can catch this to display a clear
     error message instead of null-checking.
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
    }

    @Override
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
    }

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
    }

    /*
     Delete an item by ID. Note: if the item has associated loans,
     the foreign key constraint will prevent deletion - the caller
     should check for active loans first or handle the SQL error.
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
    }

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
    }

    /*
     Search items by keyword in title, author (books), or director (DVDs).
     Uses SQL LIKE with wildcards - keyword is passed through a
     PreparedStatement to remain injection-safe.
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

    // Convenience method: returns only items currently available for loan. 
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
    }

    // Update only the availability flag - used by the checkout/return flow. 
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

    /*
     Map a ResultSet row to the correct Item subclass based on the
     item_type column. Centralizing this logic keeps the query methods
     clean and prevents bugs from duplicating the mapping in each.
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
    }
}

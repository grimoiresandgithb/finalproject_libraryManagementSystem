package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
<<<<<<< HEAD
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

=======
import java.util.ArrayList;
import java.util.List;

import exception.ItemNotFoundException;
import interfaces.Manageable;
import interfaces.Searchable;
>>>>>>> project-branch-1
import model.Book;
import model.DVD;
import model.Item;

<<<<<<< HEAD
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
public class ItemManager {
	private Connection conn;
	
	public ItemManager() {
		this.conn = DatabaseConnection.getConnection();
	}
	
	/**
	 * Adds a Book or DVD to the database
	 */
	@Override
	public void add(Item item) {
		String sql = "INSERT INTO items (title, available, item_type, author, isbn, publication_year, director, runtime_minutes) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1,  item.getTitle());
			stmt.setBoolean(2,  item.isAvailable());
			stmt.setString(3,  item.getItemType());
			
			// book specific fields
			if(item instanceof Book book) {
				stmt.setString(4,  book.getAuthor());
				stmt.setString(5,  book.getIsbn());
				stmt.setInt(6,  book.getPublicationYear());
				stmt.setNull(7,  Types.VARCHAR);
				
			}
			
			// DVD specific fields
			else if(item instanceof DVD dvd) {
				stmt.setNull(4,  Types.VARCHAR);
				stmt.setNull(5,  Types.VARCHAR);
				stmt.setNull(6,  Types.INTEGER);
				stmt.setString(7,  dvd.getDirector());
				stmt.setInt(8,  dvd.getRuntimeMinutes());
			}
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Failed to add item: " + item + " \n" + e.getMessage());
		}
	} // end add
	
	/**
	 * Retrieves an Item by ID
	 * Returns a Book or DVD depending on item_type
	 */
	public Item getById(int id) {
		String sql = "SELECT * FROM items WHERE id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1,  id);
			
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				return mapRowToItem(rs);
			}
		} catch (SQLException e) {
			System.out.println("Failed to fetch item with id: " + id + " \n" + e.getMessage())
		}
	} // end getByID
	
	/**
	 * Updates an existing item
	 */
	public void update(Item item) {
		String sql = "UPDATE items SET title=?, available=?, author=?, isbn=?, publication_year=?, director=?, runtime_minutes=? "
                + "WHERE id=?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, item.getTitle());
            stmt.setBoolean(2, item.isAvailable());
            
            // book specific fields
            if (item instanceof Book book) {
                stmt.setString(3, book.getAuthor());
                stmt.setString(4, book.getIsbn());
                stmt.setInt(5, book.getPublicationYear());
                stmt.setNull(6, Types.VARCHAR);
                stmt.setNull(7, Types.INTEGER);
            // DVD specific fields
            } else if (item instanceof DVD dvd) {
                stmt.setNull(3, Types.VARCHAR);
                stmt.setNull(4, Types.VARCHAR);
                stmt.setNull(5, Types.INTEGER);
                stmt.setString(6, dvd.getDirector());
                stmt.setInt(7, dvd.getRuntimeMinutes());
            }

            stmt.setInt(8, item.getId());
            stmt.executeUpdate();			
			
		} catch (SQLException e) {
			System.out.println("Failed to update item: " + item + " \n" + e.getMessage());
		}
		
	} // end update
	
	/**
	 * Deletes an item by ID
	 */
	@Override
	public void delete(int id) {
		String sql = "DELETE FROM items WHERE id=?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1,  id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Failed to delete item: " + e.getMessage());
		}
	}// end delete
	
	/**
	 * Returns all items in the database
	 */
	@Override
	public List<Item> getAll() {
		List<Item> items = new ArrayList<>();
		String sql = "SELECT * FROM items";
		
		try (Statement stmt = conn.createStatement()) {
			ResultSet rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				items.add(mapRowToItem(rs));
			}
		} catch (SQLException e) {
			System.out.println("Failed to fetch items: " + e.getMessage());
		}
		return items;
	}// end getAll
	
	/**
	 * Keyword search by title
	 */
	@Override
	public List<Item> search(String keyword) {
		List<Item> items = new ArrayList<>();
		String sql = "SELECT * FROM items WHERE title LIKE ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, "%" + keyword + "%");
			
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				items.add(mapRowToItem(rs));
				
			}
		} catch (SQLException e) {
			System.out.println("Search failed: " + e.getMessage());
		}
		return items;
		
	}// end search
	
	/**
	 * Returns items where available = true
	 */
	public List<Item> getAvailableItems() {
		List<Item> items = new ArrayList<>();
		String sql = "SELECT * FROM items WHERE available = TRUE";
		
		try (Statement stmt = conn.createStatement()) {
			ResultSet rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				items.add(mapRowToItem(rs));
			}
		} catch (SQLException e) {
			System.out.println("Failed to fetch available items: " + e.getMessage());
		}
		
		return items;
	} // end getAvailableItems
	
	/**
	 * Helper method that converst a SQL row into either a Book or DVD object
	 */
	private Item mapRowToItem(ResultSet rs) throws SQLException {
		String type = rs.getString("item_type");
		
		if ("book".equals(type)) {
			return new Book (
				rs.getInt("id"), rs.getString("title"), rs.getBoolean("available"), rs.getString("author"),
				rs.getString("isbn"), rs.getInt("publication_year")
			);
		} else {
			return new DVD(
					rs.getInt("id"), rs.getString("title"), rs.getBoolean("available"), rs.getString("director"), rs.getInt("runtime_minutes"));
		}
	}
	
} // end class
=======
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
>>>>>>> project-branch-1

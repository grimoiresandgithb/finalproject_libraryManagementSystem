package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

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

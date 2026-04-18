package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import interfaces.Manageable;
import interfaces.Searchable;
import model.Librarian;
import model.Member;
import model.User;

/**
 * Handles all CRUD operations for User objects (Member + Librarian)
 * Implements:
 * 	- Manageable<User>
 * 	- Serachable<User>
 * 
 * Responsibilities
 * 	- Insert/update/delete users
 * 	- Convery SQL rows into Member or Librarian objects
 * 	- Keyword search by name
 */
public class UserManager implements Manageable<User>, Searchable<User>{
	
	private Connection conn;
	
	public UserManager() {
		this.conn = DatabaseConnection.getConnection();
	}
	
	/**
	 * Adds a Member or Librarian to the database
	 * Uses user.getUserType() to determine which fields to populate
	 */
	@Override
	public void add(User user) {
		String sql = "INSERT INTO users (name, email, user_type, membership_type, employee_id) "
                + "VALUES (?, ?, ?, ?, ?)";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setString(1,  user.getName());
			stmt.setString(2,  user.getEmail());
			stmt.setString(3,  user.getUserType());
			
			if (user instanceof Member member) {
				stmt.setString(4,  member.getMembershipType());
				stmt.setNull(5,  Types.VARCHAR);
			} else if (user instanceof Librarian librarian) {
				stmt.setNull(4,  Types.VARCHAR);
				stmt.setSting(5, librarian.getEmployeeId());
			}
			
		} catch (SQLException e) {
			System.out.println("Falied to add user: " + user + " \n" + e.getMessage());
		}
	} // end add
	
	/**
	 * Retrieves a user by ID
	 * Automatically returns a Member or Librarian depending on user_type
	 */
	@Override
	public User getById(int id) {
		String sql = "SELECT * FROM users WHERE user_id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return mapRowToUser(rs);
			}
			
		} catch (SQLException e) {
			System.out.println("Failed to fetch user id: " + id + "\n" + e.getMessage());
		}
		
		return null;
	} // end getById
	
	/**
	 * Updates an existing user
	 */
	@Override
	public void update(User user) {
		 String sql = "UPDATE users SET name=?, email=?, membership_type=?, employee_id=? "
                 + "WHERE user_id=?";
		 
		 try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			 
			 stmt.setString(1,  user.getName());
			 stmt.setString(2,  user.getEmail);
			 
			 if (user instanceof Member member) {
				 stmt.setString(3,  member.getMembershipType());
				 stmt.setNull(4,  Types.VARCHAR);
			 } else if (user instanceof Librarian librarian) {
				 stmt.setNull(3,  Types.VARCHAR);
				 stmt.setString(4,  librarian.getEmployeeId);
			 }
			 
			 stmt.setInt(5,  user.getUserId);
			 stmt.executeUpdate();
			 
		 } catch (SQLException e) {
			 System.out.println("Failed to update user: " + user + "\n" + e.getMessage());
		 }
	} // end update
	
	/**
	 * Deletes a user by ID
	 */
	@Override
	public void delete(int id) {
		String sql = "DELETE FROM users WHERE user_id=?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Failed to delete user with id: " + id + "\n" + e.getMessage());
		}
	} // end delete
	
	/**
	 * Returns all users
	 */
	@Override
	public List<User> getAll() {
		
		List<User> users = new ArrayList<>();
		String sql = "SELECT * FROM users";
		
		try (Statement stmt = conn.createStatement()) {
			ResultSet rs = stmt.executeQuery(sql);
			
			while (rs.next()) {
				users.add(mapRowToUser(rs));
			}
		} catch (SQLException e) {
			System.out.println("Failed to fetch users: " + e.getMessage());
		}
		
		return users;
		
	} // end getAll
	
	/**
	 * Keyword search by name
	 */
	public List<User> search(String keyword) {
		List<User> users = new ArrayList<>();
		String sql = "SELECT * FROM users WHERE name LIKE ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1,  "%" + keyword + "%");
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				users.add(mapRowToUser(rs));
			}
		} catch (SQLException e) {
			System.out.println("Search failed: " + e.getMessage());
		}
		return users;
	} // end search
	
	/**
	 * Helper method that converts a SQL row into a Member or a Librarian
	 */
	private User mapRowToUser(ResultSet rs) throws SQLException {
		String type = rs.getString("user_type");
		
		if ("member".equals(type)) {
			return new Member(
					rs.getInt("user_id"),
					rs.getString("name"),
					rs.getString("email"),
					rs.getString("membership_type")
					);
		} else {
			return new Librarian (
					rs.getInt("user_id"),
					rs.getString("name"),
					rs.getString("email"),
					rs.getString("employee_id")
					);
		}
	} // end mapRowToUser

} // end class

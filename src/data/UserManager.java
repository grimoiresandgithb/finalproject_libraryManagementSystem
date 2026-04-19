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

import exception.UserNotFoundException;
>>>>>>> project-branch-1
import interfaces.Manageable;
import interfaces.Searchable;
import model.Librarian;
import model.Member;
import model.User;

<<<<<<< HEAD
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
=======
/*

Description:
Data-access class for User records (Members and Librarians).
Implements Manageable<User> for CRUD and Searchable<User> for
name-based search. Mirrors the structure of ItemManager so that
the two classes remain consistent and easy to reason about.

Inputs: User (Member or Librarian) instances or a user ID.
Processing: executes parameterized SQL against the users table.
Outputs: individual Users, lists of Users, or void for writes.
 */
public class UserManager implements Manageable<User>, Searchable<User> {

    @Override
    public void add(User user) {
        if (user instanceof Member) {
            addMember((Member) user);
        } else if (user instanceof Librarian) {
            addLibrarian((Librarian) user);
        } else {
            throw new IllegalArgumentException(
                    "Unknown user type: " + user.getClass().getSimpleName());
        }
    }

    private void addMember(Member member) {
        String sql = "INSERT INTO users "
                + "(name, email, user_type, membership_type) "
                + "VALUES (?, ?, 'member', ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getMembershipType());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    member.setUserId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add member: " + e.getMessage(), e);
        }
    }

    private void addLibrarian(Librarian librarian) {
        String sql = "INSERT INTO users "
                + "(name, email, user_type, employee_id) "
                + "VALUES (?, ?, 'librarian', ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, librarian.getName());
            ps.setString(2, librarian.getEmail());
            ps.setString(3, librarian.getEmployeeId());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    librarian.setUserId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add librarian: " + e.getMessage(), e);
        }
    }

    @Override
    public User getById(int id) throws UserNotFoundException {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
                throw new UserNotFoundException(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(User user) {
        if (user instanceof Member) {
            updateMember((Member) user);
        } else if (user instanceof Librarian) {
            updateLibrarian((Librarian) user);
        } else {
            throw new IllegalArgumentException(
                    "Unknown user type: " + user.getClass().getSimpleName());
        }
    }

    private void updateMember(Member member) {
        String sql = "UPDATE users SET name = ?, email = ?, membership_type = ? "
                + "WHERE user_id = ? AND user_type = 'member'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getMembershipType());
            ps.setInt(4, member.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update member: " + e.getMessage(), e);
        }
    }

    private void updateLibrarian(Librarian librarian) {
        String sql = "UPDATE users SET name = ?, email = ?, employee_id = ? "
                + "WHERE user_id = ? AND user_type = 'librarian'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, librarian.getName());
            ps.setString(2, librarian.getEmail());
            ps.setString(3, librarian.getEmployeeId());
            ps.setInt(4, librarian.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update librarian: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch users: " + e.getMessage(), e);
        }
        return users;
    }

    @Override
    public List<User> search(String keyword) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users "
                + "WHERE LOWER(name) LIKE ? OR LOWER(email) LIKE ? "
                + "ORDER BY user_id";

        String pattern = "%" + keyword.toLowerCase() + "%";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search users: " + e.getMessage(), e);
        }
        return users;
    }

    /** Map a ResultSet row to the correct User subclass based on user_type. */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        String type = rs.getString("user_type");

        if ("member".equalsIgnoreCase(type)) {
            Member member = new Member();
            member.setUserId(rs.getInt("user_id"));
            member.setName(rs.getString("name"));
            member.setEmail(rs.getString("email"));
            member.setMembershipType(rs.getString("membership_type"));
            return member;
        } else if ("librarian".equalsIgnoreCase(type)) {
            Librarian librarian = new Librarian();
            librarian.setUserId(rs.getInt("user_id"));
            librarian.setName(rs.getString("name"));
            librarian.setEmail(rs.getString("email"));
            librarian.setEmployeeId(rs.getString("employee_id"));
            return librarian;
        } else {
            throw new SQLException("Unknown user_type in database: " + type);
        }
    }
}
>>>>>>> project-branch-1

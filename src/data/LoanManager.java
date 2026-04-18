package data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import model.Loan;

/**
 * Handdles all CRUD operstaions for Loan objects
 * Implements Manageable<Loan>
 * 
 * Responsibilities:
 * 	- Insert/update/delete loans
 * 	- Retrieve loans by member
 * 	- Retrieve overdue loans
 * 	- Convert SQL rows into Loan objects
 */


public class LoanManager {
	
	private Connection conn;
	private ItemManager itemManager;
	private UserManager userManager;
	
	public LoanManager() {
		this.conn = DatabaseConnection.getConnection();
		this.itemManager = new ItemManager();
		this.userManager = new UserManager();
	}
	
	/**
	 * Adds a new loan to the database
	 */
	@Override
	public void add(Loan loan) {
		String sql = "INSERT INTO loans (item_id, member_id, loan_date, due_date, return_date) "
                + "VALUES (?, ?, ?, ?, ?)";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1,  loan.getItem().getId());
			stmt.setInt(2,  loan.getMember().getUserId());
			stmt.setDate(3, Date.valueOf(loan.getLoanDate()));
			stmt.setDate(4,  Date.valueOf(loan.getDueDate()));
			
			if (loan.getReturnDate() != null) {
				stmt.setDate(5,  Date.valueOf(loan.getReturnDate()));
			} else {
				stmt.setNull(5,  Types.DATE);
			}
			
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("Failed to add loan: " + e.getMessage());
		}
	} // end add
	
	/**
	 * Retreives a loan by ID
	 */
	public Loan getById(int id) {
		String sql = "SELECT * FROM loans WHERE loan_id = ?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1,  id);
			
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				return mapRowToLoan(rs);
			}
		} catch (SQLException e) {
			System.out.println("Failed to fetch loan: " + e.getMessage());
		}
		
		return null;
	} // end getById
	
	/**
	 * Updates an existing loan
	 */
	public void update(Loan loan) {
		String sql = "UPDATE loans SET item_id=?, member_id=?, loan_date=?, due_date=?, return_date=? "
                + "WHERE loan_id=?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			stmt.setInt(1,  loan.getItem().getId());
			stmt.setInt(2,  loan.getMember().getUserId());
			stmt.setDate(3,  Date.valueOf(loan.getLoanDate()));
			stmt.setDate(4,  Date.valueOf(loan.getDueDate()));
			
			if (loan.getReturnDate() != null) {
				stmt.setDate(5,  Date.valueOf(loan.getReturnDate()));
			} else {
				stmt.setNull(5,  Types.DATE);
			}
			
			stmt.setInt(6,  loan.getLoanId());
			stmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println("Failed to update loan: " + e.getMessage());
		}
	} // end update
	
	/**
	 * Deletes a loan by ID
	 */
	public void delete(int id) {
		String sql = "DELETE FROM loans WHERE loan_id=?";
		
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1,  id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Failed to delete loan: " + e.getMessage());
		}
	}

}

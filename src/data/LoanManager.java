package data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import exception.ItemNotFoundException;
import exception.UserNotFoundException;
import interfaces.Manageable;
import model.Item;
import model.Loan;
import model.Member;

public class LoanManager implements Manageable<Loan> {

    private final ItemManager itemManager;
    private final UserManager userManager;

    public LoanManager(ItemManager itemManager, UserManager userManager) {
        this.itemManager = itemManager;
        this.userManager = userManager;
    }

    @Override
    public void add(Loan loan) {
        String sql = "INSERT INTO loans "
                + "(item_id, member_id, loan_date, due_date, return_date) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, loan.getItem().getId());
            ps.setInt(2, loan.getMember().getUserId());
            ps.setDate(3, Date.valueOf(loan.getLoanDate()));
            ps.setDate(4, Date.valueOf(loan.getDueDate()));
            if (loan.getReturnDate() != null) {
                ps.setDate(5, Date.valueOf(loan.getReturnDate()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    loan.setLoanId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add loan: " + e.getMessage(), e);
        }
    }

    @Override
    public Loan getById(int id) throws ItemNotFoundException, UserNotFoundException {
        String sql = "SELECT * FROM loans WHERE loan_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToLoan(rs);
                }
                // No dedicated LoanNotFoundException - reuse ItemNotFoundException
                // since loans are conceptually records of items.
                throw new ItemNotFoundException("No loan found with ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch loan: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Loan loan) {
        String sql = "UPDATE loans SET item_id = ?, member_id = ?, "
                + "loan_date = ?, due_date = ?, return_date = ? "
                + "WHERE loan_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, loan.getItem().getId());
            ps.setInt(2, loan.getMember().getUserId());
            ps.setDate(3, Date.valueOf(loan.getLoanDate()));
            ps.setDate(4, Date.valueOf(loan.getDueDate()));
            if (loan.getReturnDate() != null) {
                ps.setDate(5, Date.valueOf(loan.getReturnDate()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            ps.setInt(6, loan.getLoanId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update loan: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM loans WHERE loan_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete loan: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Loan> getAll() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans ORDER BY loan_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                try {
                    loans.add(mapRowToLoan(rs));
                } catch (ItemNotFoundException | UserNotFoundException e) {
                    // Skip loans whose item or member has been deleted.
                    System.err.println("Warning: skipping orphaned loan: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch loans: " + e.getMessage(), e);
        }
        return loans;
    }

    //Return all loans belonging to a specific member. 
    public List<Loan> getLoansByMember(int memberId) {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE member_id = ? ORDER BY loan_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        loans.add(mapRowToLoan(rs));
                    } catch (ItemNotFoundException | UserNotFoundException e) {
                        System.err.println("Warning: skipping orphaned loan: " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch member's loans: " + e.getMessage(), e);
        }
        return loans;
    }

    /**
     * Return all loans that are currently overdue. Overdue here means
     * the due date has passed and return_date is NULL (not yet returned).
     */
    public List<Loan> getOverdueLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans "
                + "WHERE return_date IS NULL AND due_date < CURRENT_DATE "
                + "ORDER BY due_date";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                try {
                    loans.add(mapRowToLoan(rs));
                } catch (ItemNotFoundException | UserNotFoundException e) {
                    System.err.println("Warning: skipping orphaned loan: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch overdue loans: " + e.getMessage(), e);
        }
        return loans;
    }

    /**
     * Find the active (unreturned) loan for a given item, if any.
     * Used by the return-item workflow.
     */
    public Loan findActiveLoanForItem(int itemId)
            throws ItemNotFoundException, UserNotFoundException {
        String sql = "SELECT * FROM loans "
                + "WHERE item_id = ? AND return_date IS NULL "
                + "LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToLoan(rs);
                }
                throw new ItemNotFoundException(
                        "No active loan found for item ID: " + itemId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find active loan: " + e.getMessage(), e);
        }
    }

    /**
     * Re-enters data into Loan row by looking up the related Item and Member.
     * If either lookup fails, the caller (usually a list method) decides
     * whether to skip the row or surface the error.
     */
    private Loan mapRowToLoan(ResultSet rs)
            throws SQLException, ItemNotFoundException, UserNotFoundException {

        Loan loan = new Loan();
        loan.setLoanId(rs.getInt("loan_id"));

        int itemId = rs.getInt("item_id");
        int memberId = rs.getInt("member_id");

        Item item = itemManager.getById(itemId);
        loan.setItem(item);

        // The related user should always be a Member since loans are
        // only issued to members, but we defensively cast.
        if (userManager.getById(memberId) instanceof Member) {
            loan.setMember((Member) userManager.getById(memberId));
        }

        Date loanDate = rs.getDate("loan_date");
        Date dueDate = rs.getDate("due_date");
        Date returnDate = rs.getDate("return_date");

        if (loanDate != null)   loan.setLoanDate(loanDate.toLocalDate());
        if (dueDate != null)    loan.setDueDate(dueDate.toLocalDate());
        if (returnDate != null) loan.setReturnDate(returnDate.toLocalDate());

        return loan;
    }
}

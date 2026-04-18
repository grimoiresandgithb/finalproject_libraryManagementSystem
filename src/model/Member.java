package model;

import java.util.List;

public class Member extends User {
	
	private String membershipType;
	private List<Loan> loans;

	public Member(int userId, String name, String email, String membershipType, List<Loan> loans) {
		super(userId, name, email);
		this.membershipType = membershipType;
		this.loans = loans;
	}
	
	// getters + setters
	public String getMembershipType() { return membershipType; }
	public List<Loan> getLoans() { return loans; }
	public void setMembershipType(String newMembershipType) { this.membershipType = newMembershipType; }
	public void setLoans(List<Loan> newLoans) { this.loans = newLoans; }
	
	// methods
	
	public void addLoan(Loan loan) {
		loans.add(loan);
	}
	
	public void removeLoan(Loan loan) {
		loans.remove(loan);
	}

	@Override
	public String getUserType() {
		return "member";
	}
	
	@Override
	public String toString() {
		return super.toString() + 
				", Membership: " + 
				", Loans: " + loans.size();
	}
	

}

package model;

import java.util.ArrayList;
import java.util.List;

public class Member extends User {

    private String membershipType;
    private List<Loan> loans;

    public Member() {
        super();
        this.loans = new ArrayList<>();
    }

    public Member(int userId, String name, String email, String membershipType) {
        super(userId, name, email);
        this.membershipType = membershipType;
        this.loans = new ArrayList<>();
    }

    public String getMembershipType() { return membershipType; }
    public void setMembershipType(String membershipType) { this.membershipType = membershipType; }

    public List<Loan> getLoans() { return loans; }
    public void setLoans(List<Loan> loans) { this.loans = loans; }

    public void addLoan(Loan loan) {
        if (loan != null) {
            this.loans.add(loan);
        }
    }

    public void removeLoan(Loan loan) {
        this.loans.remove(loan);
    }

    @Override
    public String getUserType() {
        return "member";
    }

    @Override
    public String toString() {
        return String.format("[%d] %s <%s> (member - %s)",
                userId, name, email, membershipType);
    }
}

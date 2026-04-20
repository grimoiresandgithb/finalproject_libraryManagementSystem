package model;

import java.time.LocalDate;


public class Loan {

    //Attributes
    private int loanId;
    private Item item;
    private Member member;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;   // null until item is returned

    //Constructors
    public Loan() {}

    public Loan(int loanId, Item item, Member member,
                LocalDate loanDate, LocalDate dueDate, LocalDate returnDate) {
        this.loanId = loanId;
        this.item = item;
        this.member = member;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    //GettersSetters 
    public int getLoanId() { return loanId; }
    public void setLoanId(int loanId) { this.loanId = loanId; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    //A loan is overdue if it has not been returned yet and the due date is in the past.
 
    public boolean isOverdue() {
        if (returnDate != null) return false;      // already returned
        if (dueDate == null) return false;
        return LocalDate.now().isAfter(dueDate);
    }

    @Override
    public String toString() {
        String status = returnDate != null
                ? "Returned " + returnDate
                : (isOverdue() ? "OVERDUE (due " + dueDate + ")" : "Due " + dueDate);
        String itemTitle = (item != null) ? item.getTitle() : "?";
        String memberName = (member != null) ? member.getName() : "?";
        return String.format("Loan #%d: '%s' -> %s [%s]",
                loanId, itemTitle, memberName, status);
    }
}

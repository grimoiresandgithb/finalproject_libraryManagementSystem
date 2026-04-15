package model;


public class Librarian extends User {

    private String employeeId;

    public Librarian() {
        super();
    }

    public Librarian(int userId, String name, String email, String employeeId) {
        super(userId, name, email);
        this.employeeId = employeeId;
    }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    @Override
    public String getUserType() {
        return "librarian";
    }

    @Override
    public String toString() {
        return String.format("[%d] %s <%s> (librarian - emp #%s)",
                userId, name, email, employeeId);
    }
}

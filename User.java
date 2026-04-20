package model;

public abstract class User {

    protected int userId;
    protected String name;
    protected String email;

    public User() {}

    public User(int userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    //Each subclass must declare its user type (e.g. member or librarian).
    public abstract String getUserType();

    @Override
    public String toString() {
        return String.format("[%d] %s <%s> (%s)",
                userId, name, email, getUserType());
    }
}

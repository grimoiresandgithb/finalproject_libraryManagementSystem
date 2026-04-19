package model;

public abstract class User {
<<<<<<< HEAD
	
	protected int userId;
	protected String name;
	protected String email;
	
	public User(int userId, String name, String email) {
		this.userId = userId;
		this.name = name;
		this.email = email;
	}
	
	// getters + setters
	public int getUserId() { return userId; }
	public String getName() { return name; }
	public String getEmail() { return email; }
	public void setUserId(int newUserId) { this.userId = newUserId; }
	public void setName(String newName) { this.name = newName; }
	public void setEmail(String newEmail) { this.email = newEmail; }
	
	
	// methods
	
	public abstract String getUserType();
	
	public String toString() {
		return userId + ": \nName: " + name + "\nEmail: " + email;
	}
=======
>>>>>>> project-branch-1

    protected int userId;
    protected String name;
    protected String email;

    public User() {
   
    }

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

package model;

public abstract class User {
	
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

}

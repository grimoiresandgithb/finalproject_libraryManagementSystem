package model;
/*
 * Abstract base class for all library items
 */

<<<<<<< HEAD
public abstract class Item {
	
	protected int id;
	protected String title;
	protected boolean available;
	
	public Item(int id, String title, boolean available) {
		this.id = id;
		this.title = title;
		this.available = available;
	}
	
	// getters +  setters
	public int getId() { return id; }
	public String getTitle() { return title; }
	public boolean getAvailable() { return available; }
	
	public void setId(int newId) { this.id = newId; }
	public void setTitle(String title) { this.title = title; }
	public void setAvailable(boolean available) { this.available = available; }
 	
	public abstract String getItemType();
	
	public String toString() {
		return id + ": Title: " + title + ", Is Available?: " + available;
	}
	
	
=======

public abstract class Item {

    protected int id;
    protected String title;
    protected boolean available;
    
    public Item() {
    	
    }

   
    public Item(int id, String title, boolean available) {
        this.id = id;
        this.title = title;
        this.available = available;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }


     //Each subclass must declare what kind of item it represents (e.g. "book", "dvd")
    public abstract String getItemType();

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - %s",
                id, title, getItemType(), available ? "Available" : "On Loan");
    }
>>>>>>> project-branch-1
}

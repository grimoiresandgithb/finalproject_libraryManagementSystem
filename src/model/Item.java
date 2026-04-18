package model;
/*
 * Abstract base class for all library items
 */

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
	
	
}

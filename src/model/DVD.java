package model;

public class DVD extends Item {
	
	
	private String director;
	private int runtimeMinutes;
	
	public DVD(int id, String title, boolean available, String director, int runtimeMinutes) {
		super(id, title, available);
		this.director = director;
		this.runtimeMinutes = runtimeMinutes;
	}
	
	// getters + setters
	public String getDirector() { return director; }
	public int getRuntimeMinutes() { return runtimeMinutes; }
	public void setDirector(String newDirector) { this.director = newDirector; }
	public void setRuntimeMinutes(int newRuntimeMinutes) { this.runtimeMinutes = newRuntimeMinutes; }
	
	
	@Override
	public String getItemType() {
		return "dvd";
	}

}

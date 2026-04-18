package model;

public class Book extends Item {

	
	private String author;
	private String isbn;
	private int publicationYear;
	
	public Book(int id, String title, boolean available, String author, String isbn, int publicationYear) {
		super(id, title, available);
		this.author = author;
		this.isbn = isbn;
		this.publicationYear = publicationYear;
	}
	
	//getters + setters
	public String getAuthor() { return author; }
	public String getIsbn() { return isbn; }
	public int getPublicationYear() { return publicationYear; }
	
	public void setAuthor(String newAuthor) { this.author = newAuthor; }
	public void setIsbn(String newIsbn) { this.isbn = newIsbn; }
	public void setPublicationYear(int newPublicationYear) { this.publicationYear = newPublicationYear; }
	
	
	
	// methods

	@Override
	public String getItemType() {
		return "book";
	}
	

}

package model;

public class Book extends Item {

    private String author;
    private String isbn;
    private int publicationYear;
    
    public Book() {
        super();
    }

    public Book(int id, String title, boolean available,
                String author, String isbn, int publicationYear) {
        super(id, title, available);
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }

    @Override
    public String getItemType() {
        return "book";
    }

    @Override
    public String toString() {
        return String.format("[%d] %s by %s (ISBN: %s, %d) - %s",
                id, title, author, isbn, publicationYear,
                available ? "Available" : "On Loan");
    }
}

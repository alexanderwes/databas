package booksdbclient.model;

import java.util.ArrayList;

/**
 * A representation of a book. A book has a title, an ISBN, a genre, rating and
 * authors. The ISBN, title, genre and authors are immutable.
 */
public class Book {

    private ArrayList<Author> authors;
    private String title, isbn;
    private Genre genre;
    private int rating;

    /**
     * Creates the book
     *
     * @param title
     * @param isbn
     * @param genre
     * @param rating
     */
    
    public Book(String isbn, String title, Genre genre, int rating) {
    	this.isbn = isbn;
    	this.title = title;
        this.authors = new ArrayList<Author>();
        this.genre = genre;
        this.rating = rating;
    }
    /**
     * @return the authors
     */
    @SuppressWarnings("unchecked")
	public ArrayList<Author> getAuthors() {
        return (ArrayList<Author>) authors.clone();
    }

    public void addAuthor(Author author) {
    	this.authors.add(author);
    }
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the isbn
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * @return the genre
     */
    public Genre getGenre() {
        return genre;
    }

    /**
     * @return the rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * @param rating the rating to set
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Title: " + title + " | Authors: " + authors + " | Genre: " + genre
        		+ " " + " | ISBN: " + isbn + " | Rating: " + rating;
    }
}

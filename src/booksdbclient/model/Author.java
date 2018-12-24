package booksdbclient.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



/**
 * A representation of an author.
 */

public class Author {

    private String name;
    private LocalDate dob;
    private ArrayList<String> isbn;
    
    
    /**
     * Creates the author
     *
     * @param name name of the author
     * @param dob birth date of the author
     */
    public Author(String name, LocalDate dob) {
        this.name = name;
        this.dob = dob;
        isbn = new ArrayList<>();
    }
    
    public ArrayList<String> getIsbn() {
    	return (ArrayList<String>) this.isbn.clone();
    }
    
    public void addIsbn(String isbn) {
    	this.isbn.add(isbn);
    }

    public ArrayList<String> getBooks() {
    	
    	return this.isbn;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public void setDob(LocalDate dob) {
    	this.dob = dob;
    }

    /**
     * @return the dob
     */
    public LocalDate getDob() {
        return dob;
    }

    @Override
    public String toString() {
        return "Name: " + name + " | Birth date: " + dob + " | Books: " + isbn;
    }
}

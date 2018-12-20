package booksdbclient.model;

import java.util.ArrayList;
import java.util.List;

public class Library {

	private List<Book> books;
	private List<Author> authors;
	
	public Library() {
		books = new ArrayList<>();
		authors = new ArrayList<>();
	}
	
	public void addBook(Book book) {
		this.books.add(book);
	}
	
	public void addBooks(List<Book> books) {
		this.books.addAll(books);
	}
	
	public void addAUthor(Author author) {
		this.authors.add(author);
	}
	
	public void addAuthors(List<Author> authors) {
		this.authors.addAll(authors);
	}
	
	public String toString() {
		return books + " " + authors;
	}
	
	
	
	
	
	
}

package booksdbclient.model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * This interface declares methods for querying a Books database.
 * Different implementations of this interface handles the connection and
 * queries to a specific DBMS and database, for example a MySQL or a MongoDB
 * database.
 * 
 * @author anderslm@kth.se
 */
public interface BooksDbInterface {
    
    /**
     * Connect to the database.
     * @param database
     * @return true on successful connection.
     */
    public boolean connect(String database) throws IOException, SQLException;
    
    /**
     * disconnect from the database
     * @throws IOException
     * @throws SQLException
     */
    public void disconnect() throws IOException, SQLException;
    
    /**
     * Search books by title
     * @param title
     * @return returns list of search results by title
     * @throws IOException
     * @throws SQLException
     */
    public List<Book> searchBooksByTitle(String title) throws IOException, SQLException;
    /**
     * Search books by author's name
     * @param name
     * @return returns list of search results by author's name
     * @throws IOException
     * @throws SQLException
     */
    public List<Book> searchBooksByAuthor(String name) throws IOException, SQLException;
    /**
     * Search books by isbn
     * @param isbn
     * @return return list of search results by isbn
     * @throws IOException
     * @throws SQLException
     */
    public List<Book> searchBooksByISBN(String isbn) throws IOException, SQLException;
    /** 
     * Search books by rating
     * @param rating
     * @return returns list of search results by rating
     * @throws IOException
     * @throws SQLException
     */
    public List<Book> searchBooksByRating(String rating) throws IOException, SQLException;
    /**
     * Search books by genre
     * @param genre
     * @return returns list of search results by genre
     * @throws IOException
     * @throws SQLException
     */
    public List<Book> searchBooksByGenre(Genre genre) throws IOException, SQLException;
    /** 
     * Inserts book object in the database
     * @param isbn
     * @param title
     * @param authors
     * @param genre
     * @param rating
     * @throws SQLException
     */
    public void insertBook(Book book) throws SQLException;
    /**
     * Updating an existing book's rating
     * @param book
     * @param rating
     * @return true if successful
     * @throws SQLException
     */
    public boolean updateRating(Book book, int rating) throws SQLException;
    /**
     * Updating an existing book's authors
     * @param book
     * @param authors
     * @return true if successful
     * @throws SQLException
     */
    public boolean addAuthors(Book book, List<Author> authors) throws SQLException;
    /**
     * Deletes an existing book from database
     * @param ISBN
     * @return true if successful
     * @throws SQLException
     */
    public boolean deleteBook(String ISBN) throws SQLException;
    /**
     * Gets all books from the database
     * @return list of all books
     * @throws SQLException
     */
    public List<Book> getBooks() throws SQLException;
    
    // TODO: Add abstract methods for all inserts, deletes and queries 
    // mentioned in the instructions for the assignement.
}

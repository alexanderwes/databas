/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package booksdbclient.model;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A mock implementation of the BooksDBInterface interface to demonstrate how to
 * use it together with the user interface.
 *
 * Your implementation should access a real database.
 *
 * @author anderslm@kth.se
 */
public class MockBooksDb implements BooksDbInterface {

	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	private Genre genre;

	public MockBooksDb() {
		
	}
	
    @Override
    public boolean connect(String database) throws SQLException  {
		String database_ = "jdbc:mysql://localhost/" + database;
		String userName = "client";
		String password = "password";
		connection = null;
		try {
			connection = DriverManager.getConnection(database_, 
					userName, password);
			statement = connection.createStatement();
	        return true;
		}
		finally {
			
		}
    }

    @Override
    public void disconnect() throws IOException, SQLException {
		try {
			if(connection != null) {
				connection.close();
			}
		}		
		finally {
			
		}
    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws SQLException   {

    	PreparedStatement searchByTitle;
    	List<Book> list = new ArrayList<>();
    	String s = "%" +searchTitle + "%";    	
    	String sql = 
    			"select t_book.isbn, t_book.title, t_book.genre, t_book.rating, t_author.name, t_author.dob "
    					+ "from t_book, t_author, t_writtenby "
    			+ "where title LIKE ? AND t_book.isbn = t_writtenby.isbn "
    					+ "AND t_author.name = t_writtenby.name";
    	try {
	    	searchByTitle = connection.prepareStatement(sql);
	    	searchByTitle.setString(1, s);
	    	resultSet=searchByTitle.executeQuery();
	    	list = copyBooksToList(resultSet);
	    	list = fixList(list);
	    	return list;
    	}
    	
    	finally {
    		resultSet.close();
    	}

    }

	@Override
	public List<Book> searchBooksByAuthor(String name) throws SQLException  {
		PreparedStatement searchByAuthor;
		List<Book> list = new ArrayList<>();
		Author author;
		List<Author> authors;
		Genre genre;
		
		
		String s = "%" + name + "%";
		String sql = 
				"select distinct t_book.isbn, t_book.title, t_book.genre, t_book.rating, t_author.name, t_author.dob "
						+ "from t_book, t_author, t_writtenby "
				+ "where t_author.name LIKE ? AND t_writtenby.isbn = t_book.isbn AND t_author.name = t_writtenby.name";
		try {
			searchByAuthor = connection.prepareStatement(sql);
			searchByAuthor.setString(1, s);
			resultSet = searchByAuthor.executeQuery();	
			list = copyBooksToList(resultSet);
			return list;
		} 
		finally {
			resultSet.close();
		}
		
	}

	@Override
	public List<Book> searchBooksByISBN(String isbn) throws SQLException {

		PreparedStatement searchByISBN;
		List<Book> list = new ArrayList<>();
		
		
		String isbn_ = "%" + isbn + "%";
		String sql = "select t_book.isbn, t_book.title, t_book.genre, t_book.rating, t_author.name, t_author.dob "
				+ " from t_book, t_author, t_writtenby "
				+ " where t_book.isbn LIKE ? AND t_writtenby.name = t_author.name AND T_book.isbn = t_writtenby.isbn";
		
		try {
			searchByISBN = connection.prepareStatement(sql);
			searchByISBN.setString(1, isbn_);
			resultSet = searchByISBN.executeQuery();
			list = copyBooksToList(resultSet);
			list = fixList(list);
		}
		finally {
			resultSet.close();
		}
		return list;
	
	}

	@Override
	public List<Book> searchBooksByRating(String rating) throws SQLException {
		PreparedStatement searchByRating;
		List<Book> list = new ArrayList<>();
		
		String sql = "select distinct t_book.isbn, t_book.title, t_book.genre, t_book.rating, t_author.name, t_author.dob "
				+ "from t_book, t_author, t_writtenby "
			    + "where t_book.rating = ? AND t_book.isbn = t_writtenby.isbn AND t_author.name = t_writtenby.name";
		
		try {
			searchByRating = connection.prepareStatement(sql);
			searchByRating.setString(1, rating);
			resultSet = searchByRating.executeQuery();
			
			list = copyBooksToList(resultSet);
			list = fixList(list);
		}
		finally {
			resultSet.close();
		}
		return list;
	}

	@Override
	public List<Book> searchBooksByGenre(Genre genre) throws SQLException {
		PreparedStatement searchByGenre;
		
		List<Book> list = new ArrayList<>();
		String s =  genre.getValue().toString(); 
		String sql = "select distinct t_book.isbn, t_book.title, t_book.genre, t_book.rating, t_author.name, t_author.dob "
				+ "from t_book, t_author, t_writtenby "
		+ "where t_writtenby.isbn = t_book.isbn AND t_writtenby.name = t_author.name AND t_book.genre = ? ";
		
		try {
			searchByGenre = connection.prepareStatement(sql);
			searchByGenre.setString(1, s);
			resultSet = searchByGenre.executeQuery();
			list = copyBooksToList(resultSet);
		} 
		finally {
			resultSet.close();
		}
		return list;
	}

	@Override
	public void insertBook(Book book) throws SQLException {
		
		PreparedStatement insertBook = null;
		PreparedStatement insertAuthor = null;
		PreparedStatement insertWrittenby = null;
		List<Author> authors_ = new ArrayList<>();
		
		String sqlInsertBook = "insert into t_book values (?, ?, ?, ?)";
		String sqlInsertAuthor = "insert into t_author values (?, ?, ?)";
		String sqlInsertWrittenby = "insert into t_writtenby values (?, ?, ?)";
		
		Author author;
		for (int i=0; i<book.getAuthors().size(); i++) {
			author = new Author(book.getAuthors().get(i).getName(), book.getAuthors().get(i).getDob());
			authors_.add(author);
		}
		
		try {
			connection.setAutoCommit(false);
			
			insertBook = connection.prepareStatement(sqlInsertBook);
			insertBook.setString(1, book.getIsbn());
			insertBook.setString(2, book.getTitle());
			insertBook.setString(3, book.getGenre().getValue());
			insertBook.setInt(4, book.getRating());
			insertBook.executeUpdate();
			
			
			insertAuthor = connection.prepareStatement(sqlInsertAuthor);
			for (int i=0; i<authors_.size(); i++) {
				insertAuthor.setString(1, authors_.get(i).getName());
				insertAuthor.setDate(2, java.sql.Date.valueOf(authors_.get(i).getDob()));
				insertAuthor.setInt(3, authors_.get(i).getIsbn().size());
				insertAuthor.executeUpdate();
			}
			
			insertWrittenby = connection.prepareStatement(sqlInsertWrittenby);
			for (int i=0; i<authors_.size(); i++) {
				insertWrittenby.setString(1, book.getIsbn());
				insertWrittenby.setString(2, book.getAuthors().get(i).getName());
				insertWrittenby.setDate(3, java.sql.Date.valueOf(book.getAuthors().get(i).getDob()));
				insertWrittenby.executeUpdate();
			}
			
			connection.commit();
		}
		
		catch (Exception e){
			if (connection != null)
				connection.rollback();
		}
		
		finally {
			if (insertBook != null) {
				
			}
			connection.setAutoCommit(true);
		}
	}

	@Override
	public boolean updateRating(Book book, int rating) throws SQLException {
		PreparedStatement updateRating = null;
	
		String sqlUpdateRating = "update t_book "
								+ "set rating = ? "
								+ "where isbn = ? ";
		
		String isbn = book.getIsbn();
		
		try {
			updateRating = connection.prepareStatement(sqlUpdateRating);
			updateRating.setInt(1, rating);
			updateRating.setString(2, isbn);
			updateRating.executeUpdate();
			System.out.println("updated");
			return true;
		}
		
		finally {
			
		}
//		return false;
	}

	@Override
	public boolean addAuthors(Book book, List<Author> authors) throws SQLException {
		
		PreparedStatement insertAuthor = null;
		PreparedStatement searchForSameAuthor = null;
		String isbn = book.getIsbn();
		String sqlInsertAuthor = "insert into t_author values (?,?,?)";
		String sqlFindAuthor = " select * from t_author where name = ? and dob = ?";
		Author author;
		List<Author> authors_ = new ArrayList<>();
		List<Author> authorsFromDB= new ArrayList<>();
		//copy to internal list of authors
		for (int i=0; i<authors.size(); i++) {
			author = new Author(authors.get(i).getName(), authors.get(i).getDob());
			authors_.add(author);
		}
		
		authorsFromDB = getAuthors();
		boolean check = false;
		
		for (int i=0; i<authors.size(); i++) {
			for (int j=0; j<authorsFromDB.size(); j++) {
				if(authors.get(i).getName().equalsIgnoreCase(authorsFromDB.get(j).getName())
						&& authors.get(i).getDob().isEqual(authorsFromDB.get(j).getDob())) {
					check=true;
				
				}
			}
		}
		searchForSameAuthor = connection.prepareStatement(sqlFindAuthor);
		for (int i=0; i<authors.size(); i++) {
			searchForSameAuthor.setString(1, authors.get(i).getName());
		}
		
		try {
			connection.setAutoCommit(false);
			insertAuthor = connection.prepareStatement(sqlInsertAuthor);
			for (int i=0; i<authors_.size(); i++) {
				insertAuthor.setString(1, authors_.get(i).getName());
				insertAuthor.setDate(2, java.sql.Date.valueOf(authors_.get(i).getDob()));
				insertAuthor.setInt(3, 4);
				insertAuthor.executeUpdate();
			}
			
			
			
			connection.commit();
			return true;
		}
		finally {
			insertAuthor.close();
		}
	}

	@Override
	public boolean deleteBook(String ISBN) throws SQLException {
		PreparedStatement lookForIsbn = null;
		PreparedStatement deleteBook = null;
		PreparedStatement deleteFromWrittenby = null;
		
		String sqlDeleteBook = "delete from t_book where isbn = ?";
		String sqlDeleteFromWrittenby = "delete from t_writtenby where isbn = ? ";
		String sqlLookForISBN = "select isbn from t_book where isbn = ?";
		
		lookForIsbn = connection.prepareStatement(sqlLookForISBN);
		lookForIsbn.setString(1, ISBN);
		resultSet = lookForIsbn.executeQuery();
		
		if (resultSet.isBeforeFirst()) {
			try {
				connection.setAutoCommit(false);
				
				deleteBook = connection.prepareStatement(sqlDeleteBook);
				deleteBook.setString(1, ISBN);
				deleteBook.executeUpdate();
				
				deleteFromWrittenby = connection.prepareStatement(sqlDeleteFromWrittenby);
				deleteFromWrittenby.setString(1, ISBN);
				deleteFromWrittenby.executeUpdate();
				
				connection.commit();
			}
			catch (Exception e){
				if (connection != null)
					connection.rollback();
			}
			finally {
				if (deleteBook != null) {
					
				}
				connection.setAutoCommit(true);
			}
			return true;
		}
		return false;
	}

	@Override
	public List<Book> getBooks() throws SQLException {		
		List<Book> list = new ArrayList<>();
		List<Author> authors = new ArrayList<>();
		resultSet = statement.executeQuery(" select t_book.isbn, t_book.title, t_book.genre, t_book.rating, t_author.name, t_author.dob"
				+ " from t_writtenby, t_author, t_book "
				+ " where t_writtenby.name = t_author.name AND t_book.isbn = t_writtenby.isbn");
		
		list = copyBooksToList(resultSet);
		list = fixList(list);
		authors = getAuthorsBooks(list);
		
		return list;
		
	}	
	
	/**
	 * Process data from database, places book-data in list of books
	 * @param resultSet - data retrieved by query statement from the database
	 * @return list of books
	 * @throws SQLException
	 */
	
	public List<Author> getAuthorsBooks (List<Book> book) {
		
		ArrayList<Author> authors = new ArrayList<>();
		
		
		for (int i=0; i<book.size(); i++) {
			for (int j=0; j<book.get(i).getAuthors().size(); j++) {
				if(book.get(i).getAuthors().get(j).getName().equals(book.get(i).getAuthors().get(j).getName()) ) {
					
				}
			}
		}
		
		return authors;
	}
	
	private List<Book> copyBooksToList(ResultSet resultSet) throws SQLException {
		Author author;
		Book book;
		ArrayList<Author> authorList = new ArrayList<>();
		List<Book> list = new ArrayList<>();
		List<Book> list2 = new ArrayList<>();
		
		try {
			while (resultSet.next()) {
				author = new Author(resultSet.getString(5), resultSet.getDate(6).toLocalDate());
				genre = Genre.valueOf(resultSet.getString(3).toUpperCase());
				book = new Book(resultSet.getString(1), resultSet.getString(2),
						genre, resultSet.getInt(4));
				book.addAuthor(author);
				
				
				for (int i=0; i<list.size(); i++) {
					for (int j=0; j<list.get(i).getAuthors().size(); j++)
					if (author.getName().equalsIgnoreCase(list.get(i).getAuthors().get(j).getName())) {
						author.addIsbn(list.get(i).getIsbn());
					}
				}
 				
				author.addIsbn(book.getIsbn());
				list.add(book);				
			}
			
//			for (int i=0; i<list.size(); i++) {
//				for (int j=0; j<list.get(i).getAuthors().size(); j++) {
//					if (list.get(i).getAuthors().get(j).getName().equalsIgnoreCase(list.get(j+1).getAuthors().get(j).getName())) {
//						list.get(j+1).getAuthors().get(j).addIsbn(list.get(i).getIsbn());
//					}
//				}
//			}
			
			return list;
		}
		finally {
			resultSet.close();
		}
	}
	
	public List<Author> getAuthors() throws SQLException {
		String sqlGetAllAuthors = "select * from t_author";
		Author author;
		List<Author> list = new ArrayList<>();
		resultSet = statement.executeQuery(sqlGetAllAuthors);
		while (resultSet.next()) {
			author = new Author(resultSet.getString(1), resultSet.getDate(2).toLocalDate());
			list.add(author);
		}
		return list;
	}
	/**
	 * Formats list of books
	 * @param books
	 * @return the formatted list
	 */
	private List<Book> fixList(List<Book> books) {

		for (int i=0; i<books.size()-1; i++) {
			if(books.get(i).getIsbn().equalsIgnoreCase(books.get(i+1).getIsbn())) {
				books.get(i).addAuthor(books.get(i+1).getAuthors().get(0));
				books.remove(i+1);
			}
		}	
		return books;
	}
}

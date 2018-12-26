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
    public boolean disconnect() throws IOException, SQLException {
    	
		try {
			if(connection != null) {
				connection.close();
				return true;
			}
		}		
		finally {
			
		}
		return false;
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
    	if (connection != null) {
	    	try {
		    	searchByTitle = connection.prepareStatement(sql);
		    	searchByTitle.setString(1, s);
		    	resultSet=searchByTitle.executeQuery();
		    	list = copyBooksToList(resultSet);
				list = fixList(list);
				list = addAuthorsSearch(list);
	    	}
	    	
	    	finally {
	    		resultSet.close();
	    	}
    	}
    	return list;
    }

	@Override
	public List<Book> searchBooksByAuthor(String name) throws SQLException  {
		PreparedStatement searchByAuthor;
		List<Book> list = new ArrayList<>();
		
		String s = "%" + name + "%";
		String sql = 
				"select distinct t_book.isbn, t_book.title, t_book.genre, t_book.rating, t_author.name, t_author.dob "
						+ "from t_book, t_author, t_writtenby "
				+ "where t_author.name LIKE ? AND t_writtenby.isbn = t_book.isbn AND t_author.name = t_writtenby.name";
		if (connection != null) {
			try {
				searchByAuthor = connection.prepareStatement(sql);
				searchByAuthor.setString(1, s);
				resultSet = searchByAuthor.executeQuery();	
				list = copyBooksToList(resultSet);
				list = fixList(list);
				list = addAuthorsSearch(list);
				
			} 
			finally {
				resultSet.close();
			}
		}
		return list;
	}

	@Override
	public List<Book> searchBooksByISBN(String isbn) throws SQLException {

		PreparedStatement searchByISBN;
		List<Book> list = new ArrayList<>();
		
		
		String isbn_ = "%" + isbn + "%";
		String sql = "select t_book.isbn, t_book.title, t_book.genre, t_book.rating, t_author.name, t_author.dob "
				+ " from t_book, t_author, t_writtenby "
				+ " where t_book.isbn LIKE ? AND t_writtenby.name = t_author.name AND T_book.isbn = t_writtenby.isbn";
		if (connection != null) {
			try {
				searchByISBN = connection.prepareStatement(sql);
				searchByISBN.setString(1, isbn_);
				resultSet = searchByISBN.executeQuery();
				list = copyBooksToList(resultSet);
				list = fixList(list);
				list = addAuthorsSearch(list);
				
			}
			finally {
				resultSet.close();
			}
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
		if (connection != null) {
			try {
				searchByRating = connection.prepareStatement(sql);
				searchByRating.setString(1, rating);
				resultSet = searchByRating.executeQuery();
				list = copyBooksToList(resultSet);
				list = fixList(list);
				list = addAuthorsSearch(list);
				
			}
			finally {
				resultSet.close();
			}
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
		if (connection != null) {
			try {
				searchByGenre = connection.prepareStatement(sql);
				searchByGenre.setString(1, s);
				resultSet = searchByGenre.executeQuery();
				list = copyBooksToList(resultSet);
				list = fixList(list);
				list = addAuthorsSearch(list);
			} 
			finally {
				resultSet.close();
			}
		}
		return list;
	}

	@Override
	public void insertBook(Book book) throws SQLException {
		if (connection != null) {
			PreparedStatement insertBook = null;
			PreparedStatement insertAuthor = null;
			PreparedStatement insertWrittenby = null;
			List<Author> authors_ = new ArrayList<>();
			
			String sqlInsertBook = "insert into t_book values (?, ?, ?, ?)";
			String sqlInsertAuthor = "insert into t_author values (?, ?)";
			String sqlInsertWrittenby = "insert into t_writtenby values (?, ?, ?)";
			
			Author author;
			
			for (int i=0; i<book.getAuthors().size(); i++) {
				author = new Author(book.getAuthors().get(i).getName(), book.getAuthors().get(i).getDob());
				authors_.add(author);
			}
			
			
			try {
				insertAuthor = connection.prepareStatement(sqlInsertAuthor);
				for (int i=0; i<authors_.size(); i++) {
					insertAuthor.setString(1, authors_.get(i).getName());
					insertAuthor.setDate(2, java.sql.Date.valueOf(authors_.get(i).getDob()));
					insertAuthor.executeUpdate();
				}
			}
			catch (Exception e){
				
			}
			
			
			try {
				connection.setAutoCommit(false);
				insertBook = connection.prepareStatement(sqlInsertBook);
				insertBook.setString(1, book.getIsbn());
				insertBook.setString(2, book.getTitle());
				insertBook.setString(3, book.getGenre().getValue());
				insertBook.setInt(4, book.getRating());
				insertBook.executeUpdate();
				
				
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
	}

	@Override
	public boolean updateRating(String isbn, int rating) throws SQLException {
		PreparedStatement updateRating = null;
		
		String sqlUpdateRating = "update t_book "
								+ "set rating = ? "
								+ "where isbn = ? ";
		
		if (connection != null) {
			try {
				updateRating = connection.prepareStatement(sqlUpdateRating);
				updateRating.setInt(1, rating);
				updateRating.setString(2, isbn);
				if (updateRating.executeUpdate()==0)
					return false;
				else 	
					return true;
			}
			
			finally {
				
			}
			
		} else return false;
	}

	@Override
	public boolean addAuthor(String isbn, Author author) throws SQLException {
		
		PreparedStatement insertAuthor = null;
		PreparedStatement insertWrittenby = null;
		
		String sqlInsertAuthor = "insert into t_author values (?, ?)";
		String sqlInsertWrittenby = "insert into t_writtenby values (?, ?, ?)";

		if (connection != null) {
			try {
				insertAuthor = connection.prepareStatement(sqlInsertAuthor);
				insertAuthor.setString(1, author.getName());
				insertAuthor.setDate(2, java.sql.Date.valueOf(author.getDob()));
				insertAuthor.executeUpdate();
			}
			
			catch (Exception e) {
				
			}
			
			try {
				connection.setAutoCommit(false);
				
				insertWrittenby = connection.prepareStatement(sqlInsertWrittenby);
				insertWrittenby.setString(1, isbn);
				insertWrittenby.setString(2, author.getName());
				insertWrittenby.setDate(3, java.sql.Date.valueOf(author.getDob()));
				if (insertWrittenby.executeUpdate()==0) {
					return false;
				}
	
				else {
					connection.commit();
					return true;
				}
			}
					
			finally {
	
				insertAuthor.close();
				insertWrittenby.close();
			}
		}
		else return false;
	}

	@Override
	public boolean deleteBook(String ISBN) throws SQLException {
		PreparedStatement lookForIsbn = null;
		PreparedStatement deleteBook = null;
		PreparedStatement deleteFromWrittenby = null;
		
		String sqlDeleteBook = "delete from t_book where isbn = ?";
		String sqlDeleteFromWrittenby = "delete from t_writtenby where isbn = ? ";
		String sqlLookForISBN = "select isbn from t_book where isbn = ?";
		
		if (connection != null) {
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
		}
		return false;
	}

	@Override
	public List<Book> getBooks() throws SQLException {		
		List<Book> list = new ArrayList<>();
		if (connection != null) {
			resultSet = statement.executeQuery(" select t_book.isbn, t_book.title, t_book.genre, t_book.rating, t_author.name, t_author.dob"
					+ " from t_writtenby, t_author, t_book "
					+ " where t_writtenby.name = t_author.name AND t_writtenby.dob = t_author.dob AND t_book.isbn = t_writtenby.isbn");
			if (resultSet!=null) {
				list = copyBooksToList(resultSet);
				list = fixList(list);
				list = fixList2(list);
			}
		}
		return list;
	}	
	
	private List<Book> copyBooksToList(ResultSet resultSet) throws SQLException {
		Author author;
		Book book;
		List<Book> list = new ArrayList<>();
		
		try {
			while (resultSet.next()) {
				author = new Author(resultSet.getString(5), resultSet.getDate(6).toLocalDate());
				genre = Genre.valueOf(resultSet.getString(3).toUpperCase());
				book = new Book(resultSet.getString(1), resultSet.getString(2),
						genre, resultSet.getInt(4));
				book.addAuthor(author);
				author.addIsbn(book.getIsbn());
				list.add(book);
			}
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
	
	
	private List<Book> addAuthorsSearch(List<Book> books) {    	
    	List<Book> allBooks = new ArrayList<>();

		try {
			resultSet = statement.executeQuery(" select t_book.isbn, t_book.title, t_book.genre, t_book.rating, t_author.name, t_author.dob"
					+ " from t_writtenby, t_author, t_book "
					+ " where t_writtenby.name = t_author.name AND t_writtenby.dob = t_author.dob AND t_book.isbn = t_writtenby.isbn");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			allBooks = copyBooksToList(resultSet);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		allBooks = fixList(allBooks);
		
		for (int i=0; i<books.size(); i++) {
			for (int j=0; j<allBooks.size(); j++) {
				
				for (int k=0; k<books.get(i).getAuthors().size(); k++) {
					for (int m=0; m<allBooks.get(j).getAuthors().size(); m++) {
						
						if ((books.get(i).getAuthors().get(k).getName().equals(allBooks.get(j).getAuthors().get(m).getName() )  
								&& !books.get(i).getIsbn().equals(allBooks.get(j).getIsbn() ))) {
							
								books.get(i).getAuthors().get(k).addIsbn(allBooks.get(j).getIsbn());
						}
					}
				}
			}
		}
		
		return books;
	}
	/**
	 * Places authors of the same book in a single row, removes extra book
	 * @param books
	 * @return
	 */
	private List<Book> fixList(List<Book> books) {
		for (int i=0; i<books.size(); i++) {
			for (int j=i; j<books.size()-1; j++) {
				if (books.get(i).getIsbn().equals(books.get(j+1).getIsbn())) {
					books.get(i).addAuthor(books.get(j+1).getAuthors().get(0));
					books.remove(j+1);
				}
			}
		}
		return books;	
	}
	
	
	/**
	 * Konstig funktion som lägger till böcker i författarnas isbn lista
	 * den gämför författarna, om dem är lika fixar grejer
	 * @param books
	 * @return
	 */
	private List<Book> fixList2(List<Book> books) {
		
		for (int i=0; i<books.size(); i++) {
			for (int j=i; j<books.size()-1; j++) {

				for (int k=0; k<books.get(i).getAuthors().size(); k++) {
					for (int m=0; m<books.get(j+1).getAuthors().size(); m++) {
						
						if (books.get(i).getAuthors().get(k).getName().equals(books.get(j+1).getAuthors().get(m).getName())
								&& books.get(i).getAuthors().get(k).getDob().equals(books.get(j+1).getAuthors().get(m).getDob())) {
								books.get(j+1).getAuthors().get(m).addIsbn(books.get(i).getIsbn());
								books.get(i).getAuthors().get(k).addIsbn(books.get(j+1).getIsbn());
						}
					}
				}
			}
		}
		return books;
	}
	
	
	
}








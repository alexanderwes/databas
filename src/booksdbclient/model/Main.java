package booksdbclient.model;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	public static void main(String args[]) throws IOException, SQLException, ParseException {
		
		MockBooksDb mock = new MockBooksDb();
		
		List<Book> list = new ArrayList<>();
		
		LocalDate date = LocalDate.of(2011, 4, 21);
		LocalDate date2 = LocalDate.of(1988, 2, 27);
		
		List<Author> authors = new ArrayList<>();
		
		Library lib = new Library();
		
		
		Author a1 = new Author("Anders2", date);
		Author a2 = new Author("Dim2", date2);
		
		
		Book book = new Book("2481-123-854-2", "IT", Genre.HORROR, 4);
		Book boo2 = new Book("123123-1313-1", "KAS", Genre.FANTASY, 2);
		
		
		
//		a1.addBook(book);
//		book.addAuthor(a1);
	//	book.addAuthor(a2);
		
		//list.add(book);
	//	list.add(boo2);
		
	//	System.out.println(book);
		
		
		try {
			mock.connect("library");
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		//	boolean b = mock.deleteBook("2481-123-854-2");
			//System.out.println(b);
			
			list = mock.getBooks();
//			for (int i=0; i<list.size(); i++) {
//				System.out.println(list.get(i));
//			}
			
			
			//list = mock.searchBooksByTitle("z");
			//list = mock.searchBooksByAuthor("b");
			//list = mock.searchBooksByRating(3);
			//list = mock.searchBooksByISBN("123");
			//list = mock.searchBooksByGenre(Genre.HORROR);
			
			//mock.updateRating(book, 4);
		
		//	list = mock.searchBooksByAuthor("a");
		
			
			//mock.addAuthors(authors);
//			authors.clear();
//			authors.add(new Author("Stephen King", LocalDate.of(1940, 06, 26)));
//			
//			mock.insertBook("2481-123-854-2", "IT", authors, Genre.HORROR, 5);			
		//	System.out.println("result: " + mock.searchBooksByGenre(Genre.ROMANCE));
		//	list = mock.searchBooksByGenre(Genre.SCIENCE);
//		Author author3 = new Author("Nicklas", LocalDate.of(1970, 8, 30));
//		Book book3 = new Book("2318-555-11", "DumAdam", Genre.DRAMA, 4);
//		book3.addAuthor(author3);
//		mock.insertBook(book3);
//		list = mock.getBooks();
//		for(int i=0; i<list.size(); i++) {
//			System.out.println(list.get(i));
//		}
		

		
		try {
			mock.disconnect();
		}
		catch (Exception e) {
			System.out.println("Already closed");
			e.printStackTrace();
		}
	}
}

//class Threadss implements Runnable {
//	
//	
//	List<Book> books = new ArrayList<>();
//	MockBooksDb mock = new MockBooksDb();
//	
//	public Threadss() {
//		
//	}
//	
//	@Override
//	public void run() {
//		try {
//			mock.connect("library");
//			books = mock.getBooks();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		for (int i=0; i<books.size(); i++) {
//			System.out.println(books.get(i));
//		}
//	}
//}

package booksdbclient.view;

import booksdbclient.model.Author;
import booksdbclient.model.Book;
import booksdbclient.model.BooksDbInterface;
import booksdbclient.model.Genre;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.scene.control.Alert.AlertType.*;

/**
 * The controller is responsible for handling user requests and update the view
 * (and in some cases the model).
 *
 * @author anderslm@kth.se
 */
public class Controller {

    private final BooksPane booksView; // view
    private final BooksDbInterface booksDb; // model

    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
    }

    protected void onSearchSelected(String searchFor, SearchMode mode) {
        try {
            if (searchFor != null && searchFor.length() > 0) {
                List<Book> result = null;
                switch (mode) {
                    case Title:
                        result = booksDb.searchBooksByTitle(searchFor);
                        break;
                    case ISBN:
                        result = booksDb.searchBooksByISBN(searchFor);
                        break;
                    case Author:
                        result = booksDb.searchBooksByAuthor(searchFor);
                        break;
                    case Genre:
                        result = booksDb.searchBooksByGenre(Genre.valueOf(searchFor.toUpperCase()));
                        break;
                    case Rating:
                        result = booksDb.searchBooksByRating(searchFor);
                    default:
                }
                if (result == null || result.isEmpty()) {
                    booksView.showAlertAndWait(
                            "No results found.", INFORMATION);
                } else {
                    booksView.displayBooks(result);
                }
            } else {
                booksView.showAlertAndWait(
                        "Enter a search string!", WARNING);
            }
        } catch (IOException | SQLException e) {
            booksView.showAlertAndWait("Database error.", ERROR);
        }
    }

    protected void connectToDb() {
        try {
            booksDb.connect("library");
            booksView.showAlertAndWait("Connected", AlertType.INFORMATION);
        } catch (IOException | SQLException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void disconnectFromDb() {
        try {
            booksDb.disconnect();
            booksView.showAlertAndWait("Disconnected", AlertType.INFORMATION);
        } catch (IOException | SQLException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected void printAllBooks() {
    	try {
			booksView.displayBooks(booksDb.getBooks());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    protected void addbook(String title, String name, int year, int month, int day, String isbn, Genre genre, int rating) {

        if (!title.trim().isEmpty() && !isbn.trim().isEmpty() && !name.trim().isEmpty()) {
            Book book = new Book(isbn, title, genre, rating);
            book.addAuthor(new Author(name, LocalDate.of(year, month, day)));
            try {
                booksDb.insertBook(book);
                
                booksView.showAlertAndWait("Book has been added", INFORMATION);
            } catch (SQLException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            booksView.showAlertAndWait("You need to provide sufficient information!", ERROR);
        }
    }

    protected void removeBook(String isbn) {
        if (!isbn.trim().isEmpty()) {
            try {
                booksDb.deleteBook(isbn);
                booksView.showAlertAndWait("Book has been removed", INFORMATION);
            } catch (SQLException ex) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            booksView.showAlertAndWait("You need to provide sufficient information!", ERROR);
        }
    }

    protected void addAuthor(String isbn, String name, int year, int month, int day) {
    	Author author = null;
    	author = new Author(name, LocalDate.of(year, month, day));
        try {
			booksDb.addAuthor(isbn, author);
		} catch (SQLException e) {
			booksView.showAlertAndWait("No book with this isbn", ERROR);
		}
        
    }

    protected void updateRating(String isbn, int rating) {
    	
    	try {
			if (booksDb.updateRating(isbn, rating)) {
				booksView.showAlertAndWait("Rating has been successfully updated", AlertType.INFORMATION);
			}
			else 
				booksView.showAlertAndWait("Book not found", AlertType.ERROR);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
   
    	
    }
}

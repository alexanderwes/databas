package booksdbclient.view;

import booksdbclient.model.Author;
import booksdbclient.model.Book;
import booksdbclient.model.BooksDbInterface;
import booksdbclient.model.Genre;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;

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
                        result = booksDb.searchBooksByGenre(Genre.valueOf(searchFor));
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
        } catch (IOException | SQLException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void disconnectFromDb() {
        try {
            booksDb.disconnect();
        } catch (IOException | SQLException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void addbook(String title, String name, String isbn, Genre genre, int rating) {

        if (!title.trim().isEmpty() && !isbn.trim().isEmpty() && !name.trim().isEmpty()) {
            Book book = new Book(isbn, title, genre, rating);
            book.addAuthor(new Author(name, LocalDate.now()));
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

    protected void addAuthor(String isbn, String name) {
        List<Author> authors = new ArrayList<>();
        authors.add(new Author(name, LocalDate.now()));
    }

    protected void updateRating(String isbn, int rating) {

    }
}

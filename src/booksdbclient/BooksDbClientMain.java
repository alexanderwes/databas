package booksdbclient;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import booksdbclient.model.Book;
import booksdbclient.model.Genre;
import booksdbclient.model.MockBooksDb;
import booksdbclient.view.BooksPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Application start up.
 *
 * @author anderslm@kth.se
 */
public class BooksDbClientMain extends Application {

    @Override
    public void start(Stage primaryStage) throws SQLException {

        MockBooksDb booksDb = new MockBooksDb(); // model
        // Don't forget to connect to the db, somewhere...

        
        
        BooksPane root = new BooksPane(booksDb);

        Scene scene = new Scene(root, 800, 600);

        Book book = new Book("41515-1-10", "Java", Genre.SCIENCE, 5);
        
        List<Book> list = new ArrayList<>();
        booksDb.connect("library");
//        list = booksDb.getBooks();

//        try {
//			booksDb.insertBook(book);
//		} catch (SQLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
        
        list = booksDb.searchBooksByTitle("a");
        for (int i=0; i<list.size(); i++) {
        	System.out.println(list.get(i));
        }
        
        primaryStage.setTitle("Books Database Client");
        // add an exit handler to the stage (X) ?
        primaryStage.setOnCloseRequest(event -> {
            try {
                booksDb.disconnect();
                primaryStage.close();
            } catch (Exception e) {}
        });
        primaryStage.setScene(scene);
//        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

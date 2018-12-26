package booksdbclient.view;

import booksdbclient.model.Author;
import booksdbclient.model.Book;
import booksdbclient.model.Genre;
import booksdbclient.model.MockBooksDb;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * The main pane for the view, extending VBox and including the menus. An
 * internal BorderPane holds the TableView for books and a search utility.
 *
 * @author anderslm@kth.se
 */
public class BooksPane extends VBox {

    private TableView<Book> booksTable;
    private ObservableList<Book> booksInTable; // the data backing the table view

    private ComboBox<SearchMode> searchModeBox;
    private TextField searchField;
    private Button searchButton;
    private Button printAllBooks;
    private MenuBar menuBar;

    public BooksPane(MockBooksDb booksDb) {
        final Controller controller = new Controller(booksDb, this);
        this.init(controller);
    }

    /**
     * Display a new set of books, e.g. from a database select, in the
     * booksTable table view.
     *
     * @param books the books to display
     */
    
    public void clearTable() {
    	booksInTable.clear();
    }
    public void displayBooks(List<Book> books) {
        booksInTable.clear();
        booksInTable.addAll(books);
    }

    /**
     * Notify user on input error or exceptions.
     *
     * @param msg the message
     * @param type types: INFORMATION, WARNING et c.
     */
    protected void showAlertAndWait(String msg, Alert.AlertType type) {
        // types: INFORMATION, WARNING et c.
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }

    private void init(Controller controller) {

        booksInTable = FXCollections.observableArrayList();

        // init views and event handlers
        initBooksTable();
        initSearchView(controller);
        initMenus(controller);

        FlowPane bottomPane = new FlowPane();
        bottomPane.setHgap(10);
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        bottomPane.getChildren().addAll(searchModeBox, searchField, searchButton, printAllBooks);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(booksTable);
        mainPane.setBottom(bottomPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().addAll(menuBar, mainPane);
        VBox.setVgrow(mainPane, Priority.ALWAYS);
    }

    private void initBooksTable() {
        booksTable = new TableView<>();
        booksTable.setStyle("-fx-focus-color: transparent;");

        TableColumn<Book, String> isbn = new TableColumn<>("ISBN");
        TableColumn<Book, String> title = new TableColumn<>("Title");
        TableColumn<Book, String> genre = new TableColumn<>("Genre");
        TableColumn<Book, Integer> rating = new TableColumn<>("Rating");
        TableColumn<Book, Author> authors = new TableColumn<>("Authors");

        booksTable.getColumns().addAll(title, authors, genre, isbn, rating);

        title.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.30));
        authors.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.30));
        isbn.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.20));

        title.setCellValueFactory(new PropertyValueFactory<>("Title"));
        authors.setCellValueFactory(new PropertyValueFactory<>("authors"));
        genre.setCellValueFactory(new PropertyValueFactory<>("Genre"));
        rating.setCellValueFactory(new PropertyValueFactory<>("Rating"));
        isbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        // associate the table view with the data
        booksTable.setItems(booksInTable);
    }

    private void initSearchView(Controller controller) {
        searchField = new TextField();
        searchField.setPromptText("Search for...");
        searchModeBox = new ComboBox<>();
        searchModeBox.getItems().addAll(SearchMode.values());
        searchModeBox.setValue(SearchMode.Title);
        searchButton = new Button("Search");
        printAllBooks = new Button("Print all books");

        // event handling (dispatch to controller)
        searchButton.setOnAction((ActionEvent event) -> {
            String searchFor = searchField.getText();
            SearchMode mode = searchModeBox.getValue();
            controller.onSearchSelected(searchFor, mode);
        });
        
        printAllBooks.setOnAction(e -> {
        	controller.printAllBooks();
        });
    }

    private void initMenus(Controller controller) {

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        MenuItem connectItem = new MenuItem("Connect to Db");
        MenuItem disconnectItem = new MenuItem("Disconnect");
        fileMenu.getItems().addAll(connectItem, disconnectItem, exitItem);

        Menu manageMenu = new Menu("Manage");
        MenuItem addItem = new MenuItem("Add book");
        MenuItem removeItem = new MenuItem("Remove book");
        MenuItem authorItem = new MenuItem("Add author");
        MenuItem updateItem = new MenuItem("Update rating");
        manageMenu.getItems().addAll(addItem, removeItem, authorItem, updateItem);

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, manageMenu);

        exitItem.setOnAction(e -> {
            controller.disconnectFromDb();
            Platform.exit();
        });

        connectItem.setOnAction(e -> {
            controller.connectToDb();
        });

        disconnectItem.setOnAction(e -> {
            controller.disconnectFromDb();
        });

        addItem.setOnAction(e -> {
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            TextField title = new TextField();
            grid.add(new Label("Title"), 0, 0);
            grid.add(title, 1, 0);

            TextField isbn = new TextField();
            grid.add(new Label("ISBN"), 0, 1);
            grid.add(isbn, 1, 1);

            TextField author = new TextField();
            grid.add(new Label("Author"), 0, 2);
            grid.add(author, 1, 2);
            
            TextField year = new TextField();
            TextField month = new TextField();
            TextField day = new TextField();
            grid.add(new Label("Birth Date \n(yyyy-mm-dd) "), 0, 3);
            grid.add(year, 1, 3);
            grid.add(month, 2, 3);
            grid.add(day, 3, 3);
           

//            year.setMaxWidth(50);
            month.setMaxWidth(50);
            day.setMaxWidth(50);
            
            
            Button OK = new Button();
            grid.add(OK, 1, 4);

            ObservableList<Genre> choicesGenre = FXCollections.observableArrayList();
            choicesGenre.addAll(Genre.FANTASY, Genre.DRAMA, Genre.HORROR, Genre.ROMANCE, Genre.SCIENCE);
            ComboBox<Genre> genre = new ComboBox<Genre>(choicesGenre);
            genre.getSelectionModel().selectFirst();
            grid.add(new Label("Genre"), 0, 5);
            grid.add(genre, 1, 5);

            ObservableList<Integer> choiceRating = FXCollections.observableArrayList();
            choiceRating.addAll(1, 2, 3, 4, 5);
            ComboBox<Integer> rating = new ComboBox<Integer>(choiceRating);
            rating.getSelectionModel().selectFirst();
            grid.add(new Label("Rating"), 0, 4);
            grid.add(rating, 1, 4);

            ButtonType submit = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            Dialog<Book> dialog = new Dialog<Book>();

            dialog.setTitle("Add a book");
            dialog.getDialogPane().setContent(grid);

            dialog.getDialogPane().getButtonTypes().addAll(submit, cancel);
            dialog.getDialogPane().lookupButton(submit).addEventFilter(
                    ActionEvent.ACTION, event -> {
                    	
                        controller.addbook(title.getText(), author.getText(), Integer.parseInt("0" + year.getText()),
                        		Integer.parseInt("0" + month.getText()), Integer.parseInt("0" + day.getText()), isbn.getText(), genre.getValue(), rating.getValue());
                    });
            dialog.showAndWait();
        });

        removeItem.setOnAction(e -> {
            GridPane grid = new GridPane();
            TextField isbn = new TextField();
            grid.add(new Label("ISBN"), 0, 1);
            grid.add(isbn, 1, 1);

            Dialog<Book> dialog = new Dialog<>();
            ButtonType remove = new ButtonType("Remove", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            dialog.setTitle("Remove a book");
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(remove, cancel);
            dialog.getDialogPane().lookupButton(remove).addEventHandler(
                    ActionEvent.ACTION, event -> {
                        controller.removeBook(isbn.getText());
                    });
            isbn.requestFocus();
            dialog.showAndWait();
        });

        authorItem.setOnAction(e -> {
            GridPane grid = new GridPane();
            TextField isbn = new TextField();
            TextField author = new TextField();
            TextField year = new TextField();
            TextField month = new TextField();
            TextField day = new TextField();
            
            grid.add(new Label("ISBN"), 0, 1);
            grid.add(isbn, 1, 1);

            grid.add(new Label("Author"), 0, 2);
            grid.add(author, 1, 2);
         
            grid.add(new Label("Birth Date \n(yyyy-mm-dd) "), 0, 3);
            grid.add(year, 1, 3);
            grid.add(month, 2, 3);
            grid.add(day, 3, 3);
           

//            year.setMaxWidth(50);
            month.setMaxWidth(50);
            day.setMaxWidth(50);

            grid.setPadding(new Insets(10, 10, 10, 10));
            
            Dialog<Book> dialog = new Dialog<>();
            ButtonType submit = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            dialog.setTitle("Add an author to a book");
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(submit, cancel);

            dialog.getDialogPane().lookupButton(submit).addEventHandler(ActionEvent.ACTION, event -> {
                controller.addAuthor(isbn.getText(), author.getText(), Integer.parseInt("0" + year.getText()),
                		Integer.parseInt("0" + month.getText()), Integer.parseInt("0" + day.getText()));
            });
            dialog.showAndWait();
        });
        
        updateItem.setOnAction(e -> {
        	GridPane grid = new GridPane();
        	TextField isbn = new TextField();
        	
            grid.add(new Label("ISBN"), 0, 1);
            grid.add(isbn, 1, 1);
            
            ObservableList<Integer> choiceRating = FXCollections.observableArrayList();
            choiceRating.addAll(1, 2, 3, 4, 5);
            ComboBox<Integer> rating = new ComboBox<Integer>(choiceRating);
            rating.getSelectionModel().selectFirst();
            grid.add(new Label("Rating "), 0, 2);
            grid.add(rating, 1, 2);
            
            Dialog<Book> dialog = new Dialog<>();
            ButtonType submit = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            
            dialog.setTitle("Update rating");
            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(submit, cancel);
            
            dialog.getDialogPane().lookupButton(submit).addEventHandler(ActionEvent.ACTION, event -> {
            	controller.updateRating(isbn.getText(), rating.getValue());
            });
            dialog.showAndWait();
        });
    }
}

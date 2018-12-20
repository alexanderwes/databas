package booksdbclient.view;

import booksdbclient.model.SearchMode;
import booksdbclient.model.Book;
import booksdbclient.model.Genre;
import booksdbclient.model.MockBooksDb;
import java.sql.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
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
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
        bottomPane.getChildren().addAll(searchModeBox, searchField, searchButton);

        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(booksTable);
        mainPane.setBottom(bottomPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().addAll(menuBar, mainPane);
        VBox.setVgrow(mainPane, Priority.ALWAYS);
    }

    private void initBooksTable() {
        booksTable = new TableView<>();
        booksTable.setEditable(false); // don't allow user updates (yet)

        // define columns
        TableColumn<Book, String> genreCol = new TableColumn<>("Genre");
        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        TableColumn<Book, Integer> ratingCol = new TableColumn<>("Rating");
        booksTable.getColumns().addAll(titleCol, isbnCol, genreCol, ratingCol);
        // give title column some extra space
        titleCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.5));

        // define how to fill data for each cell, 
        // get values from Book properties
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
       // publishedCol.setCellValueFactory(new PropertyValueFactory<>("published"));
        
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
        
        // event handling (dispatch to controller)
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String searchFor = searchField.getText();
                SearchMode mode = searchModeBox.getValue();
                controller.onSearchSelected(searchFor, mode);
            }
        });
    }

    private void initMenus(Controller controller) {

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> {
        	controller.disconnectFromDb();
        	Platform.exit();
        });
        
        MenuItem connectItem = new MenuItem("Connect to Db");
        connectItem.setOnAction(e -> 
        controller.connectToDB());
        MenuItem disconnectItem = new MenuItem("Disconnect");
        disconnectItem.setOnAction(e -> controller.disconnectFromDb());
        fileMenu.getItems().addAll(exitItem, connectItem, disconnectItem);

//        Menu searchMenu = new Menu("Search");
//        
//        MenuItem titleItem = new MenuItem("Title");
//        MenuItem isbnItem = new MenuItem("ISBN");
//        MenuItem authorItem = new MenuItem("Author");
//        searchMenu.getItems().addAll(titleItem, isbnItem, authorItem);

        
        
        Menu manageMenu = new Menu("Manage");
        MenuItem addItem = new MenuItem("Add");
        addItem.setOnAction(e -> {
        	Stage window = new Stage();
    		window.initModality(Modality.APPLICATION_MODAL);
    		window.setTitle("Add book");
    		
    		GridPane layout = new GridPane();
    		layout.setPadding(new Insets(10, 10, 10, 10));
    		layout.setVgap(10);
    		layout.setHgap(10);
    		
    		Label title = new Label("Title: ");
    		GridPane.setConstraints(title, 0, 0);
    		Label isbn = new Label("ISBN: ");
    		GridPane.setConstraints(isbn, 0, 1);
    		Label authorName = new Label("Authors name: ");
    		GridPane.setConstraints(authorName, 0, 2);
    		Label authorDob = new Label("Authors date of birth: ");
    		GridPane.setConstraints(authorDob, 0, 3);
    		Label genre = new Label("Genre: ");
    		GridPane.setConstraints(genre, 0, 4);
    		Label rating = new Label("Rating: ");
    		GridPane.setConstraints(rating, 0, 5);
    		
    		TextField titleF = new TextField();
    		GridPane.setConstraints(titleF, 1, 0);
    		TextField isbnF = new TextField();
    		GridPane.setConstraints(isbnF, 1, 1);
    		TextField authorNameF = new TextField();
    		GridPane.setConstraints(authorNameF, 1, 2);
    		TextField authorDobF = new TextField();
    		GridPane.setConstraints(authorDobF, 1, 3);
    		ComboBox genreF = new ComboBox();
    		genreF.getItems().addAll(Genre.values());
    		GridPane.setConstraints(genreF, 1, 4);
    		TextField ratingF = new TextField();
    		GridPane.setConstraints(ratingF, 1, 5);
    		
    		Button submit = new Button("Submit");
    		GridPane.setConstraints(submit, 1, 8);
    		
    		layout.getChildren().addAll(title, isbn, genre, authorName, authorDob, rating, titleF, isbnF, authorNameF, authorDobF, genreF, ratingF, submit);
    		
    		Scene scene = new Scene(layout, 300, 350);
    		
    		window.setScene(scene);
    		window.showAndWait();
        });
        MenuItem removeItem = new MenuItem("Remove");
        MenuItem updateItem = new MenuItem("Update");
        updateItem.setOnAction(e -> {
        	TextInputDialog dialog = new TextInputDialog();
        	dialog.setTitle("Update rating");
        	dialog.setContentText("Enter the rating");
        });
        manageMenu.getItems().addAll(addItem, removeItem, updateItem);

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, /*searchMenu,*/ manageMenu);
    }
    
    
}

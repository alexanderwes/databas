package booksdbclient.view;

import booksdbclient.model.Genre;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Form {
	
	public static void display() {
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
	}
}

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
    	new Thread() {
    		List<Book> result;
    		public void run() {
    			try {
    	            if (searchFor != null && searchFor.length() > 0) {
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
    	                    	try {
    	                    		result = booksDb.searchBooksByGenre(Genre.valueOf(searchFor.toUpperCase()));
    	                        	break;
    	                    	}
    	                    	catch (Exception e){
    	                    		
    	                    	}
    	                    	finally {
    	                    		
    	                    	}
    	                    case Rating:
    	                        result = booksDb.searchBooksByRating(searchFor);
    	                    default:
    	                }
    	            } 
    	        } catch (IOException | SQLException e) {
    	            booksView.showAlertAndWait("Database error.", ERROR);
    	        }

        		javafx.application.Platform.runLater(new Runnable() {
        			public void run() {
        				if (searchFor == null || searchFor.length() == 0) {
        					booksView.showAlertAndWait("Enter a search string!", WARNING);
        				}
        				else if (result == null || result.isEmpty()) {
        					booksView.clearTable();
    	                    booksView.showAlertAndWait(
    	                            "No results found.", INFORMATION);
    	                
    	                } else {
    	                    booksView.displayBooks(result);
    	                }
        			}
        		});
    		}
    	}.start();
    }

    protected void connectToDb() {
    	
    	new Thread() {
    		public void run() {
    	        try {
    	            booksDb.connect("library"); 
    	        } catch (IOException | SQLException ex) {
    	            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
    	        }
    	        
    	        javafx.application.Platform.runLater(new Runnable() {
    	        	public void run() {
    	        		 booksView.showAlertAndWait("Connected", AlertType.INFORMATION);
    	        	}
    	        });
    		}
    	}.start();
    	
    }

   protected void disconnectFromDb() {
    	new Thread() {
    		public void run() {
    			try {
    				if (booksDb.disconnect()) {
    					javafx.application.Platform.runLater(new Runnable() {
    						public void run() {
    							booksView.showAlertAndWait("Disconnected", AlertType.INFORMATION);
    						}
    					});
    				} else {
    					
    					javafx.application.Platform.runLater(new Runnable() {
    						public void run() {
    							try {
    								booksView.showAlertAndWait("Error", AlertType.ERROR);
    							}
    							catch (Exception e){
    								
    							}
    						}
    					});
    				}
    			} catch (IOException | SQLException ex) {
    	            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
    	        }
    		}
    	}.start();
    }
    
    protected void printAllBooks() {
    	
    	new Thread() {
    		ArrayList<Book> books;
    		public void run() {
    			try {
    				books = (ArrayList<Book>) booksDb.getBooks();
    			}
    		    catch (SQLException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			if (books!=null) {
	    			javafx.application.Platform.runLater(new Runnable() {
	    				public void run() {
	    					booksView.displayBooks(books);
	    				}
	    			});
    			}
    		}
    	}.start();
    }

    protected void addbook(String title, String name, int year, int month, int day, String isbn, Genre genre, int rating) {
 
        if (!title.trim().isEmpty() && !isbn.trim().isEmpty() && !name.trim().isEmpty()
        		&& year != 0 && month !=0 && day !=0) {
            Book book = new Book(isbn, title, genre, rating);
            book.addAuthor(new Author(name, LocalDate.of(year, month, day)));
            
            new Thread() {
            	public void run() {
            		try {
            			if (booksDb.insertBook(book)) {
            				javafx.application.Platform.runLater(new Runnable() {
            					public void run() {
            						booksView.showAlertAndWait("Book has been added", INFORMATION);
            					}
            				});
            			}
            			
            			else {
        					javafx.application.Platform.runLater(new Runnable() {
        						public void run() {
        							booksView.showAlertAndWait("Book already exists or not connected to DB", ERROR);
        						}
        					});
        				}
            		} catch (SQLException ex){
            			Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
            		}
            	}
            }.start();
            
        	} else {
            	booksView.showAlertAndWait("You need to provide sufficient information!", ERROR);
            }
    }

    protected void removeBook(String isbn) {
        if (!isbn.trim().isEmpty()) {
        	
        	new Thread() {
        		public void run() {
                    try {
                        if (booksDb.deleteBook(isbn)) {
                        	 javafx.application.Platform.runLater(new Runnable() {
                             	public void run() {
                             		booksView.showAlertAndWait("Book has been removed", INFORMATION);
                             	}
                             });
                        }
                        else {
                        	 javafx.application.Platform.runLater(new Runnable() {
                             	public void run() {
                             		booksView.showAlertAndWait("Book not found", ERROR);
                             	}
                             });
                        }
                        
                    } catch (SQLException ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   
        		}
        	}.start();

        } else {
            booksView.showAlertAndWait("You need to provide sufficient information!", ERROR);
        }
    }

    protected void addAuthor(String isbn, String name, int year, int month, int day) {
    	
    	if (isbn != null && name != null && year != 0 && month != 0 && day != 0) {
	    	new Thread() {
	        	Author author = new Author(name, LocalDate.of(year, month, day));
	    		public void run() {
	    	        try {
	    				if (booksDb.addAuthor(isbn, author)) {
	    				    javafx.application.Platform.runLater(new Runnable() {
	    	    	        	public void run() {
	    	    	        		booksView.showAlertAndWait("Author added", INFORMATION);
	    	    	        	}
	    	    	        });
	    				} else {
	    					javafx.application.Platform.runLater(new Runnable() {
	    						public void run () {
	    							booksView.showAlertAndWait("Book not found", ERROR);
	    						}
	    					});
	    				}
	    			} catch (SQLException e) {
	    				booksView.showAlertAndWait("No book with this isbn", ERROR);
	    			}
	    		}
	    		
	    		
	    	}.start();
    	}
    	else
    		booksView.showAlertAndWait("No sufficient info", ERROR);
        
    }
    
    protected void updateRating(String isbn, int rating) {
    	new Thread() {
    		public void run() {
    	    	try {
    				if (booksDb.updateRating(isbn, rating)) {
    					javafx.application.Platform.runLater(new Runnable() {
    						public void run() {
    							booksView.showAlertAndWait("Rating has been successfully updated", AlertType.INFORMATION);	
    						}	
    					});
    					
    				}
    				else 
    					javafx.application.Platform.runLater(new Runnable() {
    						public void run() {
    							
    		    				booksView.showAlertAndWait("Book not found", AlertType.ERROR);
    						}
    					});
    				
    			} catch (SQLException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    	}.start();

    }
}

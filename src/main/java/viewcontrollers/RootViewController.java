package viewcontrollers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import models.User;

import org.apache.log4j.Logger;

import db.SQLManager;

/**
 * 
 * @author Sami
 *
 */
public class RootViewController implements Initializable, RightPaneSetter {

	/** Log4j */ 
	private static final Logger log = Logger.getLogger(RootViewController.class);

	/** the logged-on user */ 
	private User user; 

	private Stage stage; 

	@FXML AnchorPane rightPane; 
	@FXML AnchorPane bottomPane; 


	/**
	 * 
	 */
	private SQLManager sqlManager = SQLManager.getSQL();

	@FXML ExpenseViewController expenseViewController; 
	@FXML AccountViewController accountViewController; 
	@FXML LoggerViewController 	loggerViewController; 

	public RootViewController() {}

	public void initialize(URL location, ResourceBundle resources) {
		
	
		expenseViewController.setExpenseReadWriter(SQLManager.getSQL());
		expenseViewController.setRightPaneSetter(this);
		expenseViewController.setUser(user);
		expenseViewController.refresh();
		
		accountViewController.setAccountsReadWriter(SQLManager.getSQL());
		accountViewController.setRightPaneSetter(this);
		accountViewController.setUser(user);
		accountViewController.refresh();
	}

	@FXML private void importCSV() { 
		FileChooser fc = new FileChooser();
		//TODO: generalise default directory
		fc.setInitialDirectory(new File("/Users/Sami/Desktop/"));
		fc.setSelectedExtensionFilter(new ExtensionFilter("Comma-separated-file", ".csv")); 
		File chosenFile = fc.showOpenDialog(stage);
		
		/* if operation is aborted */ 
		if (chosenFile == null) 
			return; 
		
		expenseViewController.importCSV(chosenFile, sqlManager);
	}

	@FXML private void addCard() {
		log.warn("addCard() function not implemented"); 
	}

//	@FXML private void addUser() {
//		log.trace("addUser");
//
//		final String filename = "../views/popups/NewUser.fxml"; 
//		FXMLLoader loader = new FXMLLoader(); 
//		loader.setLocation(Launcher.class.getResource(filename));
//		AnchorPane rootPane;
//		try {
//			rootPane = loader.load();
//			NewUserController controller =  loader.getController(); 
//			Scene scene = new Scene(rootPane);
//			Stage newUserStage = new Stage(); 
//
//			/* when the user selects cancel*/ 
//			controller.getCancelButtonProperty().addListener(
//					(change, oldVal, newVal) -> {
//						/* close the stage */
//						newUserStage.close();
//					});
//
//			newUserStage.setScene(scene);
//			newUserStage.showAndWait(); 
//
//		} catch (IOException e) {
//			log.error("Porblem creating user");
//		}
//
//	}

	
	@Override
	public void showAddCardOnRightPane() { 

		final String filename = "../views/NewCard.fxml"; 
		try {
			/* load fxml */ 
			FXMLLoader loader = new FXMLLoader(); 
			loader.setLocation(RootViewController.class.getResource(filename));
			AnchorPane rootPane = loader.load(); 
			NewCardViewController controller = loader.getController(); 
			/* configure the controller
			 * call autofillTextFields at the end
			 */
//			controller.setAccountReader(sqlManager);
			controller.setWriter(sqlManager);
			controller.setRightPaneSetter(this);
			controller.setUser(user);

			/* change the displayed pane on the right side */ 
			rightPane.getChildren().clear(); 
			rightPane.getChildren().add(rootPane);
		} catch (IOException io) { 
			io.printStackTrace();
			log.error(String.format("Problem loading %s", filename));
		}
	}
	
	@Override
	public void showAddExpenseOnRightPane() { 

		final String filename = "../views/NewExpense.fxml"; 
		try {
			/* load fxml */ 
			FXMLLoader loader = new FXMLLoader(); 
			loader.setLocation(RootViewController.class.getResource(filename));
			AnchorPane rootPane = loader.load(); 
			NewExpenseViewController controller = loader.getController(); 
			/* configure the controller
			 * call autofillTextFields at the end
			 */
//			controller.setAccountReader(sqlManager);
			controller.setWriter(sqlManager);
			controller.setExpenseReader(sqlManager);
			controller.setRightPaneSetter(this);
			controller.setUser(user);
			controller.refresh();
			
			/* change the displayed pane on the right side */ 
			rightPane.getChildren().clear(); 
			rightPane.getChildren().add(rootPane);
		} catch (IOException io) { 
			io.printStackTrace();
			log.error(String.format("Problem loading %s", filename));
		}
	}
	
	@Override
	public void showAddAccountOnRightPane(String sortCode, String accountNumber) {

		final String filename = "../views/NewAccount.fxml"; 
		try {
			/* load fxml */ 
			FXMLLoader loader = new FXMLLoader(); 
			loader.setLocation(RootViewController.class.getResource(filename));
			AnchorPane rootPane = loader.load(); 
			NewAccountController controller = loader.getController();

			/* configure the controller
			 * call autofillTextFields at the end
			 */
			controller.setUser(user);
			controller.setWriter(sqlManager);
			controller.setRightPaneSetter(this);
			controller.autfillTextFields(sortCode, accountNumber);

			/* change the displayed pane on the right side */ 
			rightPane.getChildren().clear(); 
			rightPane.getChildren().add(rootPane);
		} catch (IOException io) { 
			io.printStackTrace();
			log.error(String.format("Problem loading %s", filename));
		}
	}
	@Override
	public void hideRightPane() {
		rightPane.getChildren().clear();
	}

	public void setUser(User user) {
		this.user = user;
		accountViewController.setUser(user);
		expenseViewController.setUser(user);
		expenseViewController.refresh(); 
		accountViewController.refresh();
	}
	public void setStage(Stage stage) {
		this.stage = stage;
	}

}

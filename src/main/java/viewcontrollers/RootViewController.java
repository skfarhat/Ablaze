package viewcontrollers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import logic.BarclaysCSVParser;
import models.Expense;
import models.User;

import org.apache.log4j.Logger;

import viewcontrollers.popups.NewUserController;
import db.SQLManager;
import exceptions.NullAccountException;

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

	@FXML private Label userLabel;  

	@FXML AnchorPane rightPane; 
	@FXML AnchorPane bottomPane; 

	/**
	 * 
	 */
	private SQLManager sqlManager = SQLManager.getSQL();

	@FXML ExpenseViewController expenseViewController; 
	@FXML AccountViewController accountViewController; 
	

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
		fc.setInitialDirectory(new File("/Users/Sami/Desktop/"));
		fc.setSelectedExtensionFilter(new ExtensionFilter("Comma-separated-file", ".csv")); 
		File chosenFile = fc.showOpenDialog(stage);

		try {
			/* save in database */ 
			List<Expense> expenses = BarclaysCSVParser.parse(sqlManager, chosenFile.getPath());
			sqlManager.saveExpenses(expenses);

			/* refresh table */  
//			refresh();
			expenseViewController.refresh();

		} catch (NullAccountException exc) {
			String mess = "Create a new account ?"; 
			Alert alert = new Alert(AlertType.CONFIRMATION, mess, ButtonType.YES, ButtonType.NO); 
			Optional<ButtonType> result = alert.showAndWait(); 

			if (result.get() == ButtonType.YES) {
				showAddAccountOnRightPane(exc.getSortCode(), exc.getAccountNumber().toString());
			}

			String message = exc.getMessage(); 
			log.error(message);
			//			exc.printStackTrace();
		} catch (IOException ioExc) { 
			ioExc.printStackTrace();
		}
	}

	@FXML private void addCard() {

	}

	@FXML private void addUser() {
		log.trace("addUser");

		final String filename = "../views/popups/NewUser.fxml"; 
		FXMLLoader loader = new FXMLLoader(); 
		loader.setLocation(Launcher.class.getResource(filename));
		AnchorPane rootPane;
		try {
			rootPane = loader.load();
			NewUserController controller =  loader.getController(); 
			Scene scene = new Scene(rootPane);
			Stage newUserStage = new Stage(); 

			/* when the user selects cancel*/ 
			controller.getCancelButtonProperty().addListener(
					(change, oldVal, newVal) -> {
						/* close the stage */
						newUserStage.close();
					});

			newUserStage.setScene(scene);
			newUserStage.showAndWait(); 

		} catch (IOException e) {
			log.error("Porblem creating user");
		}

	}

	
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
			controller.setAccountReader(sqlManager);
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
		userLabel.setText(user.getFullName());
		accountViewController.setUser(user);
		expenseViewController.setUser(user);
		expenseViewController.refresh(); 
		accountViewController.refresh();
	}
	public void setStage(Stage stage) {
		this.stage = stage;
	}

}

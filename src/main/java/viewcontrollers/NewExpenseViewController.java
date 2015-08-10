package viewcontrollers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Account;
import models.Expense;
import models.User;

import org.apache.log4j.Logger;

import viewcontrollers.popups.DuplicateExpenseViewController;
import db.ExpenseReader;
import db.ExpenseWriter;

/**
 * 
 * @author Sami
 *
 */
public class NewExpenseViewController implements Initializable {


	/** Log4j */ 
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(NewExpenseViewController.class);

	private ExpenseWriter expenseWriter; 
	private ExpenseReader expenseReader; 

	@FXML private TextField descriptionTextField; 
	@FXML private TextField amountTextField; 
	@FXML private TextField nameTextField; 


	@FXML private ChoiceBox<String> currencyChoiceBox; 
	@FXML private ChoiceBox<String> categoryChoiceBox; 
	@FXML private ChoiceBox<String> subCategoryChoiceBox; 
	@FXML private DatePicker datePicker; 
	@FXML private ChoiceBox<Account> accountChoiceBox; 


	private User user; 

	/**
	 * interface implemented by RootViewController, 
	 * allows setting the right pane
	 */
	private RightPaneSetter rightPaneSetter;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		datePicker.setValue(LocalDate.now());
	}

	@FXML private void confirmButtonPressed() {

		
		// TODO: confirm if is duplicate
		
		//		categoryChoiceBox.getSelectionModel().getSelectedItem()
		final String category = categoryChoiceBox.getSelectionModel().getSelectedItem();//.toString();  
		final String subCategory = subCategoryChoiceBox.getSelectionModel().getSelectedItem();//.toString();
		final String description = descriptionTextField.getText(); 
		final Account account = accountChoiceBox.getValue(); 
		final LocalDate date = datePicker.getValue(); 
		final String type = ""; // TODO: do type field
		
		// TODO: make sure the entered value is a number
		final Double amount = Double.parseDouble(amountTextField.getText());

		Expense expense = new Expense();
		expense.setAccount(account);
		expense.setCategory(category);
		expense.setSubCategory(subCategory);
		expense.setDateIncurred(date); 
		expense.setDescription(description);
		expense.setType(type);
		expense.setAmount(amount);

		if (expenseReader.expenseIsSuspectDuplicate(expense)) { 
			FXMLLoader loader = new FXMLLoader(); 
			loader.setLocation(ExpenseViewController.class.getResource("../views/popups/DuplicateExpense.fxml"));
			AnchorPane pane; 
			try { 
				pane = (AnchorPane) loader.load(); 
				DuplicateExpenseViewController controller = loader.getController();
				Stage stage = new Stage(); 
				Scene scene = new Scene(pane); 
				stage.setScene(scene);
				controller.setStage(stage);
				controller.setWriter(expenseWriter);
				List<Expense> duplicates = new ArrayList<>(); 
				duplicates.add(expense); 
				controller.setExpenses(duplicates);
				stage.showAndWait();

			} catch(IOException io) {
				logger.error(io.getMessage()); 
				io.printStackTrace();
			}
		} else { 
			expenseWriter.createExpense(expense);
		}
		rightPaneSetter.hideRightPane();
	}
	public void refresh(){

		//TODO: fill categories options 
		accountChoiceBox.setItems(FXCollections.observableArrayList(user.getAccounts()));
	}

	@FXML private void cancelButtonPressed() { 
		rightPaneSetter.hideRightPane();
	}
	public void setWriter(ExpenseWriter writer) {
		this.expenseWriter = writer;
	}

	public void setRightPaneSetter(RightPaneSetter rightPaneSetter) {
		this.rightPaneSetter = rightPaneSetter;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public void setExpenseReader(ExpenseReader expenseReader) {
		this.expenseReader = expenseReader;
	}
}

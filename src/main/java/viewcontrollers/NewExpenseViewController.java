package viewcontrollers;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import models.Account;
import models.Expense;
import models.User;

import org.apache.log4j.Logger;

import db.AccountReader;
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
	
	@FXML private TextField descriptionTextField; 
	@FXML private TextField amountTextField; 
	@FXML private TextField nameTextField; 

	
	@FXML private ChoiceBox<String> currencyChoiceBox; 
	@FXML private ChoiceBox<String> categoryChoiceBox; 
	@FXML private ChoiceBox<String> subCategoryChoiceBox; 
	@FXML private DatePicker datePicker; 
	@FXML private ChoiceBox<Account> accountChoiceBox; 
	
	private AccountReader accountReader; 
	
	
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

		final String category = categoryChoiceBox.getSelectionModel().getSelectedItem().toString();  
		final String subCategory = subCategoryChoiceBox.getSelectionModel().getSelectedItem().toString();
		final String description = descriptionTextField.getText(); 
		final Account account = accountChoiceBox.getValue(); 
		final LocalDate date = datePicker.getValue(); 
		final String type = ""; // TODO: do type field
		
		Expense expense = new Expense();
		expense.setAccount(account);
		expense.setCategory(category);
		expense.setSubCategory(subCategory);
		expense.setDateIncurred(date); 
		expense.setDescription(description);
		expense.setType(type);
	
		expenseWriter.saveExpense(expense);
	}
	public void refresh(){
		
		//TODO: fill categories options 
		
		if (accountReader != null) { 

			/* extract the names of the accounts from the list */ 
			List<Account> accounts = accountReader.getAllAcountsForUser(user);   
			
			/* set the options in the choicebox */ 
			accountChoiceBox.setItems(FXCollections.observableArrayList(accounts));
		}
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

	public void setAccountReader(AccountReader accountReader) {
		this.accountReader = accountReader;
	}
	
}

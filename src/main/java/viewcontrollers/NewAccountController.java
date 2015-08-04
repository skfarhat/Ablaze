package viewcontrollers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import models.Account;
import models.User;

import org.apache.log4j.Logger;

import db.AccountWriter;

/**
 * 
 * @author Sami
 *
 */
public class NewAccountController  {

	/** Log4j */ 
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(NewAccountController.class);
	
	private AccountWriter writer; 
	
	@FXML private TextField sortCodeTextField; 
	@FXML private TextField accountNumberTextField; 
	@FXML private TextField nameTextField; 
	@FXML private ChoiceBox<String> userChoiceBox; 
	
	private User user; 
	
	/**
	 * interface implemented by RootViewController, 
	 * allows setting the right pane
	 */
	private RightPaneSetter rightPaneSetter;
	
	/**
	 * set the text fields with the passed strings 
	 * @param sortCode
	 * @param accountNumber
	 */
	public void autfillTextFields(String sortCode, String accountNumber) { 
		sortCodeTextField.setText(sortCode);
		accountNumberTextField.setText(accountNumber);
		if (user != null) {
			userChoiceBox.setItems(FXCollections.observableArrayList(user.getFullName()));
			userChoiceBox.getSelectionModel().selectFirst();
		}
	}
	
	@FXML private void confirmButtonPressed()  {
		
		final String sortCode 		= sortCodeTextField.getText(); 
		final String accountNumber 	= accountNumberTextField.getText(); 
		final String name			= nameTextField.getText();
		
		Integer accountNum = Integer.parseInt(accountNumber); 

		/* create account */ 
		Account account = new Account();
		account.setName(name);
		account.setAccountNumber(accountNum);
		account.setSortCode(sortCode);
		account.setUser(user);
		
		writer.createAccount(account);
		rightPaneSetter.hideRightPane();
	}
	
	@FXML private void cancelButtonPressed() {
		/* hide the rightPane -- remove all children */ 
		rightPaneSetter.hideRightPane();
	}

	public void setWriter(AccountWriter writer) {
		this.writer = writer;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public void setRightPaneSetter(RightPaneSetter rightPaneSetter) {
		this.rightPaneSetter = rightPaneSetter;
	}
}

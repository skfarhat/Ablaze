package viewcontrollers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Account;
import models.User;

import org.apache.log4j.Logger;

import db.AccountReadWriter;

/**
 * 
 * @author Sami
 *
 */
public class AccountViewController implements Initializable {

	/** Log 4j */ 
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(AccountViewController.class);
	
	@FXML private TableView<Account> accountsTable;

	private AccountReadWriter accountsReadWriter; 
	
	@FXML private TableColumn<Account, String> accountNumberColumn; 
	@FXML private TableColumn<Account, String> sortCodeColumn; 
	@FXML private TableColumn<Account, String> nameColumn; 
	@FXML private TableColumn<Account, Double> amountColumn; 
	
	/* the below should be set by the parent controller */ 
	private ObservableList<Account> accountsList; 
	private User user; 
	private RightPaneSetter rightPaneSetter; 
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		accountNumberColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
		sortCodeColumn.setCellValueFactory(new PropertyValueFactory<>("sortCode"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
	
		refresh();
	}
	
	public void refresh() {
		if (user != null && accountsReadWriter != null) { 
			accountsList = FXCollections.observableArrayList(); 
			accountsList.addAll(accountsReadWriter.getAllAcountsForUser(user));

			accountsTable.setItems(accountsList);
		}
	}
	
	@FXML private void addAccountButtonPressed() {
		/* these values can be used to pre-fill some textfields
		 * we don't want to do that now so set to empty
		 */
		String sortCode = ""; 
		String accountNumber = ""; 
		rightPaneSetter.showAddAccountOnRightPane(sortCode, accountNumber);
	}

	public void setAccountsReadWriter(AccountReadWriter accountsReadWriter) {
		this.accountsReadWriter = accountsReadWriter;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public void setRightPaneSetter(RightPaneSetter rightPaneSetter) {
		this.rightPaneSetter = rightPaneSetter;
	}
}

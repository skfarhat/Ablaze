package viewcontrollers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import models.Account;
import models.Card;
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


	private AccountReadWriter accountsReadWriter; 
	private ObservableList<Account> accountsList;
	@FXML private TableView<Account> accountsTable;
	@FXML private TableColumn<Account, String> accountNumberColumn; 
	@FXML private TableColumn<Account, String> sortCodeColumn; 
	@FXML private TableColumn<Account, String> nameColumn; 
	@FXML private TableColumn<Account, Double> amountColumn; 


	private ObservableList<Card> cardsList;
	@FXML private TableView<Card> cardsTable;
	@FXML private TableColumn<Card, String> visaTypeColumn;
	@FXML private TableColumn<Card, String> cardNumberColumn;
	@FXML private TableColumn<Card, String> accountColumn;
	@FXML private TableColumn<Card, String> securityCodeColumn;
	@FXML private TableColumn<Card, String> expiryDateColumn;


	@FXML ToggleButton revealToggleButton; 

	/* the below should be set by the parent controller */ 
	private User user; 
	private RightPaneSetter rightPaneSetter; 


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		/* Account TableView */ 
		accountNumberColumn.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
		sortCodeColumn.setCellValueFactory(new PropertyValueFactory<>("sortCode"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));


		/* Card TableView */
		visaTypeColumn.setCellValueFactory(new PropertyValueFactory<>("visaType"));
		cardNumberColumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Card,String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(
							CellDataFeatures<Card, String> param) {
						final String cardNumber = param.getValue().getCardNumber();
						if (revealToggleButton.isPressed()) { 
							return new SimpleStringProperty(cardNumber);  
						}
						logger.debug("outside pressed");
						return new SimpleStringProperty(cardNumber.replaceAll("\\d", "*")); 
					}});

//		cardNumberColumn.setCellValueFactory(new PropertyValueFactory<>("cardNumber"));
		accountColumn.setCellValueFactory(new PropertyValueFactory<>("account"));
		securityCodeColumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Card,String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(
							CellDataFeatures<Card, String> param) {
						final String securityCode = param.getValue().getSecurityCode();

						if (revealToggleButton.isPressed()) {
							logger.debug("in pressed");
							return new SimpleStringProperty(securityCode);  
						}
						return new SimpleStringProperty(securityCode.replaceAll("\\d", "*")); 
					}});
//		securityCodeColumn.setCellValueFactory(new PropertyValueFactory<>("securityCode"));
		expiryDateColumn.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));


		refresh();
	}

	@FXML public void refresh() {

		if (user != null && accountsReadWriter != null) {

			/* accounts data source */ 
			accountsList = FXCollections.observableArrayList(); 
			accountsList.addAll(accountsReadWriter.getAllAcountsForUser(user));

			/* cards data source */ 
			cardsList = FXCollections.observableArrayList(); 
			accountsList.forEach(a -> cardsList.addAll(a.getCards())); 

			/* set table items */ 
			accountsTable.setItems(accountsList);
			cardsTable.setItems(cardsList);
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

	@FXML private void addCardButtonPressed() { 

		rightPaneSetter.showAddCardOnRightPane();
	}

	@FXML private void revealButtonPressed() { 
		refreshCardsTableView(); 
	}
	
	private void refreshCardsTableView() { 
		/* force the tableview to refresh */ 
		cardsList.set(0, cardsList.get(0));
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

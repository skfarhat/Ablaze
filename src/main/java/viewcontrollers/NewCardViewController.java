package viewcontrollers;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import models.Account;
import models.Card;
import models.User;

import org.apache.log4j.Logger;

import db.CardWriter;

/**
 * 
 * @author Sami
 *
 */
public class NewCardViewController implements Initializable {


	/** Log4j */ 
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(NewAccountController.class);
	
	private CardWriter writer; 
	
	@FXML private TextField cardNumberTextField; 
	@FXML private TextField cardTypeTextField; 
	@FXML private TextField securityCodeTextField; 
	@FXML private TextField nameTextField; 
	@FXML private ChoiceBox<Account> accountChoiceBox; 
	@FXML private DatePicker expiryDatePicker; 
	
	private User user; 
	
	/**
	 * interface implemented by RootViewController, 
	 * allows setting the right pane
	 */
	private RightPaneSetter rightPaneSetter;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	@FXML private void confirmButtonPressed()  {
		
		final String cardNumber 	= cardNumberTextField.getText(); 
		final String securityCode 	= securityCodeTextField.getText(); 
		final String type			= cardTypeTextField.getText(); 
		final String name			= nameTextField.getText(); 
		final Account account 		= accountChoiceBox.getValue(); 
		final LocalDate expiry 		= expiryDatePicker.getValue(); 
		
		Card card = new Card();
		card.setCardType(type);
		card.setName(name);
		card.setAccount(account);
		card.setCardNumber(cardNumber);
		card.setSecurityCode(securityCode);
		card.setExpiryDate(expiry);
		
		
		writer.createCard(card);

		rightPaneSetter.hideRightPane();
	}
	
	@FXML private void cancelButtonPressed() {
		/* hide the rightPane -- remove all children */ 
		rightPaneSetter.hideRightPane();
	}
	public void setRightPaneSetter(RightPaneSetter rightPaneSetter) {
		this.rightPaneSetter = rightPaneSetter;
	}
	public void setUser(User user) {
		this.user = user;
		
		List<Card> cards = new ArrayList<>(); 
		
		accountChoiceBox.setItems(FXCollections.observableArrayList(user.getAccounts())); 
	}

	public void setWriter(CardWriter writer) {
		this.writer = writer;
	}
}

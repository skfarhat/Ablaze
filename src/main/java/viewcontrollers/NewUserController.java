package viewcontrollers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.apache.log4j.Logger;

import db.UserWriter;

/**
 * 
 * @author Sami
 *
 */
public class NewUserController implements Initializable {

	
	/* Log4j */ 
	private final static Logger log = Logger.getLogger(NewUserController.class);
	
	private UserWriter userWriter; 
	private Stage stage; 
	
	@FXML private TextField 	firstNameTextField; 
	@FXML private TextField 	lastNameTextField;
	@FXML private PasswordField passwordTextField;
	@FXML private PasswordField confirmPassTextField;
	@FXML private Button 		confirmButton; 
	
	private Refreshable parentRefreshable; 
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ChangeListener<String> listener = new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				if (passwordTextField.getText().equals(confirmPassTextField.getText())) { 
					confirmButton.setDisable(false);
				}
			}
		}; 
		
		confirmPassTextField.textProperty().addListener(listener); 
		passwordTextField.textProperty().addListener(listener); 
		
	}
	
	@FXML private void confirmButtonPressed() {
		log.trace("confirmButtonPressed");
		if (stage != null && userWriter != null && parentRefreshable != null) { 
			String firstName 	= firstNameTextField.getText(); 
			String lastName 	= lastNameTextField.getText(); 
			String password 	= passwordTextField.getText(); 
			
			userWriter.createUser(firstName, lastName, password);
			stage.close();
			parentRefreshable.refresh();
		}
	}
	
	@FXML private void cancelButtonPressed() {
		log.trace("cancelButtonPressed");
		stage.close();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public void setUserWriter(UserWriter userWriter) {
		this.userWriter = userWriter;
	}

	public void setParentRefreshable(Refreshable parentRefreshable) {
		this.parentRefreshable = parentRefreshable;
	}

}

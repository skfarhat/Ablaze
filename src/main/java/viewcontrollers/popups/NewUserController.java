package viewcontrollers.popups;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import org.apache.log4j.Logger;

/**
 * 
 * @author Sami Farhat
 *
 */
public class NewUserController {
	
	/** Log4j */ 
	private final static Logger log = Logger.getLogger(NewUserController.class); 
	
	/** initially the property is set to false */ 
	private BooleanProperty cancelButtonProperty = new SimpleBooleanProperty(false);  

	/** initially the property is set to false */ 
	private BooleanProperty confirmButtonProperty = new SimpleBooleanProperty(false);  
	
	@FXML private TextField firstNameTextField; 
	@FXML private TextField lastNameTextField; 
	
	@FXML private void cancelButtonPressed() {
		log.trace("cancelButtonPressed");
		cancelButtonProperty.setValue(true);
	}

	@FXML private void confirmButtonPressed() {
		log.trace("confrimButtonPressed");
		confirmButtonProperty.setValue(true);
	}
	
	public ObservableBooleanValue getCancelButtonProperty() {
		return cancelButtonProperty;
	}
	
	public ObservableBooleanValue getConfirmButtonProperty() {
		return confirmButtonProperty;
	}
	
	public String getFirstName() { 
		return firstNameTextField.getText(); 
	}

	public String getLastName() { 
		return lastNameTextField.getText(); 
	}
	
}

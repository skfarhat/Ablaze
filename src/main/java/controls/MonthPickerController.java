package controls;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import org.apache.log4j.Logger;

import util.VoidFunction;

/**
 * this could well have been a custom control implemented with fx:root 
 * but SceneBuilder doesn't handle them well, and so the better alternative 
 * is to put the content of MonthPicker inside an AnchorView and use fx:include 
 * @author Sami
 *
 */
public class MonthPickerController implements Initializable {

	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(MonthPicker.class);

	private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yy");

	private ObjectProperty<LocalDate> date = new SimpleObjectProperty<LocalDate>(LocalDate.now());	
	
	/**
	 *  void method function interface
	 *  that can be set to execute everytime the month control is updated */ 
	private VoidFunction delegate; 

	@FXML private Label monthLabel; 
	@FXML private Button leftButton; 
	@FXML private Button rightButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		date.set(date.get().minusDays(date.get().getDayOfMonth()+1)); 
		update(); 
	}

	@FXML private void leftButtonPressed() {
		date.set(date.get().minusMonths(1));

		/* adjusts text on the month label */ 
		update(); 

		delegate.perform();
	}
	@FXML private void rightButtonPressed() { 
		date.set(date.get().plusMonths(1)); 
	
		/* adjusts text on the month label */ 
		update(); 
		
		delegate.perform();
	}

	public ObjectProperty<LocalDate> getDate() {
		return date;
	}
	
	private void update() {
		
		/* update month label */ 
		monthLabel.setText(formatter.format(date.getValue()));
		
		/* remove focus from the button and place it on label (shouldnt be able to do anything anw) */  
		monthLabel.requestFocus();
	}

	public void setDelegate(VoidFunction delegate) {
		this.delegate = delegate;
	}
}

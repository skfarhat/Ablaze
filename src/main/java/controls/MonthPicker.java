package controls;

import java.io.IOException;
import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import org.apache.log4j.Logger;

/**
 * 
 * @author Sami
 *
 */
public class MonthPicker extends AnchorPane {

	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(MonthPicker.class);

	private ObjectProperty<LocalDate> date = new SimpleObjectProperty<LocalDate>(LocalDate.now()); 
	
	@FXML private Label monthLabel; 
	@FXML private Button leftButton; 
	@FXML private Button rightButton;

	public MonthPicker() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MonthPickerView.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}	
	}
	
	@FXML private void leftButtonPressed() {
		date.set(date.get().minusMonths(1));
		monthLabel.setText(date.get().getMonth().toString());
	}
	@FXML private void rightButtonPressed() { 
		date.set(date.get().plusMonths(1)); 
		monthLabel.setText(date.get().getMonth().toString());
	}

}

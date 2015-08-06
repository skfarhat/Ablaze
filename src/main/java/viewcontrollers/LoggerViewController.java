package viewcontrollers;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import util.TextAreaAppender;

/**
 * 
 * @author Sami
 *
 */
public class LoggerViewController implements Initializable {
	/** Log 4j */ 
	private final static Logger logger = Logger.getLogger(LoggerViewController.class);

	@FXML private TextArea logTextArea;
	@FXML private ComboBox<String> priorityComboBox; 



	/* default debug */ 
	private static final Map<String, Level> PRIORITIES = new HashMap<>(); 
	private static final int DEFAULT_PRIORITY = 1;
	
	/**
	 * should have the elements in the same order as the HashMap below it 
	 */
	private static final String PRIORITIES_STR[]= { 
		"Trace",
		"Debug",
		"Info",
		"Warn",
		"Error"
	};
	static {
		PRIORITIES.put("Trace", Level.TRACE); 
		PRIORITIES.put("Debug", Level.DEBUG); 
		PRIORITIES.put("Info", 	Level.INFO); 
		PRIORITIES.put("Warn", 	Level.WARN); 
		PRIORITIES.put("Error", Level.ERROR);
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		TextAreaAppender.setTextArea(logTextArea);
		priorityComboBox.setItems(FXCollections.observableArrayList(PRIORITIES_STR));
		priorityComboBox.getSelectionModel().select(DEFAULT_PRIORITY);

		priorityComboBox.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {

				/* filter past message */ 
				/* change the level at the TextAreaAppender (handles all future messages) */ 
				Level level = PRIORITIES.get(newValue); 
				TextAreaAppender.setLevel(level);
			}
		});
		priorityComboBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				logger.info("in handle of priorityComboBox");
			}
		});
	} 

	@FXML private void clearButtonPressed() { 
		logTextArea.clear();
	}
}

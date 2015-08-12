package viewcontrollers.settings;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

import org.apache.log4j.Logger;
import javafx.fxml.FXML;

/**
 * 
 * @author Sami
 *
 */
public class SettingsViewController implements Initializable {
	/** Log 4j */ 
	private final static Logger logger = Logger.getLogger(SettingsViewController.class);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	@FXML public void backupButtonPressed() {}
	
}

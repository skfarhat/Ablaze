package viewcontrollers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.User;

import org.apache.log4j.Logger;

import util.PasswordManager;
import db.UserReader;
import db.UserWriter;

/**
 * 
 * @author Sami
 *
 */
public class LoginViewController implements Initializable, Refreshable {

	/** Log4j */ 
	private final static Logger logger = Logger.getLogger(LoginViewController.class);

	@FXML private ComboBox<String> comboBox; 
	@FXML private Button proceedButton; 
	@FXML private TextField passwordTextField;

	/** user selected from the choiceBox */ 
	private User selectedUser; 

	private UserReader userReader; 
	private UserWriter userWriter; 

	private Stage stage; 

	private ObservableList<User> usersList;
	private ObservableList<String> users;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		usersList 	= FXCollections.observableArrayList(); 
		users		= FXCollections.observableArrayList(); 

		/* checks the password as the user is typing to check if it matches */ 
		passwordTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {

				String hashed = PasswordManager.getHash(newValue);
				if (hashed.equals(selectedUser.getPassword())) { 
					logger.trace("success!");
					proceedButton.setDisable(false);
				}
			}
		});

		comboBox.getSelectionModel().selectedIndexProperty().addListener(
				new ChangeListener<Number>() {
					@Override
					public void changed(
							ObservableValue<? extends Number> observable,
							Number oldValue, Number newValue) {

						// if user has a password
						passwordTextField.setDisable(false);
						int index = newValue.intValue();
						selectedUser = usersList.get(index);
						logger.debug("selected user is " + selectedUser.getFullName());
					}
				});

		refresh();
	}

	@Override
	public void refresh() { 
		if (userReader != null) { 

			usersList.clear();
			users.clear();
			
			usersList.addAll(userReader.getAllUsers());

			/* create a list with all the names of the users */ 
			usersList.forEach(user -> users.add(user.getFullName()));

			comboBox.setItems(users);
			comboBox.getSelectionModel().select(0);
		}
	}

	@FXML private void addUser() { 
		final String filename = "../views/NewUser.fxml"; 

		FXMLLoader loader = new FXMLLoader(); 
		loader.setLocation(Launcher.class.getResource(filename));
		try {
			AnchorPane rootPane = loader.load();
			NewUserController controller = loader.getController(); 
			Stage stage = new Stage(); 
			controller.setStage(stage);
			controller.setUserWriter(userWriter);
			controller.setParentRefreshable(this);
			Scene scene = new Scene(rootPane); 
			stage.setScene(scene);
			stage.showAndWait(); 
			refresh(); 

		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		logger.debug("end of newUserFrame"); 
	}

	@FXML private void proceedButtonPressed() {
		
		/* update last-login time for the user */ 
		userWriter.updateLastLogin(selectedUser);
		/* load fxml */ 
		final String filename = "../views/RootView.fxml"; 
		FXMLLoader loader = new FXMLLoader(); 
		loader.setLocation(Launcher.class.getResource(filename));
		try {
			AnchorPane rootPane = loader.load(); 

			Stage newStage = new Stage(); 
			Scene scene = new Scene(rootPane);
			newStage.setScene(scene);

			RootViewController controller = loader.getController();
			controller.setUser(selectedUser);
			controller.setStage(newStage);
			newStage.show();
			stage.close();
		} 
		catch(IOException exc) { 
			logger.error(exc.getMessage());
			exc.printStackTrace();
		}

	}

	@FXML private void exitButtonPressed() { 
		stage.close();
	}

	@FXML public void change() {
		logger.trace("here in onchange()"); 
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setUserReader(UserReader userReader) {
		this.userReader = userReader;
	}
	public void setUserWriter(UserWriter userWriter) {
		this.userWriter = userWriter;
	}

}

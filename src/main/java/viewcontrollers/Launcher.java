package viewcontrollers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import logic.CategoriesManager;

import org.apache.log4j.BasicConfigurator;

import db.SQLManager;

/**
 * 
 * @author Sami
 *
 */
public class Launcher extends Application {

	static {
		BasicConfigurator.configure(); 
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		SQLManager sqlManager = SQLManager.getSQL(); 
		
		final String filename = "../views/LoginView.fxml"; 
		FXMLLoader loader = new FXMLLoader(); 
		loader.setLocation(Launcher.class.getResource(filename));
		AnchorPane rootPane = loader.load(); 
		
		LoginViewController controller = loader.getController(); 
		controller.setStage(primaryStage);
		controller.setUserReader(sqlManager);
		controller.setUserWriter(sqlManager);
		controller.refresh();
		Scene scene = new Scene(rootPane);
		
		primaryStage.setScene(scene);
		primaryStage.show(); 
	}

	
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		CategoriesManager.initialise(); 
		launch(args); 
	}
}

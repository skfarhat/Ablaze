package viewcontrollers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import logic.AblazeConfiguration;
import logic.CurrenciesManager;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import util.CryptManager;
import util.PasswordManager;
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
	private final static Logger logger = Logger.getLogger(Launcher.class);
	
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
		
//		String key = PasswordManager.getHash("123").substring(0, 16); 
//		
//		String data = "4658593297256001"; 
//		
//		try {
//			String enc = CryptManager.encrypt(data, key);
//			logger.debug(String.format("encoded: %s", enc));
//			String dec = CryptManager.decrypt(data, key); 
//			logger.debug(String.format("decoded: %s", dec)); 
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		
		/* loads configuration file */ 
		AblazeConfiguration.initialise();
		
		/* makes sure currencies are in the database */ 
		CurrenciesManager.initialise();
		
		launch(args); 
	}
}

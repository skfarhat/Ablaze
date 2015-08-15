
package viewcontrollers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import logic.AblazeConfiguration;
import logic.BarclaysCSVParser;
import logic.CategoriesManager;
import logic.CurrenciesManager;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

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
	/** Log4j */ 
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

		/* loads configuration file */ 
		AblazeConfiguration.initialise();
		
		/* makes sure currencies are in the database */ 
		CurrenciesManager.initialise();
		
//		CategoriesManager.categoriesLike("gre");
//		
//		logger.debug("result after this");
//		logger.debug("---------------------------------------------------------------------");
//		logger.debug(": " + BarclaysCSVParser.findCategory("a s d f g fBLA BLA BLA GREATER ANGLIA ON")); 
//		SQLManager.getSQL().getCategoriesLinkedTo("GREATER").forEach(c -> logger.debug(c.getName())); 
//		SQLManager.getSQL().getCategoriesLinkedTo("GREATER ANGLIA").forEach(c -> logger.debug(c.getName()));
//		SQLManager.getSQL().getCategoriesLinkedTo("GREATER ANGLIA").forEach(c -> logger.debug(c.getName()));
		
		launch(args); 
	}
}

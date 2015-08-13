package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Currency;

import org.apache.log4j.Logger;

import db.SQLManager;

/**
 * 
 * @author Sami
 *
 */
public class CurrenciesManager {

	private final static Logger logger = Logger.getLogger(CurrenciesManager.class);

	private static List<Currency> currencies; 

	/**
	 * parses file to link keywords to categories
	 * @throws IOException 
	 */
	public static void initialise() throws IOException {
		if (AblazeConfiguration.isInitialized()) {
			logger.debug("CurrenciesManager intialise()"); 
			List<Currency> transientCurrencies = AblazeConfiguration.getCurrencies();

			/* create currencies */ 
			SQLManager.getSQL().createCurrencies(transientCurrencies);
			
			/* fetch created currencies */ 
			List<Currency> currencies = SQLManager.getSQL().getAllCurrencies();
			
			/* save to class variable */ 
			CurrenciesManager.currencies = new ArrayList<Currency>(currencies); 
		}
	}
	public static List<Currency> getCurrencies() {
		return currencies;
	}

}

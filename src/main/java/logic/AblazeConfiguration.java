package logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.Currency;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author Sami
 *
 */
public class AblazeConfiguration {
	/** Log4j */ 
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(CategoriesManager.class);

	/* constants */
	private final static String KEY_CURRENCIES 	= "currencies"; 
	private final static String filename 		= "src/main/java/res/config.json"; 

	/* Singleton */ 
	private final static AblazeConfiguration configuration = new AblazeConfiguration(); 
	public static AblazeConfiguration getInstance() { 
		return configuration; 
	}
	/** state */ 
	private static boolean isInitialized = false; 

	private static HashMap<String, Object> configMap = new HashMap<>(); 

	/**
	 * parses file to link keywords to categories
	 * @throws IOException 
	 */
	public static void initialise() throws IOException {
		
		String json = readFile(filename); 
		JSONObject object = new JSONObject(json); 

		JSONArray array = object.getJSONArray(KEY_CURRENCIES);
		ArrayList<Currency> currencies = new ArrayList<>(); 

		for (int i = 0; i < array.length(); i++) {
			JSONObject o = (JSONObject) array.get(i); 
			String name 	= (String) o.get("name"); 
			String symbol 	= (String) o.get("symbol");
			String code 	= (String) o.get("code");
			Currency currency = new Currency(); 
			currency.setName(name);
			currency.setCode(code);
			currency.setSymbol(symbol);
			currencies.add(currency); 
		}

		configMap.put(KEY_CURRENCIES, currencies);
		
		isInitialized = true; 
	}
	private static String readFile(String path)  throws IOException  {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded); 
	}
	

	public static List<Currency> getCurrencies() {
		Object o = configMap.get(KEY_CURRENCIES);
		
		@SuppressWarnings("unchecked")
		ArrayList<Currency> curr = new ArrayList<>((List<Currency>) o); 

		return curr; 
	}
	
	public static boolean isInitialized() {
		return isInitialized;
	}
}

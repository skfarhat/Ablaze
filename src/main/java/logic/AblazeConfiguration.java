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
	private final static String filename 					= "src/main/java/res/config.json"; 
	private final static String KEY_CURRENCIES 				= "currencies"; 
	private final static String SYSTEM 						= "system";
	private final static String SYSTEM_INITIAL_DIR 			= "initial-directory";
	private final static String SYSTEM_LAST_OPENED_DIR		= "last-opened-directory";

	/* Singleton */ 
	private final static AblazeConfiguration configuration = new AblazeConfiguration(); 
	public static AblazeConfiguration getInstance() { 
		return configuration; 
	}
	/** state */ 
	private static boolean isInitialized = false; 

	private static JSONObject root;
	private static HashMap<String, Object> configMap = new HashMap<>(); 

	/**
	 * parses configuration file
	 * @throws IOException 
	 */
	public static void initialise() throws IOException {

		final String json = readFile(filename); 
		AblazeConfiguration.root = new JSONObject(json); 

		JSONObject system = root.getJSONObject(SYSTEM);
		
		/* initial-directory, used in case there is no 'last-opened directory' */
		String initialDir = system.get(SYSTEM_INITIAL_DIR).toString();
		logger.debug("reading initial as : " + initialDir);
		configMap.put(SYSTEM_INITIAL_DIR, initialDir);
		
		/* last opened directory */ 
		if (!system.isNull(SYSTEM_LAST_OPENED_DIR)) { 
			String lastOpenedDir = system.get(SYSTEM_LAST_OPENED_DIR). toString();
			configMap.put(SYSTEM_LAST_OPENED_DIR, lastOpenedDir);
		}

		/* handle currencies */ 
		JSONArray array = root.getJSONArray(KEY_CURRENCIES);
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

	/**
	 * reads a file and returns string of the read text
	 * @param path
	 * @return
	 * @throws IOException
	 */
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

	/**
	 * 
	 * @return the directory the user opened the last time. If no such directory exists, 
	 * then the 'initial-directory' from the config.json file is returned 
	 */
	public static String getRootDirectory() {
		Object lastOpen = configMap.get(SYSTEM_LAST_OPENED_DIR);
		if (lastOpen != null) {
			return lastOpen.toString();  
		} else { 
			String initial = configMap.get(SYSTEM_INITIAL_DIR).toString();
			return  initial; 
		}
	}

	public static void setLastOpened(String lastOpened) { 
		configMap.put(SYSTEM_LAST_OPENED_DIR, lastOpened);
		JSONObject system = (JSONObject) root.get(SYSTEM); 
		system.put(SYSTEM_LAST_OPENED_DIR, lastOpened);
		
		try { 
			rewrite();
		} catch (IOException io) {
			logger.error("failed to update " + filename); 
			io.printStackTrace();
		}
	}
	
	private static void rewrite() throws IOException { 
		Files.write(Paths.get(filename), root.toString().getBytes()); 
	}
}

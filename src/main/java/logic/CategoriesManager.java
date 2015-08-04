package logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Singleton
 * @author Sami
 *
 */
public class CategoriesManager {

	/** Log4j */ 
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(CategoriesManager.class);

	private final static String filename = "src/main/java/res/categories.json"; 

	private static HashMap<String, String> categories = new HashMap<>(); 

	/* Singleton */ 
	private final static CategoriesManager categoriesManager = new CategoriesManager(); 
	public static CategoriesManager getInstance() { 
		return categoriesManager; 
	}


	/**
	 * parses file to link keywords to categories
	 * @throws IOException 
	 */
	public static void initialise() throws IOException {
		
		String json = readFile(filename); 
		JSONObject object = new JSONObject(json); 
		
		for (String key : object.keySet()) {
			JSONArray array = object.getJSONArray(key);
			for (int i = 0; i < array.length(); i++) { 
				String s = (String) array.get(i);
				
				/*
				 * puts (Keyword, 	Category)
				 * puts (tesco, 	Groceries)   
				 * */
				categories.put(s.toLowerCase(), key); 
			}
		}
		
	}
	
	public static String getCategory(String key) { 
		return categories.get(key.toLowerCase()); 
	}
	private static String readFile(String path)  throws IOException  {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded); 
	}
}

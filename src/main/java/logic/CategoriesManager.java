package logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import models.Category;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import util.algs4.TST;
import db.SQLManager;

/**
 * Singleton
 * @author Sami
 *
 */
public class CategoriesManager {

	/** Log4j */ 
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(CategoriesManager.class);

	private static HashMap<String, String> categories = new HashMap<>(); 

	/* Singleton */ 
	private final static CategoriesManager categoriesManager = new CategoriesManager(); 
	public static CategoriesManager getInstance() { 
		return categoriesManager; 
	}
	
	private static TST<String> tst = new TST<>(); 

	

	
	/**
	 * 
	 * @param root JSONObject containing the 'categories' JSONObject
	 */
	public static void handleCategories(JSONObject root) { 
		
		/* categories */
		if (root!= null) {
		
			for (String key : root.keySet()) { 
				
				Category c = new Category(key);
				
				JSONArray links = (JSONArray) root.get(key);
				
				for (int i = 0; i < links.length(); i++) {
					String link = (String) links.get(i); 
					if (categories.containsKey(link)) { 
						logger.warn("one of the category links may exist twice - this 'might' cause problems/confusions");
					} else { 
						categories.put(link, key);
						tst.put(link, key);
					}
					
					/* add link to the category object */ 
					c.addLink(link);
				}

				logger.debug("about to create category: " + c);
				/* create Category in the database */
				SQLManager.getSQL().createCategory(c);
			}
		}
	}
	
	
	/**
	 * 
	 * @param link
	 * @return null if not linked category was found 
	 */
	public static Category categoriesLike(String link) {
		List<Category> results = SQLManager.getSQL().getCategoriesLinkedTo(link);
		if ( !results.isEmpty() )
			return results.get(0); 
		else 
			return null; 
	}
//	public static String categoriesLike(String key) {
//		List<String> list = new ArrayList<>();
//		tst.keysWithPrefix(key).forEach( s -> {list.add(s); logger.debug(s); });
//		if (list.size() > 0)
//			return list.get(0);
//		else return null; 
////		return tst.keysWithPrefix(key).iterator().next(); 
//	}
//	
	public static String getCategory(String key) { 
		return categories.get(key.toLowerCase()); 
	}
	private static String readFile(String path)  throws IOException  {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded); 
	}
}

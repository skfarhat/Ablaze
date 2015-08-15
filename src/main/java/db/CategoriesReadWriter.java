package db;

import java.util.List;

import models.Category;

/**
 * 
 * @author Sami
 *
 */
public interface CategoriesReadWriter {
	
	public List<Category> getAllCategories(); 
	
	public boolean categoryExists(Category category); 
	
	public void createCategory(Category category);
	
	public void createCategories(List<Category> list); 
}

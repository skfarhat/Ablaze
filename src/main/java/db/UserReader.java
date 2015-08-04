/**
 * 
 */
package db;

import java.util.List;

import models.User;

/**
 * @author Sami
 *
 */
public interface UserReader {
	
	public List<User> getAllUsers(); 

}

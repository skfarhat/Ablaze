package db;

import models.User;

public interface UserWriter {

	public User createUser(String firstName, String lastName, String password); 
	
	public void updateLastLogin(User user); 
	public void deleteUser(User user); 
}

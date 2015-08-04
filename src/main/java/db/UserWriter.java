package db;

import models.User;

public interface UserWriter {

	public User createUser(String firstName, String lastName, String password); 
	
	public void deleteUser(User user); 
}

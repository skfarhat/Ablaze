package db;

import java.util.List;

import models.Account;
import models.User;

/**
 * 
 * @author Sami
 *
 */
public interface AccountReader {
	
	/**
	 * 
	 * @param user
	 * @return all accounts pertaining to the user
	 */
	public List<Account> getAllAcountsForUser(User user); 
	
	/**
	 * 
	 * @param sortCode
	 * @param accountNumber
	 * @return null if no matching account is found 
	 */
	public Account getAccount(String sortCode, int accountNumber); 
}

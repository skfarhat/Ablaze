package db;

import java.time.Month;
import java.util.List;

import models.Transaction;
import models.User;

/**
 * 
 * @author Sami
 *
 */
public interface ExpenseReader {

	public List<Transaction> getAllTransactions(); 
	public List<Transaction> getAllTransactionsForUser(User user);
	public List<Transaction> getExpensesForMonth(Month month, int year, User user);

	public boolean expenseIsSuspectDuplicate(Transaction expense); 
}

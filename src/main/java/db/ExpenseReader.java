package db;

import java.util.List;

import models.Expense;
import models.User;

/**
 * 
 * @author Sami
 *
 */
public interface ExpenseReader {

	public List<Expense> getAllExpenses(); 
	public List<Expense> getAllExpensesForUser(User user); 
}

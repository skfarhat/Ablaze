package db;

import java.time.Month;
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
	public List<Expense> getExpensesForMonth(Month month, int year, User user);

	public boolean expenseIsSuspectDuplicate(Expense expense); 
}

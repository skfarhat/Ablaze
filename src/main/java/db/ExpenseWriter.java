package db;

import java.util.List;

import models.Expense;

public interface ExpenseWriter {

	
	public void saveExpenses(List<Expense> expenses); 
	
	/**
	 * this method expects an expense object that will be used to create 
	 * an SQL entry
	 * @param expense
	 */
	public void saveExpense(Expense expense); 
}
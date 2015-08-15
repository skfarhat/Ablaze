package db;

import java.util.List;

import models.Transaction;

public interface ExpenseWriter {

	
	public void createTransactions(List<Transaction> expenses); 
	
	/**
	 * this method expects an expense object that will be used to create 
	 * an SQL entry
	 * @param expense
	 */
	public void createTransaction(Transaction expense); 
}

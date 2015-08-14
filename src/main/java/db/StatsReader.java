package db;

import java.util.List;

public interface StatsReader {
	
	/**
	 * 
	 * @return a list of Object[] where the first index is String of date format
	 * (%y-%m) and the second index is the net expense for that month
	 */
	public List<Object[]> netExpensesByMonth(); 
	
}

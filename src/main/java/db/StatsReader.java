package db;

import java.time.LocalDate;
import java.util.List;

public interface StatsReader {
	
	/**
	 * 
	 * @return a list of Object[] where the first index is String of date format
	 * (%y-%m) and the second index is the net expense for that month
	 */
	public List<Object[]> netExpensesByMonth(); 
	
	/**
	 * splits expenses into categories organized by month
	 * @return a list of Object[] where 
	 * [0] : date formatted as (%y-%m)
	 * [1] : Category 
	 * [2] : Count, number of expense entries in that category
	 * [3] : amount spent during that month for that category
	 */
	public List<Object[]> categoriesData();
	/**
	 * splits expenses into categories organized by month
	 * @param date
	 * @return a list of Object[] where 
	 * [0] : date formatted as (%y-%m)
	 * [1] : Category 
	 * [2] : Count, number of expense entries in that category
	 * [3] : amount spent during that month for that category
	 */
	public List<Object[]> categoriesDataForDates(LocalDate date);  
}

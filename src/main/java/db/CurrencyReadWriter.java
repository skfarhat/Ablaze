package db;

import java.util.List;

import models.Currency;

/**
 * 
 * @author Sami
 *
 */
public interface CurrencyReadWriter {
	
	public void createCurrencies(List<Currency> list);
	
	public List<Currency> getAllCurrencies(); 
	
}

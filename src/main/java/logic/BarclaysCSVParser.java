package logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.util.converter.LocalDateStringConverter;
import models.Account;
import models.Expense;

import org.apache.log4j.Logger;

import db.AccountReader;
import exceptions.NullAccountException;

/**
 * 
 * @author Sami
 *
 */
public class BarclaysCSVParser {

	/** Log4j */ 
	private final static Logger logger = Logger.getLogger(BarclaysCSVParser.class);
	
	/** used to extract date from memos in expenses */ 
	private final static String dateRegex = "ON (\\d\\d \\w\\w\\w)"; 
	private static Pattern datePattern = Pattern.compile(dateRegex);
	
	/** used to convert dates to strings and vice versa*/ 
	private final static DateTimeFormatter formatter 		= DateTimeFormatter.ofPattern("dd MMM yyyy");
	
	/**
	 * parses the .csv file and returns a list of expenses <br> 
	 * this method makes no database interaction, <br>
	 * it is the responsibility of the caller to save to the database
	 *   
	 * @param filename
	 * @param acctReader needed to read accounts from the database and associate expenses with their accounts 
	 * @return null if an exception occurs while reading 
	 * @throws IOException 
	 */
	public static List<Expense> parse(AccountReader acctReader, String filename) throws IOException {

		/* some file reading stuff */ 
		FileInputStream stream = new FileInputStream(new File(filename));
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		/* some utils used in the loop */ 
		LocalDateStringConverter converter 	= new LocalDateStringConverter();
		

		/* create list of expenses, 20 is an arbitrary value */ 
		List<Expense> expenses = new ArrayList<Expense>(20); 

		/*
		 * to avoid fetching the corresponding account from the database for every expense 
		 * e.g. getAccount(sortCode, accountNumber);  
		 * a reference to the last account used will be cached and checked before performing a db fetch  
		 */
		Account cachedAccount = null; 

		/* ignore the first line because it contains the titles of the columns */ 
		String line = reader.readLine();
		
		/* read the rest of the file */ 
		line = reader.readLine(); 
		while (line != null) { 
			String parts[] = line.split(","); 
			logger.debug(parts);
			String date = parts[1];
			
			/* account sort-code and number
			 *  20-17-19 10090476  */ 
			String accountDetails[] = parts[2].split(" ");
			String sortCode 		= accountDetails[0]; 
			Integer accountNumber	= Integer.parseInt(accountDetails[1]); 

			Double amount 			= Double.parseDouble(parts[3]); 
			String type 			= parts[4]; // payment, transfer, direct debit..
			String memo 			= parts[5];	// memo  

			/// TODO; can we use formatter instead ? 
			LocalDate dateIncurred = converter.fromString(date);
			
			/* extract date from memo -- more accurate */ 
			LocalDate temp = extractDateFromMemo(memo, dateIncurred.getYear()); 
			if (temp != null) 
				dateIncurred = temp; 
			
			
			/* Account
			 * check the cached account
			 * if matches, then no need to fetch from db  
			 * */
			if (cachedAccount == null
					|| cachedAccount.getAccountNumber() != accountNumber 
					|| !cachedAccount.getSortCode().equals(sortCode))  {
				cachedAccount = acctReader.getAccount(sortCode, accountNumber);
				
				/* if no such account could be found from the database then throw an exception */ 
				if (cachedAccount == null) { 
					String message = String.format("%s,%d; account does not exist.", sortCode, accountNumber);
					/* pass the sort-code and account-number in the exception to allow identification of the account 
					 * that needs creation
					 */
					throw new NullAccountException(message, sortCode, accountNumber); 
				}
				
			}
			
			/* Create Expense */ 
			Expense expense = new Expense();
			expense.setAccount(cachedAccount);
			expense.setValue(amount);
			expense.setDescription(memo);
			expense.setType(type);
			expense.setDateIncurred(dateIncurred);
			
			// TODO: add smart predictor for categories, subcategories

			/* add each expense to the list */ 
			expenses.add(expense); 
			line = reader.readLine(); 
		}
		return expenses; 
	}
	
	/**
	 * extracts the date from a memo
	 * e.g. 'xxxxxxxx ON 01 AUG' returns LocalDate with 
	 * 01/08/year
	 * @param memo contains the date that needs to be extracted
	 * @param year needed as parameter as memos do not specify it 
	 * @return LocalDate corresponding to the text in the memo, null if no date was found
	 */
	private static LocalDate extractDateFromMemo(String memo, int year) { 
		
		Matcher m = datePattern.matcher(memo);
		if (m.find()) {
			StringBuilder sb = new StringBuilder(m.group(1)); 
			
			/* add year to the date extracted from the memo (usually of format '14 AUG') */ 
			sb.append(String.format(" %d", year)); 
			
			/* lower case the whole thing
			 * 01 AUG 2015 --> 01 aug 2015
			 * */
			sb  = new StringBuilder(sb.toString().toLowerCase()); 

			/* change the third char (first char of month) to upper case
			 * 01 aug 2015 --> 01 Aug 2015 
			 */ 
			sb.setCharAt(3, Character.toUpperCase(sb.charAt(3)));
			
			/* get date object from the string */ 
			return LocalDate.from(formatter.parse(sb.toString()));
		}
		
		/* if no regex match is found */ 
		return null; 
	}
}

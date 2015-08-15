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
import models.Category;
import models.Transaction;

import org.apache.log4j.Logger;

import com.sun.org.apache.bcel.internal.generic.LSTORE;

import util.algs4.TST;
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
	public static List<Transaction> parse(AccountReader acctReader, String filename) throws IOException {

		/* some file reading stuff */ 
		FileInputStream stream = new FileInputStream(new File(filename));
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		/* some utils used in the loop */ 
		LocalDateStringConverter converter 	= new LocalDateStringConverter();


		/* create list of expenses, 20 is an arbitrary value */ 
		List<Transaction> expenses = new ArrayList<Transaction>(20); 

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
			String type 			= parts[4]; 					// payment, transfer, direct debit..
			String memo 			= processMemo(parts[5]);		// memo  

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
			Transaction expense = new Transaction();
			expense.setAccount(cachedAccount);
			expense.setAmount(amount);
			expense.setDescription(memo);
			expense.setType(type);
			expense.setDateIncurred(dateIncurred);

			Category category = findCategory(memo); 
			if (category != null) 
				expense.setCategory(category); 
			
			//			CategoriesManager.getCategory(categor)
			// TODO: add smart predictor for categories, subcategories

			/* add each expense to the list */ 
			expenses.add(expense); 
			line = reader.readLine(); 
		}
		return expenses; 
	}

	/**
	 * longest prefix matching in finding category
	 * @param memo
	 * @return
	 */
	public static Category findCategory(String memo) {
		/* replaces multiple spaces by one and removes leading and trailing spaces*/ 
		String details[] = memo.trim().replaceAll("( )+", " " ).split(" ");
		StringBuilder sb = new StringBuilder(); //= new StringBuilder(memo); 

		/** 
		 * any matched strings with less than the below number of characters will be 
		 * disregarded. This is to prevent matches with single letters to occur. 
		 */
		final int MIN_MATCHING_CHAR_COUNT = 3; 
		int j = 0;
		int i = 0; 
		int n = details.length;
		
		/* a reference to the last found category is stored here, to allow for backup */ 
		Category lastFound = null; 
		while ( i < n) {
			
			/* if no previous was found, but the current string is less than the allowed length, 
			 * then skip and ignore
			 */
			if (lastFound == null && details[j].length() < MIN_MATCHING_CHAR_COUNT){ 
				i++; j++;
				continue; 
			}
			
			/* build string */  
			if (sb.length() == 0)
				sb = new StringBuilder(details[j]);
			else 
				sb.append(" " + details[j]);
			
			/* search for alike matches*/ 
			Category category = CategoriesManager.categoriesLike(sb.toString());

			/* no match */ 
			if (category == null) {

				/* single string doesn't match, check next*/ 
				if ( lastFound == null ) {
					i++;j++;
					sb = new StringBuilder(); 
					continue; 	
				}

				/* composite string doesn't match, assert the previous one */ 
				else {
					return lastFound; 
				}
			}
			/* found */ 
			else { 
				if ( j < n - 1)
					j++;
				lastFound = category; 
			}
		}
		return null; 
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

	private static String processMemo(String memo) { 
		Matcher m = datePattern.matcher(memo); 
		if (m.find()) { 
			return m.replaceAll("");
		}
		return memo; 
	}
}

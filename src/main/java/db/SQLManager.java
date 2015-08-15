package db;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;

import models.Account;
import models.Card;
import models.Category;
import models.Currency;
import models.Transaction;
import models.User;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.jasypt.digest.StandardStringDigester;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.hibernate4.encryptor.HibernatePBEEncryptorRegistry;

import util.PasswordManager;


/**
 * Class responsible for reads and writes to the SQL database
 * @author Sami
 */
public class SQLManager implements UserReadWriter, AccountReadWriter, 
ExpenseReadWriter, CardReadWriter, CurrencyReadWriter, StatsReader, CategoriesReadWriter {

	/** Log4j */ 
//	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(SQLManager.class);

	/* class made a singleton */ 
	private static final SQLManager sqlManager = new SQLManager(); 
	public static SQLManager getSQL() { return sqlManager; }

	private static final SessionFactory sessionFactory = buildSessionFactory();

	private Session getCurrentSession() { 
		return sessionFactory.getCurrentSession(); 
	}
	/**
	 * used to initialise Hibernate 
	 * @return
	 */
	private static SessionFactory buildSessionFactory() {
		try {
			Configuration config = new Configuration().configure(); 
			ServiceRegistry serviceRegistry =
					new StandardServiceRegistryBuilder()
			.applySettings(config.getProperties()).build();

			HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();
			
			StandardPBEStringEncryptor myEncryptor = new StandardPBEStringEncryptor();
			// FIXME : this is insecure and needs to be fixed
			myEncryptor.setPassword("123");
			registry.registerPBEStringEncryptor("myHibernateStringEncryptor", myEncryptor);
			
			
			return config.buildSessionFactory(serviceRegistry); 
		}
		catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	// ================================================================================================
	// ============
	// |   USER   |
	// ============

	public List<User> getAllUsers() {
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction();

		Query query = session.createQuery("from User u order by u.lastLogin DESC");
		List<User> userList = query.list();

		/* commit */ 
		session.getTransaction().commit();

		return userList;
	}

	/**
	 * 
	 * @param firstName
	 * @param lastName
	 * @param password the hash will be saved in the database
	 * @return
	 */
	public User createUser(String firstName, String lastName, String password) {
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction(); 

		/* create user */ 
		User user = new User();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPassword(password);
//		user.setPassword(PasswordManager.getHash(password));
		user.setLastLogin(LocalDateTime.now());

		/* persist */ 
		session.save(user); 

		/* commit */ 
		session.getTransaction().commit();

		return user; 
	}

	public void deleteUser(User user) {

		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction(); 

		/* delete */ 
		session.delete(user);

		/* commit */ 
		session.getTransaction().commit(); 
	}

	@Override
	public void updateLastLogin(User user) { 
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction(); 

		user.setLastLogin(LocalDateTime.now()); 
		session.update(user);

		session.getTransaction().commit();
	}

	// ================================================================================================
	// ============
	// | ACCOUNTS |
	// ============
	@Override
	public List<Account> getAllAcountsForUser(User user) {
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction();

		Query query = session.createQuery("from Account where user.id=:user");
		query.setParameter("user", user.getId()); // maybe put the id here ? 
		List<Account> accountList = query.list();

		/* commit */ 
		session.getTransaction().commit();

		return accountList;
	}

	public void createAccount(Account account) { 
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction(); 

		/* persist */ 
		session.save(account); 

		/* commit */ 
		session.getTransaction().commit();

	}

	/**
	 * 
	 * @param sortCode
	 * @param accountNumber
	 * @return null if no matching account is found 
	 */
	@Override
	public Account getAccount(String sortCode, int accountNumber) { 
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction();

		Query query = session.createQuery("from Account where accountNumber=:nb and sortCode=:sc");
		query.setParameter("nb", accountNumber); 
		query.setParameter("sc", sortCode); 

		List<Account> accountList = query.list();

		/* commit */ 
		session.getTransaction().commit();

		/* no account with */ 
		if (accountList.size() == 0) {
			return null;
		}

		/* return the first found account */ 
		return accountList.get(0); 
	}

	// ================================================================================================
	// ===============
	// | TRANSACTIONS |
	// ===============

	@Override
	public List<Transaction> getAllTransactions() { 
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction();

		Query query = session.createQuery("from Transaction");
		List<Transaction> expenses = query.list();

		/* commit */ 
		session.getTransaction().commit();

		return expenses; 
	}

	@Override
	public List<Transaction> getAllTransactionsForUser(User user) { 
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction();

		Query query = session.createQuery("from Transaction where account.user.id = :user");
		query.setParameter("user", user.getId());

		List<Transaction> expenses = query.list();

		/* commit */ 
		session.getTransaction().commit();

		return expenses; 
	}

	@Override
	public void createTransactions(List<Transaction> transactions) { 
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction(); 

		for (Transaction e : transactions) {
			session.save(e);
		}
		/* commit */ 
		session.getTransaction().commit();

	}
	/**
	 * this method expects a Transaction object that will be used to create 
	 * an SQL entry
	 * @param t
	 */
	@Override
	public void createTransaction(Transaction t) { 
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction(); 

		/* save */ 
		session.save(t);

		/* commit */ 
		session.getTransaction().commit();
	}

	@Override
	public List<Transaction> getExpensesForMonth(Month month, int year, User user) {
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction();

		Query query = session.createQuery("from Transaction where account.user.id = :user "
				+ "and dateIncurred between :startDate and :endDate order by dateIncurred DESC");

		LocalDate start 	= LocalDate.of(year, month.getValue(), 1);
		LocalDate end 		= start.plusDays(start.lengthOfMonth() - 1); 

		logger.debug("startDate: " + start.toString() + " endDate: " + end.toString());
		query.setParameter("user", user.getId());
		query.setParameter("startDate", start); 
		query.setParameter("endDate", end); 

		List<Transaction> expenses = query.list(); 

		/* commit */ 
		session.getTransaction().commit();

		return expenses;
	}

	public boolean expenseIsSuspectDuplicate(Transaction expense) { 
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction();

		Query query = session.createQuery("from Transaction where "
				+ "account.user.id = :user "
				+ "and dateIncurred = :dateIncurred "
				+ "and amount = :amount ");
		query.setParameter("user", expense.getAccount().getUser().getId());
		query.setParameter("dateIncurred", expense.getDateIncurred()); 
		query.setParameter("amount", expense.getAmount()); 

		List<Transaction> expenses = query.list();

		/* commit */ 
		session.getTransaction().commit();
		return expenses.size() > 0; 
	}

	// ================================================================================================
	// ============
	// | CARD     |
	// ============

	@Override
	public void createCard(Card card) {
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction(); 

		/* persist */ 
		session.save(card); 

		/* commit */ 
		session.getTransaction().commit();
	}

	// ==============
	// | CURRENCIES  |
	// ==============

	/**
	 * for each item in the list, this method checks to see if it exists in the database. 
	 * Only upon its absence is a new item created in the database
	 */
	@Override
	public void createCurrencies(List<Currency> list) {
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction(); 

		for (Currency c : list) {
			Query query = session.createQuery("from Currency where code=:code"); 
			query.setParameter("code", c.getCode()); 
			if (query.list().size() > 0)
				continue; 
			else 
				session.save(c); 
		}
		session.getTransaction().commit(); 
	}

	@Override
	public List<Currency> getAllCurrencies() {
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction();

		Query query = session.createQuery("from Currency ");

		List<Currency> currencyList = query.list();

		/* commit */ 
		session.getTransaction().commit();

		return currencyList;
	}



	@Override
	public List<Object[]> netExpensesByMonth() {

		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction();

		final String sql = "SELECT DATE_FORMAT(dateIncurred,'%Y-%m') as date, ROUND(SUM(amount),2) "
				+ "FROM Transaction "
				+ "WHERE amount< 0 "
				+ "GROUP BY Year(dateIncurred), Month(dateIncurred) "
				+ "ORDER BY dateIncurred DESC" ;

		Query query = session.createSQLQuery(sql); 
		List<Object[]> results = query.list();

		session.getTransaction().commit();
		return results;  
	}

	@Override
	public List<Object[]> categoriesData() {
		logger.trace("categoriesData");

		/* put all categories in an map <id, category>  */ 
		HashMap<Integer, Category> map = new HashMap<>();
		List<Category> categories = getAllCategories(); 
		categories.forEach(c -> {
			map.put(c.getId(), c); 
		});

		final String sql = "SELECT DATE_FORMAT(dateIncurred,'%Y-%m') as date, "
				+ "category as cat, Count(*) as count, ROUND(SUM(amount),2) as sum "
				+ "FROM Transaction "
				+ "WHERE amount < 0 and dateIncurred "
				+ "GROUP BY Year(dateIncurred) , Month(dateIncurred), category "
				+ "ORDER BY dateIncurred DESC";

		/* begin transaction */ 
		Session session = getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(sql);// 

		/* replace Category_id with the actual category object by fetching the objects from db */ 
		List<Object[]> results = query.list();
		results.forEach(o -> {
			if (o[1] != null) { 
				/* get category id */ 
				Integer cat_id = Integer.parseInt(o[1].toString());

				/* put category object in map */ 
				o[1] = map.get(cat_id);	
			}
		});

		session.getTransaction().commit();
		return results;
	}
	
	@Override
	public List<Object[]> categoriesDataForDates(LocalDate date) {

		/* put all categories in an map <id, category>  */ 
		HashMap<Integer, Category> map = new HashMap<>();
		List<Category> categories = getAllCategories(); 
		categories.forEach(c -> {
			map.put(c.getId(), c); 
		});

		final String sql = "SELECT DATE_FORMAT(dateIncurred,'%Y-%m') as date, "
				+ "category as cat, Count(*) as count, ROUND(SUM(amount),2) as sum "
				+ "FROM Transaction "
				+ "WHERE amount < 0 and (dateIncurred between ? and ?)"
				+ "GROUP BY Year(dateIncurred) , Month(dateIncurred), category "
				+ "ORDER BY dateIncurred DESC";

		/* begin transaction */ 
		Session session = getCurrentSession();
		session.beginTransaction();
		SQLQuery query = session.createSQLQuery(sql);

		// TODO: simplify
		LocalDate from 		= date; 
		LocalDate to		= from.plusDays(from.lengthOfMonth() - 1);
		Timestamp from1 = Timestamp.valueOf(from.atStartOfDay());
		Timestamp to1	= Timestamp.valueOf(to.atStartOfDay());
		
		query.setTimestamp(0, from1);
		query.setTimestamp(1, to1);
		
		/* replace Category_id with the actual category object by fetching the objects from db */ 
		List<Object[]> results = query.list();
		results.forEach(o -> {
			if (o[1] != null) { 
				/* get category id */ 
				Integer cat_id = Integer.parseInt(o[1].toString());

				/* put category object in map */ 
				o[1] = map.get(cat_id);	
			}
		});
		
		session.getTransaction().commit();
		return results;
	}
	

	public Category getCategory(Integer id) { 
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction();

		Query query = session.createQuery("from Category where id=:id");
		query.setParameter("id", id);

		List<Category> categoryList = query.list();
		/* commit */ 
		session.getTransaction().commit();

		if (categoryList.size() > 0) 
			return categoryList.get(0); 

		return null; 

	}

	@Override
	public boolean categoryExists(Category category) {
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction();

		Query query = session.createQuery("from Category where name=:name");
		query.setParameter("name", category.getName());

		List<Currency> currencyList = query.list();

		/* commit */ 
		session.getTransaction().commit();

		return currencyList.size() > 0; 
	}
	@Override
	public void createCategory(Category category) {

		/* if it already exists skip */ 
		if (categoryExists(category))
			return; 

		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction();

		session.save(category);

		/* commit */ 
		session.getTransaction().commit();

	}
	public void createCategories(List<Category> list) { 
		/* begin transaction */ 
		Session session = getCurrentSession(); 

		for (Category c : list) {
			if (categoryExists(c))
				continue; 

			session.beginTransaction(); 
			session.save(c); 
			session.getTransaction().commit(); 
		}
	}
	@Override
	public List<Category> getAllCategories() {
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction();

		Query query = session.createQuery("from Category ");

		List<Category> categoryList = query.list();

		/* commit */ 
		session.getTransaction().commit();

		return categoryList;
	}


	/**
	 * returns all categories that are/or could be linked to the 
	 * link parameter
	 * @param link
	 * @return
	 */
	public List<Category> getCategoriesLinkedTo(String link) { 
		logger.trace("getCategoriesLinkedTo");
		//		String sql = "from Link where name Like '%:link%'";
		String sql = "SELECT * FROM Category "
				+ "WHERE id in (SELECT category FROM Link WHERE name LIKE ?)";
		Session session = getCurrentSession(); 
		session.beginTransaction();

		SQLQuery query = session.createSQLQuery(sql)
				.addEntity(Category.class);

		String part = String.format("%%%s%%", link); 
		query.setParameter(0, part);

		List<Category> categories = query.list();

		/* commit */ 
		session.getTransaction().commit();

		return categories;  
	}

}

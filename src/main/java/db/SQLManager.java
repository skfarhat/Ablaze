package db;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import models.Account;
import models.Card;
import models.Expense;
import models.User;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;

import util.PasswordManager;


/**
 * Class responsible for reads and writes to the SQL database
 * @author Sami
 */
public class SQLManager implements UserReadWriter, AccountReadWriter, ExpenseReadWriter, CardReadWriter {

	/** Log4j */ 
	@SuppressWarnings("unused")
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
			return config.buildSessionFactory(serviceRegistry); 
		}
		catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

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
		user.setPassword(PasswordManager.getHash(password));
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
	// ================================================================================================

	// ============
	// | EXPENSES |
	// ============

	@Override
	public List<Expense> getAllExpenses() { 
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction();

		Query query = session.createQuery("from Expense");
		List<Expense> expenses = query.list();

		/* commit */ 
		session.getTransaction().commit();

		return expenses; 
	}

	@Override
	public List<Expense> getAllExpensesForUser(User user) { 
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction();

		Query query = session.createQuery("from Expense where account.user.id = :user");
		query.setParameter("user", user.getId());

		List<Expense> expenses = query.list();

		/* commit */ 
		session.getTransaction().commit();

		return expenses; 
	}

	@Override
	public void saveExpenses(List<Expense> expenses) { 
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction(); 

		for (Expense e : expenses) { 
			session.save(e);
		}
		/* commit */ 
		session.getTransaction().commit();

	}
	/**
	 * this method expects an expense object that will be used to create 
	 * an SQL entry
	 * @param expense
	 */
	@Override
	public void saveExpense(Expense expense) { 
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction(); 

		/* save */ 
		session.save(expense);

		/* commit */ 
		session.getTransaction().commit();
	}
	
	@Override
	public List<Expense> getExpensesForMonth(Month month, int year, User user) {
		/* begin transaction */ 
		Session session = getCurrentSession(); 
		session.beginTransaction();

		Query query = session.createQuery("from Expense where account.user.id = :user");
		Criteria c1 = session.createCriteria(Expense.class); 
		query.setParameter("user", user.getId());
		LocalDate start 	= LocalDate.of(year, month.getValue(), 1);
		LocalDateTime end 	= start.plusMonths(1).atStartOfDay().minusSeconds(1); 
		c1.add(Restrictions.between("dateIncurred", start, end)); 
		List<Expense> expenses = query.list();

		/* commit */ 
		session.getTransaction().commit();

		return expenses;
	}
	// ================================================================================================
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
}

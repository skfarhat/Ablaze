package exceptions;

/**
 * 
 * @author Sami
 *
 */
public class NullAccountException extends NullPointerException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String 	sortCode; 
	private Integer accountNumber;
	
	public NullAccountException(String message, String sortCode, Integer accountNumber) {
		super(message);
		
		this.sortCode 		= sortCode; 
		this.accountNumber 	= accountNumber; 
	}
	public String getSortCode() {
		return sortCode;
	}
	public Integer getAccountNumber() {
		return accountNumber;
	}
}

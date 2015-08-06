package viewcontrollers;



/**
 * 
 * @author Sami
 *
 */
public interface RightPaneSetter {

	public void hideRightPane();
	
	public void showAddAccountOnRightPane(String sortCode, String accountNumber);

	public void showAddExpenseOnRightPane();

	public void showAddCardOnRightPane(); 
}

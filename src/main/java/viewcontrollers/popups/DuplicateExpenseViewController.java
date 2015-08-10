package viewcontrollers.popups;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Expense;

import org.apache.log4j.Logger;

import db.ExpenseWriter;
/**
 * 
 * @author Sami
 *
 */
public class DuplicateExpenseViewController implements Initializable {

	/** Log 4j */ 
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(DuplicateExpenseViewController.class);

	private Stage stage;
	private ExpenseWriter writer; 
	private int index = 0;
	private int size; 
	private List<Expense> expenses; 

	@FXML Label accountLabel;
	@FXML Label descriptionLabel;
	@FXML Label dateLabel;
	@FXML Label messageLabel;
	@FXML Label amountLabel;
	@FXML Label indexLabel;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	private void refresh() { 
		Expense e = expenses.get(index); 
		
		accountLabel.setText(e.getAccount().getName());
		descriptionLabel.setText(e.getDescription());
		dateLabel.setText(e.getDateIncurred().toString());
		amountLabel.setText(e.getAmount().toString());
		indexLabel.setText(String.format("%d/%d", index+1, size)); 
	}
	private void next() { 
		
		if ( !isLastItem() )
			index++; 

		refresh();
	}
	
	private boolean isLastItem() { 
		return index == (size - 1); 
	}
	
	/**
	 * close controller
	 */
	private void close() { 
		stage.close(); 
	}
	
	@FXML public void addAnywayButtonPressed() {
		/* add expense to database */ 
		Expense e = expenses.get(index);
		writer.createExpense(e);
		
		/* if last item close controller */
		if ( isLastItem() )
			close(); 
		
		else 
			next(); 
	}

	@FXML public void skipButtonPressed() {
		
		/* if last item then close the stage */ 
		if (isLastItem())
			close(); 
		
		/* if not last item skip to next item */ 
		else
			next(); 
		
	}
	@FXML public void cancelButtonPressed() {
		close(); 
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	public void setWriter(ExpenseWriter writer) {
		this.writer = writer;
	}
	public void setExpenses(List<Expense> expenses) {
		this.expenses = expenses;
		this.size = expenses.size(); 
		refresh();
	}
}

package viewcontrollers;

import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;
import models.Account;
import models.Expense;
import models.User;

import org.apache.log4j.Logger;

import controls.MonthPickerController;
import db.ExpenseReadWriter;

/**
 * 
 * @author Sami
 *
 */
public class ExpenseViewController implements Initializable {

	/** Log 4j */ 
	private final static Logger logger = Logger.getLogger(ExpenseViewController.class);

	/* Table Stuff */ 
	@FXML private TreeTableView<Expense>				expensesTreeView;
	@FXML private TreeTableColumn<Expense, String> 		dateColumn;
	@FXML private TreeTableColumn<Expense, String> 		descriptionColumn;
	@FXML private TreeTableColumn<Expense, String> 		amountColumn;
	@FXML private TreeTableColumn<Expense, String> 		categoryColumn;
	@FXML private TreeTableColumn<Expense, String> 		subCategoryColumn;
	@FXML private TreeTableColumn<Expense, String> 		accountColumn;

	/* Data Source */ 
	private ObservableList<Expense> expensesList; 

	/* the below should be set by the parent controller */ 
	// --> expensesReadWriter
	private ExpenseReadWriter expenseReadWriter; 
	private User user; 
	private RightPaneSetter rightPaneSetter; 

	@FXML private MonthPickerController monthPickerController; 

	private ObjectPropertyBase<LocalDate> monthPicker = new ObjectPropertyBase<LocalDate>() {

		@Override
		public Object getBean() {
			return null;
		}

		@Override
		public String getName() {
			return "dateProperty"; 
		}
	};


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		expensesList = FXCollections.observableArrayList(); 
		expensesTreeView.setRoot(new TreeItem<Expense>());

		/*
		 * we dont' want dates to show for the expenses themselves but only for their parent node, 
		 * which corresponds to an Expense object with only the dateIncured attribute set.   
		 */
		dateColumn.setCellValueFactory(
				new Callback<CellDataFeatures<Expense, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Expense, String> p) {

						/* check if this is a real expense object or just one that contains the date 
						 * */ 
						if (p.getValue().getValue().getAccount() == null) { 
							return new SimpleStringProperty(p.getValue().getValue().getDateIncurred().toString());  
						}
						else 
							return new SimpleStringProperty("");
					}
				});
		descriptionColumn.setCellValueFactory(
				new Callback<CellDataFeatures<Expense, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Expense, String> p) {
						String description = p.getValue().getValue().getDescription(); 
						if (description != null)
							return new SimpleStringProperty(description);
						else 
							return new SimpleStringProperty(""); 
					}
				});

		amountColumn.setCellValueFactory(
				new Callback<CellDataFeatures<Expense, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Expense, String> p) {
						Double amount = p.getValue().getValue().getAmount(); 
						if (amount != null)
							return new SimpleStringProperty(amount.toString());
						else 
							return new SimpleStringProperty("");

					}
				});

		accountColumn.setCellValueFactory(
				new Callback<CellDataFeatures<Expense,String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<Expense, String> param) {
						Account acct = param.getValue().getValue().getAccount(); 
						if (acct != null)
							return new ReadOnlyStringWrapper(acct.getName());
						else 
							return new ReadOnlyStringWrapper(""); 
					}
				});

		monthPicker.bind(monthPickerController.getDate()); 
		monthPicker.addListener(new ChangeListener<LocalDate>() {

			@Override
			public void changed(
					ObservableValue<? extends LocalDate> observable,
					LocalDate oldValue, LocalDate newValue) {
				logger.debug("detected the change !"); 
				refresh(); 
			}
		});
		refresh(); 
		filterArray(); 
	}

	private void filterArray() {
		expensesTreeView.getRoot().getChildren().clear();
		
		logger.trace("filterArray"); 
		HashMap<LocalDate, ArrayList<Expense>> map = new HashMap<>(100); 

		for (Expense e : expensesList) {

			ArrayList<Expense> array ;//= new ArrayList<>(); 
			if (!map.containsKey(e.getDateIncurred())) { 
				array = new ArrayList<>(); 
				map.put(e.getDateIncurred(), array); 
			} else { 
				array = map.get(e.getDateIncurred()); 
			}
			array.add(e); 
		}

		for (Entry<LocalDate, ArrayList<Expense>> e: map.entrySet()) {
			Expense temp = new Expense(); 
			temp.setDateIncurred(e.getKey());

			TreeItem<Expense> item = new TreeItem<Expense>(temp);
			item.setExpanded(true);
			for (int i = 0; i < e.getValue().size(); i++) { 
				Expense expense = e.getValue().get(i); 
				item.getChildren().add(new TreeItem<Expense>(expense)); 
			}

			expensesTreeView.getRoot().getChildren().add(item); 
		}

	}
	@FXML public void refresh() {
		logger.trace("refresh() - trace"); 
		logger.debug("refresh() - debug"); 
		logger.info("refresh() - info"); 
		logger.warn("refresh() - warn"); 
		logger.error("refresh() - error"); 

		logger.debug("user: " + user + " readwriter: " + expenseReadWriter);
		if (user != null && expenseReadWriter != null) {
			logger.debug("inside the condition"); 
			//			expensesList = FXCollections.observableArrayList(expenseReadWriter.getAllExpensesForUser(user));
			Month month = monthPickerController.getDate().get().getMonth(); 
			int year = monthPickerController.getDate().get().getYear(); 
			expensesList = FXCollections.observableArrayList(expenseReadWriter.getExpensesForMonth(month, year, user)); 

			logger.debug("expensesList: " + expensesList.size());
			filterArray(); 
		}

	}
	@FXML private void addExpense() {
		if (rightPaneSetter != null) {
			rightPaneSetter.showAddExpenseOnRightPane(); 
		}
	}


	@FXML private void leftButtonPressed() { 
		logger.debug("left button presed on month picker");
	}
	@FXML private void rightButtonPressed() { 
		logger.debug("right button presed on month picker");
	}

	private void leftButtonMonthPicker() { 

	}
	private void rightButtonMonthPicker() { 

	}

	public void setRightPaneSetter(RightPaneSetter rightPaneSetter) {
		this.rightPaneSetter = rightPaneSetter;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public void setExpenseReadWriter(ExpenseReadWriter expenseReadWriter) {
		this.expenseReadWriter = expenseReadWriter;
	}
}

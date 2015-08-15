package viewcontrollers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import logic.BarclaysCSVParser;
import models.Account;
import models.Category;
import models.Transaction;
import models.User;

import org.apache.log4j.Logger;

import viewcontrollers.popups.DuplicateExpenseViewController;
import controls.MonthPickerController;
import db.AccountReader;
import db.CategoriesReadWriter;
import db.ExpenseReadWriter;
import exceptions.NullAccountException;

/**
 * 
 * @author Sami
 *
 */
public class ExpenseViewController implements Initializable {

	/** Log 4j */ 
	private final static Logger logger = Logger.getLogger(ExpenseViewController.class);

	/* Table Stuff */ 
	@FXML private TreeTableView<Transaction>				expensesTreeView;
	@FXML private TreeTableColumn<Transaction, String> 		dateColumn;
	@FXML private TreeTableColumn<Transaction, String> 		descriptionColumn;
	@FXML private TreeTableColumn<Transaction, String> 		amountColumn;
	@FXML private TreeTableColumn<Transaction, String> 		categoryColumn;
	@FXML private TreeTableColumn<Transaction, String> 		subCategoryColumn;
	@FXML private TreeTableColumn<Transaction, String> 		accountColumn;

	@FXML private MonthPickerController monthPickerController;

	/* Data Source */ 
	private ObservableList<Transaction> expensesList; 

	/* the below should be set by the parent controller */ 
	private ExpenseReadWriter expenseReadWriter; 
	
	private User user; 
	private RightPaneSetter rightPaneSetter; 

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
		expensesTreeView.setRoot(new TreeItem<Transaction>());

		/*
		 * we dont' want dates to show for the expenses themselves but only for their parent node, 
		 * which corresponds to an Expense object with only the dateIncured attribute set.   
		 */
		dateColumn.setCellValueFactory(
				new Callback<CellDataFeatures<Transaction, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Transaction, String> p) {

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
				new Callback<CellDataFeatures<Transaction, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Transaction, String> p) {
						String description = p.getValue().getValue().getDescription(); 
						if (description != null)
							return new SimpleStringProperty(description);
						else 
							return new SimpleStringProperty(""); 
					}
				});

		amountColumn.setCellValueFactory(
				new Callback<CellDataFeatures<Transaction, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Transaction, String> p) {
						Transaction e = p.getValue().getValue(); 
						Double amount = e.getAmount(); 
						
						if (amount != null) { 
							String str = String.format("%s %.2f", e.getAccount().getCurrency().getSymbol(), amount); 
							return new SimpleStringProperty(str); 
						}
						else 
							return new SimpleStringProperty("");

					}
				});

		accountColumn.setCellValueFactory(
				new Callback<CellDataFeatures<Transaction,String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<Transaction, String> param) {
						Account acct = param.getValue().getValue().getAccount(); 
						if (acct != null)
							return new ReadOnlyStringWrapper(acct.getName());
						else 
							return new ReadOnlyStringWrapper(""); 
					}
				});
		
		categoryColumn.setCellValueFactory(
				new Callback<CellDataFeatures<Transaction,String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<Transaction, String> param) {
						Category categ = param.getValue().getValue().getCategory();  
						if (categ != null)
							return new ReadOnlyStringWrapper(categ.getName());
						else 
							return new ReadOnlyStringWrapper(""); 
					}
				});

		monthPicker.bind(monthPickerController.getDate());
		monthPickerController.setDelegate(() -> {
			refresh(); 
		});

		
		refresh(); 
		filterArray(); 
	}

	private void filterArray() {
		expensesTreeView.getRoot().getChildren().clear();

		logger.trace("filterArray"); 
		HashMap<LocalDate, ArrayList<Transaction>> map = new HashMap<>(100); 

		for (Transaction e : expensesList) {

			ArrayList<Transaction> array ;//= new ArrayList<>(); 
			if (!map.containsKey(e.getDateIncurred())) { 
				array = new ArrayList<>(); 
				map.put(e.getDateIncurred(), array); 
			} else { 
				array = map.get(e.getDateIncurred()); 
			}
			array.add(e); 
		}

		for (Entry<LocalDate, ArrayList<Transaction>> e: map.entrySet()) {
			Transaction temp = new Transaction(); 
			temp.setDateIncurred(e.getKey());

			TreeItem<Transaction> item = new TreeItem<Transaction>(temp);
			item.setExpanded(true);
			for (int i = 0; i < e.getValue().size(); i++) { 
				Transaction expense = e.getValue().get(i); 
				item.getChildren().add(new TreeItem<Transaction>(expense)); 
			}

			expensesTreeView.getRoot().getChildren().add(item); 
		}

	}

	@FXML public void refresh() {

		if (user != null && expenseReadWriter != null) {
			//			expensesList = FXCollections.observableArrayList(expenseReadWriter.getAllExpensesForUser(user));
			Month month = monthPickerController.getDate().get().getMonth(); 
			int year = monthPickerController.getDate().get().getYear(); 
			expensesList = FXCollections.observableArrayList(expenseReadWriter.getExpensesForMonth(month, year, user)); 

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

	/**
	 * 
	 * @param file file to parse
	 * @param actReader is passed to BarclaysCSVParser
	 */
	public void importCSV(File file, AccountReader actReader) { 
		try {
			
			/* will hold expenses that are non-duplicates */
			List<Transaction> expenses = new ArrayList<>(10); 

			/* save in database */ 
			List<Transaction> suspectDuplicates = new ArrayList<>(); 
			
			/* divide expenses between suspect duplicates and non-suspect */ 
			List<Transaction> temp = BarclaysCSVParser.parse(actReader, file.getPath());
			for (int i = 0; i < temp.size(); i++) { 
				Transaction e = temp.get(i); 
				
				/* if is suspect duplicate */ 
				if (expenseReadWriter.expenseIsSuspectDuplicate(e)) { 
					suspectDuplicates.add(e);
				} else { 
					expenses.add(e); 
				}
			}

			if (suspectDuplicates.size() > 0) { 
				logger.debug("suspect duplicates");
				FXMLLoader loader = new FXMLLoader(); 
				loader.setLocation(ExpenseViewController.class.getResource("../views/popups/DuplicateExpense.fxml"));
				AnchorPane pane; 
				try { 
					pane = (AnchorPane) loader.load(); 
					DuplicateExpenseViewController controller = loader.getController();
					Stage stage = new Stage(); 
					Scene scene = new Scene(pane); 
					stage.setScene(scene);
					controller.setStage(stage);
					controller.setWriter(expenseReadWriter);
					controller.setExpenses(suspectDuplicates);
					stage.showAndWait();

				} catch(IOException io) {
					logger.error(io.getMessage()); 
					io.printStackTrace();
				}
			}

			/* create all expenses */ 
			expenseReadWriter.createTransactions(expenses);

			/* refresh table */  
			refresh(); 

		} catch (NullAccountException exc) {
			String mess = "Create a new account ?"; 
			Alert alert = new Alert(AlertType.CONFIRMATION, mess, ButtonType.YES, ButtonType.NO); 
			Optional<ButtonType> result = alert.showAndWait(); 

			if (result.get() == ButtonType.YES) {
				rightPaneSetter.showAddAccountOnRightPane(exc.getSortCode(), exc.getAccountNumber().toString());
			}
			String message = exc.getMessage(); 
			logger.error(message);
		} catch (IOException ioExc) { 
			ioExc.printStackTrace();
		}
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

package viewcontrollers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
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
import models.Expense;
import models.User;

import org.apache.log4j.Logger;

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


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		expensesList = FXCollections.observableArrayList(); 
		expensesTreeView.setRoot(new TreeItem<Expense>());

		dateColumn.setCellValueFactory(
				new Callback<CellDataFeatures<Expense, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Expense, String> p) {
						return new SimpleStringProperty(p.getValue().getValue().getDateIncurred().toString());  
					}
				});
		descriptionColumn.setCellValueFactory(
				new Callback<CellDataFeatures<Expense, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Expense, String> p) {
						return new SimpleStringProperty(p.getValue().getValue().getDescription());   
					}
				});

		amountColumn.setCellValueFactory(
				new Callback<CellDataFeatures<Expense, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(CellDataFeatures<Expense, String> p) {
						return new SimpleStringProperty(p.getValue().getValue().getAmount().toString());
					}
				});

		accountColumn.setCellValueFactory(
				new Callback<CellDataFeatures<Expense,String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(
							CellDataFeatures<Expense, String> param) {
						return new ReadOnlyStringWrapper(param.getValue().getValue().getAccount().getName());
					}
				});

		refresh(); 
		filterArray(); 
	}

	private void filterArray() {

		HashMap<LocalDate, ArrayList<Expense>> map = new HashMap<>(100); 

		for (Expense e : expensesList) {

			ArrayList<Expense> ex ;//= new ArrayList<>(); 
			if (!map.containsKey(e.getDateIncurred())) { 
				ex = new ArrayList<>(); 
				map.put(e.getDateIncurred(), ex); 
			} else { 
				ex = map.get(e.getDateIncurred()); 
			}
			ex.add(e); 
		}
		
		for (Entry<LocalDate, ArrayList<Expense>> e: map.entrySet()) {
			TreeItem<Expense> item = new TreeItem<Expense>(e.getValue().get(0));

			for (int i = 1; i < e.getValue().size(); i++) { 
				Expense expense = e.getValue().get(i); 
				item.getChildren().add(new TreeItem<Expense>(expense)); 
			}

			expensesTreeView.getRoot().getChildren().add(item); 
		}

		//		
		//		
		////		for (TreeItem<Expense> node : nodeList) { 
		////			node.getChildren().addall
		////			expensesTreeView.getRoot().getChildren().add(node); 
		////		}
		//		logger.debug("map: " + nodes.size());
		//		logger.debug("expensesTreeView: " + expensesTreeView); 
		//		logger.debug("expensesTreeView:children: " + expensesTreeView.getRoot().getChildren()); 
		//		expensesTreeView.getRoot().getChildren().clear();
		//		for (Entry<LocalDate, TreeItem<Expense>> e: nodes.entrySet()) { 
		//			expensesTreeView.getRoot().getChildren().addAll(FXCollections.observableArrayList(nodeList));
		//		}


	}
	@FXML public void refresh() {
		logger.debug("refresh"); 
		logger.debug("user: " + user + " readwriter: " + expenseReadWriter);
		if (user != null && expenseReadWriter != null) {
			logger.debug("inside the condition"); 
			expensesList = FXCollections.observableArrayList(expenseReadWriter.getAllExpensesForUser(user));
			logger.debug("expensesList: " + expensesList.size());
			//			expensesTable.set
			//			expensesTable.setItems(expensesList);
			filterArray(); 
		}

	}
	@FXML private void addExpense() {
		if (rightPaneSetter != null) {
			rightPaneSetter.showAddExpenseOnRightPane(); 
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

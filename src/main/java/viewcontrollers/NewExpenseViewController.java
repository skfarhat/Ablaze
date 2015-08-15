package viewcontrollers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Account;
import models.Category;
import models.Transaction;
import models.User;

import org.apache.log4j.Logger;

import controls.superChoiceBox.SuperChoiceBox;
import viewcontrollers.popups.DuplicateExpenseViewController;
import db.CategoriesReadWriter;
import db.ExpenseReader;
import db.ExpenseWriter;

/**
 * 
 * @author Sami
 *
 */
public class NewExpenseViewController implements Initializable {


	/** Log4j */ 
	//	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(NewExpenseViewController.class);

	private CategoriesReadWriter 	categoriesReadWriter; 
	private ExpenseWriter 			expenseWriter; 
	private ExpenseReader 			expenseReader; 

	@FXML private TextField descriptionTextField; 
	@FXML private TextField amountTextField; 
	@FXML private TextField nameTextField; 
	@FXML private Label currencyLabel; 

	@FXML private ChoiceBox<String> categoryChoiceBox; 
	@FXML private ChoiceBox<String> subCategoryChoiceBox; 
	@FXML private DatePicker datePicker; 
	@FXML private ChoiceBox<Account> accountChoiceBox; 

	@FXML private SuperChoiceBox<Category> superChoiceBoxController; 

	private User user; 

	/**
	 * interface implemented by RootViewController, 
	 * allows setting the right pane
	 */
	private RightPaneSetter rightPaneSetter;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		datePicker.setValue(LocalDate.now());

		Category cate = new Category(); 
		cate.setName("New Category");
		superChoiceBoxController.setT(cate);
		superChoiceBoxController.setReadWriter(categoriesReadWriter);
		superChoiceBoxController.setAddButtonFunction(text -> {
			
			/* create new category object */ 
			Category category = new Category(); 
			category.setName(text);
			
			/* check that this category doesn't already exist in the ComboBox, 
			 * if it exists we request the combo box select it
			 * if it does not exist, we create it in the database and call for the 
			 * SuperComboBox to be refreshed 
			 * */ 
			if (categoriesReadWriter.categoryExists(category)) {
				int index = superChoiceBoxController.indexOf(category); 
				superChoiceBoxController.requestSelection(index);
				/* return null indicating we haven't added any item */ 
				return null; 
			} else { 
				/* create and return the item we created */ 
				categoriesReadWriter.createCategory(category);
				return category;	
			}
		}); 
	}

	@FXML private void confirmButtonPressed() {

		//		categoryChoiceBox.getSelectionModel().getSelectedItem()
		final String category = categoryChoiceBox.getSelectionModel().getSelectedItem();//.toString();  
		final String subCategory = subCategoryChoiceBox.getSelectionModel().getSelectedItem();//.toString();
		final String description = descriptionTextField.getText(); 
		final Account account = accountChoiceBox.getValue(); 
		final LocalDate date = datePicker.getValue(); 
		final String type = ""; // TODO: do type field

		// TODO: make sure the entered value is a number
		final Double amount = Double.parseDouble(amountTextField.getText());

		Transaction expense = new Transaction();
		expense.setAccount(account);
		// FIXME
		//		expense.setCategory(category);
		expense.setSubCategory(subCategory);
		expense.setDateIncurred(date); 
		expense.setDescription(description);
		expense.setType(type);
		expense.setAmount(amount);

		if (expenseReader.expenseIsSuspectDuplicate(expense)) { 
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
				controller.setWriter(expenseWriter);
				List<Transaction> duplicates = new ArrayList<>(); 
				duplicates.add(expense); 
				controller.setExpenses(duplicates);
				stage.showAndWait();

			} catch(IOException io) {
				logger.error(io.getMessage()); 
				io.printStackTrace();
			}
		} else { 
			expenseWriter.createTransaction(expense);
		}
		rightPaneSetter.hideRightPane();
	}
	public void refresh(){

		//TODO: fill categories options 
		accountChoiceBox.setItems(FXCollections.observableArrayList(user.getAccounts()));

		accountChoiceBox.getSelectionModel().selectedIndexProperty().addListener((o,val,newVal)-> {
			Account act = accountChoiceBox.getItems().get(newVal.intValue()); 
			currencyLabel.setText(act.getCurrency().getSymbol());
		});
	}

	@FXML private void cancelButtonPressed() { 
		rightPaneSetter.hideRightPane();
	}
	public void setWriter(ExpenseWriter writer) {
		this.expenseWriter = writer;
	}

	public void setRightPaneSetter(RightPaneSetter rightPaneSetter) {
		this.rightPaneSetter = rightPaneSetter;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public void setExpenseReader(ExpenseReader expenseReader) {
		this.expenseReader = expenseReader;
	}
	public void setCategoriesReadWriter(
			CategoriesReadWriter categoriesReadWriter) {
		this.categoriesReadWriter = categoriesReadWriter;

		superChoiceBoxController.setReadWriter(categoriesReadWriter);
		superChoiceBoxController.setItems(categoriesReadWriter.getAllCategories());
	}
}

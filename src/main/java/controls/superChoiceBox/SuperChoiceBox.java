package controls.superChoiceBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import models.Category;

import org.apache.log4j.Logger;

import util.VoidFunction;
import db.CategoriesReadWriter;

/**
 * 
 * @author Sami
 *
 * @param <T>
 */
public class SuperChoiceBox<T> implements Initializable {

	/** Log4j */ 
	private final static Logger logger = Logger.getLogger(SuperChoiceBox.class);

	@FXML private TextField textfield;
	@FXML private ComboBox<T> comboBox;
	@FXML private Button acceptButton;
	@FXML private Button cancelButton;
	@FXML private AnchorPane addModeAnchorPane;

	private CategoriesReadWriter readWriter;
	/**
	 * this class will be considered as having two states: 
	 * 'addMode' and 'normalMode', with these states distinguished
	 * by the boolean variable below
	 */
	private boolean addMode = false; 
	private ObservableList<T> items; 
	private T t; 
	
	/**
	 * function that takes in a String (in this case the selected text from the choicebox) 
	 * and returns an item T, that is supposedly the item to be added to the choicebox
	 */
	private Function<String, T> 		addButtonFunction; 
	private VoidFunction 				cancelButtonFunction;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		comboBox.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
			@Override
			public ListCell<T> call(ListView<T> param) {

				return new ListCell<T>(){
					@Override
					public void updateItem(T item, boolean empty){
						if (item == null) { 
							setText("New Category");							
						} 
						else { 
							super.updateItem(item, empty);
							if(!empty) { 
								setText(item.toString());
							}	
						}

					}
				};
			}
		});
		
		comboBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				
				/* if last item */ 
				if (newValue.intValue() == comboBox.getItems().size() - 1) {

					if (addMode == true)
						return; 
					
					switchMode();
				}
			}
		});
		
		items = FXCollections.observableArrayList(); 
		comboBox.setItems(items);
	}

	public void setItems(List<T> items) {
		logger.debug("setItems in superchoicebox");
		
		this.items.clear(); 
		this.items.addAll(items); 

		/* add null item at the end as this is the cell that allows the user to add new categories */
		this.items.add(t); 
	}

	public T getValue() { 
		return comboBox.getValue(); 
	}
	
	@FXML private void acceptButtonPressed() {
		if (addMode == false || readWriter == null)
			return; 

		String text = textfield.getText();
		
		if (addButtonFunction != null){
			
			T item = addButtonFunction.apply(text);
			if (item != null)
				items.add(items.size() - 1, item);  
						
		}
		/* switch to normal mode */ 
		switchMode(); 
	}
	@FXML private void cancelButtonPressed() { 
		if (cancelButtonFunction != null)
			cancelButtonFunction.perform();

		/* if we are in addMode switch off*/
		if (addMode == true)
			switchMode();
		
		comboBox.getSelectionModel().clearSelection();
	}

	private void switchMode() {
		if (addMode == true) { 
			addMode = false; 
			addModeAnchorPane.setVisible(false);
		}
		else {
			addMode = true;
			addModeAnchorPane.setVisible(true);
		}
		textfield.clear(); 
	}
	
	public void requestSelection(int index) { 
		if (index == -1 )
			comboBox.getSelectionModel().clearSelection();
		
		/* we don't want to allow the user to select the last item which is an "Add Item" */ 
		if (index > comboBox.getItems().size() - 2)
			return; 
		
//		comboBox.getSelectionModel().clearAndSelect(index);
		comboBox.getSelectionModel().select(index);
	}
	/**
	 * 
	 * @param category
	 * @return index of the passed category, or -1 if not found
	 */
	public int indexOf(Category category) { 
		for (int i = 0; i < items.size() - 1; i++) {
			if (category.equals(items.get(i))){
				return i; 
			}
		}
		return -1; 
	}
	public void setAddButtonFunction(Function<String, T> addButtonFunction) {
		this.addButtonFunction = addButtonFunction;
	}
	public void setCancelButtonFunction(VoidFunction cancelButtonFunction) {
		this.cancelButtonFunction = cancelButtonFunction;
	}
	public void setReadWriter(CategoriesReadWriter readWriter) {
		this.readWriter = readWriter;
	}
	public void setT(T t) {
		this.t = t;
	}
}

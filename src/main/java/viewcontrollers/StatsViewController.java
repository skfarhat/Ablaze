package viewcontrollers;

import java.net.URL;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import models.User;

import org.apache.log4j.Logger;

import controls.MonthPickerController;
import db.AccountReader;
import db.ExpenseReader;
import db.StatsReader;

public class StatsViewController implements Initializable {

	/** Log4j */ 
	private final static Logger logger = Logger.getLogger(StatsViewController.class);

	private User user; 
	
	private static final int CATEGORIES_X_COUNT = 6;
	
	private ExpenseReader 	expenseReader;
	private AccountReader 	accountReader; 
	private StatsReader 	statsReader; 
	@FXML private MonthPickerController monthPickerController;
	
	@FXML private Label totalInLabel;
	@FXML private Label totalOutLabel;
	@FXML private Label netLabel;
	@FXML private PieChart categoryPieChart;
	
	private ObjectPropertyBase<LocalDate> date = new ObjectPropertyBase<LocalDate>() {

		@Override
		public Object getBean() {
			return null;
		}

		@Override
		public String getName() {
			return "dateProperty"; 
		}
	};
	
//	private List<>
	
	@FXML private BarChart<String, Double> expensesBarChart;
//	private CategoryAxis xAxis; 
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
//		xAxis = new CategoryAxis(); 
		
		monthPickerController.setDelegate(() -> refresh());
		
		
	}
	
	public void refresh() {
		date.bind(monthPickerController.getDate());

////		/* number of months that are displayed on the x-axis */
////		ObservableList<String> xAxis = FXCollections.observableArrayList();
//
//		/* x-axis */ 
//		LocalDate now = date.get(); 
//		for (int i = 0; i < CATEGORIES_X_COUNT; i++) { 
//			now = now.minusMonths(1);
//			String date = now.getMonth().toString() + now.getYear(); 
////			xAxis.add(date);
//		}
//		/* y-axis */
//		
		
		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("date");
		NumberAxis 	yAxis = new NumberAxis(); 
		yAxis.setLabel("Expenses");
		
		List<Object[]> netExpenses = statsReader.netExpensesByMonth();
		for (Object[] o : netExpenses) { 
			String date 	= (String) o[0]; 
			Double amount 	= -(Double) o[1];

			Series<String, Double> series = new XYChart.Series<>(); 
			series.getData().add(new XYChart.Data<String, Double>(date, amount));
			series.setName(date);
			expensesBarChart.getData().add(series); 
		}
		
	}

	public void setUser(User user) {
		this.user = user;
	}	

	public void setExpenseReader(ExpenseReader expenseReader) {
		this.expenseReader = expenseReader;
	}
	public void setAccountReader(AccountReader accountReader) {
		this.accountReader = accountReader;
	}
	
	public void setStatsReader(StatsReader statsReader) {
		this.statsReader = statsReader;
	}
}

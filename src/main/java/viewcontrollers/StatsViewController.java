package viewcontrollers;

import java.net.URL;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import models.Category;
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

	@FXML private BarChart<String, Double> expensesBarChart;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		monthPickerController.setDelegate(() -> refresh());
		date.bind(monthPickerController.getDate());
	}


	public void refresh() {
		refreshBarChart();
		refreshPieChart();
	}

	private void refreshPieChart() {
		/* clear data */ 
		categoryPieChart.getData().clear(); 
		List<Object[]> data = statsReader.categoriesDataForDates(date.get()); 

		/* calculate the total amount for that month */ 
		double total = 0.0;
		for (int i = 0; i < data.size(); i++){
			Object o[] = data.get(i); 
			Double amount = Double.parseDouble(o[3].toString()); 
			total+=amount; 
		}

		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(); 
		for (int i = 0; i < data.size(); i++) { 
			Object o[] = data.get(i);
			Category category = (Category) o[1]; 

			Double amount = Double.parseDouble(o[3].toString());
			String catName; 
			if (category == null)
				catName = "Other"; 
			else 
				catName = category.getName();

			PieChart.Data d =  new PieChart.Data(catName, 100*(amount/total));

			pieChartData.add(d); 
		}

		categoryPieChart.setData(pieChartData);
	
	}
	private void refreshBarChart() {

		/* clear data */ 
		expensesBarChart.getData().clear();

		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("date");

		NumberAxis 	yAxis = new NumberAxis(); 
		yAxis.setLabel("Expenses");

		// TODO: if want to limit the number then add condition < CATEGOIRES_X_COUNT
		List<Object[]> netExpenses = statsReader.netExpensesByMonth();

		Series<String, Double> serie = new XYChart.Series<>(); 
		List<Series<String, Double>> series = new ArrayList<>(); 
		for (Object[] o : netExpenses) { 
			String date 	= (String) o[0]; 
			Double amount 	= -(Double) o[1];

			Data<String, Double> data = new XYChart.Data<String, Double>(date, amount);
			serie.getData().add(serie.getData().size(), data);
		}
		series.add(serie); 
		expensesBarChart.getData().addAll(series);
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

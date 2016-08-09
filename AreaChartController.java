package application;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import com.sun.javafx.event.EventHandlerManager;

import chartmodels.ChartDataModel;
import chartmodels.ChartPosDataModel;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import messagereader.TimeMappedEphemerisReader;
import ds.airbus.jms.util.PosTvector;
import ds.airbus.stvt.worldwindow.model.SpookDataModel;

public class AreaChartController {
	MainWindowController mainWindowController = new MainWindowController();
    Line marker = new Line();
 	private LineGraphController lineGraphController;
    public Timer timer;
	 final CategoryAxis xAxis = new CategoryAxis();
	 final NumberAxis yAxis = new NumberAxis();
	@FXML
	//LineChart <String, Number> lineChart ;
	 AreaChart<String, Number> areaChart;
	@FXML public Slider verticalMarkerSlider;
	@FXML
	Label lbl;
    private static File initDir = new File(System.getProperty("user.dir"));
    private double start = 0;
    private double end = 0;
    private ObservableList<ChartDataModel> chartPosDataModelList = FXCollections.observableArrayList();

	public void btn (ActionEvent event){

//		lineChart.setCursor(Cursor.CROSSHAIR);
		areaChart.getData().clear();

                            lineGraphController.series.getData().add(lineGraphController.getData());
                            lineGraphController.series1.getData().add(lineGraphController.getData1());
                            lineGraphController.series2.getData().add(lineGraphController.getData2());

                            lineGraphController.series.setName("X-Position");
                            lineGraphController.series1.setName("Y-Position");
                            lineGraphController.series2.setName("Z-Position");
				            areaChart.getStyleClass().add("thick-chart");
	        verticalMarkerSlider.valueProperty().bindBidirectional((Property<Number>) lineGraphController.verticalMarkerSlider.valueProperty());

				        }






	public Slider getVerticalMarkerSlider() {
		return verticalMarkerSlider;
	}


	public void setVerticalMarkerSlider(Slider verticalMarkerSlider) {
		this.verticalMarkerSlider = verticalMarkerSlider;
	}

	public double getStart() {
		return start;
	}


	public void setStart(double start) {
		this.start = start;
	}


	public double getEnd() {
		return end;
	}


	public void setEnd(double end) {
		this.end = end;
	}



	public ObservableList<ChartDataModel> getChartPosDataModelList() {
		return chartPosDataModelList;
	}


	public void setChartPosDataModelList(
			ObservableList<ChartDataModel> chartPosDataModelList) {
		this.chartPosDataModelList = chartPosDataModelList;
	}


	public AreaChart<String, Number> getAreaChart() {
		return areaChart;
	}


	public void setAreaChart(AreaChart<String, Number> areaChart) {
		this.areaChart = areaChart;
	}


	public void setupSlider(int size) {
    	Platform.runLater(new Runnable() {

			@Override
			public void run() {

		    	verticalMarkerSlider.setMin(start);
		    	verticalMarkerSlider.setMax(end);

		    	double increment = 0.0000115740741 * 60;		// 60s
		    	verticalMarkerSlider.setBlockIncrement(increment);

		    	verticalMarkerSlider.setMajorTickUnit(end-start);

		    	verticalMarkerSlider.setMinorTickCount((int)((end-start)/increment)-2);

			}
    	});

    }
	public void setupTimer(long period){
		timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				Platform.runLater(new Runnable(){

					@Override
					public void run() {
						verticalMarkerSlider.increment();

					}

				});

			}

		}, 0, period);
	}

	private void stop() {
		// TODO Auto-generated method stub
	//	lineChart.getData().clear();


	}


}

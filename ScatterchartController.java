package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Map.Entry;

import messagereader.TimeMappedEphemerisReader;
import ds.airbus.jms.util.PosTvector;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ScatterchartController {
	private LineGraphController lineGraphController;
	@FXML
	ScatterChart <String, Number> scatterChart;
	@FXML public Slider verticalMarkerSlider;
	@FXML
	Label lbl;
	public Timer timer;
	final CategoryAxis xAxis = new CategoryAxis();
	final NumberAxis yAxis = new NumberAxis();
	private double start = 0;
	private double end = 0;



	public void btn (ActionEvent event){
		scatterChart.getData().clear();
		 lineGraphController.series.getData().add(lineGraphController.getData());
         lineGraphController.series1.getData().add(lineGraphController.getData1());
         lineGraphController.series2.getData().add(lineGraphController.getData2());

         lineGraphController.series.setName("X-Position");
         lineGraphController.series1.setName("Y-Position");
         lineGraphController.series2.setName("Z-Position");
//		        barChart.getData().addAll(series, series1, series2);
         verticalMarkerSlider.valueProperty().bindBidirectional((Property<Number>) lineGraphController.verticalMarkerSlider.valueProperty());

}


	public LineGraphController getLineGraphController() {
		return lineGraphController;
	}


	public void setLineGraphController(LineGraphController lineGraphController) {
		this.lineGraphController = lineGraphController;
	}


	public ScatterChart<String, Number> getScatterChart() {
		return scatterChart;
	}


	public void setScatterChart(ScatterChart<String, Number> scatterChart) {
		this.scatterChart = scatterChart;
	}


	public Timer getTimer() {
		return timer;
	}


	public void setTimer(Timer timer) {
		this.timer = timer;
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


	public CategoryAxis getxAxis() {
		return xAxis;
	}


	public NumberAxis getyAxis() {
		return yAxis;
	}


	public Slider getVerticalMarkerSlider() {
		return verticalMarkerSlider;
	}


	public void setVerticalMarkerSlider(Slider verticalMarkerSlider) {
		this.verticalMarkerSlider = verticalMarkerSlider;
	}


	public Label getLbl() {
		return lbl;
	}


	public void setLbl(Label lbl) {
		this.lbl = lbl;
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

}

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

public class LineGraphController {
	MainWindowController mainWindowController = new MainWindowController();
    Line marker = new Line();
    public Timer timer;
	 final CategoryAxis xAxis = new CategoryAxis();
	 final NumberAxis yAxis = new NumberAxis();
	 private AreaChartController areaChartController;
	@FXML
	//LineChart <String, Number> lineChart ;
	 LineChart<String, Number> lineChart;
	@FXML public Slider verticalMarkerSlider;
	@FXML
	Label lbl;
    private static File initDir = new File(System.getProperty("user.dir"));
    private double start = 0;
    private double end = 0;
    private ObservableList<ChartDataModel> chartPosDataModelList = FXCollections.observableArrayList();
	public XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
	//lineChart.getData().clear();
	public XYChart.Series<String, Number> series1 = new XYChart.Series<String, Number>();
	//lineChart.getData().clear();
	public XYChart.Series<String, Number> series2 = new XYChart.Series<String, Number>();
	//lineChart.getData().clear();
	Data<String, Number> data;
    Data<String, Number> data1;
    Data<String, Number> data2;
	public List<File> btn (ActionEvent event){
//		lineChart.setCursor(Cursor.CROSSHAIR);
		lineChart.getData().clear();

		FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Object's Real Position File(s)");
        fileChooser.setInitialDirectory(initDir);
        List<File> files = fileChooser.showOpenMultipleDialog(new Stage());
        if (files != null) {
        	initDir = files.get(0).getParentFile();
        	for(File file : files) {
//            	realPosFilePath.setText(file.toPath().toString());
        	        	TimeMappedEphemerisReader er = new TimeMappedEphemerisReader();
        		        try {
        					er.setContent(new String(Files.readAllBytes(file.toPath())));
        					//System.out.println(new String(Files.readAllBytes(file.toPath())));
        				} catch (IOException e) {
        					e.printStackTrace();
        				}
        		TreeMap<Double, PosTvector> realPosDataMap = er.readObjectRealPosEphemeris2();
		        if (start == 0 && end == 0) {
		        	start = realPosDataMap.firstKey();

		        	end = realPosDataMap.lastKey();

		        	setupSlider(realPosDataMap.size());
		        } else if (realPosDataMap.firstKey() < start) {
		        	start = realPosDataMap.firstKey();
		        	setupSlider(realPosDataMap.size());
		        } else if (realPosDataMap.lastKey() > end) {
		        	end = realPosDataMap.lastKey();
		        	setupSlider(realPosDataMap.size());
		        }else if(realPosDataMap.size() >= 200){

		        }
						Set<Entry<Double,PosTvector>> set = realPosDataMap.entrySet();

				        Iterator<Entry<Double, PosTvector>> i1 = set.iterator();

				        Map.Entry<Double,PosTvector> map;
					int amount = 0;

				        while(i1.hasNext()) {
				         	map = i1.next();
				        	amount +=1;

				        	Double xvalue = map.getKey();
				            PosTvector yvalue = map.getValue();
				            PosTvector yvalue1 = map.getValue();
				            PosTvector yvalue2 = map.getValue();
				            data = new Data<String, Number>(xvalue.toString(), yvalue.state[0]);
				            data1 = new Data<String, Number>(xvalue.toString(), yvalue1.state[1]);
				            data2 = new Data<String, Number>(xvalue.toString(), yvalue2.state[2]);
				          //Data<String, Number> verticalMarker = new Data<String, Number>(xvalue.toString(), 0);

				            series.getData().add(data);
				            series1.getData().add(data1);
				            series2.getData().add(data2);

				        series.setName("X-Position");
				        series1.setName("Y-Position");
				        series2.setName("Z-Position");
				        lineChart.getStyleClass().add("thick-chart");
//					    	lineChart.getData().addAll(series, series1, series2);

	        verticalMarkerSlider.valueProperty().addListener(new ChangeListener<Number>() {

					@Override
					public void changed(ObservableValue<? extends Number> arg0,
							Number arg1, Number arg2) {
						double toKey = Math.round(verticalMarkerSlider.getValue()*100000000.0)/100000000.0;
						//if (toKey == end)
							//stop();
						lbl.setText("Data values for X Axis"+"\nTime: "+verticalMarkerSlider.getValue());

						Tooltip.install(data.getNode(), new Tooltip("X data"+"\n X-Time:" +data.getXValue() + "\n Y-Position:" + String.valueOf(data.getYValue())));// this for hovering the status
						Tooltip.install(data1.getNode(), new Tooltip("Y data"+"\n X-Time:" +data1.getXValue() + "\n Y-Position:" + String.valueOf(data1.getYValue())));// this for hovering the status
						Tooltip.install(data2.getNode(), new Tooltip("Z data"+"\n X-Time:" +data2.getXValue() + "\n Y-Position:" + String.valueOf(data2.getYValue())));// this for hovering the status

					}
					});


				        }
        	}

}
    	return files;
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


	public Data<String, Number> getData() {
		return data;
	}


	public void setData(Data<String, Number> data) {
		this.data = data;
	}


	public Data<String, Number> getData1() {
		return data1;
	}


	public void setData1(Data<String, Number> data1) {
		this.data1 = data1;
	}


	public Data<String, Number> getData2() {
		return data2;
	}


	public void setData2(Data<String, Number> data2) {
		this.data2 = data2;
	}


	public double getEnd() {
		return end;
	}


	public void setEnd(double end) {
		this.end = end;
	}


	public AreaChartController getAreaChartController() {
		return areaChartController;
	}


	public void setAreaChartController(AreaChartController areaChartController) {
		this.areaChartController = areaChartController;
	}


	public XYChart.Series<String, Number> getSeries() {
		return series;
	}



	public void setSeries(XYChart.Series<String, Number> series) {
		this.series = series;
	}



	public XYChart.Series<String, Number> getSeries1() {
		return series1;
	}



	public void setSeries1(XYChart.Series<String, Number> series1) {
		this.series1 = series1;
	}

	public XYChart.Series<String, Number> getSeries2() {
		return series2;
	}

	public void setSeries2(XYChart.Series<String, Number> series2) {
		this.series2 = series2;
	}

	public ObservableList<ChartDataModel> getChartPosDataModelList() {
		return chartPosDataModelList;
	}


	public void setChartPosDataModelList(
			ObservableList<ChartDataModel> chartPosDataModelList) {
		this.chartPosDataModelList = chartPosDataModelList;
	}


	public LineChart<String, Number> getLineChart(TreeMap<Double,PosTvector> realPosDataMap1) {
		return lineChart;
	}


	public void setLineChart(LineChart<String, Number> lineChart) {
		this.lineChart = lineChart;
	}


	public LineChart<String, Number> getLineChart() {
		return lineChart;
	}


	public void setupSlider(int size) {
    	Platform.runLater(new Runnable() {

			@Override
			public void run() {

		    	verticalMarkerSlider.setMin(start);
//		    	areaChartController.verticalMarkerSlider.setMin(start);
		    	verticalMarkerSlider.setMax(end);
//		    	areaChartController.verticalMarkerSlider.setMax(end);


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
//						areaChartController.verticalMarkerSlider.increment();


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

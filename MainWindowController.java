package application;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.layers.RenderableLayer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Map.Entry;
import chartmodels.ChartDataModel;
import chartmodels.ChartPosDataModel;
import ds.airbus.jms.util.PosTvector;
import ds.airbus.jms.util.PosVector;
import messagereader.TimeMappedEphemerisReader;
import ds.airbus.stvt.worldwindow.model.SpookDataModel;
import ds.airbus.stvt.worldwindow.model.SpookPosDataModel;
//import ds.airbus.stvt.controller.MenuBarPaneController;
import ds.airbus.stvt.worldwindow.panel.WorldWindowPanel;
import ds.airbus.stvt.worldwindow.renderable.SpookPositionRenderable;
import ds.airbus.stvt.worldwindow.util.InsertLayer;
import globalmodels.GlobalDataModel;
import globalmodels.GlobalPosDataModel;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;

public class MainWindowController implements Initializable{
	private static File initDir = new File(System.getProperty("user.dir"));
	private ObservableList<SpookDataModel> spookDataModelList = FXCollections.observableArrayList();
	private ObservableList<ChartDataModel> chartPosDataModelList = FXCollections.observableArrayList();
	private ObservableList<GlobalDataModel> globalPosDataModelList = FXCollections.observableArrayList();
	private LineGraphController lineGraphController;
	private AreaChartController areaChartController;
	private BarChartController barChartController;
	private ScatterchartController scatterChartController;
	private WorldWindowPanel ww3DPanel;
	private double start = 0;
	private double end = 0;
	private Timer timer;
	private boolean play = false;
	private AnchorPane linesGraph;
	private AnchorPane areaChart;
	private AnchorPane barChart;
	private AnchorPane scatterchart;
	private SpookPositionRenderable spookObserverRenderable;
	private SpookPositionRenderable spookObjectRealPosRenderable;
	private boolean spookObjectRealPosRenderableLayerSet = false;
	public MainWindowController() {}
	double[] playSpeeds = new double[] {10, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 30000};
    private int currentPlaySpeedIndex = 3;
	private double period = playSpeeds[currentPlaySpeedIndex];
	@FXML
	private Button resetBtn;
	@FXML
	private Button stopBtn;
	@FXML
	private Button startBtn;
	@FXML
    public Slider timeSlider;
	@FXML
	private VBox topVBox;
	@FXML
	private VBox bottomVBox;
	@FXML
	private StackPane leftMainStackPane;
	@FXML
	private SwingNode swingNode3D;
	@FXML
	private StackPane rightMainStackPane;
	@FXML
	private TableView<SpookDataModel> spookFilesTable;
	@FXML
	private TableColumn<SpookDataModel, String> displayNameColumn;
	@FXML
	private TableColumn<SpookDataModel, String> spookFileColumn;
	@FXML
	public TableView<ChartDataModel> spookFilesTable1;
	@FXML
	public TableColumn<ChartDataModel, String> displayNameColumn1;
	@FXML
	public TableColumn<ChartDataModel, String> spookFileColumn1;
	@FXML
	public TableView<SpookDataModel> spookFilesTable2;
	@FXML
	public TableColumn<SpookDataModel, String> displayNameColumn2;
	@FXML
	public TableColumn<SpookDataModel, String> spookFileColumn2;
	@FXML
	private Button forwardBtn;
	@FXML
	private Button backwardBtn;
	@FXML
	void handleCloseRequest(ActionEvent event)
	{
	 System.exit(0);
	}
	@FXML
	void handleClearData(ActionEvent event) {
		if(play==true || play==false){
			timeSlider.setValue(start);
			lineGraphController.verticalMarkerSlider.setValue(start);
			areaChartController.verticalMarkerSlider.setValue(start);
	      timer.cancel();
	      lineGraphController.timer.cancel();
	      areaChartController.timer.cancel();
	spookDataModelList.clear();
	chartPosDataModelList.clear();
	lineGraphController.lineChart.getData().clear();
	areaChartController.areaChart.getData().clear();
	play = false;
	}
		timeSlider.setValue(start);
		lineGraphController.verticalMarkerSlider.setValue(start);
		areaChartController.verticalMarkerSlider.setValue(start);
	      timer.cancel();
	      lineGraphController.timer.cancel();
	      areaChartController.timer.cancel();
	spookDataModelList.clear();
	chartPosDataModelList.clear();
	lineGraphController.lineChart.getData().clear();
	areaChartController.areaChart.getData().clear();

	 }

	@FXML
    void handleStartBtn(ActionEvent event) {
		setupTimer((long) period);
	lineGraphController.setupTimer((long) period);
	areaChartController.setupTimer((long) period);
		play = true;
		startBtn.setDisable(true);
		stopBtn.setDisable(false);
		resetBtn.setDisable(false);
		lineGraphController.lineChart.getData().addAll(lineGraphController.getSeries(),lineGraphController.getSeries1(),lineGraphController.getSeries2() );

//		lineGraphController.getAreaChartController().areaChart.getData().addAll(lineGraphController.getSeries(),lineGraphController.getSeries1(),lineGraphController.getSeries2() );
	}
	 @FXML
	    void handleBackwardBtn(ActionEvent event) {
	    	currentPlaySpeedIndex++;
	    	updateTimer();
	    }

	    @FXML
	    void handleForwardBtn(ActionEvent event) {
	    	currentPlaySpeedIndex--;
	        updateTimer();
	    }
	    private void updateTimer() {
			if(currentPlaySpeedIndex < 0) {
				currentPlaySpeedIndex = 0;
				forwardBtn.setDisable(true);
			} else {
				forwardBtn.setDisable(false);
			}
			if(currentPlaySpeedIndex > (playSpeeds.length-1)) {
			   	currentPlaySpeedIndex = (playSpeeds.length-1);
			   	backwardBtn.setDisable(true);
			} else {
				backwardBtn.setDisable(false);
			}

	    	period = playSpeeds[currentPlaySpeedIndex];
	    	if(play == true) {
	    		timer.cancel();
	    		setupTimer((long) period);
	    	}

	    }

	@FXML
	 TableColumn<SpookDataModel, Boolean> showColumn;
	@FXML
	public TableColumn<ChartDataModel, Boolean> showColumn1;
	@FXML
	public TableColumn<SpookDataModel, Boolean> showColumn2;
	@FXML
    void set2DWorldWindow(ActionEvent event) {

		ww3DPanel.enableFlatGlobe(true);
    }
	 @FXML
	    void set3DWorldWindow(ActionEvent event) {

			ww3DPanel.enableFlatGlobe(false);
	    }
	@FXML
    void handleStopBtn(ActionEvent event) {
		if(play == true) {
    		timer.cancel();
    		 lineGraphController.timer.cancel();
    		 areaChartController.timer.cancel();
    		startBtn.setDisable(false);
    		stopBtn.setDisable(true);
    		resetBtn.setDisable(false);
    		play = false;
    	}
    }

	@FXML
    void handleResetBtn(ActionEvent event) {
	if(play == true || ((!startBtn.isDisable())&& stopBtn.isDisable())){
		timer.cancel();
		 lineGraphController.timer.cancel();
		 areaChartController.timer.cancel();
		timeSlider.setValue(start);
		lineGraphController.verticalMarkerSlider.setValue(start);
		areaChartController.verticalMarkerSlider.setValue(start);
		startBtn.setDisable(false);
		stopBtn.setDisable(true);
		resetBtn.setDisable(true);
		play = false;
	}
	}
    @FXML
	public void handleLineGraph(ActionEvent event) {
		getRightMainStackPane().getChildren().clear();
		getRightMainStackPane().getChildren().add(getLinesGraph());
	}

    @FXML
    public void handleAreaChart(ActionEvent event){
    	getRightMainStackPane().getChildren().clear();
		getRightMainStackPane().getChildren().add(getAreaChart());
//		 handleStartBtn(event); {
		if(play == true) {
			setupTimer((long) period);
			areaChartController.setupTimer((long) period);
				play = true;
				startBtn.setDisable(true);
				stopBtn.setDisable(false);
				resetBtn.setDisable(false);
		areaChartController.areaChart.getData().addAll(lineGraphController.getSeries(),lineGraphController.getSeries1(),lineGraphController.getSeries2() );
		}
//		 }
    }

    @FXML
    public void handleBarChart(ActionEvent event){
    	getRightMainStackPane().getChildren().clear();
		getRightMainStackPane().getChildren().add(getBarChart());
		barChartController.barChart.getData().addAll(lineGraphController.getSeries(),lineGraphController.getSeries1(),lineGraphController.getSeries2() );
    }

    @FXML
    public void handleScatterChart(ActionEvent event){
    	getRightMainStackPane().getChildren().clear();
		getRightMainStackPane().getChildren().add(getScatterchart());
		scatterChartController.scatterChart.getData().addAll(lineGraphController.getSeries(),lineGraphController.getSeries1(),lineGraphController.getSeries2() );
    }

    @FXML
    public void handleRealPosGlobes(ActionEvent event) {
//    	timeSlider.valueProperty().bindBidirectional(lineGraphController.verticalMarkerSlider.valueProperty());
		FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Object's Real Position File(s)");
        fileChooser.setInitialDirectory(initDir);
        List<File> files =fileChooser.showOpenMultipleDialog(new Stage());

        if (files != null) {
        	initDir = files.get(0).getParentFile();
        	new Thread(new Runnable() {

				@Override
				public void run() {
        	for(File file : files) {
//    	realPosFilePath.setText(file.toPath().toString());
	        	TimeMappedEphemerisReader er = new TimeMappedEphemerisReader();
		        try {
					er.setContent(new String(Files.readAllBytes(file.toPath())));
					//System.out.println(new String(Files.readAllBytes(file.toPath())));
				} catch (IOException e) {
					e.printStackTrace();
				}

		        TreeMap<Double, PosVector> realPosDataMap = er.readObjectRealPosEphemeris1();

		        String name = (file.getName().split("_")[1]).split("\\.")[0] + " (Positions)";
		        SpookPosDataModel posDataModel = new SpookPosDataModel(name, file.toPath().toString(), true, realPosDataMap);
//		        GlobalPosDataModel posDataModel1 = new GlobalPosDataModel(name, file.toPath().toString(),  true, realPosDataMap);
		        posDataModel.showProperty().addListener(new ChangeListener<Boolean>() {
					@Override
		            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {

						getWWd().redraw();

					}

		        });

		        spookDataModelList.add(posDataModel);


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
		        }

				if(!spookObjectRealPosRenderableLayerSet) {
					setupSpookObjectRealPosRenderableLayer();

					timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
						@Override
						public void changed(ObservableValue<? extends Number> observable,
								Number oldValue, Number newValue) {
								double toKey = Math.round(timeSlider.getValue()*100000000.0)/100000000.0;
								if (toKey == end)
									stop();
								spookObjectRealPosRenderable.setCurrentTime(toKey);
								getWWd().redraw();
//						timeSlider.valueProperty().bindBidirectional(lineGraphController.verticalMarkerSlider.valueProperty());
							}
					});

				}
				spookObjectRealPosRenderable.setStartTime(start);
		        spookObjectRealPosRenderable.appendPosData(posDataModel);
//		        spookFilesTable.setItems(spookDataModelList);

        	}

    }
        	}).start();

        }

    }
    @FXML
   public void handleRealPosGlobe(ActionEvent event) {
//    	timeSlider.valueProperty().bindBidirectional(lineGraphController.verticalMarkerSlider.valueProperty());
		getRightMainStackPane().getChildren().clear();
		getRightMainStackPane().getChildren().add(getLinesGraph());
        List<File> files =lineGraphController.btn(event);
//        List<File> filess= areaChartController.btn(event);

        if (files != null) {
        	initDir = files.get(0).getParentFile();
        	new Thread(new Runnable() {

				@Override
				public void run() {
        	for(File file : files) {
//    	realPosFilePath.setText(file.toPath().toString());
	        	TimeMappedEphemerisReader er = new TimeMappedEphemerisReader();
		        try {
					er.setContent(new String(Files.readAllBytes(file.toPath())));
					//System.out.println(new String(Files.readAllBytes(file.toPath())));
				} catch (IOException e) {
					e.printStackTrace();
				}

		        TreeMap<Double, PosVector> realPosDataMap = er.readObjectRealPosEphemeris1();

		        String name = (file.getName().split("_")[1]).split("\\.")[0] + " (Positions)";
		        SpookPosDataModel posDataModel = new SpookPosDataModel(name, file.toPath().toString(), true, realPosDataMap);
//		        GlobalPosDataModel posDataModel1 = new GlobalPosDataModel(name, file.toPath().toString(),  true, realPosDataMap);
		        posDataModel.showProperty().addListener(new ChangeListener<Boolean>() {
					@Override
		            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {

						getWWd().redraw();
						if(play==true || play==false){
							timer.cancel();
				    		 lineGraphController.timer.cancel();
				    		startBtn.setDisable(false);
				    		stopBtn.setDisable(true);
				    		resetBtn.setDisable(false);

					lineGraphController.lineChart.getData().clear();

					play = false;
					}else{

		          lineGraphController.lineChart.getData().addAll(lineGraphController.getSeries(),lineGraphController.getSeries1(),lineGraphController.getSeries2() );

					}
					}

		        });

		        spookDataModelList.add(posDataModel);


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
		        }

				if(!spookObjectRealPosRenderableLayerSet) {
					setupSpookObjectRealPosRenderableLayer();

					timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
						@Override
						public void changed(ObservableValue<? extends Number> observable,
								Number oldValue, Number newValue) {
								double toKey = Math.round(timeSlider.getValue()*100000000.0)/100000000.0;
								if (toKey == end)
									stop();
								spookObjectRealPosRenderable.setCurrentTime(toKey);
								getWWd().redraw();
						timeSlider.valueProperty().bindBidirectional(lineGraphController.verticalMarkerSlider.valueProperty());
//					timeSlider.valueProperty().bindBidirectional(areaChartController.verticalMarkerSlider.valueProperty());
							}
					});

				}
				spookObjectRealPosRenderable.setStartTime(start);
		        spookObjectRealPosRenderable.appendPosData(posDataModel);

//		        spookFilesTable.setItems(spookDataModelList);

        	}

    }
        	}).start();

        }

        }

	public void setupSlider(int size){
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				timeSlider.setMin(start);
				timeSlider.setMax(end);

				double increment =  0.0000115740741 * 60;
				timeSlider.setBlockIncrement(increment);

				timeSlider.setMajorTickUnit(end-start);

				timeSlider.setMinorTickCount((int)((end-start)/increment)-2);

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
						timeSlider.increment();

					}

				});

			}

		}, 0, period);
	}

    public void handleRealPosGraph(ActionEvent event) {
//    	timeSlider.valueProperty().bindBidirectional(lineGraphController.verticalMarkerSlider.valueProperty());
    	getRightMainStackPane().getChildren().clear();
		getRightMainStackPane().getChildren().add(getLinesGraph());
		 List<File> files =lineGraphController.btn(event);
		//lineGraphController.btn(event);
		if (files != null) {
        	initDir = files.get(0).getParentFile();
        	new Thread(new Runnable() {

				@Override
				public void run() {
        	for(File file : files) {

        	        	TimeMappedEphemerisReader er = new TimeMappedEphemerisReader();
        		        try {
        					er.setContent(new String(Files.readAllBytes(file.toPath())));
        					//System.out.println(new String(Files.readAllBytes(file.toPath())));
        				} catch (IOException e) {
        					e.printStackTrace();
        				}
        		TreeMap<Double, PosTvector> realPosDataMap = er.readObjectRealPosEphemeris2();
        		String name = (file.getName().split("_")[1]).split("\\.")[0] + " (Positions)";
		        ChartPosDataModel posDataModel1 = new ChartPosDataModel(name, file.toPath().toString(),  true, realPosDataMap);

		        posDataModel1.showProperty().addListener(new ChangeListener<Boolean>(){
					@Override
					public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
						if(play==true || play==false){
							timer.cancel();
				    		 lineGraphController.timer.cancel();
				    		 areaChartController.timer.cancel();
				    		startBtn.setDisable(false);
				    		stopBtn.setDisable(true);
				    		resetBtn.setDisable(false);

					lineGraphController.lineChart.getData().clear();
					areaChartController.areaChart.getData().clear();

					play = false;
					}else{

		          lineGraphController.lineChart.getData().addAll(lineGraphController.getSeries(),lineGraphController.getSeries1(),lineGraphController.getSeries2() );
		          areaChartController.areaChart.getData().addAll(lineGraphController.getSeries(),lineGraphController.getSeries1(),lineGraphController.getSeries2() );
					}
					}
		        });

		        if(start == 0 && end == 0){
		        	start = realPosDataMap.firstKey();
		        	end = realPosDataMap.lastKey();
		        	setupSlider(realPosDataMap.size());
		        }else if(realPosDataMap.firstKey() < start){
		        	start = realPosDataMap.firstKey();
		        	setupSlider(realPosDataMap.size());
		        }else if(realPosDataMap.lastKey() > end){
		        	end = realPosDataMap.lastKey();
		        	setupSlider(realPosDataMap.size());
		        }


		        timeSlider.valueProperty().addListener(new ChangeListener<Number>(){

					@Override
					public void changed(ObservableValue<? extends Number> observable,
							Number oldValue, Number newValue) {
						double toKey = Math.round(timeSlider.getValue()*100000000.0)/100000000.0;
//						if(toKey == end)
							//stop();

//						timeSlider.valueProperty().bindBidirectional(lineGraphController.verticalMarkerSlider.valueProperty());
					}
		        });
		        chartPosDataModelList.add(posDataModel1);
        	}
        	}

        	}).start();
		}
		}
    private void setupSpookObjectRealPosRenderableLayer() {
		RenderableLayer renderableLayer = new RenderableLayer();
		RenderableLayer surfaceShapesRenderableLayer = new RenderableLayer();
		spookObjectRealPosRenderable = new SpookPositionRenderable(getWw3DPanel(), surfaceShapesRenderableLayer);
		renderableLayer.addRenderable(spookObjectRealPosRenderable);
		renderableLayer.setName("Spook Object's Real Position Layer");
		renderableLayer.setEnabled(true);
		surfaceShapesRenderableLayer.setEnabled(true);
		InsertLayer.insertBeforePlacenames(ww3DPanel.getWwd(), renderableLayer);
		//InsertLayer.insertAfterCompass(ww3DPanel.getWwd(), surfaceShapesRenderableLayer);

		spookObjectRealPosRenderableLayerSet = true;
	}
private void stop() {
	if(play == true) {
		timer.cancel();
		startBtn.setDisable(false);
		stopBtn.setDisable(true);
		resetBtn.setDisable(false);
		play = false;
	}
}
 ObservableList<SpookDataModel> getSpookDataModelList() {
		return spookDataModelList;
	}

	public void setSpookDataModelList(
			ObservableList<SpookDataModel> spookDataModelList) {
		this.spookDataModelList = spookDataModelList;
	}

	public ObservableList<ChartDataModel> getChartPosDataModel() {
		return chartPosDataModelList;
	}

	public void setChartPosDataModel(
			ObservableList<ChartDataModel> chartPosDataModel) {
		this.chartPosDataModelList = chartPosDataModel;
	}

	public TableView<SpookDataModel> getSpookFilesTable() {
		return spookFilesTable;
	}

	public void setSpookFilesTable(TableView<SpookDataModel> spookFilesTable) {
		this.spookFilesTable = spookFilesTable;
	}

	public SpookPositionRenderable getSpookObjectRealPosRenderable() {
		return spookObjectRealPosRenderable;
	}
	public void setSpookObjectRealPosRenderable(
			SpookPositionRenderable spookObjectRealPosRenderable) {
		this.spookObjectRealPosRenderable = spookObjectRealPosRenderable;
	}
	public Button getResetBtn() {
		return resetBtn;
	}
	public void setResetBtn(Button resetBtn) {
		this.resetBtn = resetBtn;
	}
	public Button getStopBtn() {
		return stopBtn;
	}
	public void setStopBtn(Button stopBtn) {
		this.stopBtn = stopBtn;
	}
	public Button getStartBtn() {
		return startBtn;
	}
	public void setStartBtn(Button startBtn) {
		this.startBtn = startBtn;
	}
	public Slider getTimeSlider() {
		return timeSlider;
	}
	public void setTimeSlider(Slider timeSlider) {
		this.timeSlider = timeSlider;
	}
	public TableView<ChartDataModel> getSpookFilesTable1() {
		return spookFilesTable1;
	}
	public void setSpookFilesTable1(TableView<ChartDataModel> spookFilesTable1) {
		this.spookFilesTable1 = spookFilesTable1;
	}
	public TableColumn<ChartDataModel, String> getDisplayNameColumn1() {
		return displayNameColumn1;
	}
	public void setDisplayNameColumn1(
			TableColumn<ChartDataModel, String> displayNameColumn1) {
		this.displayNameColumn1 = displayNameColumn1;
	}
	public TableColumn<ChartDataModel, String> getSpookFileColumn1() {
		return spookFileColumn1;
	}
	public void setSpookFileColumn1(
			TableColumn<ChartDataModel, String> spookFileColumn1) {
		this.spookFileColumn1 = spookFileColumn1;
	}
	public TableColumn<SpookDataModel, Boolean> getShowColumn() {
		return showColumn;
	}
	public void setShowColumn(TableColumn<SpookDataModel, Boolean> showColumn) {
		this.showColumn = showColumn;
	}
	public TableColumn<ChartDataModel, Boolean> getShowColumn1() {
		return showColumn1;
	}
	public void setShowColumn1(TableColumn<ChartDataModel, Boolean> showColumn1) {
		this.showColumn1 = showColumn1;
	}
	public TableColumn<SpookDataModel, String> getSpookFileColumn() {
		return spookFileColumn;
	}

	public AnchorPane getLinesGraph() {
		return linesGraph;
	}

	public void setSpookFileColumn(
			TableColumn<SpookDataModel, String> spookFileColumn) {
		this.spookFileColumn = spookFileColumn;
	}
	private Stage stage;
	@Override
	public void initialize(URL location, ResourceBundle arg1) {
		ww3DPanel = new WorldWindowPanel();
		this.swingNode3D.setContent(ww3DPanel);


    	timeSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
					//	double toKey = Math.round(timeSlider.getValue()*100000000.0)/100000000.0;

						}
					});
			}
		});
		initTable();
		initTableColumns();
	    onitTableColumns();
		onitTable();
//		globeTableColumns();
//		globalTable();
		FXMLLoader lineGraphLoader = new FXMLLoader(getClass().getResource("/application/LineGraph.fxml"));
		FXMLLoader areaChartLoader = new FXMLLoader(getClass().getResource("/application/AreaChart.fxml"));
		FXMLLoader barChartLoader = new FXMLLoader(getClass().getResource("/application/BarChart.fxml"));
		FXMLLoader scatterchartLoader = new FXMLLoader(getClass().getResource("/application/Scatterchart.fxml"));
		try{
			linesGraph = lineGraphLoader.load();
			lineGraphController = (LineGraphController)lineGraphLoader.getController();
			areaChart = areaChartLoader.load();
			areaChartController = (AreaChartController) areaChartLoader.getController();
			barChart = barChartLoader.load();
			barChartController = (BarChartController) barChartLoader.getController();
			scatterchart = scatterchartLoader.load();
			scatterChartController = (ScatterchartController) scatterchartLoader.getController();
		}catch (IOException e) {
			e.printStackTrace();

	}

}

	public WorldWindow getWWd() {
		return getWw3DPanel().getWwd();
	}

	public WorldWindowPanel getWw3DPanel() {
		return ww3DPanel;
	}

	public void setWw3DPanel(WorldWindowPanel ww3dPanel) {
		ww3DPanel = ww3dPanel;
	}

	public VBox getTopVBox() {
		return topVBox;
	}
	public void setTopVBox(VBox topVBox) {
		this.topVBox = topVBox;
	}
	public VBox getBottomVBox() {
		return bottomVBox;
	}
	public void setBottomVBox(VBox bottomVBox) {
		this.bottomVBox = bottomVBox;
	}
	public StackPane getLeftMainStackPane() {
		return leftMainStackPane;
	}
	public void setLeftMainStackPane(StackPane leftMainStackPane) {
		this.leftMainStackPane = leftMainStackPane;
	}
	public SwingNode getSwingNode3D() {
		return swingNode3D;
	}
	public void setSwingNode3D(SwingNode swingNode3D) {
		this.swingNode3D = swingNode3D;
	}
	public StackPane getRightMainStackPane() {
		return rightMainStackPane;
	}
	public AnchorPane getLinesGraph(ActionEvent event) {
		return linesGraph;
	}

	public void setLinesGraph(AnchorPane linesGraph) {
		this.linesGraph = linesGraph;
	}

	public void setRightMainStackPane(StackPane rightMainStackPane) {
		this.rightMainStackPane = rightMainStackPane;
	}
	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage primaryStage) {
		this.stage = primaryStage;

	}
	private void initTableColumns() {
		displayNameColumn.setCellValueFactory(new PropertyValueFactory<SpookDataModel, String>("name"));
		spookFileColumn.setCellValueFactory(new PropertyValueFactory<SpookDataModel, String>("fileName"));
		showColumn.setCellValueFactory(new PropertyValueFactory<SpookDataModel, Boolean>("show"));
		//showColumn.setCellFactory(cellFactory -> new CheckBoxTableCell<TrackBean, Boolean>());
		showColumn.setCellFactory(CheckBoxTableCell.forTableColumn(showColumn));

		showColumn.setEditable(true);
		displayNameColumn2.setCellValueFactory(new PropertyValueFactory<SpookDataModel, String>("name"));
		spookFileColumn2.setCellValueFactory(new PropertyValueFactory<SpookDataModel, String>("fileName"));
		showColumn2.setCellValueFactory(new PropertyValueFactory<SpookDataModel, Boolean>("show"));
		//showColumn.setCellFactory(cellFactory -> new CheckBoxTableCell<TrackBean, Boolean>());
		showColumn2.setCellFactory(CheckBoxTableCell.forTableColumn(showColumn2));

		showColumn2.setEditable(true);

	}
	private void initTable() {
		spookFilesTable.setItems(spookDataModelList);
		spookFilesTable2.setItems(spookDataModelList);
	}

	private void onitTableColumns(){
		displayNameColumn1.setCellValueFactory(new PropertyValueFactory<ChartDataModel, String>("name"));
		spookFileColumn1.setCellValueFactory(new PropertyValueFactory<ChartDataModel, String>("fileName"));
		showColumn1.setCellValueFactory(new PropertyValueFactory<ChartDataModel, Boolean>("show"));
		//showColumn.setCellFactory(cellFactory -> new CheckBoxTableCell<TrackBean, Boolean>());
		showColumn1.setCellFactory(CheckBoxTableCell.forTableColumn(showColumn1));

		showColumn1.setEditable(true);
	}

	private void onitTable() {
		spookFilesTable1.setItems(chartPosDataModelList);
	}
	/*private void globeTableColumns(){
		displayNameColumn2.setCellValueFactory(new PropertyValueFactory<GlobalDataModel, String>("name"));
		spookFileColumn2.setCellValueFactory(new PropertyValueFactory<GlobalDataModel, String>("fileName"));
		showColumn2.setCellValueFactory(new PropertyValueFactory<GlobalDataModel, Boolean>("show"));
		//showColumn.setCellFactory(cellFactory -> new CheckBoxTableCell<TrackBean, Boolean>());
		showColumn2.setCellFactory(CheckBoxTableCell.forTableColumn(showColumn2));

		showColumn2.setEditable(true);
	}

	private void globalTable() {
		spookFilesTable2.setItems(globalPosDataModelList);
	}

*/
	public boolean isSpookObjectRealPosRenderableLayerSet() {
		return spookObjectRealPosRenderableLayerSet;
	}

	public void setSpookObjectRealPosRenderableLayerSet(
			boolean spookObjectRealPosRenderableLayerSet) {
		this.spookObjectRealPosRenderableLayerSet = spookObjectRealPosRenderableLayerSet;
	}

	public AnchorPane getAreaChart() {
		return areaChart;
	}

	public void setAreaChart(AnchorPane areaChart) {
		this.areaChart = areaChart;
	}

	public AreaChartController getAreaChartController() {
		return areaChartController;
	}

	public void setAreaChartController(AreaChartController areaChartController) {
		this.areaChartController = areaChartController;
	}

	public BarChartController getBarChartController() {
		return barChartController;
	}

	public void setBarChartController(BarChartController barChartController) {
		this.barChartController = barChartController;
	}

	public AnchorPane getBarChart() {
		return barChart;
	}

	public void setBarChart(AnchorPane barChart) {
		this.barChart = barChart;
	}

	public AnchorPane getScatterchart() {
		return scatterchart;
	}

	public void setScatterchart(AnchorPane scatterchart) {
		this.scatterchart = scatterchart;
	}

	public ScatterchartController getScatterChartController() {
		return scatterChartController;
	}

	public void setScatterChartController(ScatterchartController scatterChartController) {
		this.scatterChartController = scatterChartController;
	}


}

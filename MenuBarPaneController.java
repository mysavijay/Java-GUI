package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import ds.airbus.stvt.worldwindow.panel.WorldWindowPanel;
import ds.airbus.stvt.worldwindow.setup.Setup;

public class MenuBarPaneController implements Initializable{

	//private SpookPaneController spookPaneController;
	private MainWindowController mainWindowController;
    private WorldWindowPanel worldWindowPanel;
    private final Stage dialog = new Stage();


	private Stage stage;




    @FXML
    void handleRealPosGraph(ActionEvent event){
    	mainWindowController.handleRealPosGlobe(event);
    }

    @FXML
    void handleCloseRequest(ActionEvent event) {
		System.exit(0);
    }

    @FXML
    void set2DWorldWindow() {
    	worldWindowPanel.enableFlatGlobe(true);
    }

    @FXML
    void set3DWorldWindow(ActionEvent event) {
    	worldWindowPanel.enableFlatGlobe(false);
    }


    @FXML
    void handleInternetSetup(ActionEvent event) {
		dialog.show();
    }

	public WorldWindowPanel getWorldWindowPanel() {
		return worldWindowPanel;
	}

	public void setWorldWindowPanel(WorldWindowPanel worldWindowPanel) {
		this.worldWindowPanel = worldWindowPanel;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {


			}






	}


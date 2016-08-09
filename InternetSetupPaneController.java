package application;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.util.Logging;

import java.net.URL;
import java.util.ResourceBundle;

import ds.airbus.stvt.worldwindow.setup.Setup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class InternetSetupPaneController implements Initializable{

    @FXML
    private CheckBox enableInternetChkBox;

    @FXML
    private CheckBox enableProxyChkBox;

    @FXML
    private TextField portTxtFld;

    @FXML
    private TextField hostTxtFld;

	private Stage stage;
	
	private String proxyType = "Proxy.Type.Http";
	private String host = "wproxy-fdh.de.astrium.corp";
	private String port = "8080";

    @FXML
    void handleEnableInternet(ActionEvent event) {
    	Setup.setOfflineMode(!enableInternetChkBox.isSelected());

    	if(enableInternetChkBox.isSelected()) {
    		enableProxyChkBox.setDisable(false);    		
    	}
    	else {
    		enableProxyChkBox.setSelected(false);
    		enableProxyChkBox.setDisable(true);
    	}
    	
    	handleEnableProxy(event);
    }

    @FXML
    void handleEnableProxy(ActionEvent event) {
    	if(enableProxyChkBox.isSelected()) {
    		portTxtFld.setDisable(false);
    		hostTxtFld.setDisable(false);
    		
    		Configuration.setValue(AVKey.URL_PROXY_TYPE, proxyType);
    	} else {
    		portTxtFld.setDisable(true);
    		hostTxtFld.setDisable(true);
    		
    		new Thread(new Runnable() {
				
				@Override
				public void run() {
		    		Configuration.setValue(AVKey.URL_PROXY_TYPE, "");
		    		Configuration.setValue(AVKey.URL_PROXY_HOST, "");
		    		Configuration.setValue(AVKey.URL_PROXY_PORT, "");					
				}
			}).start();
    	}
    }

    @FXML
    void handleOkBtn(ActionEvent event) {
    	getStage().hide();
    	
    	if(enableProxyChkBox.isSelected()) {
    		new Thread(new Runnable() {
				
				@Override
				public void run() {

		    		Configuration.setValue(AVKey.URL_PROXY_HOST, hostTxtFld.getText());
		    		Configuration.setValue(AVKey.URL_PROXY_PORT, portTxtFld.getText());
				}
			}).start();

    	}
    	
		Logging.logger().info("Network Available: " + (!WorldWind.getNetworkStatus().isNetworkUnavailable()));		
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		Setup.setOfflineMode(true);
		
		enableInternetChkBox.setSelected(!WorldWind.getNetworkStatus().isOfflineMode());
		enableProxyChkBox.setDisable(true);
		portTxtFld.setDisable(true);
		hostTxtFld.setDisable(true);
		
		hostTxtFld.setText(host);
		portTxtFld.setText(port);
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

}

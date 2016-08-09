package application;
import java.util.Locale;

import ds.airbus.stvt.worldwindow.setup.Setup;
import javafx.application.Application;
import javafx.application.Preloader.ProgressNotification;
import javafx.application.Preloader.StateChangeNotification;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;


public class Main extends Application {




	MainWindowController mainWindowController;
	LineGraphController lineGraphController;
	Thread th;
	BooleanProperty ready = new SimpleBooleanProperty(false);

    private void longStart() {
        //simulate long init in background
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
            	while(!ready.getValue()){
	                notifyPreloader(new ProgressNotification(0.5));
	                Thread.sleep(20000);
            	}
                return null;
            }
        };
        th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

	@Override
	public void start(Stage primaryStage) {
		longStart();

		try {
			Locale.setDefault(new Locale("en", "EN"));
			FXMLLoader mainWindowLoader = new FXMLLoader(getClass().getResource("/application/MainWindow.fxml"));
			//BorderPane mainWindowLoader = (BorderPane)FXMLLoader.load(getClass().getResource("/application/MainWindow.fxml"));
			BorderPane root = (BorderPane)mainWindowLoader.load();

			mainWindowController = (MainWindowController) mainWindowLoader.getController();


			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Satellite Data Visualization Tool");
			primaryStage.getIcons().setAll(new Image("/res/airbus-logo.png"));
			primaryStage.show();

			if(primaryStage.isShowing()){
				ready.setValue(Boolean.TRUE);
				th.interrupt();
				notifyPreloader(new StateChangeNotification(StateChangeNotification.Type.BEFORE_START));
			}


			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent event) {
					Setup.shutdownWorldWind();

					System.exit(0);
				}
			});

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}

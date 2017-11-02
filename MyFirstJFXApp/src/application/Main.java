package application;
	
import org.opencv.core.Core;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

/*
 * The main class for a JavaFX application, it creates and handle the main
 * window with its resources(style,graphics,etc..).
 * 
 *这个JavaFX application 的主类，它用一系列资源创建处理了一个主窗口。
 * @
 * */
public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			//load the FXML resource
			FXMLLoader loader = new FXMLLoader(getClass().getResource("FirstJFX.fxml"));
			//store the root element so that the controllers can use it
			BorderPane rootElement = (BorderPane) loader.load();
			//create and style a scene
			Scene scene = new Scene(rootElement,800,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			//create the stage with the given title and the previously created
			//scene
			primaryStage.setTitle("JavaFX meets OpenCV");
			primaryStage.setScene(scene);
			//show the GUI
			primaryStage.show();
			
			//set the proper behavior on closing the application
			FXController controller = loader.getController();
			primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					controller.setClosed();
				}
			}));
			/*BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("FirstJFX.fxml"));
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();*/
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/*
	 * For launching the application....
	 * application运行
	 * @param args
	 * 			optional params
	 * */
	public static void main(String[] args) {
		/*launch(args);*/
		//load the native OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		launch(args);
	}
}

/*
 * Starts the application. Calls welcome window welcome.Welcome.fxml
 *
 * @author Petar Deveric
 */

package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;


public class Main extends Application{
	
	@Override
	public void start(Stage primaryStage) {
		try {
                    primaryStage.setTitle("School Timetable");
                    Parent root = FXMLLoader.load(getClass().getResource("/welcome/Welcome.fxml"));
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(getClass().getResource("/general/General.css").toExternalForm());
                    primaryStage.setScene(scene);
                    primaryStage.sizeToScene();
                    primaryStage.centerOnScreen();
                    primaryStage.show();
		} catch(Exception e) {
                    System.err.println("Exception in class Main, method start()");
                    e.getMessage();
		}
	}
        
	public static void main(String[] args) {
		launch(args);
	}
}
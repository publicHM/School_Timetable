/*
 * Controller class for TimetablesList.fxml. Provides methods for showing list of saved timetables
 * loading data from old timetable.
 *
 * @author Petar Deveric
 */

package checktable;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class TimetablesListController implements Initializable {
    
    private final String timetableName;
    private final String tableName;
    private ObservableList tables;
    private final Stage primaryStage;
    
    @FXML
    private ListView timetablesListView;
    
    public TimetablesListController(Stage primaryStage, String timetableName, String tableName){
        this.timetableName = timetableName;
        this.tableName = tableName;
        this.primaryStage = primaryStage;
    }
        
    //Show ListView items
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tables = FXCollections.observableArrayList();
        getTimetables(tables);
    }
    
    private void getTimetables(ObservableList tables){
        String sqlquery = "SELECT Names FROM TimetablesNames ORDER BY Names ASC";
        try(Connection conn = DriverManager.getConnection("jdbc:derby:TableDB");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlquery);){
            while(rs.next()){
                tables.add(rs.getString(1));
            }
        }
        catch(SQLException e){
            System.err.println("SQLException in SavedTimetablesController, method getTimetables()");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        timetablesListView.setItems(tables);
    }
    
    @FXML
    private void cancel (Event event){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.hide();
    }
    
    @FXML
    private void load (Event event){
        try{String tempTimetable = timetablesListView.getSelectionModel().getSelectedItem().toString();
        
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/checktable/CheckTable.fxml"));

            loader.setControllerFactory((Class<?> passedController) -> {
                    CheckTableController controller = new CheckTableController(timetableName, tableName, tempTimetable);
                    return controller ;
                    }
            );

            Scene scene = new Scene((Pane) loader.load());
            scene.getStylesheets().add(getClass().getResource("/general/GeneralTwo.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
        }
        catch(Exception e){
            System.err.println("Exception in class SavedTimetablesController, method loadExistingTableWindow()");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        cancel (event);
    }
    
    @FXML
    private void mouseDoubleClicked(MouseEvent mouseEvent){
        if(mouseEvent.getClickCount() == 2){
            load (mouseEvent);
        }
    }
}
/*
 * Controller class for AddTables.fxml. Provides methods for showing ListView with tables names
 * (Classes, Courses or Professors) tabels, colling the class for updating them and calling
 * the class for generating new timetable
 *
 * @author Petar Deveric
 */

package addtables;

import checktable.CheckTableController;
import finaltable.FinalTimetableController;
import general.SetNewStage;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

public class AddTablesController extends SetNewStage implements Initializable{
    
    private final String timetableName;
    private ObservableList tables;
    
    public AddTablesController(String name){
        this.timetableName = name;
    }
    
    @FXML
    private ListView tablesListView;
    
    //Show ListView items
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tables = FXCollections.observableArrayList();
        String[] names = {"Classes", "Courses", "Professors"};
        tables.addAll(names);
        tablesListView.setItems(tables);
    }

    @FXML
    private void updateTable(Event event){
        try{// Get selected table from ListView
            String table = getSelectedTable();
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/checktable/CheckTable.fxml"));

            loader.setControllerFactory((Class<?> controllerClass) -> {
                if (controllerClass == CheckTableController.class) {
                    CheckTableController controller = new CheckTableController(timetableName, table);
                    return controller;
                } else {
                    try {
                        return controllerClass.newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            
            Scene scene = new Scene((Pane) loader.load());
            scene.getStylesheets().add(getClass().getResource("/general/GeneralTwo.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        }
        catch(Exception e){
            System.err.println("Exception in class AddTablesController, method updateTable()");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    private String getSelectedTable(){
        String item = tablesListView.getSelectionModel().getSelectedItem().toString();
        if(item.equals("Courses"))
            return "AllCourses";
        else if(item.equals("Professors"))
            return "AllProfessors";
        else
            return "AllClasses";
    }
        
    @FXML
    private void generateNewTimetable(ActionEvent event){
        GenerateTimetable gt = new GenerateTimetable(timetableName);
        gt.generateNewTimetable();
        setNewStageWithPassedValue(event, "/finaltable/FinalTimetable.fxml", FinalTimetableController.class, 
                timetableName);
    }
    
    @FXML
    private void goBack(ActionEvent event){
        setNewStage(event, "/savedtimetables/SavedTimetables.fxml");
    }
    
    @FXML
    private void goForward(ActionEvent event){
        setNewStageWithPassedValue(event, "/finaltable/FinalTimetable.fxml", FinalTimetableController.class, 
                timetableName);
    }
    
    @FXML
    private void mouseDoubleClicked(MouseEvent mouseEvent){
        if(mouseEvent.getClickCount() == 2){
                updateTable(mouseEvent);
        }
    }
}
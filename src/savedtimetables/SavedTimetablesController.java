/*
 * Controller for SavedTimetables.fxml. Provides methods to view old timetable, delete old timetables and load data to 
 * modify list of three tables (Classes, Courses and Professors)
 *
 * @author Petar Deveric
 */

package savedtimetables;

import addtables.AddTablesController;
import finaltable.FinalTimetableController;
import general.SetNewStage;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class SavedTimetablesController extends SetNewStage implements Initializable {
    
    private ObservableList tables;
    private String selectedTimetable;
    
    @FXML
    private ListView timetables_listview;
        
    //Show ListView items
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tables = FXCollections.observableArrayList();
        getTimetables(tables);
        timetables_listview.setItems(tables);
    }
    
    public void getTimetables(ObservableList tables){
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
    }
    
    @FXML
    private void viewTimetable(Event event){               
        // Get selected timetable from ListView
        selectedTimetable = timetables_listview.getSelectionModel().getSelectedItem().toString();
        setNewStageWithPassedValue(event, "/finaltable/FinalTimetable.fxml", FinalTimetableController.class, 
                selectedTimetable);
    }
    
    @FXML
    private void updateTimetable(ActionEvent event){
        selectedTimetable = timetables_listview.getSelectionModel().getSelectedItem().toString();
        setNewStageWithPassedValue(event, "/addtables/AddTables.fxml", AddTablesController.class, selectedTimetable);
    }
    
    @FXML
    private void goBack(ActionEvent event){
        setNewStage(event, "/welcome/Welcome.fxml");
    }
    
    @FXML
    private void goForward(ActionEvent event){
        selectedTimetable = timetables_listview.getSelectionModel().getSelectedItem().toString();
        setNewStageWithPassedValue(event, "/finaltable/FinalTimetable.fxml", FinalTimetableController.class, 
                selectedTimetable);
    }
    
    @FXML
    private void deleteTimetable(ActionEvent event){
        // Remove timetable from database
        selectedTimetable = timetables_listview.getSelectionModel().getSelectedItem().toString();
        try(Connection conn = DriverManager.getConnection("jdbc:derby:TableDB");
            PreparedStatement psPT = conn.prepareStatement("DELETE FROM ProfessorsTimetables WHERE Timetable = ?");
            PreparedStatement psCT = conn.prepareStatement("DELETE FROM ClassesTimetables WHERE Timetable = ?");
            PreparedStatement psCourse = conn.prepareStatement("DELETE FROM AllCourses WHERE Timetable = ?");
            PreparedStatement psProf = conn.prepareStatement("DELETE FROM AllProfessors WHERE Timetable = ?");
            PreparedStatement psClass = conn.prepareStatement("DELETE FROM AllClasses WHERE Timetable = ?");
            PreparedStatement psTN = conn.prepareStatement("DELETE FROM TimetablesNames WHERE Names = ?");){
                psPT.setString(1,selectedTimetable);
                psPT.executeUpdate();
                psCT.setString(1,selectedTimetable);
                psCT.executeUpdate();
                psCourse.setString(1,selectedTimetable);
                psCourse.executeUpdate();
                psProf.setString(1,selectedTimetable);
                psProf.executeUpdate();
                psClass.setString(1,selectedTimetable);
                psClass.executeUpdate();
                psTN.setString(1,selectedTimetable);
                psTN.executeUpdate();
        }
        catch(SQLException e){
            System.err.println("SQLException in SavedTimetablesController, method deleteTimetable()");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        catch(Exception e){
            System.err.println("Exception in SavedTimetablesController, method deleteTimetable()");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
         
        //Remove database from selected ObservableList
        int selectedIndex = timetables_listview.getSelectionModel().getSelectedIndex();
        tables.remove(selectedIndex);
    }

    @FXML
    private void mouseDoubleClicked(MouseEvent mouseEvent){
        if(mouseEvent.getClickCount() == 2){
                viewTimetable(mouseEvent);
        }
    }
}
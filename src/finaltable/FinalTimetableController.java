/*
 * Helper class for FinalTimetable.fxml. Provides methods for showing timetables for all classes and all professors. 
 * Provides method for saving timetable as CSV.
 *
 * @author Petar Deveric
 */

package finaltable;

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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;

public class FinalTimetableController extends SetNewStage implements Initializable {
    
    private ObservableList rows;
    private final String timetableName;
    
    public FinalTimetableController (String timetableName){
        this.timetableName = timetableName;
    }
    
    @FXML
    private TableView timetablesView;
    
    @FXML
    private MenuButton menuButton;
    
    @FXML
    private Label headerLabel;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshList();
    }
    
     private void refreshList(){
        rows = FXCollections.observableArrayList();
        getData();
        timetablesView.setItems(rows);
        FillTableView fillTable = new FillTableView(timetablesView);
        timetablesView = fillTable.showTable();
     }
     
    private void getData(){
        String showProfOrClass = menuButton.getText();
        String sourceColumn;
        String sourceTable;
        
        // Find out what to show: Professors or Classes
        if(showProfOrClass.equals("Classes")){
            sourceColumn = "Class_Label";
            sourceTable = "AllClasses";
            headerLabel.setText("Classes");
        }
        else{
            sourceColumn = "Professor_Name";
            sourceTable = "AllProfessors";
            headerLabel.setText("Professors");
        }
        
        String allProfOrClasses = "SELECT " + sourceColumn + " FROM " + sourceTable
                    + " WHERE Timetable = '" + timetableName + "' ORDER BY " + sourceColumn + " ASC";
        try(Connection conn = DriverManager.getConnection("jdbc:derby:TableDB");
            PreparedStatement psAllProfOrClasses = conn.prepareStatement(allProfOrClasses);
            ResultSet rsAllProfOrClasses = psAllProfOrClasses.executeQuery();){
            
            // Iterate for all professors or classes
            while(rsAllProfOrClasses.next()){
                String profOrClass = rsAllProfOrClasses.getString(1);
                // Fill rows for each professor or class
                fillRows(conn, profOrClass, showProfOrClass);
            }
        }
        catch(SQLException e){
            System.err.println("SQLException in class FinalTimetableController, method getData()");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    // Fill rows for each professor or class
    private void fillRows(Connection conn, String profOrClass, String showProfOrClass){
        String sourceColumn;
        String sourceTable;
        
        // Find out what to show: Professors or Classes
        if(showProfOrClass.equals("Classes")){
            sourceColumn = "Class_Label";
            sourceTable = "ClassesTimetables";
        }
        else{
            sourceColumn = "Professor";
            sourceTable = "ProfessorsTimetables";
        }
        //  Add professor or class name
        TimetableRows profOrClassTR = new TimetableRows(0, profOrClass, null, null, null, null);
        rows.add(profOrClassTR);
        // Iterate over and get lectures during week
        try(Statement stmt = conn.createStatement();
            ResultSet rsProfOrClass = stmt.executeQuery("SELECT * FROM " + sourceTable + " WHERE " + sourceColumn
                + " = '" + profOrClass + "' AND Timetable = '" + timetableName + "' ORDER BY tHour ASC");){
            while (rsProfOrClass.next()){
                TimetableRows tr = new TimetableRows(rsProfOrClass.getInt("tHour"), rsProfOrClass.getString("Monday"), 
                        rsProfOrClass.getString("Tuesday"), rsProfOrClass.getString("Wednesday"), 
                        rsProfOrClass.getString("Thursday"), rsProfOrClass.getString("Friday"));
                rows.add(tr);
            }
         //  Add empty row
        TimetableRows emptyTR = new TimetableRows(0, null, null, null, null, null);
        rows.add(emptyTR);
        }
        catch(SQLException e){
            System.err.println("SQLException in class FinalTimetableController, method fillRows()");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        catch(Exception e){
            System.err.println("Exception in class FinalTimetableController, method fillRows()");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void showMenuItem (ActionEvent event) {
        timetablesView.getColumns().clear();
        MenuItem menu = (MenuItem) event.getSource();
        menuButton.setText(menu.getText());
        refreshList();
    }
    
    @FXML
    private void saveAsCSV(){
        SaveCSV save = new SaveCSV(rows);
        save.saveCSV();
    }
    
    @FXML
    private void goBack(ActionEvent event){
        setNewStage(event, "/savedtimetables/SavedTimetables.fxml");
    }
}

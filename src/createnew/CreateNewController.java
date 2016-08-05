/*
 * Controller class for CreateNew.fxml. Provides methods for getting new timetable name from text field
 * and creating new timetable.
 *
 * @author Petar Deveric
 */

package createnew;

import addtables.AddTablesController;
import general.SetNewStage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CreateNewController extends SetNewStage {
    
    private String newName;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private Label warningLabel;
    
     @FXML
    private void goBack(ActionEvent event){
        setNewStage(event, "/welcome/Welcome.fxml");
    }
          
    @FXML
    private void goForward(ActionEvent event){
        createTimetable(event);
    }
    
    @FXML
    private void createTimetable(ActionEvent event){
        newName = nameField.getText();
        if(newName.isEmpty()){
            warningLabel.setText("Please enter appropriate name with \nletters and/or numbers");
        }
        else{
            boolean goodName = updateTimetablesNames();
            if(goodName){
                setNewStageWithPassedValue(event, "/addtables/AddTables.fxml", AddTablesController.class, newName);
            }
        }
    }
    
    private boolean updateTimetablesNames(){
        try(
            Connection conn = DriverManager.getConnection("jdbc:derby:TableDB");
            Statement stmt = conn.createStatement();
                
            //Check if timetable name already exist
            ResultSet rs = stmt.executeQuery("SELECT Names FROM TimetablesNames WHERE Names = '" + newName +"'");){
            
            boolean tableExist = false;
            while(rs.next()){
                tableExist = true; 
            }
            
             //Issue warning if timetable name already exists
            if(tableExist){
                warningLabel.setText("This name already exists. Please \nenter a new name");
            }
            // Create new timetable if name doesn't exist
            else{
                warningLabel.setText("In process. Please wait...");
                stmt.executeUpdate("INSERT INTO TimetablesNames VALUES ('" + newName + "')");
                //createNewTimetable(stmt);
                return true;
            }
        }
        catch(SQLException e){
            System.err.println("SQLException in class CreateNewController, method updateTimetablesNames()");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }
}

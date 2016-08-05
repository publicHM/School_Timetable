/*
 * Controller class for CheckTable.fxml. Provides methods for getting and showing
 * selected table (Classes, Courses or Professors). It also provides methods for loading data from previous timetable, 
 * loading from CSV file, saving table, going back, adding rows and deleting row..
 *
 * @author Petar Deveric
 */

package checktable;

import addtables.AddTablesController;
import general.SetNewStage;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CheckTableController extends SetNewStage implements Initializable{
    
    private final String timetableName;
    private final String tableName;
    private String tempTimetable;
    private ObservableList rows;
    
    @FXML
    private TextField numberOfRowsField;
    
    @FXML
    private TableView table;
    
    public CheckTableController(String timetableName, String tableName){
        this.timetableName = timetableName;
        this.tableName = tableName;
        this.tempTimetable = timetableName;
        numberOfRowsField = new TextField();
    }
    
    public CheckTableController(String timetableName, String tableName, String tempTimetable){
        this.timetableName = timetableName;
        this.tableName = tableName;
        this.tempTimetable = tempTimetable;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        numberOfRowsField.setText("1");
        refresh();
    }
    
    private void refresh(){
        rows = FXCollections.observableArrayList();
        table.setEditable(true);
        getData();
        table.setItems(rows);
        InitializeTable it = new InitializeTable(tableName, table);
        table = it.showTable();
    }
    
    private void getData(){
        String query;
        if(tableName.equals("AllCourses"))  query= "SELECT * FROM AllCourses WHERE Timetable = ?";
        else if(tableName.equals("AllProfessors"))  query= "SELECT * FROM AllProfessors WHERE Timetable = ?";
        else    query= "SELECT * FROM AllClasses WHERE Timetable = ?";
         try(Connection conn = DriverManager.getConnection("jdbc:derby:TableDB");
            PreparedStatement ps = conn.prepareCall(query);){
            ps.setString(1, tempTimetable);
            try(ResultSet rs = ps.executeQuery();){
            
            // Get data from rows
            if(tableName.equals("AllCourses")){
                while(rs.next()){
                    AllCourses as = new AllCourses(rs.getString(1), rs.getInt(2), rs.getInt(3));
                    rows.add(as);
                }
            }
            else if(tableName.equals("AllProfessors")){
                while(rs.next()){
                    AllProfessors ap = new AllProfessors(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                            rs.getInt(5), rs.getInt(6));
                    rows.add(ap);
                }
            }
            else{
                while(rs.next()){
                    AllClasses ac = new AllClasses(rs.getString(1), rs.getInt(2));
                    rows.add(ac);
                }
            }
            }
        }
        catch(SQLException e){
            System.err.println("SQLException in class CheckTableController, method getData()");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void navigateBack(ActionEvent event) {        
        setNewStageWithPassedValue(event, "/addtables/AddTables.fxml", AddTablesController.class, timetableName);
    }
    
    @FXML
    private void saveData(){
        String delete;
        if(tableName.equals("AllCourses"))  delete = "DELETE FROM AllCourses WHERE Timetable = ?";
        else if(tableName.equals("AllProfessors"))   delete = "DELETE FROM AllProfessors WHERE Timetable = ?";
        else    delete = "DELETE FROM AllClasses WHERE Timetable = ?";
        Exception exception = null;
        try(Connection conn = DriverManager.getConnection("jdbc:derby:TableDB");
            PreparedStatement ps = conn.prepareStatement(delete);){
            
            // Copy previously stored data to temporary table so it could be copied back in case of exception
            setRecoveryDatabase(conn);
            
            // Delete old data for Timetable
            ps.setString(1, tempTimetable);
            ps.executeUpdate();
            
            int numberOfRows = rows.size();
            for(int k=0; k<numberOfRows; k++){
                Object object = rows.get(k);
                if (object instanceof AllCourses){
                    String courses = "INSERT INTO AllCourses VALUES (?, ?, ?, ?)";
                    try(PreparedStatement psCourses = conn.prepareStatement(courses);){
                        psCourses.setString(1, ((AllCourses)object).getCourse());
                        psCourses.setInt(2, ((AllCourses)object).getYear());
                        psCourses.setInt(3, ((AllCourses)object).getLessonsPerWeek());
                        psCourses.setString(4, timetableName);
                        psCourses.executeUpdate();
                    }
                }
                else if(object instanceof AllProfessors){
                    String professors = "INSERT INTO AllProfessors VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try(PreparedStatement psProfessors = conn.prepareStatement(professors);){
                        psProfessors.setString(1, ((AllProfessors)object).getProfessor());
                        psProfessors.setString(2, ((AllProfessors)object).getCourse());
                        psProfessors.setString(3, ((AllProfessors)object).getClassLabel().toLowerCase());
                        psProfessors.setString(4, ((AllProfessors)object).getAvailability());
                        psProfessors.setInt(5, ((AllProfessors)object).getShift());
                        psProfessors.setInt(6, ((AllProfessors)object).getMaxHours());
                        psProfessors.setString(7, timetableName);
                        psProfessors.executeUpdate();
                    }
                }
                else{String classes = "INSERT INTO AllClasses VALUES (?, ?, ?)";
                    try(PreparedStatement psClasses = conn.prepareStatement(classes);){                    
                        psClasses.setString(1, ((AllClasses)object).getLabel().toLowerCase());
                        psClasses.setInt(2, ((AllClasses)object).getShift());
                        psClasses.setString(3, timetableName);
                        psClasses.executeUpdate();
                    }
                }
            }
        }
        catch(SQLIntegrityConstraintViolationException e){
            getRecovery();
            exception = e;
            System.err.println("SQLException in class CheckTableController, method saveData()");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        catch(SQLException e){
            getRecovery();
            exception = e;
            System.err.println("SQLException in class CheckTableController, method saveData()");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        catch(Exception e){
            getRecovery();
            exception = e;
            System.err.println("Exception in class CheckTableController, method saveData()");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        if(exception == null) setOKBox("Data is saved");
        else if(exception.getClass().getName().contains("SQLIntegrityConstraintViolationException"))
            setOKBox("Cells can not be empty");
        else setOKBox("Error: Data not saved");
    }
    
    // Copy previously stored data to temporary table so it could be copied back in case of exception
    private void setRecoveryDatabase(Connection conn){
        String delete, insert;
        if(tableName.equals("AllCourses")){
            delete = "DELETE FROM Temporary_AllCourses";
            insert = "INSERT INTO Temporary_AllCourses SELECT * FROM AllCourses WHERE Timetable = ?";
        }
        else if(tableName.equals("AllProfessors")){
            delete = "DELETE FROM Temporary_AllProfessors";
            insert = "INSERT INTO Temporary_AllProfessors SELECT * FROM AllProfessors WHERE Timetable = ?";
        }
        else{
            delete = "DELETE FROM Temporary_AllClasses";
            insert = "INSERT INTO Temporary_AllClasses SELECT * FROM AllClasses WHERE Timetable = ?";
        }
        
        try(PreparedStatement psDelete = conn.prepareStatement(delete);
            PreparedStatement psInsert = conn.prepareStatement(insert);){
            psDelete.executeUpdate();
            psInsert.setString(1, timetableName);
            psInsert.executeUpdate();
        }
        catch(SQLException e){
            System.err.println("SQLException in class CheckTableController, method setRecoveryDatabase()");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        catch(Exception e){
            System.err.println("Exception in class CheckTableController, method setRecoveryDatabase()");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    // Copy previously stored data from temporary table to original in case of exception
    private void getRecovery(){
        try(Connection conn = DriverManager.getConnection("jdbc:derby:TableDB");){
            String delete1, insert, delete2;
            if(tableName.equals("AllCourses")){
                delete1 = "DELETE FROM AllCourses WHERE Timetable = ?";
                insert = "INSERT INTO AllCourses SELECT * FROM Temporary_AllCourses WHERE Timetable = ?";
                delete2 = "DELETE FROM Temporary_AllCourses";
            }
            else if(tableName.equals("AllProfessors")){
                delete1 = "DELETE FROM AllProfessors WHERE Timetable = ?";
                insert = "INSERT INTO AllProfessors SELECT * FROM Temporary_AllProfessors WHERE Timetable = ?";
                delete2 = "DELETE FROM Temporary_AllProfessors";
            }
            else{
                delete1 = "DELETE FROM AllClasses WHERE Timetable = ?";
                insert = "INSERT INTO AllClasses SELECT * FROM Temporary_AllClasses WHERE Timetable = ?";
                delete2 = "DELETE FROM Temporary_AllClasses";
            }
            
            try(PreparedStatement psDelete1 = conn.prepareStatement(delete1);
                PreparedStatement psInsert = conn.prepareStatement(insert);
                PreparedStatement psDelete2 = conn.prepareStatement(delete2);){
                psDelete1.setString(1, timetableName);
                psDelete1.executeUpdate();
                psInsert.setString(1, timetableName);
                psInsert.executeUpdate();
                psDelete2.executeUpdate();
            }
        }
        catch(SQLException e){
            System.err.println("SQLException in class CheckTableController, method getRecovery()");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        catch(Exception e){
            System.err.println("Exception in class CheckTableController, method getRecovery()");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    @FXML
    private void addNewRows(){
        int numberOfRows = Integer.parseInt(numberOfRowsField.getText());
        for(int k = 1; k <= numberOfRows; k++){
            if(tableName.equals("AllCourses"))
                rows.add(new AllCourses(null, 1, 1));
            else if(tableName.equals("AllProfessors"))
                rows.add(new AllProfessors(null, null, null, "Monday, Tuesday, Wednesday, Thursday, Friday", 1, 24));
            else
                rows.add(new AllClasses(null, 1));
        }
    }
    
    @FXML
    private void deleteRow(){
        int selectedRow = table.getSelectionModel().getSelectedIndex();
        rows.remove(selectedRow);
    }
    
    @FXML
    private void keyPressed(KeyEvent keyEvent){
        if (keyEvent.getCode().equals(KeyCode.DELETE)){
            deleteRow();
        }
    }
    
    @FXML
    private void loadCSVFile(){
        ImportCSV importCSV = new ImportCSV(tableName);
        rows = importCSV.getCSVFile();
        table.setItems(rows);
        table.refresh();
    }
    
    @FXML
    private void loadExistingTable(ActionEvent event){
        try{Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/checktable/TimetablesList.fxml"));
            
            loader.setControllerFactory((Class<?> passedController) -> {
                TimetablesListController controller = new TimetablesListController(primaryStage, timetableName, tableName);
                return controller ;
                }
            );

            Scene scene = new Scene((Pane) loader.load());
            scene.getStylesheets().add(getClass().getResource("/general/GeneralTwo.css").toExternalForm());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        }
        catch(Exception e){
            System.err.println("Exception in class SavedTimetablesController, method loadExistingTable()");
            e.printStackTrace(System.out);
        }
    }
    
    public void setTempTimetable(String tempTimetable){
        this.tempTimetable = tempTimetable;

    }
}
/*
 * Helper class that initialize new window for importing data from CSV file and fills 
 * ObservableList that is returned to be appanded to table TableView
 *
 * @author Petar Deveric
 */

package checktable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ImportCSV {
    
    private final ObservableList rows;
    private final String tableName;
    
    public ImportCSV(String tableName){
        rows = FXCollections.observableArrayList();
        this.tableName = tableName;
    }
    
    public ObservableList getCSVFile(){
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try(FileInputStream fileStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);){
                String rowData;

                int start = 0;
                if(tableName.equals("AllCourses")){
                    while ( (rowData = reader.readLine()) != null ){
                        int[] indexes = new int[2];
                        for(int k=0; k<2; k++){
                            int index = rowData.indexOf(";", start);
                            indexes[k] = index;
                            start= index + 1;
                        }
                        AllCourses as = new AllCourses(
                                rowData.substring(0, indexes[0]), 
                                Integer.parseInt(rowData.substring(indexes[0]+1,indexes[1])), 
                                Integer.parseInt(rowData.substring(indexes[1] +1, indexes[1]+2)));
                        rows.add(as);
                        start = 0;
                }
                }
                else if(tableName.equals("AllProfessors")){
                    while ( (rowData = reader.readLine()) != null ){
                        int[] indexes = new int[5];
                        for(int k=0; k<5; k++){
                            int index = rowData.indexOf(";", start);
                            indexes[k] = index;
                            start= index + 1;
                        }
                        AllProfessors ap = new AllProfessors(
                                rowData.substring(0, indexes[0]), 
                                rowData.substring(indexes[0] + 1, indexes[1]), 
                                rowData.substring(indexes[1] + 1, indexes[2]), 
                                rowData.substring(indexes[2] + 1, indexes[3]),
                                Integer.parseInt(rowData.substring(indexes[3]+1,indexes[4])), 
                                Integer.parseInt(rowData.substring(indexes[4]+1)));
                        rows.add(ap);
                        start = 0;
                    }
                }
                else if(tableName.equals("AllClasses")){
                    while ( (rowData = reader.readLine()) != null ){
                    int index = rowData.indexOf(";");

                    AllClasses ac = new AllClasses(
                            rowData.substring(0, index), 
                            Integer.parseInt(rowData.substring(index+1,index+2)));
                    rows.add(ac);
                    }
                    }
            } catch (IOException ioe) {
                System.out.println("IOException in class ImportCSV, method getCSVFile()");
                System.err.println(ioe.getClass().getName() + ": " + ioe.getMessage());
            }
        }
    return rows;
    }
}
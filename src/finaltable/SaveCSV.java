/*
 * Helper class for initializing and saving table from database to CSV file. 
 *
 * @author Petar Deveric
 */

package finaltable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *
 * @author Petar
 */
public class SaveCSV {
    private final ObservableList<TimetableRows> rows;
    private static String FILE_HEADER;
    
    public SaveCSV(ObservableList rows){
        this.rows = rows;
        FILE_HEADER = "Hour; Monday; Tuesday; Wednesday; Thrusday; Friday";
    }
    
    public void saveCSV(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("CSV", "*.csv"));
        File file = fileChooser.showSaveDialog(new Stage());
        
        if(file != null){
            try(FileWriter fileWriter = new FileWriter(file);){
                fileWriter.append(FILE_HEADER);

                for(TimetableRows tr : rows){
                    fileWriter.append(String.valueOf(tr.getHour()));
                    fileWriter.append(";");
                    fileWriter.append(tr.getMonday());
                    fileWriter.append(";");
                    fileWriter.append(tr.getTuesday());
                    fileWriter.append(";");
                    fileWriter.append(tr.getWednesday());
                    fileWriter.append(";");
                    fileWriter.append(tr.getThursday());
                    fileWriter.append(";");
                    fileWriter.append(tr.getFriday());
                    fileWriter.append("\n");
                }
            }
            catch(IOException ioe){
                System.err.println("IOException in class SaveCSV, method saveCSV()");
                System.err.println(ioe.getClass().getName() + ": " + ioe.getMessage());
            }
            catch(Exception e){
                System.err.println("Exception in class SaveCSV, method saveCSV()");
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }
}

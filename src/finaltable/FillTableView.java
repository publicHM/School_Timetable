/*
 * Helper class that initializes table columns of final timetable so they could be shown on UI
 *
 * @author Petar Deveric
 */

package finaltable;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

public class FillTableView {
    
    private final TableView table;
    private final TableColumn<TimetableRows,Integer> hour;
    private final TableColumn<TimetableRows,String> monday;
    private final TableColumn<TimetableRows,String> tuesday;
    private final TableColumn<TimetableRows,String> wednesday;
    private final TableColumn<TimetableRows,String> thursday;
    private final TableColumn<TimetableRows,String> friday;
    
    public FillTableView (TableView table){
        hour = new TableColumn<>("Hour");
        monday = new TableColumn<>("Monday");
        tuesday = new TableColumn<>("Tuesday");
        wednesday = new TableColumn<>("Wednesday");
        thursday = new TableColumn<>("Thursday");
        friday = new TableColumn<>("Friday");
        this.table = table;
    }
    
    public TableView showTable(){
        hour.setCellValueFactory(new PropertyValueFactory("hour"));
        monday.setCellValueFactory(new PropertyValueFactory("monday"));
        tuesday.setCellValueFactory(new PropertyValueFactory("tuesday"));
        wednesday.setCellValueFactory(new PropertyValueFactory("wednesday"));
        thursday.setCellValueFactory(new PropertyValueFactory("thursday"));
        friday.setCellValueFactory(new PropertyValueFactory("friday"));

        hour.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        monday.setCellFactory(TextFieldTableCell.forTableColumn());
        tuesday.setCellFactory(TextFieldTableCell.forTableColumn());
        wednesday.setCellFactory(TextFieldTableCell.forTableColumn());
        thursday.setCellFactory(TextFieldTableCell.forTableColumn());
        friday.setCellFactory(TextFieldTableCell.forTableColumn());
        
        hour.setOnEditCommit((TableColumn.CellEditEvent<TimetableRows, Integer> tc) -> {
            tc.getRowValue().setHour(tc.getNewValue());
        });
        
        monday.setOnEditCommit((TableColumn.CellEditEvent<TimetableRows, String> tc) -> {
            tc.getRowValue().setMonday(tc.getNewValue());
        });
        
        tuesday.setOnEditCommit((TableColumn.CellEditEvent<TimetableRows, String> tc) -> {
            tc.getRowValue().setTuesday(tc.getNewValue());
        });
        
        wednesday.setOnEditCommit((TableColumn.CellEditEvent<TimetableRows, String> tc) -> {
            tc.getRowValue().setWednesday(tc.getNewValue());
        });
        
        thursday.setOnEditCommit((TableColumn.CellEditEvent<TimetableRows, String> tc) -> {
            tc.getRowValue().setThursday(tc.getNewValue());
        });
        
        friday.setOnEditCommit((TableColumn.CellEditEvent<TimetableRows, String> tc) -> {
            tc.getRowValue().setFriday(tc.getNewValue());
        });
        
        table.getColumns().addAll(hour, monday, tuesday, wednesday, thursday, friday);
        
        return table;
    }
}

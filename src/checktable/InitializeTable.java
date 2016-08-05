/*
 * Helper class that initializes table columns of Classes, Courses and Professors tables so they could be shown on UI
 *
 * @author Petar Deveric
 */

package checktable;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

public class InitializeTable {
    
    private final String tableName;
    private final TableView table;
    
    public InitializeTable(String tableName, TableView table){
        this.tableName = tableName;
        this.table = table;
    }
    
    public TableView showTable(){
        if(tableName.equals("AllCourses")){
            TableColumn<AllCourses,String> courseColumn = new TableColumn("Course");
            TableColumn<AllCourses,Integer> yearColumn = new TableColumn<>("Year");
            TableColumn<AllCourses,Integer> lessonsPerWeekColumn = new TableColumn<>("Lessons Per Week");
            
            courseColumn.setCellValueFactory(new PropertyValueFactory("course"));
            yearColumn.setCellValueFactory(new PropertyValueFactory("year"));
            lessonsPerWeekColumn.setCellValueFactory(new PropertyValueFactory("lessonsPerWeek"));
            
            courseColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            yearColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
            lessonsPerWeekColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
            
            courseColumn.setOnEditCommit((TableColumn.CellEditEvent<AllCourses, String> tc) -> {
                tc.getRowValue().setCourse(tc.getNewValue());
            });
            
            yearColumn.setOnEditCommit((TableColumn.CellEditEvent<AllCourses, Integer> tc) -> {
                tc.getRowValue().setYear(tc.getNewValue());
            });
            
            lessonsPerWeekColumn.setOnEditCommit((TableColumn.CellEditEvent<AllCourses, Integer> tc) -> {
                tc.getRowValue().setLessonsPerWeek(tc.getNewValue());
            });
                
            table.getColumns().addAll(courseColumn, yearColumn, lessonsPerWeekColumn);
        }
        else if(tableName.equals("AllProfessors")){
            TableColumn<AllProfessors,String> professorColumn = new TableColumn<>("Professor");
            TableColumn<AllProfessors,String> courseColumn = new TableColumn<>("Course");
            TableColumn<AllProfessors,String> classLabelColumn = new TableColumn<>("Class Names");
            TableColumn<AllProfessors,String> availabilityColumn = new TableColumn<>("Availability");
            TableColumn<AllProfessors,Integer> shiftColumn = new TableColumn<>("Shift");
            TableColumn<AllProfessors,Integer> maxHoursColumn = new TableColumn<>("Max Hours");
            
            professorColumn.setCellValueFactory(new PropertyValueFactory("professor"));
            courseColumn.setCellValueFactory(new PropertyValueFactory("course"));
            classLabelColumn.setCellValueFactory(new PropertyValueFactory("classLabel"));
            availabilityColumn.setCellValueFactory(new PropertyValueFactory("availability"));
            shiftColumn.setCellValueFactory(new PropertyValueFactory("shift"));
            maxHoursColumn.setCellValueFactory(new PropertyValueFactory("maxHours"));
            
            professorColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            courseColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            classLabelColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            availabilityColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            shiftColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
            maxHoursColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
            
            professorColumn.setOnEditCommit((TableColumn.CellEditEvent<AllProfessors, String> tc) -> {
                tc.getRowValue().setProfessor(tc.getNewValue());
            });
            
            courseColumn.setOnEditCommit((TableColumn.CellEditEvent<AllProfessors, String> tc) -> {
                tc.getRowValue().setCourse(tc.getNewValue());
            });
            
            classLabelColumn.setOnEditCommit((TableColumn.CellEditEvent<AllProfessors, String> tc) -> {
                tc.getRowValue().setClassLabel(tc.getNewValue());
            });
            
            availabilityColumn.setOnEditCommit((TableColumn.CellEditEvent<AllProfessors, String> tc) -> {
                tc.getRowValue().setAvailability(tc.getNewValue());
            });
            
            shiftColumn.setOnEditCommit((TableColumn.CellEditEvent<AllProfessors, Integer> tc) -> {
                tc.getRowValue().setShift(tc.getNewValue());
            });
            
            maxHoursColumn.setOnEditCommit((TableColumn.CellEditEvent<AllProfessors, Integer> tc) -> {
                tc.getRowValue().setMaxHours(tc.getNewValue());
            });

            table.getColumns().addAll(professorColumn, courseColumn, classLabelColumn, availabilityColumn, 
                    shiftColumn, maxHoursColumn);
        }
        else{
            TableColumn<AllClasses,String> labelColumn = new TableColumn<>("Class Label");
            TableColumn<AllClasses,Integer> shiftColumn = new TableColumn<>("Shift");
            
            labelColumn.setCellValueFactory(new PropertyValueFactory("label"));
            shiftColumn.setCellValueFactory(new PropertyValueFactory("shift"));
            
            labelColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            shiftColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
            
            labelColumn.setOnEditCommit((TableColumn.CellEditEvent<AllClasses, String> tc) -> {
                tc.getRowValue().setLabel(tc.getNewValue());
            });
            
            shiftColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<AllClasses, Integer>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<AllClasses, Integer> tc) {
                tc.getRowValue().setShift(tc.getNewValue());
            }});
            
            table.getColumns().addAll(labelColumn, shiftColumn);
        }
    return table;
    }
}

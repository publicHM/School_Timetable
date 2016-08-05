/*
 * Helper object for initializing and showing timetables. Takes parameters that are showen in one row of timetable.
 *
 * @author Petar Deveric
 */
package finaltable;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Petar
 */
public class TimetableRows {
    
    private IntegerProperty hour;
    private StringProperty monday;
    private StringProperty tuesday;
    private StringProperty wednesday;
    private StringProperty thursday;
    private StringProperty friday;
    
    public TimetableRows(int hour, String monday, String tuesday, String wednesday, String thursday, String friday){
        hourProperty().set(hour);
        mondayProperty().set(monday);
        tuesdayProperty().set(tuesday);
        wednesdayProperty().set(wednesday);
        thursdayProperty().set(thursday);
        fridayProperty().set(friday);
    }
    
    
    public void setHour(int hour){
        hourProperty().set(hour);
    }
    public int getHour(){
        return hourProperty().get();
    }
    private IntegerProperty hourProperty(){
        if (hour == null) hour = new SimpleIntegerProperty(this, "hour");
        return hour;
    }
    
    
    
    public void setMonday(String monday){
        mondayProperty().set(monday);
    }
    public String getMonday(){
        return mondayProperty().get();
    }
    private StringProperty mondayProperty(){
        if (monday == null) monday = new SimpleStringProperty(this, "Monday");
        return monday;
    }
    
    
     public void setTuesday(String tuesday){
        tuesdayProperty().set(tuesday);
    }
    public String getTuesday(){
        return tuesdayProperty().get();
    }
    private StringProperty tuesdayProperty(){
        if (tuesday == null) tuesday = new SimpleStringProperty(this, "Tuesday");
        return tuesday;
    }
    
    
    
     public void setWednesday(String wednesday){
        wednesdayProperty().set(wednesday);
    }
    public String getWednesday(){
        return wednesdayProperty().get();
    }
    private StringProperty wednesdayProperty(){
        if (wednesday == null) wednesday = new SimpleStringProperty(this, "Wednesday");
        return wednesday;
    }
    
    
    public void setThursday(String thursday){
        thursdayProperty().set(thursday);
    }
    public String getThursday(){
        return thursdayProperty().get();
    }
    private StringProperty thursdayProperty(){
        if (thursday == null) thursday = new SimpleStringProperty(this, "Thursday");
        return thursday;
    }
    
    
    public void setFriday(String friday){
        fridayProperty().set(friday);
    }
    public String getFriday(){
        return fridayProperty().get();
    }
    private StringProperty fridayProperty(){
        if (friday == null) friday = new SimpleStringProperty(this, "Friday");
        return friday;
    }
}

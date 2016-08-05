/*
 * Helper object for initializing and showing table for professors. 
 * Takes parameters that are showen in one row of the table.
 *
 * @author Petar Deveric
 */

package checktable;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Petar
 */
public class AllProfessors {
    
    private StringProperty professor;
    private StringProperty course;
    private StringProperty classLabel;
    private StringProperty availability;
    private IntegerProperty shift;
    private IntegerProperty maxHours;
    
    public AllProfessors(String professor, String course, String classLabel, String availability, 
            int shift, int maxHours) {

        professorProperty().set(professor);
        courseProperty().set(course);
        classLabelProperty().set(classLabel);
        availabilityProperty().set(availability);
        shiftProperty().set(shift);
        maxHoursProperty().set(maxHours);
    }

    public void setProfessor(String professor){
        professorProperty().set(professor);
    }
    public String getProfessor(){
        return professorProperty().get();
    }
    private StringProperty professorProperty(){
        if (professor == null) professor = new SimpleStringProperty(this, "professor");
        return professor;
    }
    
    public void setCourse(String course){
        courseProperty().set(course);
    }
    public String getCourse(){
        return courseProperty().get();
    }
    private StringProperty courseProperty(){
        if (course == null) course = new SimpleStringProperty(this, "course");
        return course;
    }
    
    
    
     public void setClassLabel(String classLabel){
        classLabelProperty().set(classLabel);
    }
    public String getClassLabel(){
        return classLabelProperty().get();
    }
    private StringProperty classLabelProperty(){
        if (classLabel == null) classLabel = new SimpleStringProperty(this, "classLabel");
        return classLabel;
    }
    
    
    public void setAvailability(String availability){
        availabilityProperty().set(availability);
    }
    public String getAvailability(){
        return availabilityProperty().get();
    }
    private StringProperty availabilityProperty(){
        if (availability == null) availability = new SimpleStringProperty(this, "availability");
        return availability;
    }
    
    
    
    
    public void setShift(int shift){
        shiftProperty().set(shift);
    }
    public int getShift(){
        return shiftProperty().get();
    }
    private IntegerProperty shiftProperty(){
        if (shift == null) shift = new SimpleIntegerProperty(this, "shift");
        return shift;
    }
    
    
    
    public void setMaxHours(int lessonsPerWeek){
        maxHoursProperty().set(lessonsPerWeek);
    }
    public int getMaxHours(){
        return maxHoursProperty().get();
    }
    private IntegerProperty maxHoursProperty(){
        if (maxHours == null) maxHours = new SimpleIntegerProperty(this, "maxHours");
        return maxHours;
    }
}

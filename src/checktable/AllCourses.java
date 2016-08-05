/*
 * Helper object for initializing and showing table for coursess. 
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
public class AllCourses {
    
    private StringProperty course;
    private IntegerProperty year;
    private IntegerProperty lessonsPerWeek;
    
    public AllCourses(String passedCourse, int passedYear, int passedLessonsPerWeek) {
        this.course = new SimpleStringProperty(passedCourse);
        this.year = new SimpleIntegerProperty(passedYear);
        this.lessonsPerWeek = new SimpleIntegerProperty(passedLessonsPerWeek);
        coursetProperty().set(passedCourse);
        yearProperty().set(passedYear);
        lessonsPerWeekProperty().set(passedLessonsPerWeek);
    }
    
    
    public void setCourse(String course){
        this.course.set(course);
    }
    public String getCourse(){
        return this.course.get();
    }
    private StringProperty coursetProperty(){
        if (course == null) course = new SimpleStringProperty(this, "course");
        return course;
    }
    
    
    public void setYear(int year){
        this.year.set(year);
    }
    public int getYear(){
        return this.year.get();
    }
    private IntegerProperty yearProperty(){
        if (year == null) year = new SimpleIntegerProperty(this, "year");
        return year;
    }
    
    
    
    public void setLessonsPerWeek(int lessonsPerWeek){
        this.lessonsPerWeek.set(lessonsPerWeek);
    }
    public int getLessonsPerWeek(){
        return this.lessonsPerWeek.get();
    }
    private IntegerProperty lessonsPerWeekProperty(){
        if (lessonsPerWeek == null) lessonsPerWeek = new SimpleIntegerProperty(this, "lessonsPerWeek");
        return lessonsPerWeek;
    }
}

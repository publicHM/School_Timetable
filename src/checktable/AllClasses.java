/*
 * Helper object for initializing and showing table for classes. 
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
public class AllClasses {
    private final StringProperty label;
    private final IntegerProperty shift;
    
    public AllClasses(String passedLabel, int passedShift) {
        this.label = new SimpleStringProperty(passedLabel);
        this.shift = new SimpleIntegerProperty(passedShift);
    }
    
    public void setLabel(String label){
        this.label.set(label);
    }
    public String getLabel(){
        return this.label.get();
    }
    
    
    public void setShift(int shift){
        this.shift.set(shift);
    }
    public int getShift(){
        return this.shift.get();
    }
}

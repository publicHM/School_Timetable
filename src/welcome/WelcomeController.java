/*
 * Controller for Welcome.fxml. Provides methods to go to new window for creating new timetable or 
 * to window of saved timetables. It also provides exit method for exiting the application.
 *
 * @author Petar Deveric
 */

package welcome;

import general.SetNewStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class WelcomeController extends SetNewStage {
    
	@FXML
	private void newTimetable(ActionEvent event){               
            setNewStage(event, "/createnew/CreateNew.fxml");
	}
        
        @FXML
        private void savedTimetable(ActionEvent event){               
            setNewStage(event, "/savedtimetables/SavedTimetables.fxml");
	}
        
        @FXML
        private void exitProgram(){
            System.exit(0);
        }
}

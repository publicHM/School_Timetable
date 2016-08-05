/*
 * Controller for OKDialogBox.fxml. Initializes popup box with passed message and 
 * provides method for closing the box
 *
 * @author Petar Deveric
 */
package general;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class OKDialogBoxController implements Initializable{
    
    private final String message;
    
    @FXML
    Label label;
    
    
    public OKDialogBoxController (String message){
        label = new Label(message);
        this.message = message;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText(message);
    }
    
    @FXML
    private void pressedOKButton(ActionEvent event){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.hide();
    }    


}

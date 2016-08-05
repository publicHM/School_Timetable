/*
 * Abstract class that provide methods to initialize new stages, with one or without any passed argument.
 * Also provides method for showing popup box with passed message for the box.
 */
package general;

import addtables.AddTablesController;
import finaltable.FinalTimetableController;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Petar
 */
public abstract class SetNewStage {
    
    //Set new stages
    public void setNewStage(Event event, String location){
        try{
            Parent root = FXMLLoader.load(getClass().getResource(location));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/general/General.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();
        }
        catch(Exception e){
            System.err.println("Exception in class SetNewStage, method setNewStage()");
            e.printStackTrace(System.out);
        }
    }
    
    public void setNewStageWithPassedValue(Event event, String location, Class<?> controllerClass, String timetableName){
        try{            
            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            
            loader.setControllerFactory((Class<?> passedController) -> {
                if (controllerClass == AddTablesController.class) {
                    AddTablesController controller = new AddTablesController(timetableName);
                    return controller ;
                }
                else if(controllerClass == FinalTimetableController.class){
                    FinalTimetableController controller = new FinalTimetableController(timetableName);
                    return controller;
                }
                else {
                    try {
                        return controllerClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace(System.out);
                        throw new RuntimeException(e);
                    }
                }
            });
            
            Scene scene = new Scene((Pane) loader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();
        }
        catch(Exception e){
            System.err.println("Exception in class SetNewStage, method setNewStageWithPassedValue()");
            e.printStackTrace(System.out);
        }
    }
    
    public void setOKBox(String message){
         try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/general/OKDialogBox.fxml"));
            
            loader.setControllerFactory((Class<?> passedController) -> {
                OKDialogBoxController controller = new OKDialogBoxController(message);
                return controller ;
            });
            
            Scene scene = new Scene((Pane) loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        }
        catch(Exception e){
            System.err.println("Exception in class SetNewStage, method setOKBox()");
            e.printStackTrace(System.out);
        }
    }
}

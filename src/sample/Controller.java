
package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;

public class Controller {

    @FXML
    private MenuItem openFile;

    @FXML
    private AnchorPane graphShow;

    @FXML
    private StackPane graph;

    @FXML
    void Action(ActionEvent event) {
        Platform.exit();
        System.exit(0);

    }

    @FXML
    private MenuItem fileOpen;
    public void fileOpenAction(ActionEvent event){
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);
        if(selectedFile != null){
            System.out.println(selectedFile.getAbsolutePath());
        }else{
            System.out.println("File is not valid");
        }
    }

}

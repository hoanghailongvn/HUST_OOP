
package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.Point2D;

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
            Main.readGraph(selectedFile);
            Main.graphDisplay = (new GraphDisplay<>(Main.g))
                    .size(400) //khoảng cách giữa các đỉnh
                    .algorithm(new FRLayoutAlgorithm2D<>())
//				.vertices(character -> new Circle(20, Character.isDigit(character) ? Color.RED : Color.BLUE))
                    .vertices(character -> new Circle(15, Color.BLUE))
                    .labels(point2D -> new Point2D(point2D.getX(), point2D.getY() -25), character -> new Text(character.toString()))
                    .edges(true, (edge, path) -> {
                        path.setFill(Color.DARKBLUE);
                        path.getStrokeDashArray().addAll(20., 0.);
                        path.setStrokeWidth(2);
                        return path;
                    })
                    .withActionOnClick(ActionOnClick.MY_ACTION)
                    .withCustomActionOnClick((character, shape) -> {
                        System.out.println(character);
                        shape.setFill(Color.YELLOW);
                    })
                    .withCustomActionOnClickReset((character, shape) -> shape.setFill(Color.BLUE))
                    .withActionOnClick_2(ActionOnClick.MY_ACTION_2)
                    .withCustomActionOnClick_2((character, shape) -> {
                        shape.setFill(Color.YELLOW);
                    })
                    .withCustomActionOnClickReset_2(ActionOnClick.MY_ACTION_2_RESET);
            Main.graphDisplay.render();

            
        }else{
            System.out.println("File is not valid");

        }
    }

}

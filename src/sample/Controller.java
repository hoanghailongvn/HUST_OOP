
package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
        GraphDisplay graphDisplay1 = Main.graphDisplay;

        System.out.println("Minh Thu");
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
//                        System.out.println(character);
                        shape.setFill(Color.YELLOW);
                    })
                    .withCustomActionOnClickReset((character, shape) -> shape.setFill(Color.BLUE))
                    .withActionOnClick_2(ActionOnClick.MY_ACTION_2)
                    .withCustomActionOnClick_2((character, shape) -> {
                        shape.setFill(Color.YELLOW);
                    })
                    .withCustomActionOnClickReset_2(ActionOnClick.MY_ACTION_2_RESET);
            Main.graphDisplay.render();
            AnchorPane anchorPane = ( AnchorPane) Main.root.lookup("#graphShow");
            anchorPane.getChildren().remove(graphDisplay1);
            anchorPane.getChildren().add(Main.graphDisplay);


            Main.stage.show();


            
        }else{
            System.out.println("File is not valid");

        }
    }

    public static String textBegin;
    public static String textEnd;
    @FXML
    private TextField inputBegin;
    public void inputBeginAction(ActionEvent event){
        textBegin = inputBegin.getText();
        System.out.println(textBegin);
    }

    @FXML
    private TextField inputEnd;
    public void inputEndAction(ActionEvent event){
        textEnd = inputEnd.getText();
        System.out.println(textEnd);
    }
    @FXML
    private Button btnStart;
    public void btnStartAction(ActionEvent event){

    }
    @FXML
    private Button btnStop;
    public void btnStopAction(ActionEvent event){

    }
    @FXML
    private Button btnNext;
    public void btnNextAction(ActionEvent event){

    }

    @FXML
    private Button btnBack;
    public void btnBackAction(ActionEvent event){

    }
}

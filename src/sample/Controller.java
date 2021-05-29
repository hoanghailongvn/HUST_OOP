
package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.Point2D;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

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
    private TextArea historicalPath;


    @FXML
    private TextArea allPath;



    @FXML
    private MenuItem fileOpen;
    public void fileOpenAction(ActionEvent event){
        GraphDisplay graphDisplay1 = Main.graphDisplay;

        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);
        if(selectedFile != null){
            Main.readGraph(selectedFile);
            Main.graphDisplay = (new GraphDisplay<>(Main.g))
                    .size(400) //khoảng cách giữa các đỉnh
                    .algorithm(new FRLayoutAlgorithm2D<>())
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

    }

    @FXML
    private TextField inputEnd;
    public void inputEndAction(ActionEvent event){

    }
    @FXML
    private Button btnStart;
    public void btnStartAction(ActionEvent event){
        textEnd = inputEnd.getText();
        textBegin = inputBegin.getText();
        Main.graphDisplay.render(textBegin, textEnd);


    }
    @FXML
    private Button btnStop;
    public void btnStopAction(ActionEvent event){
        Main.graphDisplay.situation = false;
        Main.graphDisplay.setElements();
    }
    @FXML
    private Button btnNext;
    public void btnNextAction(ActionEvent event){
        if(Main.graphDisplay.count < Main.graphDisplay.passedVertex.size()) {
            Main.graphDisplay.customActionOnClickReset_2.execute(Main.graphDisplay,
                    Main.graphDisplay.passedVertex.get(Main.graphDisplay.count - 1));
            Main.graphDisplay.count += 1;
            String previous_vertex = Main.graphDisplay.passedVertex.get(Main.graphDisplay.count - 1);
            Main.graphDisplay.actionOnClick_2.execute(Main.graphDisplay, previous_vertex);
            Main.graphDisplay.customActionOnClick_2.accept(previous_vertex,
                    Main.graphDisplay.nodes.get(previous_vertex));
            Main.graphDisplay.lastVertexClicked = previous_vertex;
            Main.graphDisplay.lastShapeClicked = Main.graphDisplay.nodes.get(previous_vertex);

            Main.historicalPath.setText(Main.graphDisplay.passedVertex.subList(0, Main.graphDisplay.count).toString());
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Stop");
            alert.setHeaderText("Kich roi, khong tien duoc nua");
            alert.show();
        }
    }

    @FXML
    private Button btnBack;
    public void btnBackAction(ActionEvent event){
        if(Main.graphDisplay.count > 1) {
            Main.graphDisplay.customActionOnClickReset_2.execute(Main.graphDisplay,
                    Main.graphDisplay.passedVertex.get(Main.graphDisplay.count - 1));
            Main.graphDisplay.count -= 1;
            String previous_vertex = Main.graphDisplay.passedVertex.get(Main.graphDisplay.count - 1);
            Main.graphDisplay.actionOnClick_2.execute(Main.graphDisplay, previous_vertex);
            Main.graphDisplay.customActionOnClick_2.accept(previous_vertex,
                    Main.graphDisplay.nodes.get(previous_vertex));
            Main.graphDisplay.lastVertexClicked = previous_vertex;
            Main.graphDisplay.lastShapeClicked = Main.graphDisplay.nodes.get(previous_vertex);

            Main.historicalPath.setText(Main.graphDisplay.passedVertex.subList(0, Main.graphDisplay.count).toString());
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Stop");
            alert.setHeaderText("Kich roi, khong lui duoc nua");
            alert.show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private Button exportImage;
    public void btnExportImage(){

    }
}


package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("xác nhận");
        alert.setHeaderText("Xác nhận muốn đóng hay không ? ");
        // option != null.
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }

    @FXML
    private TextArea historicalPath;


    @FXML
    private TextArea allPath;


    @FXML
    private MenuItem fileOpen;
    public static File select;
    private Double lastMouseX = null;
    private Double lastMouseY = null;
    private LocalDateTime last = null;

    public void fileOpenAction(ActionEvent event) {
        GraphDisplay graphDisplay1 = Main.graphDisplay;
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null) {
            select = selectedFile;
            //Main.writeFileAdd(selectedFile,"8 5","8");
            Main.readGraph(selectedFile);

            Main.graphDisplay = (new GraphDisplay<>(Main.g))
                    .size(400) //khoảng cách giữa các đỉnh
                    .algorithm(new FRLayoutAlgorithm2D<>())
                    .vertices(character -> new Circle(15, Color.BLUE))
                    .labels(point2D -> new Point2D(point2D.getX(), point2D.getY() - 25), character -> new Text(character.toString()))
                    .edges(true, (edge, path) -> {
                        path.setFill(Color.DARKBLUE);
                        path.getStrokeDashArray().addAll(20., 0.);
                        path.setStrokeWidth(2);
                        return path;
                    })
                    .withActionOnClick(ActionOnClick.MY_ACTION) //khi chưa vào trạng thái tìm đường
                    .withCustomActionOnClick((character, shape) -> { //khi click vào node đó thì node đó thành màu vàng
                        shape.setFill(Color.YELLOW);
                    })
                    .withCustomActionOnClickReset((character, shape) -> shape.setFill(Color.BLUE))  // click lại thì reset về màu xanh
                    .withActionOnClick_2(ActionOnClick.MY_ACTION_2) // khi vào trạng thái tìm đường
                    .withCustomActionOnClick_2((character, shape) -> {
                        shape.setFill(Color.YELLOW);
                    })
                    .withCustomActionOnClickReset_2(ActionOnClick.MY_ACTION_2_RESET);

            Main.graphDisplay.render();


            AnchorPane anchorPane = (AnchorPane) Main.root.lookup("#graphShow");

            anchorPane.getChildren().remove(graphDisplay1);
            anchorPane.getChildren().add(Main.graphDisplay);

            anchorPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.isControlDown()) {
                        if (lastMouseY == null) {
                            lastMouseY = mouseEvent.getX();
                        } else {
                            if(mouseEvent.getY() > lastMouseY) {
                                Main.graphDisplay.setScaleX(Math.min(Main.graphDisplay.getScaleX() + 0.03, 2));
                                Main.graphDisplay.setScaleY(Math.min(Main.graphDisplay.getScaleY() + 0.03, 2));
                            } else {
                                Main.graphDisplay.setScaleX(Math.max(Main.graphDisplay.getScaleX() - 0.03, 0.5));
                                Main.graphDisplay.setScaleY(Math.max(Main.graphDisplay.getScaleY() - 0.03, 0.5));
                            }
                            lastMouseY = mouseEvent.getY();
                        }
                    } else {
                        if (lastMouseX == null || ChronoUnit.MILLIS.between(last, LocalDateTime.now()) > 100) {
                            lastMouseX = mouseEvent.getX();
                            lastMouseY = mouseEvent.getY();
                            last = LocalDateTime.now();
                        } else {
                            Main.graphDisplay.setLayoutX(Main.graphDisplay.getLayoutX() + mouseEvent.getX() - lastMouseX);
                            Main.graphDisplay.setLayoutY(Main.graphDisplay.getLayoutY() + mouseEvent.getY() - lastMouseY);
                            lastMouseX = mouseEvent.getX();
                            lastMouseY = mouseEvent.getY();
                            last = LocalDateTime.now();
                        }
                    }
                }
            });


            Main.allEdge = Main.g.vertexSet();
            Main.stage.show();

        } else {
            System.out.println("File is not valid");

        }
    }

    public static String textBegin;
    public static String textEnd;

    @FXML
    private TextField inputBegin;

    public void inputBeginAction(ActionEvent event) {

    }

    @FXML
    private TextField inputEnd;

    public void inputEndAction(ActionEvent event) {

    }

    @FXML
    private Button btnStart;

    public void btnStartAction(ActionEvent event) {
        textEnd = inputEnd.getText();
        textBegin = inputBegin.getText();
        Main.graphDisplay.render(textBegin, textEnd);


    }

    @FXML
    private Button btnStop;

    public void btnStopAction(ActionEvent event) {
        Main.graphDisplay.situation = false;
        Main.graphDisplay.setElements();
    }

    @FXML
    private Button btnNext;

    public void btnNextAction(ActionEvent event) {
        if (Main.g == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Open file first");
            alert.show();
            return;
        }
        if (Main.graphDisplay.count < Main.graphDisplay.passedVertex.size()) {
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

    public void btnBackAction(ActionEvent event) {
        if (Main.g == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Open file first");
            alert.show();
            return;
        }
        if (Main.graphDisplay.count > 1) {
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

    public void btnExportImage() {
        if (Main.g == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Open file first");
            alert.show();
            return;
        }
        Main.export(Main.graphDisplay);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ok");
        alert.setHeaderText("Export success!");
        alert.show();
    }

    public static String addEdgeStartText;
    public static String addEdgeEndText;
    public static String addVertexText;

    @FXML
    private TextField addEdgeStart;

    public void inputAddEdge() {

    }

    @FXML
    private TextField addEdgeEnd;

    @FXML
    private TextField addVertex;

    public void inputAddVertex() {

    }

    @FXML
    private Button submitVertex;

    public void addSubmitVertex() {
        addEdgeStartText = addEdgeStart.getText();
        addEdgeEndText = addEdgeEnd.getText();
        addVertexText = addVertex.getText().trim();

        GraphDisplay graphDisplay1 = Main.graphDisplay;
        String test = "";
        System.out.println(addVertexText);
        if (addEdgeStartText.trim() != null || addEdgeEndText.trim() != null) {
            test = addEdgeStartText.trim() + " " + addEdgeEndText.trim();
            test = test.trim();
        }
        if ((!Main.allEdge.contains(addEdgeEndText) || !Main.allEdge.contains(addEdgeStartText))) {
            if (!addEdgeEndText.equals(addVertexText) && !addEdgeStartText.equals(addVertexText)) {
                test = "";
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ok");
                alert.setHeaderText("Đỉnh chưa tồn tại nên không thẻ tạo cạnh");
                alert.show();
            }

        }
        if (Main.allEdge.contains(addEdgeStartText)) {
            List<String> vertexes = Main.getNeighbors(addEdgeStartText);
            if (vertexes.contains(addEdgeEndText)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ok");
                alert.setHeaderText("Canh da ton tai");
                alert.show();
                test = "";
            }
        }
        if (Main.allEdge.contains(addVertexText)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ok");
            alert.setHeaderText("Đỉnh da ton tai roi");
            alert.show();
            addVertexText = "";
        }

        Main.writeFileAdd(select, test, addVertexText);
        if (select != null) {
            Main.readGraph(select);
            Main.allEdge = Main.g.vertexSet();
            Main.graphDisplay = (new GraphDisplay<>(Main.g))
                    .size(400) //khoảng cách giữa các đỉnh
                    .algorithm(new FRLayoutAlgorithm2D<>())
                    .vertices(character -> new Circle(15, Color.BLUE))
                    .labels(point2D -> new Point2D(point2D.getX(), point2D.getY() - 25), character -> new Text(character.toString()))
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
            Main.allEdge = Main.g.vertexSet();
            AnchorPane anchorPane = (AnchorPane) Main.root.lookup("#graphShow");
            anchorPane.getChildren().remove(graphDisplay1);
            anchorPane.getChildren().add(Main.graphDisplay);
            anchorPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    Main.graphDisplay.setLayoutX(mouseEvent.getX());
                    Main.graphDisplay.setLayoutY(mouseEvent.getY());
                }
            });
            Main.stage.show();

        } else {
            System.out.println("File is not valid");

        }

    }

    @FXML
    private MenuItem helpAbout;

    //@FXML private WebView webViewTest;
    public void helpAbout(ActionEvent event) {
        // WebView webView = new WebView();

//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setHeaderText("Help");
//        alert.showAndWait();
    }


}

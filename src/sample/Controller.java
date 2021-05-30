
package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    public static File selectedFile;
    private Double lastMouseX = null;
    private Double lastMouseY = null;
    private LocalDateTime last = null;

    public void fileOpenAction(ActionEvent event) {
        GraphDisplay<String, DefaultEdge> graphDisplay1 = Main.graphDisplay;
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);
        if (selectedFile != null) {
            Controller.selectedFile = selectedFile;
            Main.readGraph(selectedFile);
            Main.graphDisplay = Main.g_to_graphDisplay(Main.g);
            Main.graphDisplay.render();

            AnchorPane anchorPane = (AnchorPane) Main.root.lookup("#graphShow");

            anchorPane.getChildren().remove(graphDisplay1);
            anchorPane.getChildren().add(Main.graphDisplay);

            anchorPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.isControlDown()) {
                        if (lastMouseY == null) {
                            lastMouseY = mouseEvent.getX();
                        } else {
                            if (mouseEvent.getY() < lastMouseY) {
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
        if (Main.g == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Open file first");
            alert.show();
            return;
        }

        addEdgeStartText = addEdgeStart.getText();
        addEdgeEndText = addEdgeEnd.getText();
        addVertexText = addVertex.getText().trim();
        String error_output = "";

        if (addEdgeStartText.length() == 0 && addEdgeEndText.length() == 0 && addVertexText.length() == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Input Empty");
            alert.show();
            return;
        }
        boolean check = false;
        if (addVertexText.length() > 0) {
            if (Main.g.vertexSet().contains(addVertexText)) {
                error_output += "Vertex Existed! Add Vertex Fail\n";
            } else {
                Main.g.addVertex(addVertexText);
                error_output += "Vertex add successfully!\n";
                check = true;
            }
        }

        if (addEdgeStartText.length() > 0 || addEdgeEndText.length() > 0) {
            if (addEdgeStartText.length() == 0) {
                error_output += "Edge Start Empty! Add edge Fail\n";
            } else if (addEdgeEndText.length() == 0) {
                error_output += "Edge End Empty! Add edge Fail\n";
            } else if (!Main.g.vertexSet().contains(addEdgeStartText) || !Main.g.vertexSet().contains(addEdgeEndText)) {
                if (!Main.g.vertexSet().contains(addEdgeStartText)) {
                    error_output += "Vertex ";
                    error_output += addEdgeStartText;
                    error_output += " does not exist\n";
                }
                if (!Main.g.vertexSet().contains(addEdgeEndText)) {
                    error_output += "Vertex ";
                    error_output += addEdgeEndText;
                    error_output += " does not exist\n";
                }
            } else if (!Main.g.getAllEdges(addEdgeStartText, addEdgeEndText).isEmpty()) {
                System.out.println("ok");
                error_output += "Edge Existed! Add edge Fail\n";
            } else {
                Main.g.addEdge(addEdgeStartText, addEdgeEndText);
                error_output += "Edge add successfully!\n";
                check = true;
            }
        }


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alert");
        alert.setHeaderText(error_output);
        alert.show();

        if (check) {
            GraphDisplay<String, DefaultEdge> temp = Main.graphDisplay;

            Main.graphDisplay = Main.g_to_graphDisplay(Main.g);
            Main.graphDisplay.render();
            AnchorPane anchorPane = (AnchorPane) Main.root.lookup("#graphShow");

            anchorPane.getChildren().remove(temp);
            anchorPane.getChildren().add(Main.graphDisplay);

            anchorPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.isControlDown()) {
                        if (lastMouseY == null) {
                            lastMouseY = mouseEvent.getX();
                        } else {
                            if (mouseEvent.getY() < lastMouseY) {
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
        }

    }

    @FXML
    private MenuItem helpAbout;

    //@FXML private WebView webViewTest;
    public void helpAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("INFORMATION");
        alert.setHeaderText("Introduce");
        alert.setContentText("MY TEAM: \n 1. Hoàng Hải Long \n 2. Nguyễn Hoàng Sơn \n 3. Chu Thị Hiền" +
                " \n 4. Phạm Thị Mai Tuyết \n 5.Bùi Vân Anh \n 6. Phạm Thị Minh Thư\n Cần trợ giúp và góp ý về project của team mình các bạn hãy liên hệ qua email : minhthutb111@gmail.com");
        alert.show();
    }

    @FXML
    private MenuItem itemSave;
    public void itemSaveAction(){
        try {
            FileWriter fileWriter = new FileWriter(selectedFile);
            for(int i = 0; i < Main.g_adj.size(); i++) {
                fileWriter.write(Main.g_adj.get(i).get(0));
                for(int j = 1; j < Main.g_adj.get(i).size(); j++) {
                    fileWriter.write(" " + Main.g_adj.get(i).get(j));
                }
                fileWriter.write("\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private MenuItem itemSaveAs;
    public void itemSaveAsAction(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"));
        //Adding action on the menu item
        itemSaveAs.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                //Opening a dialog box
                selectedFile = fileChooser.showSaveDialog(Main.stage);
                itemSaveAction();
            }
        });

    }





}

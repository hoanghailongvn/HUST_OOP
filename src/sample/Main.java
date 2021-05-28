package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Main extends Application {
    public static Graph<String, DefaultEdge> g = null;
    public static GraphDisplay<String, DefaultEdge> graphDisplay = new GraphDisplay<>(g);

    public static int readGraph(File path) {
        // args: Đường dẫn đến file danh sách kề txt
        // đọc và lưu vào graph Main.g
        // return -1 nếu không tìm thấy file
        Main.g = new DefaultDirectedGraph<>(DefaultEdge.class);
        try {
            Scanner myReader = new Scanner(path);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                data = data.strip();
                String[] splitted_data = data.split("\\s+");
                String start = splitted_data[0];
                Main.g.addVertex(start);
                for(int i = 1; i < splitted_data.length; i++) {
                    Main.g.addVertex(splitted_data[i]);
                    Main.g.addEdge(start, splitted_data[i]);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return -1;
        }
        return 0;
    }


    @Override
    public void start(Stage primaryStage) throws Exception{


//        graphDisplay.render("1", "7");
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        primaryStage.setTitle("GraphFX usage example");
        StackPane layout = new StackPane();
        layout.setPadding(new Insets(20));
        layout.getChildren().add(graphDisplay);
        StackPane stackPane = (StackPane) root.lookup("#graph");
        stackPane.getChildren().add(layout);

        //------------------------------------------------------------------------------------------------------------

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

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

    public static int readGraph(String path) {
        // args: Đường dẫn đến file danh sách kề txt
        // đọc và lưu vào graph Main.g
        // return -1 nếu không tìm thấy file
        Main.g = new DefaultDirectedGraph<>(DefaultEdge.class);
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
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
        readGraph("C:\\Users\\hoang\\IdeaProjects\\untitled\\src\\sample\\input.txt");

        GraphDisplay<String, DefaultEdge> graphDisplay = (new GraphDisplay<>(Main.g))
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
        graphDisplay.render();
        graphDisplay.render("1", "7");

        primaryStage.setTitle("GraphFX usage example");
        StackPane layout = new StackPane();
        layout.setPadding(new Insets(50));
        layout.getChildren().add(graphDisplay);
        primaryStage.setScene(new Scene(layout, 1200, 600));
        primaryStage.show();


        //------------------------------------------------------------------------------------------------------------
//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(root, 300, 275));
//
//        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

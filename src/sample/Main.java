package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.scene.layout.StackPane;
import javafx.util.Pair;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main extends Application {
    public static Graph<String, DefaultEdge> g = null;
    public static GraphDisplay<String, DefaultEdge> graphDisplay = new GraphDisplay<>(g);
    public static List<List<String>> g_adj = null;
    public static Parent root;
    public static Stage stage;
    public static TextArea historicalPath;
    public static TextArea allPath;
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

    public static List<List<String>> g_to_adj(Graph<String, DefaultEdge> g) {
        List<List<String>> res = new ArrayList<>();
        for(String string: Main.g.vertexSet()) {
            List<String> temp = new ArrayList<>();
            temp.add(string);
            temp.addAll(Graphs.successorListOf(g, string));
            res.add(temp);
        }
        return res;
    }

    public static GraphDisplay<String, DefaultEdge> g_to_graphDisplay(Graph<String, DefaultEdge> g) {
        return (new GraphDisplay<>(Main.g))
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
    }
    public static void writeDotFile(List<List<String>> g_adj, File path){
        try {
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write("strict digraph myGraph {\n");
            for(List<String> line: g_adj) {
                fileWriter.write(line.get(0) + "\n");
                for(int i = 1; i < line.size(); i++) {
                    fileWriter.write(line.get(0));
                    fileWriter.write(" -> ");
                    fileWriter.write(line.get(i));
                    fileWriter.write("\n");
                }
            }
            fileWriter.write("}");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeDotFile(List<List<String>> g_adj, File path, List<String> red) {
        try {
            FileWriter fileWriter = new FileWriter(path);

            List<Pair<String, String>> listPair = new ArrayList<>();
            for(int i = 0; i < red.size() - 1; i++) {
                listPair.add(new Pair<>(red.get(i), red.get(i+1)));
            }
            fileWriter.write("strict digraph myGraph {\n");
            for(List<String> line: g_adj) {
                fileWriter.write(line.get(0) + "\n");
                for(int i = 1; i < line.size(); i++) {
                    fileWriter.write(line.get(0));
                    fileWriter.write(" -> ");
                    fileWriter.write(line.get(i));
                    if(listPair.contains(new Pair<>(line.get(0), line.get(i)))) {
                        fileWriter.write(" [color = red]");
                        listPair.remove(new Pair<>(line.get(0), line.get(i)));
                    }
                    fileWriter.write("\n");
                }
            }
            fileWriter.write("}");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writePngFile(File pathDot, File pathPng) {
        try {
            Runtime rt =Runtime.getRuntime();
            String[] c = {"dot", "-Tpng", pathDot.toString(), "-o", pathPng.toString()};
            Process pr = rt.exec(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static File pathDot = new File(System.getProperty("user.dir") + File.separator +
            "src" + File.separator +
            "output" + File.separator +
            "dot");
    public static File pathPng = new File(System.getProperty("user.dir") + File.separator +
            "src" + File.separator +
            "output" + File.separator +
            "png");
    public static void export(GraphDisplay<String, DefaultEdge> graphDisplay) {
        File[] fileDot = Main.pathDot.listFiles();
        File[] filePng = Main.pathPng.listFiles();

        for(File file: fileDot) {
            file.delete();
        }
        for(File file: filePng) {
            file.delete();
        }
        writeDotFile(Main.g_adj, new File(Main.pathDot + File.separator + "all.dot"));
        writePngFile(new File(Main.pathDot  + File.separator + "all.dot"),
                new File(Main.pathPng + File.separator + "all.png"));
        if(graphDisplay.situation) {
            for (int i = 0; i < graphDisplay.allPath.size(); i++) {
                String name = graphDisplay.allPath.get(i).toString().replace(", ", "_");
                writeDotFile(Main.g_adj, new File(Main.pathDot + File.separator + name + ".dot"),
                        graphDisplay.allPath.get(i));
                writePngFile(new File(Main.pathDot + File.separator + name + ".dot"),
                        new File(Main.pathPng + File.separator + name + ".png"));
            }
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("My Project");
        primaryStage.setScene(new Scene(root, 1300, 700));
        stage = primaryStage;
        historicalPath = (TextArea) root.lookup("#historicalPath") ;
        allPath = (TextArea) root.lookup("#allPath");

        primaryStage.show();


    }


    public static void main(String[] args) {

        launch(args);
    }
}

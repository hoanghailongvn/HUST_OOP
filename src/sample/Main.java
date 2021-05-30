package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import javafx.scene.layout.StackPane;
import javafx.util.Pair;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
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
    public static StackPane layout;
    public static Parent root;
    public static Stage stage;
    public static TextArea historicalPath;
    public static TextArea allPath;
    public static Set<String> allEdge = new HashSet<>();
    public static int readGraph(File path) {
        // args: Đường dẫn đến file danh sách kề txt
        // đọc và lưu vào graph Main.g
        // return -1 nếu không tìm thấy file
        Main.g = new DefaultDirectedGraph<>(DefaultEdge.class);
        try {
            Scanner myReader = new Scanner(path);
            Main.g_adj = new ArrayList<>();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                data = data.strip();
                String[] splitted_data = data.split("\\s+");
                Main.g_adj.add(Arrays.asList(splitted_data));
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
    public static void writeDotFile(List<List<String>> g_adj, File path){
        try {
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write("strict digraph myGraph {\n");
            for(List<String> line: Main.g_adj) {
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
            for(List<String> line: Main.g_adj) {
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
    public static void writeFileAdd(File path, String addEdgeFromInput, String addVertexFromInput){
        try{
            FileWriter fw = new FileWriter(path,true);
            BufferedWriter bw = new BufferedWriter(fw);

            if((addEdgeFromInput.trim() != null && addEdgeFromInput != "")){
                bw.write("\n" + addEdgeFromInput);
            }
            if((addVertexFromInput.trim() != null && addVertexFromInput != "") && !allEdge.contains(addVertexFromInput) ){
                bw.write( "\n" + addVertexFromInput);
            }
            bw.close();

        }catch(FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static List<String> getNeighbors (String part)
    {
        return Graphs.neighborListOf(Main.g, part);
    }

    public static void mouseEventVertex(String vertex, MouseEvent event){

        List<String> list_vertex = new ArrayList<>();
            list_vertex = getNeighbors(vertex);
        for(int i = 0; i < list_vertex.size(); i++){


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

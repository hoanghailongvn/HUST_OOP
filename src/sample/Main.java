package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import javafx.scene.layout.StackPane;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class Main extends Application {
    public static Graph<String, DefaultEdge> g = null;
    public static GraphDisplay<String, DefaultEdge> graphDisplay = new GraphDisplay<>(g);
    public static StackPane layout;
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


    public static void writeFileAdd(File path, String addEdgeFromInput, String addVertexFromInput){
        try{
           FileWriter fw = new FileWriter(path,true);
            BufferedWriter bw = new BufferedWriter(fw);

            if(addEdgeFromInput.trim() != null && addEdgeFromInput != ""){
                bw.write("\n" + addEdgeFromInput);
            }
           if(addVertexFromInput.trim() != null && addVertexFromInput != ""){

                  bw.write( "\n" + addVertexFromInput);
           }
           bw.close();

        }catch(FileNotFoundException e){
           e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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

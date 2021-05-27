package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.FRLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class UsageExample extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {


		Graph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

		//Create a graph
		graph.addVertex("1");
		graph.addVertex("2");
		graph.addVertex("3");
		graph.addVertex("4");
		graph.addVertex("5");
		graph.addVertex("6");
		graph.addVertex("7");
		graph.addVertex("8");

		graph.addEdge("1", "2");
		graph.addEdge("2", "3");
		graph.addEdge("2", "8");
		graph.addEdge("3", "8");
		graph.addEdge("3", "4");
		graph.addEdge("4", "5");
		graph.addEdge("4", "7");
		graph.addEdge("7", "6");
		graph.addEdge("7", "3");

		//Build the graph display
		GraphDisplay<String, DefaultEdge> graphDisplay = (new GraphDisplay<>(graph))
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

		//Build JavaFX scene
		primaryStage.setTitle("GraphFX usage example");
		StackPane layout = new StackPane();
		layout.setPadding(new Insets(20));
		layout.getChildren().add(graphDisplay);
		primaryStage.setScene(new Scene(layout, 600, 600));
		primaryStage.show();
		graphDisplay.render("1", "8");
	}
}

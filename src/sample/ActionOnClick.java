package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public enum ActionOnClick {
	HIGHLIGHT_CONNECTED_EDGES(){
		@Override
		<V, E> void execute(GraphDisplay<V, E> graphDisplay, V clicked) {
			List<Shape> toBeHighlighted = new ArrayList<>();
			toBeHighlighted.add(graphDisplay.getNodes().get(clicked));
			toBeHighlighted.add(graphDisplay.getLabels().get(clicked));

			Graph<V, E> graph = graphDisplay.getGraph();
			List<E> connectedEdges = new ArrayList<>(graph.incomingEdgesOf(clicked));
			connectedEdges.addAll(graph.outgoingEdgesOf(clicked));
			toBeHighlighted.addAll(connectedEdges.stream().map(e -> graphDisplay.getEdges().get(e)).collect(Collectors.toList()));
			highlightAll(toBeHighlighted, graphDisplay);
		}
	},
	HIGHLIGHT_OUTGOING_EDGES(){
		@Override
		<V, E> void execute(GraphDisplay<V, E> graphDisplay, V clicked) {
			List<Shape> toBeHighlighted = new ArrayList<>();
			toBeHighlighted.add(graphDisplay.getNodes().get(clicked));
			toBeHighlighted.add(graphDisplay.getLabels().get(clicked));
			toBeHighlighted.addAll(graphDisplay.getGraph().outgoingEdgesOf(clicked).stream().map(e -> graphDisplay.getEdges().get(e)).collect(Collectors.toList()));
			highlightAll(toBeHighlighted, graphDisplay);
		}
	},
	HIGHLIGHT_INCOMING_EDGES(){
		@Override
		<V, E> void execute(GraphDisplay<V, E> graphDisplay, V clicked) {
			List<Shape> toBeHighlighted = new ArrayList<>();
			toBeHighlighted.add(graphDisplay.getNodes().get(clicked));
			toBeHighlighted.add(graphDisplay.getLabels().get(clicked));
			toBeHighlighted.addAll(graphDisplay.getGraph().incomingEdgesOf(clicked).stream().map(e -> graphDisplay.getEdges().get(e)).collect(Collectors.toList()));
			highlightAll(toBeHighlighted, graphDisplay);
		}

	},
	HIGHLIGHT_ALL_CONNECTED_VERTICES(){
		@Override
		<V, E> void execute(GraphDisplay<V, E> graphDisplay, V clicked) {
			List<Shape> toBeHighlighted = new ArrayList<>();
			toBeHighlighted.add(graphDisplay.getNodes().get(clicked));
			toBeHighlighted.add(graphDisplay.getLabels().get(clicked));

			Graph<V, E> graph = graphDisplay.getGraph();
			List<V> connectedVertices = graph.incomingEdgesOf(clicked).stream().map(graph::getEdgeSource).collect(Collectors.toList()); //for all incoming edges, add their source vertices to the list
			connectedVertices.addAll(graph.outgoingEdgesOf(clicked).stream().map(graph::getEdgeTarget).collect(Collectors.toList()));
			toBeHighlighted.addAll(connectedVertices.stream().map(v -> graphDisplay.getNodes().get(v)).collect(Collectors.toList()));
			highlightAll(toBeHighlighted, graphDisplay);
		}

	},

	/**
	 * Highlight vertices that are the source of incoming edges.
	 */
	HIGHLIGHT_UPSTREAM_VERTICES(){
		@Override
		<V, E> void execute(GraphDisplay<V, E> graphDisplay, V clicked) {
			List<Shape> toBeHighlighted = new ArrayList<>();
			toBeHighlighted.add(graphDisplay.getNodes().get(clicked));
			toBeHighlighted.add(graphDisplay.getLabels().get(clicked));

			Graph<V, E> graph = graphDisplay.getGraph();
			toBeHighlighted.addAll(graph.incomingEdgesOf(clicked).stream().map(e -> graphDisplay.getNodes().get(graph.getEdgeSource(e))).collect(Collectors.toList()));
			highlightAll(toBeHighlighted, graphDisplay);
		}
	},

	/**
	 * Highlight vertices that are the target of outgoing edges.
	 */
	HIGHLIGHT_DOWNSTREAM_VERTICES() {
		@Override
		<V, E> void execute(GraphDisplay<V, E> graphDisplay, V clicked) {
			List<Shape> toBeHighlighted = new ArrayList<>();
			toBeHighlighted.add(graphDisplay.getNodes().get(clicked));
			toBeHighlighted.add(graphDisplay.getLabels().get(clicked));

			Graph<V, E> graph = graphDisplay.getGraph();
			toBeHighlighted.addAll(graph.outgoingEdgesOf(clicked).stream().map(e -> graphDisplay.getNodes().get(graph.getEdgeTarget(e))).collect(Collectors.toList()));
			highlightAll(toBeHighlighted, graphDisplay);
		}

	},
	MY_ACTION() {
		@Override
		<V, E> void execute(GraphDisplay<V, E> graphDisplay, V clicked) {
			// list những node cần sáng
			List<Shape> toBeHighlighted = new ArrayList<>();
			toBeHighlighted.add(graphDisplay.getNodes().get(clicked));
			toBeHighlighted.add(graphDisplay.getLabels().get(clicked));
			Graph<V, E> graph = graphDisplay.getGraph();
			toBeHighlighted.addAll(graph.outgoingEdgesOf(clicked).stream().map(e -> graphDisplay.getNodes().get(graph.getEdgeTarget(e))).collect(Collectors.toList()));
			toBeHighlighted.addAll(graphDisplay.getGraph().outgoingEdgesOf(clicked).stream().map(e -> graphDisplay.getEdges().get(e)).collect(Collectors.toList()));
			highlightAll(toBeHighlighted, graphDisplay);
		}

	},
	MY_ACTION_2() {
		@Override
		<V, E> void execute(GraphDisplay<V, E> graphDisplay, V clicked) {
			List<Shape> toBeHighlighted = new ArrayList<>();
			toBeHighlighted.add(graphDisplay.getNodes().get(clicked));
			toBeHighlighted.add(graphDisplay.getLabels().get(clicked));
			Graph<V, E> graph = graphDisplay.getGraph();
			toBeHighlighted.addAll(graphDisplay.adj(graphDisplay.allPath, clicked));
			highlightAll(toBeHighlighted, graphDisplay);
		}
	},

	MY_ACTION_2_RESET() {
		@Override
		<V, E> void execute(GraphDisplay<V, E> graphDisplay, V clicked) {
			Shape temp = graphDisplay.getNodes().get(clicked);
			if (clicked.equals(graphDisplay.start) || clicked.equals(graphDisplay.end)) {
				temp.setFill(Color.RED);
			} else {
				temp.setFill(Color.BLUE);
			}
		}
	};



	abstract <V,E> void execute(GraphDisplay<V, E> graphDisplay, V clicked);


	//Khôi phục độ đậm về 1.
	<V,E> void reset(GraphDisplay<V, E> graphDisplay){
		for (Shape shape : getAllShapesFrom(graphDisplay)) {
			shape.setOpacity(1.);
		}
	}

	//lấy tất cả shape đỉnh và cạnh
	private static <V,E> Collection<Shape> getAllShapesFrom(GraphDisplay<V, E> graphDisplay){
		Collection<Shape> result = new ArrayList<>(graphDisplay.getNodes().values());
		result.addAll(graphDisplay.getEdges().values());
		result.addAll(graphDisplay.getLabels().values());
		return result;
	}


	//Highlight shapes, làm mờ cái còn lại
	private static <V,E> void highlightAll(Collection<Shape> shapes, GraphDisplay<V, E> graphDisplay){
		for (Shape shape : getAllShapesFrom(graphDisplay)) {
			shape.setOpacity(.3);
		}

		for (Shape shape : shapes) {
			shape.setOpacity(1.);
		}
	}

}

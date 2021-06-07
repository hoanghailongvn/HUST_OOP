package sample;

import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.drawing.LayoutAlgorithm2D;
import org.jgrapht.alg.drawing.RandomLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.RescaleLayoutAlgorithm2D;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.MapLayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;

import java.awt.event.MouseEvent;
import java.util.*;
import java.util.function.*;

import static java.util.stream.Collectors.toMap;

public class GraphDisplay<V, E> extends Region {
    public V start;
    public V end;
    boolean situation;
    List<List<V>> allPath;
    public ArrayList<V> passedVertex;
    public int count;
    /**
     * The default side size for the diagram
     */
    public static final double DEFAULT_SIZE = 500;
    public double size;
    public final Graph<V, E> graph;
    Map<V, Point2D> vertices2D;
    Map<V, Shape> nodes;
    Map<V, Text> labels;
    Map<E, Path> edges;
    LayoutAlgorithm2D<V, E> algorithm;
    public BiConsumer<V, Shape> vertexUpdater;
    public BiConsumer<V, Text> labelUpdater;
    public BiConsumer<E, Path> edgeUpdater;
    public Function<V, Shape> nodeSupplier;
    public UnaryOperator<Point2D> labelPlacer;
    public Function<V, Text> textMapper;
    public boolean arrow;
    public BiFunction<E, Path, Path> edgeFormatter;
    public LayoutModel2D<V> layout;
    public ActionOnClick actionOnClick;
    public Shape lastShapeClicked;
    public V lastVertexClicked;
    public BiConsumer<V, Shape> customActionOnClick;
    public BiConsumer<V, Shape> customActionOnClickReset;
    public ActionOnClick actionOnClick_2;
    public BiConsumer<V, Shape> customActionOnClick_2;
    public ActionOnClick customActionOnClickReset_2;

    public GraphDisplay(Graph<V, E> graph) {
        this.graph = graph;
        this.passedVertex = new ArrayList<>();
        this.count = 0;
        this.situation = false;
    }

    public GraphDisplay<V, E> size(double size) {
        this.size = size;
        return this;
    }

    public GraphDisplay<V, E> algorithm(LayoutAlgorithm2D<V, E> algorithm) {
        if (vertices2D != null)
            throw new IllegalStateException("algorithm(...) needs to be called before vertices, text or edges");
        this.algorithm = algorithm;
        return this;
    }

    public GraphDisplay<V, E> vertices(Function<V, Shape> nodeSupplier) {
        this.nodeSupplier = nodeSupplier;
        return this;
    }

    public void layoutGraph() {
        size = Objects.requireNonNullElse(size, DEFAULT_SIZE);
        algorithm = Objects.requireNonNullElse(algorithm, new RandomLayoutAlgorithm2D<>());
        layout = new MapLayoutModel2D<>(new Box2D(size + 50, size + 50));
        algorithm.layout(graph, layout);
    }

    public Map<V, Shape> produceVertices() {
        Objects.requireNonNull(layout, "Need to call layoutGraph before calling this for the first time");
        vertices2D = layout.collect();
        return nodes = graph.vertexSet().stream().collect(toMap(v -> v, v -> {
            Point2D point2D = vertices2D.get(v);
            Shape node = nodeSupplier.apply(v);
            node.setLayoutX(point2D.getX());
            node.setLayoutY(point2D.getY());
            return node;
        }));
    }

    public GraphDisplay<V, E> labels(UnaryOperator<Point2D> labelPlacer, Function<V, Text> textMapper) {
        this.labelPlacer = labelPlacer;
        this.textMapper = textMapper;
        return this;
    }

    public Map<V, Text> produceLabels() {
        return labels = graph.vertexSet().stream().collect(toMap(v -> v, v -> {
            Point2D place = labelPlacer.apply(vertices2D.get(v));
            Text text = textMapper.apply(v);
            text.setX(place.getX());
            text.setY(place.getY());
            return text;
        }));
    }

    public GraphDisplay<V, E> edges(boolean arrow, BiFunction<E, Path, Path> edgeFormatter) {
        this.arrow = arrow;
        this.edgeFormatter = edgeFormatter;
        return this;
    }

    public GraphDisplay<V, E> edges(BiFunction<E, Path, Path> edgeFormatter) {
        return edges(graph.getType().isDirected(), edgeFormatter);
    }

    public Map<E, Path> produceEdges() {
        return edges = graph.edgeSet().stream().collect(toMap(e -> e, e -> {
            Path edge2d = new Edge2D(vertices2D.get(graph.getEdgeSource(e)), vertices2D.get(graph.getEdgeTarget(e)), arrow);
            return edgeFormatter.apply(e, edge2d);
        }));
    }

    public void render() { //todo this should be the step that returns the finished object, refactor
        this.situation = false;
        layoutGraph();
        setElements();
        Main.g_adj = Main.g_to_adj(Main.g);
    }

    class NodeAllPath {
        private V label;
        private List<V> visited;

        public NodeAllPath(V label, List<V> visited) {
            this.label = label;
            this.visited = visited;
        }

        @Override
        public String toString() {
            return "NodeAllPath{" +
                    "label='" + label + '\'' +
                    ", visited=" + visited +
                    '}';
        }
    }

    public List<List<V>> findALlPaths(V start, V end) {
        List<List<V>> result = new ArrayList<>();
        Stack<NodeAllPath> stack = new Stack<NodeAllPath>();
        stack.push(new NodeAllPath(start, Arrays.asList(start)));
        while (!stack.isEmpty()) {
            NodeAllPath node_popped = stack.pop();
            for (V v : Graphs.successorListOf(this.graph, node_popped.label)) {
                if (Collections.frequency(node_popped.visited, v) < 2) {
                    List<V> res_path = new ArrayList<>(node_popped.visited);
                    res_path.add(v);
                    System.out.println("(res_path) " + res_path);
                    if (v.equals(end)) {
                        result.add(res_path);
                    } else {
                        stack.push(new NodeAllPath(v, res_path));
                    }
                }
            }
        }
        return result;
    }

    public List<Shape> adj(List<List<V>> allPath_intput, V clicked) {
        List<Shape> res = new ArrayList<>();
        for (List<V> vs : allPath_intput) {
            for (int j = 0; j < vs.size() - 1; j++) {
                if (vs.get(j).equals(clicked)) {
                    res.add(this.getNodes().get(vs.get(j + 1)));
                    res.add(this.getEdges().get(this.graph.getEdge(clicked, vs.get(j + 1))));
                }
            }
        }
        return res;
    }

    // return 1 if graph contain both start and end
    // return 0 otherwise
    //public static Set<V> key = produceLabels().keySet();
    public int render(V start, V end) {
        if (Main.g == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Open file first");
            alert.show();
            return -1;
        }
        Set<V> keys = produceLabels().keySet();
        if (keys.contains(start) && keys.contains(end)) {
            this.start = start;
            this.end = end;
            this.situation = true;
            this.lastShapeClicked = this.nodes.get(start);
            this.lastVertexClicked = start;
            this.allPath = this.findALlPaths(this.start, this.end);
            this.passedVertex = new ArrayList<>();
            this.passedVertex.add(start);
            this.count = 1;

            if (this.adj(this.allPath, start).size() == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Path not found");
                alert.setHeaderText("Not found");
                StringBuilder sbuf = new StringBuilder();
                Formatter fmt = new Formatter(sbuf);
                fmt.format("Not found path from %s to %s", start, end);
                alert.setContentText(sbuf.toString());
                alert.show();
                Main.graphDisplay.situation = false;
                Main.graphDisplay.setElements();
                Main.historicalPath.setText("");
                Main.allPath.setText("");
                return -1;
            }

            this.setElements();

            this.getNodes().get(start).setFill(Color.RED);
            this.getNodes().get(end).setFill(Color.RED);
            actionOnClick_2.execute(this, start);
            customActionOnClick_2.accept(start, this.nodes.get(start));

            StringBuilder output_allPath = new StringBuilder();
            for (List<V> temp : this.allPath) {
                output_allPath.append(temp.get(0));
                for (int i = 1; i < temp.size(); i++) {
                    output_allPath.append(" -> ");
                    output_allPath.append(temp.get(i));
                }
                output_allPath.append("\n");
            }
            Main.allPath.setText(output_allPath.toString());
            return 0;
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Key not existed");
            alert.setHeaderText("Key not existed");
            alert.setContentText("Key not existed");
            alert.show();
            return -1;
        }

    }

    // hàm để xử lý click vào node thì sẽ ntn
    public void setElements() {
        getChildren().clear();
        if (nodeSupplier != null) {
            getChildren().addAll(produceVertices().values());
            //click events
            if (!this.situation) {
                // trạng thái không tìm đường
                if (actionOnClick != null || customActionOnClick != null) {
                    for (Shape shape : nodes.values()) {
                        // lấy ra phần tử được click
                        shape.setOnMouseClicked(event -> {
                            V clicked = nodes.entrySet().stream().filter(entry -> entry.getValue().equals(shape)).findAny().orElseThrow().getKey(); //find the vertex represented by this shape. The nodes map contains just one shape for every vertex.
                            if (actionOnClick != null) {
                                if (shape.equals(lastShapeClicked)) {
                                    // đưa đồ thị về ban đầu
                                    actionOnClick.reset(this);
                                } else {
                                    //
                                    actionOnClick.execute(this, clicked);
                                }
                            }
                            if (customActionOnClick != null) {
                                if (shape.equals(lastShapeClicked)) {
                                    customActionOnClickReset.accept(clicked, shape);
                                } else {
                                    if (lastShapeClicked != null)
                                        customActionOnClickReset.accept(lastVertexClicked, lastShapeClicked);
                                    customActionOnClick.accept(clicked, shape);
                                }
                            }

                            lastShapeClicked = shape.equals(lastShapeClicked) ? null : shape; //if a reset happened in this click, also set lastShapeClicked to null
                            lastVertexClicked = clicked;
                        });

                    }
                }
            } else {
                // trạng thái tìm đường
                if (actionOnClick_2 != null || customActionOnClickReset_2 != null) {
                    for (Shape shape : nodes.values()) {
                        shape.setOnMouseClicked(event -> {
                            V clicked = nodes.entrySet().stream().filter(entry -> entry.getValue().equals(shape)).findAny().orElseThrow().getKey();
                            List<Shape> click_able = this.adj(this.allPath, lastVertexClicked);
                            if (click_able.contains(shape)) {
                                if (actionOnClick_2 != null) {
                                    if (!shape.equals(lastShapeClicked)) {
                                        if (lastShapeClicked != null)
                                            customActionOnClickReset_2.execute(this, lastVertexClicked);
                                        actionOnClick_2.execute(this, clicked);

                                        if (this.count < this.passedVertex.size()) {
                                            this.passedVertex.subList(this.count, this.passedVertex.size()).clear();
                                        }
                                        this.count += 1;
                                        this.passedVertex.add(clicked);
                                        Main.historicalPath.setText(this.passedVertex.subList(0, this.count).toString());
                                    }
                                }
                                if (customActionOnClick_2 != null) {
                                    if (!shape.equals(lastShapeClicked)) {
                                        customActionOnClick_2.accept(clicked, shape);
                                    }
                                }
                                lastShapeClicked = shape.equals(lastShapeClicked) ? null : shape; //if a reset happened in this click, also set lastShapeClicked to null
                                lastVertexClicked = clicked;
                            }

                            if (clicked.equals(this.end)) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Ok");
                                alert.setHeaderText("Ve dich");
                                alert.show();
                            }
                        });
                    }
                }
            }


        }
        if (edgeFormatter != null) getChildren().addAll(produceEdges().values());
        if (textMapper != null) getChildren().addAll(produceLabels().values());
    }

    public void rescale(double scale) {
        (new RescaleLayoutAlgorithm2D<V, E>(scale)).layout(graph, layout);
        setElements();

    }

    public void rescale(double scale, Consumer<Shape> vertexRescaler) {
        rescale(scale);
        nodes.values().forEach(vertexRescaler);
    }

    public GraphDisplay<V, E> withVertexUpdater(BiConsumer<V, Shape> vertexUpdater) {
        this.vertexUpdater = vertexUpdater;
        return this;
    }

    public GraphDisplay<V, E> withLabelUpdater(BiConsumer<V, Text> labelUpdater) {
        this.labelUpdater = labelUpdater;
        return this;
    }

    public GraphDisplay<V, E> withEdgeUpdater(BiConsumer<E, Path> edgeUpdater) {
        this.edgeUpdater = edgeUpdater;
        return this;
    }

    public void update() {
        if (vertexUpdater != null) nodes.forEach(vertexUpdater);
        if (labelUpdater != null) labels.forEach(labelUpdater);
        if (edgeUpdater != null) edges.forEach(edgeUpdater);
    }

    public GraphDisplay<V, E> withActionOnClick_2(ActionOnClick action) {
        actionOnClick_2 = action;
        return this;
    }

    public GraphDisplay<V, E> withCustomActionOnClick_2(BiConsumer<V, Shape> action) {
        customActionOnClick_2 = action;
        return this;
    }

    public GraphDisplay<V, E> withActionOnClick(ActionOnClick action) {
        actionOnClick = action;
        return this;
    }

    public GraphDisplay<V, E> withCustomActionOnClick(BiConsumer<V, Shape> action) {
        customActionOnClick = action;
        return this;
    }

    /**
     * Define a function to be executed before every custom action on click.
     *
     * @param reset the function to be executed before every custom action on click. This consumer function takes the last vertex to be clicked, and its Shape
     * @return this GraphDisplay
     */

    public GraphDisplay<V, E> withCustomActionOnClickReset_2(ActionOnClick reset) {
        customActionOnClickReset_2 = reset;
        return this;
    }

    public GraphDisplay<V, E> withCustomActionOnClickReset(BiConsumer<V, Shape> reset) {
        customActionOnClickReset = reset;
        return this;
    }

    protected Graph<V, E> getGraph() {
        return graph;
    }

    protected Map<V, Shape> getNodes() {
        return Objects.requireNonNull(nodes, "GraphDisplay hasn't been rendered yet");
    }

    protected Map<V, Text> getLabels() {
        return Objects.requireNonNull(labels, "GraphDisplay hasn't been rendered yet");
    }

    protected Map<E, Path> getEdges() {
        return Objects.requireNonNull(edges, "GraphDisplay hasn't been rendered yet");
    }
}

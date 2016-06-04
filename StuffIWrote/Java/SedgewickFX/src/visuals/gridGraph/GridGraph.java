package visuals.gridGraph;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import visuals.graph.Graph;
import visuals.graph.GraphAlgos;
import visuals.graph.GraphEdge;
import visuals.graph.GraphNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Ethan Petuchowski 5/30/16
 */
class GridGraph {

    private final int numRows = 5;
    private final int numCols = 5;
    private final GridPainter gridPainter;
    private final Graph<GridGraphNode> graph = new Graph<>();

    GridGraph(GraphicsContext graphicsContext) {
        graph.addNodes(createNodeGrid());
        gridPainter = new GridPainter(graphicsContext, numRows, numCols);
        gridPainter.redrawNodeGrid(graph.getNodes());
    }

    private List<GridGraphNode> createNodeGrid() {
        List<GridGraphNode> ret = new ArrayList<>();
        for (int row = 0; row < numRows; row++)
            for (int col = 0; col < numCols; col++)
                ret.add(new GridGraphNode(row, col));
        return ret;
    }

    void addRandomEdges(int numEdges) {
        graph.addRandomEdges(numEdges);
        drawEdges();
    }

    private void drawEdges() {
        graph.getEdges().forEach(gridPainter::drawEdge);
    }

    List<GraphEdge> findPath(GraphEdge between) {
        return GraphAlgos.findPath(between.getFromNode(), between.getToNode(), this.graph);
    }

    class GridGraphNode extends GraphNode {
        private GridCoordinates coordinates;

        private GridGraphNode(int row, int col) {
            this.coordinates = new GridCoordinates(row, col);
        }
        @Override public String toString() {
            return "Node("+coordinates+')';
        }
        GridCoordinates getCoordinates() {
            return coordinates;
        }
        @Override public Point2D getCanvasCoordinates() {
            return gridPainter.canvasLocForGridCoords(coordinates);
        }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GridGraphNode node = (GridGraphNode) o;
            return coordinates.equals(node.coordinates);

        }
        @Override public int hashCode() {
            return coordinates.hashCode();
        }
    }

}
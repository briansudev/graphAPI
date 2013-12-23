package make;

import graph.Traversal;
import graph.Graph;
import graph.NoLabel;

/** Checks if there is a cycle.
 * @author Brian Su*/
public class CycleTraversal extends Traversal<String, NoLabel> {

    /** Takes in a GRAPH and the TARGET to start checking a
     * cycle from. */
    public CycleTraversal(Graph<String, NoLabel> graph,
            Graph<String, NoLabel>.Vertex target) {
        _target = target;
        _graph = graph;
    }

    /** Graph. */
    private Graph<String, NoLabel> _graph;
    /** Target vertex. */
    private Graph<String, NoLabel>.Vertex _target;

    @Override
    protected void preVisit(Graph<String, NoLabel>.Edge e,
            Graph<String, NoLabel>.Vertex v) {
        Graph<String, NoLabel>.Vertex succ = e.getV1();
        for (Graph<String, NoLabel>.Vertex v1 : _graph.successors(succ)) {
            if (v1 == v) {
                throw new IllegalArgumentException();
            }
        }
    }
}

package graph;

/** Testing pre, visit, post in the traversals
 * by using print statements.
 * @author Brian Su. */
class TraversalTest extends Traversal<String, String> {

    @Override
    protected void preVisit(Graph<String, String>.Edge e,
            Graph<String, String>.Vertex v) {
        System.out.println("pre: " + e.getV1());
    }

    @Override
    protected void visit(Graph<String, String>.Vertex v) {
        System.out.println("visit: " + v);
    }

    @Override
    protected void postVisit(Graph<String, String>.Vertex v) {
        System.out.println("pos: " + v);
    }
}

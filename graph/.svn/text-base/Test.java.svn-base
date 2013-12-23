package graph;
import static java.lang.System.*;
import java.util.Comparator;

public class Test {
    public static <T> void pl(T arg) {
        out.println(arg);
    }
    public static <T> void plt(T arg) {
        out.println("Testing: " + arg);
    }

    public static void main(String[] args) {
        Graph<String, String> g =
            new DirectedGraph<String, String>();
        Graph<String, String>.Vertex v0 = g.add("v0");
        Graph<String, String>.Vertex v1 = g.add("v1");
        Graph<String, String>.Vertex v2 = g.add("v2");
        Graph<String, String>.Vertex v3 = g.add("v3");
        Graph<String, String>.Vertex v4 = g.add("v4");
        pl("Added 5 vertices into the graph.");
        pl("Vertex size: " + g.vertexSize());
        g.add(v0, v1, "v0 - v1");
        g.add(v0, v2, "v0 - v2");
        g.add(v0, v3, "v0 - v3");
        g.add(v1, v4, "v1 - v4");
        g.add(v2, v3, "v2 - v3");
        pl("Added 5 edges.");
        pl("Edge size: " + g.edgeSize());
        pl("v0 should have an degree of 3.");
        pl("v0 degree: " + g.degree(v0));
        pl("v0 shoud have an inDegree of 0.");
        pl("v0 inDegree: " + g.inDegree(v0));
        pl("v3 should have an inDegree of 2");
        pl("v3 inDegree: " + g.inDegree(v3));
        pl("Should not have an edge 'v4 - v1'");
        pl("Testing: " + g.contains(v4, v1));
        pl("Should have an edge 'v1 - v4'");
        pl("Testing: " + g.contains(v1, v4));
        pl("Should have an edge 'v1 - v4'");
        plt(g.contains(v1, v4, "v1 - v4"));
        pl("Removing v3.");
        g.remove(v3);
        pl("Vertex size should be 4.");
        plt(g.vertexSize());
        pl("v0 should have a degree of 2.");
        plt(g.degree(v0));
        pl("Should not have an edge 'v2 - v3'");
        plt(g.contains(v2, v3));
        pl("Printing out vertices still in graph.");
        for (Graph<String, String>.Vertex v : g.vertices()) {
            out.print(v + " ");
        }
        pl("");
        pl("Printing out vertices and their successors.");
        for (Graph<String, String>.Vertex v : g.vertices()) {
            out.print(v + ": ");
            for (Graph<String, String>.Vertex vS : g.successors(v)) {
                out.print(vS + " ");
            }
            pl("");
        }
        pl("Printing out vertices and their predecssors.");
        for (Graph<String, String>.Vertex v : g.vertices()) {
            out.print(v + ": ");
            for (Graph<String, String>.Vertex vS : g.predecessors(v)) {
                out.print(vS + " ");
            }
            pl("");
        }
        act(g);
    }

    /** Do more with G. */
    protected static void act(Graph<String, String> g) {
        pl("Printing out all the edges in the graph.");
        for (Graph<String, String>.Edge e : g.edges()) {
            pl(e);
        }
        pl("Ordering the edges by comparing the strings. ");
        g.orderEdges(g.<String>naturalOrder());
        for (Graph<String, String>.Edge e : g.edges()) {
            pl(e);
        }
        pl("==========Testing Traversal==========");
        TraversalTest t = new TraversalTest();
        pl("Created new Traversal.");
        pl("Create a new graph.");
        Graph<String, String> g2 = new UndirectedGraph<String, String>();
        Graph<String, String>.Vertex a = g2.add("A");
        Graph<String, String>.Vertex b = g2.add("B");
        Graph<String, String>.Vertex c = g2.add("C");
        Graph<String, String>.Vertex d = g2.add("D");
        Graph<String, String>.Vertex e = g2.add("E");
        Graph<String, String>.Vertex f = g2.add("F");
        pl("Add edges.");
        g2.add(a, b);
        g2.add(a, c);
        g2.add(a, d);
        g2.add(d, e);
        g2.add(d, f);
        g2.add(c, e);
        pl("=====General Traversal=====");
        t.traverse(g2, a, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });
        pl("=====Depth First Traversal=====");
        t.depthFirstTraverse(g2, a);
        pl("=====Breadth First Traversal=====");
        g2.remove(c, e);
        t.breadthFirstTraverse(g2, a);

    }
}

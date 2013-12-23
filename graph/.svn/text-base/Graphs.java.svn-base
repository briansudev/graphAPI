package graph;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Comparator;

/** Assorted graph algorithms.
 *  @author Brian Su
 */
public final class Graphs {

    /* A* Search Algorithms */

    /** Returns a path from V0 to V1 in G of minimum weight, according
     *  to the edge weighter EWEIGHTER.  VLABEL and ELABEL are the types of
     *  vertex and edge labels.  Assumes that H is a distance measure
     *  between vertices satisfying the two properties:
     *     a. H.dist(v, V1) <= shortest path from v to V1 for any v, and
     *     b. H.dist(v, w) <= H.dist(w, V1) + weight of edge (v, w), where
     *        v and w are any vertices in G.
     *
     *  As a side effect, uses VWEIGHTER to set the weight of vertex v
     *  to the weight of a minimal path from V0 to v, for each v in
     *  the returned path and for each v such that
     *       minimum path length from V0 to v + H.dist(v, V1)
     *              < minimum path length from V0 to V1.
     *  The final weights of other vertices are not defined.  If V1 is
     *  unreachable from V0, returns null and sets the minimum path weights of
     *  all reachable nodes.  The distance to a node unreachable from V0 is
     *  Double.POSITIVE_INFINITY. */
    public static <VLabel, ELabel> List<Graph<VLabel, ELabel>.Edge>
    shortestPath(Graph<VLabel, ELabel> G,
                 Graph<VLabel, ELabel>.Vertex V0,
                 Graph<VLabel, ELabel>.Vertex V1,
                 Distancer<? super VLabel> h,
                 Weighter<? super VLabel> vweighter,
                 Weighting<? super ELabel> eweighter) {
        HashSet<Graph<VLabel, ELabel>.Vertex> closedSet =
            new HashSet<Graph<VLabel, ELabel>.Vertex>();
        PriorityQueue<Graph<VLabel, ELabel>.Vertex> openSet =
            new PriorityQueue<Graph<VLabel, ELabel>.Vertex>(G.vertexSize(),
                    new FComp<VLabel, ELabel>(vweighter));
        openSet.add(V0);
        HashMap<Graph<VLabel, ELabel>.Vertex, Graph<VLabel, ELabel>.Edge>
            parent = new HashMap<Graph<VLabel, ELabel>.Vertex,
                   Graph<VLabel, ELabel>.Edge>();
        parent.put(V0, null);
        HashMap<Graph<VLabel, ELabel>.Vertex, Double> gScores =
            new HashMap<Graph<VLabel, ELabel>.Vertex, Double>();
        gScores.put(V0, 0.0);
        List<Graph<VLabel, ELabel>.Edge> path =
            new ArrayList<Graph<VLabel, ELabel>.Edge>();

        for (Graph<VLabel, ELabel>.Vertex v : G.vertices()) {
            if (v != V0) {
                vweighter.setWeight(v.getLabel(), Double.POSITIVE_INFINITY);
            }
        }
        vweighter.setWeight(V0.getLabel(), h.dist(V0.getLabel(),
                    V1.getLabel()));
        while (!openSet.isEmpty()) {
            Graph<VLabel, ELabel>.Vertex curr = openSet.peek();
            if (curr == V1) {
                Graph<VLabel, ELabel>.Edge e = parent.get(curr);
                while (e != null) {
                    curr = e.getV(curr);
                    path.add(e);
                    e = parent.get(curr);
                }
                Collections.reverse(path);
                return path;
            }
            openSet.remove(curr);
            closedSet.add(curr);
            for (Graph<VLabel, ELabel>.Edge e : G.outEdges(curr)) {
                double tG = gScores.get(curr) + eweighter.weight(e.getLabel());
                double F = tG + h.dist(e.getV(curr).getLabel(), V1.getLabel());
                if (closedSet.contains(e.getV(curr))
                        && F >= vweighter.weight(e.getV(curr).getLabel())) {
                    continue;
                } else if (!openSet.contains(e.getV(curr))
                        || F < vweighter.weight(e.getV(curr).getLabel())) {
                    parent.put(e.getV(curr), e);
                    gScores.put(e.getV(curr), tG);
                    vweighter.setWeight(e.getV(curr).getLabel(), F);
                    if (!openSet.contains(e.getV(curr))) {
                        openSet.add(e.getV(curr));
                        parent.put(e.getV(curr), e);
                    }
                }
            }
        }
        return path;
    }

    /** shortest_path comparator. */
    private static class FComp<VLabel, ELabel>
            implements Comparator<Graph<VLabel, ELabel>.Vertex> {

        /** Vweighter. */
        private Weighter<? super VLabel> _vweighter;

        /** Initialize a new comparator with VWEIGHTER. */
        FComp(Weighter<? super VLabel> vweighter) {
            _vweighter = vweighter;
        }

        @Override
        public int compare(Graph<VLabel, ELabel>.Vertex v0,
                Graph<VLabel, ELabel>.Vertex v1) {
            double f1 = _vweighter.weight(v0.getLabel());
            double f2 = _vweighter.weight(v1.getLabel());
            return ((Double) f1).compareTo((Double) f2);
        }
    }

    /** Comparator for 2nd shortest_path. */
    private static class FComp2<VLabel extends Weightable,
                                ELabel extends Weighted>
            implements Comparator<Graph<VLabel, ELabel>.Vertex> {

        @Override
        public int compare(Graph<VLabel, ELabel>.Vertex v0,
                            Graph<VLabel, ELabel>.Vertex v1) {
            double f1 = v0.getLabel().weight();
            double f2 = v1.getLabel().weight();
            return ((Double) f1).compareTo((Double) f2);
        }
    }

    /** Returns a path from V0 to V1 in G of minimum weight, according
     *  to the weights of its edge labels.  VLABEL and ELABEL are the types of
     *  vertex and edge labels.  Assumes that H is a distance measure
     *  between vertices satisfying the two properties:
     *     a. H.dist(v, V1) <= shortest path from v to V1 for any v, and
     *     b. H.dist(v, w) <= H.dist(w, V1) + weight of edge (v, w), where
     *        v and w are any vertices in G.
     *
     *  As a side effect, sets the weight of vertex v to the weight of
     *  a minimal path from V0 to v, for each v in the returned path
     *  and for each v such that
     *       minimum path length from V0 to v + H.dist(v, V1)
     *           < minimum path length from V0 to V1.
     *  The final weights of other vertices are not defined.
     *
     *  This function has the same effect as the 6-argument version of
     *  shortestPath, but uses the .weight and .setWeight methods of
     *  the edges and vertices themselves to determine and set
     *  weights. If V1 is unreachable from V0, returns null and sets
     *  the minimum path weights of all reachable nodes.  The distance
     *  to a node unreachable from V0 is Double.POSITIVE_INFINITY. */
    public static
    <VLabel extends Weightable, ELabel extends Weighted>
    List<Graph<VLabel, ELabel>.Edge>
    shortestPath(Graph<VLabel, ELabel> G,
                 Graph<VLabel, ELabel>.Vertex V0,
                 Graph<VLabel, ELabel>.Vertex V1,
                 Distancer<? super VLabel> h) {
        HashSet<Graph<VLabel, ELabel>.Vertex> closedSet =
            new HashSet<Graph<VLabel, ELabel>.Vertex>();
        PriorityQueue<Graph<VLabel, ELabel>.Vertex> openSet =
            new PriorityQueue<Graph<VLabel, ELabel>.Vertex>(G.vertexSize(),
                    new FComp2<VLabel, ELabel>());
        openSet.add(V0);
        HashMap<Graph<VLabel, ELabel>.Vertex, Graph<VLabel, ELabel>.Edge>
            parent = new HashMap<Graph<VLabel, ELabel>.Vertex,
                   Graph<VLabel, ELabel>.Edge>();
        parent.put(V0, null);
        HashMap<Graph<VLabel, ELabel>.Vertex, Double> gScores =
            new HashMap<Graph<VLabel, ELabel>.Vertex, Double>();
        gScores.put(V0, 0.0);
        List<Graph<VLabel, ELabel>.Edge> path =
            new ArrayList<Graph<VLabel, ELabel>.Edge>();
        for (Graph<VLabel, ELabel>.Vertex v : G.vertices()) {
            if (v != V0) {
                v.getLabel().setWeight(Double.POSITIVE_INFINITY);
            }
        }
        V0.getLabel().setWeight(h.dist(V0.getLabel(), V1.getLabel()));
        while (!openSet.isEmpty()) {
            Graph<VLabel, ELabel>.Vertex curr = openSet.peek();
            if (curr == V1) {
                Graph<VLabel, ELabel>.Edge e = parent.get(curr);
                while (e != null) {
                    curr = e.getV(curr);
                    path.add(e);
                    e = parent.get(curr);
                }
                Collections.reverse(path);
                return path;
            }
            openSet.remove(curr);
            closedSet.add(curr);
            for (Graph<VLabel, ELabel>.Edge e : G.outEdges(curr)) {
                double tG = gScores.get(curr) + e.getLabel().weight();
                double F = tG + h.dist(e.getV(curr).getLabel(), V1.getLabel());
                if (closedSet.contains(e.getV(curr))
                        && F >= e.getV(curr).getLabel().weight()) {
                    continue;
                } else if (!openSet.contains(e.getV(curr))
                        || F < e.getV(curr).getLabel().weight()) {
                    parent.put(e.getV(curr), e);
                    gScores.put(e.getV(curr), tG);
                    e.getV(curr).getLabel().setWeight(F);
                    if (!openSet.contains(e.getV(curr))) {
                        openSet.add(e.getV(curr));
                        parent.put(e.getV(curr), e);
                    }
                }
            }
        }
        return path;
    }

    /** Returns a distancer whose dist method always returns 0. */
    public static final Distancer<Object> ZERO_DISTANCER =
        new Distancer<Object>() {
            @Override
            public double dist(Object v0, Object v1) {
                return 0.0;
            }
        };

}

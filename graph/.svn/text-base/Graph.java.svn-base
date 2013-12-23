package graph;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;

/* Do not add or remove public or protected members, or modify the signatures of
 * any public methods.  You may make changes that don't affect the API as seen
 * from outside the graph package:
 *   + You may make methods in Graph abstract, if you want different
 *     implementations in DirectedGraph and UndirectedGraph.
 *   + You may add bodies to abstract methods, modify existing bodies,
 *     or override inherited methods.
 *   + You may change parameter names, or add 'final' modifiers to parameters.
 *   + You may private and package private members.
 *   + You may add additional non-public classes to the graph package.
 */

/** Represents a general graph whose vertices are labeled with a type
 *  VLABEL and whose edges are labeled with a type ELABEL. The
 *  vertices are represented by the inner type Vertex and edges by
 *  inner type Edge.  A graph may be directed or undirected.  For
 *  an undirected graph, outgoing and incoming edges are the same.
 *  Graphs may have self edges and may have multiple edges between vertices.
 *
 *  The vertices and edges of the graph, the edges incident on a
 *  vertex, and the neighbors of a vertex are all accessible by
 *  iterators.  Changing the graph's structure by adding or deleting
 *  edges or vertices invalidates these iterators (subsequent use of
 *  them is undefined.)
 *  @author Brian Su
 */
public abstract class Graph<VLabel, ELabel> {

    /** Represents one of my vertices. */
    public class Vertex {

        /** A new vertex with LABEL as the value of getLabel(). */
        Vertex(VLabel label) {
            _label = label;
        }

        /** Returns the label on this vertex. */
        public VLabel getLabel() {
            return _label;
        }

        @Override
        public String toString() {
            return String.valueOf(_label);
        }

        /** The label on this vertex. */
        private final VLabel _label;

    }

    /** Represents one of my edges. */
    public class Edge {

        /** An edge (V0,V1) with label LABEL.  It is a directed edge (from
         *  V0 to V1) in a directed graph. */
        Edge(Vertex v0, Vertex v1, ELabel label) {
            _label = label;
            _v0 = v0;
            _v1 = v1;
        }

        /** Returns the label on this edge. */
        public ELabel getLabel() {
            return _label;
        }

        /** Return the vertex this edge exits. For an undirected edge, this is
         *  one of the incident vertices. */
        public Vertex getV0() {
            return _v0;
        }

        /** Return the vertex this edge enters. For an undirected edge, this is
         *  the incident vertices other than getV1(). */
        public Vertex getV1() {
            return _v1;
        }

        /** Returns the vertex at the other end of me from V.  */
        public final Vertex getV(Vertex v) {
            if (v == _v0) {
                return _v1;
            } else if (v == _v1) {
                return _v0;
            } else {
                throw new
                    IllegalArgumentException("vertex not incident to edge");
            }
        }

        @Override
        public String toString() {
            return String.format("(%s,%s):%s", _v0, _v1, _label);
        }

        /** Endpoints of this edge.  In directed edges, this edge exits _V0
         *  and enters _V1. */
        private final Vertex _v0, _v1;

        /** The label on this edge. */
        private final ELabel _label;

    }

    /*=====  Methods and variables of Graph =====*/

    /** Returns the number of vertices in me. */
    public int vertexSize() {
        return _succ.size();
    }

    /** Returns the number of edges in me. */
    public int edgeSize() {
        return (isDirected()) ? _edges.size() : _edges.size() / 2;
    }

    /** Returns true iff I am a directed graph. */
    public abstract boolean isDirected();

    /** Returns the number of outgoing edges incident to V. Assumes V is one of
     *  my vertices.  */
    public int outDegree(Vertex v) {
        return _succ.get(v).size();
    }

    /** Returns the number of incoming edges incident to V. Assumes V is one of
     *  my vertices. */
    public int inDegree(Vertex v) {
        return _pred.get(v).size();
    }

    /** Returns outDegree(V). This is simply a synonym, intended for
     *  use in undirected graphs. */
    public final int degree(Vertex v) {
        return outDegree(v);
    }

    /** Returns true iff there is an edge (U, V) in me with any label. */
    public boolean contains(Vertex u, Vertex v) {
        if (!_succ.containsKey(u) || !_succ.containsKey(v)) {
            return false;
        }
        return _succ.get(u).contains(v);
    }

    /** Returns true iff there is an edge (U, V) in me with label LABEL. */
    public boolean contains(Vertex u, Vertex v,
                            ELabel label) {
        for (Edge e : _edges) {
            if (e.getV0() == u && e.getV1() == v
                    && e.getLabel().equals(label)) {
                return true;
            }
        }
        return false;
    }

    /** Returns a new vertex labeled LABEL, and adds it to me with no
     *  incident edges. */
    public Vertex add(VLabel label) {
        Vertex ver = new Vertex(label);
        _succ.put(ver, new ArrayList<Vertex>());
        _pred.put(ver, new ArrayList<Vertex>());
        return ver;
    }

    /** Returns the edge (U, V). Assumes the contains(u, v) is true. */
    Edge getEdge(Vertex u, Vertex v) {
        for (Edge e : _edges) {
            if (e.getV0() == u && e.getV1() == v) {
                return e;
            }
        }
        return null;
    }

    /** Returns an edge incident on FROM and TO, labeled with LABEL
     *  and adds it to this graph. If I am directed, the edge is directed
     *  (leaves FROM and enters TO). */
    public Edge add(Vertex from,
                    Vertex to,
                    ELabel label) {

        Edge edg = new Edge(from, to, label);
        _edges.add(edg);
        _unDupEdges.add(edg);
        if (!isDirected()) {
            Edge edg2 = new Edge(to, from, label);
            _edges.add(edg2);
        }

        _succ.get(from).add(to);
        _pred.get(to).add(from);
        if (!isDirected()) {
            _succ.get(to).add(from);
            _pred.get(from).add(to);
        }

        return edg;
    }

    /** Returns an edge incident on FROM and TO with a null label
     *  and adds it to this graph. If I am directed, the edge is directed
     *  (leaves FROM and enters TO). */
    public Edge add(Vertex from,
                    Vertex to) {
        return add(from, to, null);
    }

    /** Remove V and all adjacent edges, if present. */
    public void remove(Vertex v) {

        _succ.remove(v);
        _pred.remove(v);
        for (List<Vertex> succ : _succ.values()) {
            while (succ.contains(v)) {
                succ.remove(v);
            }
        }
        for (List<Vertex> pred : _pred.values()) {
            while (pred.contains(v)) {
                pred.remove(v);
            }
        }

        Iterator<Edge> i = _edges.listIterator();
        while (i.hasNext()) {
            try {
                Edge curr = i.next();
                if (curr.getV(v) != null) {
                    i.remove();
                }
            } catch (IllegalArgumentException err) {
                continue;
            }
        }
        i = _unDupEdges.listIterator();
        while (i.hasNext()) {
            try {
                Edge curr = i.next();
                if (curr.getV(v) != null) {
                    i.remove();
                }
            } catch (IllegalArgumentException err) {
                continue;
            }
        }
    }


    /** Remove E from me, if present.  E must be between my vertices,
     *  or the result is undefined.  */
    public void remove(Edge e) {

        _succ.get(e.getV0()).remove(e.getV1());
        _pred.get(e.getV1()).remove(e.getV0());
        if (!isDirected()) {
            _succ.get(e.getV1()).remove(e.getV0());
            _pred.get(e.getV0()).remove(e.getV1());
        }

        Iterator<Edge> i = _edges.listIterator();
        while (i.hasNext()) {
            try {
                Edge curr = i.next();
                if (sameEdge(curr, e)) {
                    i.remove();
                }
            } catch (IllegalArgumentException err) {
                continue;
            }
        }
        i = _unDupEdges.listIterator();
        while (i.hasNext()) {
            try {
                Edge curr = i.next();
                if (e.getV0() == curr.getV0() && e.getV1() == curr.getV1()) {
                    i.remove();
                }
            } catch (IllegalArgumentException err) {
                continue;
            }
        }
    }

    /** Return true of E1 and E2 are the same. */
    private boolean sameEdge(Edge e1, Edge e2) {
        boolean dir = e1.getV0() == e2.getV0() && e1.getV1() == e2.getV1();
        boolean undir = e1.getV0() == e2.getV1() && e1.getV1() == e2.getV0();
        return (isDirected()) ? dir : dir || undir;
    }

    /** Remove all edges from V1 to V2 from me, if present.  The result is
     *  undefined if V1 and V2 are not among my vertices.  */
    public void remove(Vertex v1, Vertex v2) {
        while (_succ.get(v1).contains(v2)) {
            _succ.get(v1).remove(v2);
        }
        while (_pred.get(v2).contains(v1)) {
            _pred.get(v2).remove(v1);
        }
        if (!isDirected()) {
            while (_pred.get(v1).contains(v2)) {
                _pred.get(v1).remove(v2);
            }
            while (_succ.get(v2).contains(v1)) {
                _succ.get(v2).remove(v1);
            }
        }

        Iterator<Edge> i = _edges.listIterator();
        while (i.hasNext()) {
            try {
                Edge curr = i.next();
                if (curr.getV(v1) == v2) {
                    i.remove();
                }
            } catch (IllegalArgumentException err) {
                continue;
            }
        }
        i = _unDupEdges.listIterator();
        while (i.hasNext()) {
            try {
                Edge curr = i.next();
                if (curr.getV0() == v1 && curr.getV1() == v2) {
                    i.remove();
                }
            } catch (IllegalArgumentException err) {
                continue;
            }
        }
    }

    /** Returns an Iterator over all vertices in arbitrary order. */
    public Iteration<Vertex> vertices() {
        return Iteration.iteration(_succ.keySet().iterator());
    }

    /** Returns an iterator over all successors of V. */
    public Iteration<Vertex> successors(Vertex v) {
        return Iteration.iteration(_succ.get(v).iterator());
    }

    /** Returns an iterator over all predecessors of V. */
    public Iteration<Vertex> predecessors(Vertex v) {
        return Iteration.iteration(_pred.get(v).iterator());
    }

    /** Returns successors(V).  This is a synonym typically used on
     *  undirected graphs. */
    public final Iteration<Vertex> neighbors(Vertex v) {
        return successors(v);
    }

    /** Returns an iterator over all edges in me. */
    public Iteration<Edge> edges() {
        if (!isDirected()) {
            return Iteration.iteration(_unDupEdges.iterator());
        }
        return Iteration.iteration(_edges.iterator());
    }

    /** Returns iterator over all outgoing edges from V. */
    public Iteration<Edge> outEdges(Vertex v) {
        ArrayList<Edge> out = new ArrayList<Edge>();
        for (Edge e : _edges) {
            if (e.getV0() == v) {
                out.add(e);
            }
        }
        return Iteration.iteration(out.iterator());
    }

    /** Returns iterator over all incoming edges to V. */
    public Iteration<Edge> inEdges(Vertex v) {
        ArrayList<Edge> in = new ArrayList<Edge>();
        for (Edge e : _edges) {
            if (e.getV1() == v) {
                in.add(e);
            }
        }
        return Iteration.iteration(in.iterator());
    }


    /** Returns outEdges(V). This is a synonym typically used
     *  on undirected graphs. */
    public final Iteration<Edge> edges(Vertex v) {
        return outEdges(v);
    }

    /** Returns the natural ordering on T, as a Comparator.  For
     *  example, if stringComp = Graph.<Integer>naturalOrder(), then
     *  stringComp.compare(x1, y1) is <0 if x1<y1, ==0 if x1=y1, and >0
     *  otherwise. */
    public static <T extends Comparable<? super T>> Comparator<T> naturalOrder()
    {
        return new Comparator<T>() {
            @Override
            public int compare(T x1, T x2) {
                return x1.compareTo(x2);
            }
        };
    }

    /** Cause subsequent calls to edges() to visit or deliver
     *  edges in sorted order, according to COMPARATOR. Subsequent
     *  addition of edges may cause the edges to be reordered
     *  arbitrarily.  */
    public void orderEdges(Comparator<ELabel> comparator) {
        final Comparator<ELabel> comp = comparator;
        Collections.sort(_edges, new Comparator<Edge>() {
            public int compare(Edge e1, Edge e2) {
                return comp.compare(e1.getLabel(), e2.getLabel());
            }
        });
    }

    /** Hashmap that represents an adjacency list. Stores the vertex's label
     * as key and a key's value contains vertexes adjacent to it.*/
    private HashMap<Vertex, List<Vertex>> _succ =
        new HashMap<Vertex, List<Vertex>>();

    /** Hashmap that stores a list containing a vertexes predecessors. */
    private HashMap<Vertex, List<Vertex>> _pred =
        new HashMap<Vertex, List<Vertex>>();

    /** A list that contains all the edges in the graph. */
    private List<Edge> _edges = new ArrayList<Edge>();
    /** A list that contains unduplicated edges in the graph. Used
     * for undirected graph. */
    private List<Edge> _unDupEdges = new ArrayList<Edge>();

}

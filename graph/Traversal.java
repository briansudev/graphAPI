package graph;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Stack;
import java.util.ArrayDeque;
import java.util.PriorityQueue;

/** Implements a generalized traversal of a graph.  At any given time,
 *  there is a particular set of untraversed vertices---the "fringe."
 *  Traversal consists of repeatedly removing an untraversed vertex
 *  from the fringe, visting it, and then adding its untraversed
 *  successors to the fringe.  The client can dictate an ordering on
 *  the fringe, determining which item is next removed, by which kind
 *  of traversal is requested.
 *     + A depth-first traversal treats the fringe as a list, and adds
 *       and removes vertices at one end.  It also revisits the node
 *       itself after traversing all successors by calling the
 *       postVisit method on it.
 *     + A breadth-first traversal treats the fringe as a list, and adds
 *       and removes vertices at different ends.  It also revisits the node
 *       itself after traversing all successors as for depth-first
 *       traversals.
 *     + A general traversal treats the fringe as an ordered set, as
 *       determined by a Comparator argument.  There is no postVisit
 *       for this type of traversal.
 *  As vertices are added to the fringe, the traversal calls a
 *  preVisit method on the vertex.
 *
 *  Generally, the client will extend Traversal, overriding the visit,
 *  preVisit, and postVisit methods, as desired (by default, they do nothing).
 *  Any of these methods may throw StopException to halt the traversal
 *  (temporarily, if desired).  The preVisit method may throw a
 *  RejectException to prevent a vertex from being added to the
 *  fringe, and the visit method may throw a RejectException to
 *  prevent its successors from being added to the fringe.
 *  @author Brian Su
 */
public class Traversal<VLabel, ELabel> {

    /** Initialize a hashmap that is true for a Vertex if it has been
     * visited for graph G. */
    private void initVisited(Graph<VLabel, ELabel> G) {
        _graph = G;
        _visited = new HashMap<Graph<VLabel, ELabel>.Vertex, Boolean>();
        for (Graph<VLabel, ELabel>.Vertex vert : G.vertices()) {
            _visited.put(vert, false);
        }
    }

    /** Used to check which traversal to continueTraversing. */
    private enum Trav {
        /** DFS, BFS, GEN for depth-first, breadth-first, and general
         * respectively. */
        DFS, BFS, GEN;
    }

    /** Perform a traversal of G over all vertices reachable from V.
     *  ORDER determines the ordering in which the fringe of
     *  untraversed vertices is visited.  The effect of specifying an
     *  ORDER whose results change as a result of modifications made during the
     *  traversal is undefined. */
    public void traverse(Graph<VLabel, ELabel> G,
            Graph<VLabel, ELabel>.Vertex v,
            Comparator<VLabel> order) {
        final Comparator<VLabel> o = order;
        _order = order;
        if (!_traversalPaused) {
            initVisited(G);
        }
        _traversalPaused = false;
        _fpq = new PriorityQueue<Graph<VLabel, ELabel>.Vertex>(G.vertexSize(),
                new Comparator<Graph<VLabel, ELabel>.Vertex>() {
                @Override
                public int compare(Graph<VLabel, ELabel>.Vertex v0,
                    Graph<VLabel, ELabel>.Vertex v1) {
                    return o.compare(v0.getLabel(), v1.getLabel());
                }
            });
        _fpq.add(v);
        while (!_fpq.isEmpty()) {
            Graph<VLabel, ELabel>.Vertex curr = _fpq.remove();
            _finalVertex = curr;
            try {
                if (!marked(curr)) {
                    try {
                        visit(curr);
                        mark(curr);
                        for (Graph<VLabel, ELabel>.Edge e : G.outEdges(curr)) {
                            try {
                                if (!marked(e.getV1())) {
                                    _finalEdge = e;
                                    preVisit(e, curr);
                                    _fpq.add(e.getV1());
                                }
                            } catch (RejectException err) {
                                continue;
                            }
                        }
                        _fpq.add(curr);
                    } catch (RejectException err) {
                        continue;
                    }
                }
            } catch (StopException err) {
                _traversalPaused = true;
                _currentTrav = Trav.GEN;
                break;
            }
        }
        if (!_traversalPaused) {
            _finalVertex = null;
            _finalEdge = null;
        }
    }

    /** Initialized the postVisited database from graph G. */
    private void initPostVisited(Graph<VLabel, ELabel> G) {
        _pV = new HashMap<Graph<VLabel, ELabel>.Vertex, Boolean>();
        for (Graph<VLabel, ELabel>.Vertex vert : G.vertices()) {
            _pV.put(vert, false);
        }
    }


    /** Performs a depth-first traversal of G over all vertices
     *  reachable from V.  That is, the fringe is a sequence and
     *  vertices are added to it or removed from it at one end in
     *  an undefined order.  After the traversal of all successors of
     *  a node is complete, the node itself is revisited by calling
     *  the postVisit method on it. */
    public void depthFirstTraverse(Graph<VLabel, ELabel> G,
            Graph<VLabel, ELabel>.Vertex v) {
        if (!_traversalPaused) {
            initVisited(G);
            initPostVisited(G);
        }
        _fs = new Stack<Graph<VLabel, ELabel>.Vertex>();
        b = new Stack<Graph<VLabel, ELabel>.Vertex>();
        _traversalPaused = false;
        _fs.push(v);
        while (!_fs.empty()) {
            Graph<VLabel, ELabel>.Vertex t = _fs.pop();
            _finalVertex = t;
            try {
                if (marked(t)) {
                    if (!_pV.get(t)) {
                        try {
                            postVisit(t);
                            _pV.put(t, true);
                        } catch (RejectException err) {
                            continue;
                        }
                    }
                } else {
                    try {
                        visit(t);
                        mark(t);
                        _fs.push(t);
                        for (Graph<VLabel, ELabel>.Edge e : G.outEdges(t)) {
                            if (!marked(e.getV(t))) {
                                try {
                                    _finalEdge = e;
                                    preVisit(e, t);
                                    _fs.push(e.getV(t));
                                    b.push(e.getV(t));
                                } catch (RejectException err) {
                                    continue;
                                }
                            }
                        }
                        while (!b.isEmpty()) {
                            _fs.push(b.pop());
                        }
                    } catch (RejectException err) {
                        continue;
                    }
                }
            } catch (StopException r) {
                _traversalPaused = true;
                _currentTrav = Trav.DFS;
                break;
            }
        }
        if (!_traversalPaused) {
            _finalVertex = null;
            _finalEdge = null;
        }
    }

    /** Performs a breadth-first traversal of G over all vertices
     *  reachable from V.  That is, the fringe is a sequence and
     *  vertices are added to it at one end and removed from it at the
     *  other in an undefined order.  After the traversal of all successors of
     *  a node is complete, the node itself is revisited by calling
     *  the postVisit method on it. */
    public void breadthFirstTraverse(Graph<VLabel, ELabel> G,
            Graph<VLabel, ELabel>.Vertex v) {
        if (!_traversalPaused) {
            initVisited(G);
            initPostVisited(G);
        }
        _traversalPaused = false;
        _fq = new ArrayDeque<Graph<VLabel, ELabel>.Vertex>();
        _fq.add(v);
        while (!_fq.isEmpty()) {
            Graph<VLabel, ELabel>.Vertex curr = _fq.remove();
            try {
                _finalVertex = curr;
                if (marked(curr)) {
                    if (!_pV.get(curr)) {
                        try {
                            postVisit(curr);
                            _pV.put(curr, true);
                        } catch (RejectException err) {
                            continue;
                        }
                    }
                } else {
                    try {
                        mark(curr);
                        visit(curr);
                        for (Graph<VLabel, ELabel>.Vertex succ
                                : G.successors(curr)) {
                            if (!marked(succ)) {
                                try {
                                    _finalEdge = G.getEdge(curr, succ);
                                    preVisit(G.getEdge(curr, succ), curr);
                                    _fq.add(succ);
                                } catch (RejectException err) {
                                    continue;
                                }
                            }
                        }
                        _fq.add(curr);
                    } catch (RejectException err) {
                        continue;
                    }
                }
            } catch (StopException err) {
                _traversalPaused = true;
                _currentTrav = Trav.BFS;
                break;
            }
        }
        if (!_traversalPaused) {
            _finalVertex = null;
            _finalEdge = null;
        }
    }

    /** Returns false is vertex V has not been visited and true if it has. */
    private boolean marked(Graph<VLabel, ELabel>.Vertex v) {
        return _visited.get(v);
    }

    /** Set the value of V in _VISITED to be true. */
    private void mark(Graph<VLabel, ELabel>.Vertex v) {
        _visited.put(v, true);
    }

    /** Continue the previous traversal starting from V.
     *  Continuing a traversal means that we do not traverse
     *  vertices that have been traversed previously. */
    public void continueTraversing(Graph<VLabel, ELabel>.Vertex v) {
        if (_traversalPaused) {
            switch (_currentTrav) {
            case DFS:
                depthFirstTraverse(theGraph(), v);
                break;
            case BFS:
                breadthFirstTraverse(theGraph(), v);
                break;
            case GEN:
                traverse(theGraph(), v, _order);
                break;
            default:
                break;
            }
        }
    }

    /** If the traversal ends prematurely, returns the Vertex argument to
     *  preVisit, visit, or postVisit that caused a Visit routine to
     *  return false.  Otherwise, returns null. */
    public Graph<VLabel, ELabel>.Vertex finalVertex() {
        return _finalVertex;
    }

    /** If the traversal ends prematurely, returns the Edge argument to
     *  preVisit that caused a Visit routine to return false. If it was not
     *  an edge that caused termination, returns null. */
    public Graph<VLabel, ELabel>.Edge finalEdge() {
        return _finalEdge;
    }

    /** Returns the last graph argument to a traverse routine, or null if none
     *  of these methods have been called. */
    protected Graph<VLabel, ELabel> theGraph() {
        return _graph;
    }

    /** Method to be called when adding the node at the other end of E from V0
     *  to the fringe. If this routine throws a StopException,
     *  the traversal ends.  If it throws a RejectException, the edge
     *  E is not traversed. The default does nothing.
     */
    protected void preVisit(Graph<VLabel, ELabel>.Edge e,
            Graph<VLabel, ELabel>.Vertex v0) {
    }

    /** Method to be called when visiting vertex V.  If this routine throws
     *  a StopException, the traversal ends.  If it throws a RejectException,
     *  successors of V do not get visited from V. The default does nothing. */
    protected void visit(Graph<VLabel, ELabel>.Vertex v) {
    }

    /** Method to be called immediately after finishing the traversal
     *  of successors of vertex V in pre- and post-order traversals.
     *  If this routine throws a StopException, the traversal ends.
     *  Throwing a RejectException has no effect. The default does nothing.
     */
    protected void postVisit(Graph<VLabel, ELabel>.Vertex v) {
    }

    /** The Vertex (if any) that terminated the last traversal. */
    protected Graph<VLabel, ELabel>.Vertex _finalVertex;
    /** The Edge (if any) that terminated the last traversal. */
    protected Graph<VLabel, ELabel>.Edge _finalEdge;
    /** The last graph traversed. */
    protected Graph<VLabel, ELabel> _graph;

    /** A stack representation of the fringe.*/
    private Stack<Graph<VLabel, ELabel>.Vertex> _fs;
    /** A queue representation of the fringe. */
    private ArrayDeque<Graph<VLabel, ELabel>.Vertex> _fq;
    /** A special Priority Queue representation of a Stack. */
    private PriorityQueue<Graph<VLabel, ELabel>.Vertex> _fpq;
    /** Used to reverse the order the children are stuck into the fringe. */
    private Stack<Graph<VLabel, ELabel>.Vertex> b;

    /** A Vertex (Key) is false/true (Value) depending on whether
     * it has been visited.*/
    private HashMap<Graph<VLabel, ELabel>.Vertex, Boolean> _visited;

    /** Used for Iter DFS. Stores whether a vertex has been postvisited. */
    private HashMap<Graph<VLabel, ELabel>.Vertex, Boolean> _pV;

    /** True if the traversal is paused. */
    private boolean _traversalPaused = false;
    /** Stores the current traversal. */
    private Trav _currentTrav;
    /** Stores traversal's comparator. */
    private Comparator<VLabel> _order;


}

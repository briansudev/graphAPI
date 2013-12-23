package make;

import graph.Graph;
import graph.NoLabel;
import graph.Traversal;

import java.util.HashMap;
import java.util.List;

/** Initial class for the 'make' program.
 *  @author Brian Su
 */
public class MakeTraversal extends Traversal<String, NoLabel> {

    @Override
    protected void preVisit(Graph<String, NoLabel>.Edge e,
                            Graph<String, NoLabel>.Vertex v) {
        if (marked(e.getV1())) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    protected void visit(Graph<String, NoLabel>.Vertex v) {
        mark(v);
        CycleTraversal t = new CycleTraversal(_graph, v);
        t.depthFirstTraverse(_graph, v);
    }

    /** Returns if V is marked. */
    private boolean marked(Graph<String, NoLabel>.Vertex v) {
        return _visited.get(v);
    }

    /** Marks V. */
    private void mark(Graph<String, NoLabel>.Vertex v) {
        _visited.put(v, true);
    }

    /** Inits the visited hashmap from TARGETLIST.*/
    protected void initVisited(HashMap<String, Graph<String, NoLabel>.Vertex>
            targetList) {
        _visited = new HashMap<Graph<String, NoLabel>.Vertex, Boolean>();
        for (Graph<String, NoLabel>.Vertex v : targetList.values()) {
            _visited.put(v, false);
        }
    }

    /** Returns true if target in rule R must be built. */
    private boolean mustBuild(Rule r) {
        if (r.getAB()) {
            return false;
        }
        if (!_ages.containsKey(r.getTarget())) {
            return true;
        }
        if (r.getArray() == null) {
            return false;
        }
        for (String p : r.getArray()) {
            if (_ages.get(r.getTarget()) < _ages.get(p)) {
                if (!_targetAndRule.get(r.getTarget()).getAB()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void postVisit(Graph<String, NoLabel>.Vertex v) {
        String target = v.getLabel();
        Rule currRule = _targetAndRule.get(target);
        if (currRule.getAB()) {
            return;
        }
        if (_targetAndRule.get(target).getCS().equals("")) {
            return;
        }
        if (mustBuild(currRule)) {
            _ages.put(target, _currentTime++);
            if (_b.length() > 0) {
                newLine(_b);
            }
            _b.append(_targetAndRule.get(target).getCS());
        }
    }

    /** Returns the string of the stringbuilder. */
    protected String getString() {
        return _b.toString();
    }

    /** Appends a new line to StringBuilder OUT. */
    private void newLine(StringBuilder out) {
        out.append(System.getProperty("line.separator"));
    }

    /** Takes in RULES and TARGETLIST and CURRENTTIME and AGES and
     * sets up Traversal for dfs for graph G. */
    public MakeTraversal(List<Rule> rules,
            HashMap<String, Graph<String, NoLabel>.Vertex> targetList,
            int currentTime,
            HashMap<String, Integer> ages,
            Graph<String, NoLabel> G) {
        _b = new StringBuilder();
        _rules = rules;
        _targetAndRule = new HashMap<String, Rule>();
        _ages = ages;
        _currentTime = currentTime;
        for (Rule r : rules) {
            _targetAndRule.put(r.getTarget(), r);
        }
        initVisited(targetList);
        _graph = G;
    }

    /** The Graph. */
    private Graph<String, NoLabel> _graph;

    /** Current time. */
    private int _currentTime;

    /** Ages of each object. */
    private HashMap<String, Integer> _ages;

    /** List of rules. */
    private List<Rule> _rules;

    /** Key = rule's target. Value = rule. */
    private HashMap<String, Rule> _targetAndRule;

    /** True if a vertex has been visited. */
    private HashMap<Graph<String, NoLabel>.Vertex, Boolean> _visited;

    /** Stringbuilder. */
    private StringBuilder _b;
}

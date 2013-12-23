package make;

import java.util.ArrayList;
import java.util.Arrays;

/** Takes in target, prereqs, command set for rule.
 * @author Brian Su
 */
public class Rule {


    /** Create a new rule with TARGET, PREREQS. */
    public Rule(String target, String prereqs) {
        _target = target;
        _prereqs = prereqs;
        _sb = new StringBuilder();
        _array = prereqs.trim().split("\\s+");
        _alreadyBuilt = false;
        if (_prereqs.equals("")) {
            _array = null;
        }
    }

    /** Merge the PREREQS together. */
    public void merge(String prereqs) {
        ArrayList<String> lst =
            new ArrayList<String>(Arrays.asList(_array));
        String[] pre = prereqs.trim().split("\\s+");
        for (String s : pre) {
            if (!lst.contains(s)) {
                lst.add(s);
            }
        }
        String[] array = new String[lst.size()];
        int count = 0;
        for (String s : lst) {
            array[count++] = s;
        }
        _array = array;
    }

    /** Set command set to ARG. */
    public void setCS(String arg) {
        _commandSet = arg;
    }

    /** Create a new rule for an ALREADYBUILT but not a TARGET, and
     * with no PREREQS. */
    public Rule(String target, String prereqs, boolean alreadybuilt) {
        this(target, prereqs);
        _alreadyBuilt = alreadybuilt;
    }

    /** Target, Pre-reqs, Command-set. */
    private String _target, _prereqs, _commandSet;
    /** Prereqs in the form of an array. */
    private String[] _array;
    /** StringBuilder that collects all the command set lines.*/
    private StringBuilder _sb;
    /** True if rule's target is not a target, but is in fileInfo.
     * Already built. */
    private boolean _alreadyBuilt;

    /** Add ARGS to the command set, on a new line if it isn't the
     * first command set.*/
    public void add(String args) {
        if (_sb.length() > 0) {
            newLine(_sb);
        }
        _sb.append(args);
    }

    /** Appends a new line to StringBuilder OUT. */
    private void newLine(StringBuilder out) {
        out.append(System.getProperty("line.separator"));
    }

    /** Convert the stringbuilder to a string. */
    public void convert() {
        _commandSet = _sb.toString();
    }

    /** Returns the command set. */
    public String getCS() {
        return _commandSet;
    }

    /** Returns the target. */
    public String getTarget() {
        return _target;
    }

    /** Returns the pre-reqs. */
    public String getPrereqs() {
        return _prereqs;
    }

    /** Returns the prereqs array. */
    public String[] getArray() {
        return _array;
    }

    /** Returns alreadyBuilt. */
    public boolean getAB() {
        return _alreadyBuilt;
    }
}

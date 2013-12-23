package trip;

import graph.Weightable;

/** Represents a location in the map.
 * @author Brian Su. */
public class Location implements Weightable {

    /** Create a new location with name C and
     * coordinates X and Y. */
    public Location(String c, double x, double y) {
        _name = c;
        _x = x;
        _y = y;
        _weight = Double.POSITIVE_INFINITY;
    }

    /** Returns X coordinate. */
    public double getX() {
        return _x;
    }

    /** Returns Y coordinate. */
    public double getY() {
        return _y;
    }

    @Override
    public String toString() {
        return _name;
    }

    @Override
    public double weight() {
        return _weight;
    }

    @Override
    public void setWeight(double weight) {
        _weight = weight;
    }

    /** Name. */
    private String _name;
    /** X and Y coordinates. */
    private double _x, _y;
    /** Current estimated distance from the starting pt. */
    private double _weight;
}



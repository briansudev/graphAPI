package trip;

import graph.Graph;
import graph.Weighted;

/** Represents a road.
 * @author Brian Su */
public class Road implements Weighted {

    /** Create a new road with NAME of LENGTH and
     * has DIRECTION of NS, EW, WE, or SN from
     * C0 to C1. */
    public Road(String name, double length, String direction,
            Graph<Location, Road>.Vertex c0, Graph<Location, Road>.Vertex c1) {
        _name = name;
        _origLength = length;
        _length = length;
        _direction = direction;
        _start = c0;
        _end = c1;
    }

    /** Returns the direction that you will be traveling from ORIGIN.*/
    public String getDirection(Graph<Location, Road>.Vertex origin) {
        char s = (origin == _start) ? (_direction.charAt(1))
            : (_direction.charAt(0));
        switch (s) {
        case 'N':
            return "north";
        case 'S':
            return "south";
        case 'W':
            return "west";
        case 'E':
            return "east";
        default:
            throw new IllegalArgumentException();
        }
    }

    /** Returns the first char of the direction. */
    public char getDir() {
        return _direction.charAt(0);
    }

    @Override
    public String toString() {
        return _name;
    }

    @Override
    public double weight() {
        return _length;
    }

    /** Increase the weight of the road by X. */
    public void setWeight(double x) {
        _length = x;
    }

    /** Reset length of road to original.*/
    public void reset() {
        _length = _origLength;
    }

    /** Road's name and its direction. */
    private String _name, _direction;
    /** Start and ending locations. */
    private Graph<Location, Road>.Vertex _start, _end;
    /** Length of the road. */
    private double _length;
    /** Original length of the road. (Without condensing.) */
    private double _origLength;
}

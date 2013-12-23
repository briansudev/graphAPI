package trip;

import java.util.List;
import graph.Distancer;
import static graph.Graphs.shortestPath;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import graph.Graph;
import graph.UndirectedGraph;

/** Initial class for the 'trip' program.
 *  @author Brian Su
 */
public final class Main {

    /** Used to capture location entries. */
    static final Pattern LOCATIONPATTERN = Pattern.compile(
            "L\\s([^\\s\\:\\=\\#]+)"
            + "\\s([-]?[0-9]+\\.?[0-9]+)\\s"
            + "([-]?[0-9]+\\.?[0-9]+)");

    /** Used to capture road entries. */
    static final Pattern ROADPATTERN = Pattern.compile(
            "R\\s([^\\s\\:\\=\\#]+)\\s([^\\s\\:\\=\\#]+)\\s"
            + "([0-9]+\\.[0-9]+)\\s"
            + "([NS|SN|EW|WE]{2})\\s([^\\s\\:\\=\\#]+)");

    /** Entry point for the CS61B trip program.  ARGS may contain options
     *  and targets:
     *      [ -m MAP ] [ -o OUT ] [ REQUEST ]
     *  where MAP (default Map) contains the map data, OUT (default standard
     *  output) takes the result, and REQUEST (default standard input) contains
     *  the locations along the requested trip.
     */
    public static void main(String... args) {
        String mapFileName;
        String outFileName;
        String requestFileName;

        mapFileName = "Map";
        outFileName = requestFileName = null;

        int a;
        for (a = 0; a < args.length; a += 1) {
            if (args[a].equals("-m")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    mapFileName = args[a];
                }
            } else if (args[a].equals("-o")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    outFileName = args[a];
                }
            } else if (args[a].startsWith("-")) {
                usage();
            } else {
                break;
            }
        }

        if (a == args.length - 1) {
            requestFileName = args[a];
        } else if (a > args.length) {
            usage();
        }

        if (requestFileName != null) {
            try {
                System.setIn(new FileInputStream(requestFileName));
            } catch  (FileNotFoundException e) {
                System.err.printf("Could not open %s.%n", requestFileName);
                System.exit(1);
            }
        }

        if (outFileName != null) {
            try {
                System.setOut(new PrintStream(new FileOutputStream(outFileName),
                                              true));
            } catch  (FileNotFoundException e) {
                System.err.printf("Could not open %s for writing.%n",
                                  outFileName);
                System.exit(1);
            }
        }

        trip(mapFileName);
    }

    /** Print a trip for the request on the standard input to the stsndard
     *  output, using the map data in MAPFILENAME.
     */
    private static void trip(String mapFileName) {
        buildDB(mapFileName);
        readRequest();
        System.out.printf("From %s:%n%n", requests.get(0));
        buildPath();

    }

    /** Builds the path from request. */
    private static void buildPath() {
        segmentNum = 1;
        for (int i = 1; i < requests.size(); i++) {
            Graph<Location, Road>.Vertex from = requests.get(i - 1);
            Graph<Location, Road>.Vertex to = requests.get(i);
            List<Graph<Location, Road>.Edge> path =
                new ArrayList<Graph<Location, Road>.Edge>();
            path.addAll(shortestPath(map, from, to, PYTH));
            destination = to.getLabel().toString();
            printRoute(path, from);
            reset(map);
        }
    }

    /** Reset the roads in M to have original length. */
    private static void reset(Graph<Location, Road> m) {
        for (Graph<Location, Road>.Edge e : m.edges()) {
            e.getLabel().reset();
        }
    }

    /** Print the route from PATH from FROM. */
    private static void printRoute(List<Graph<Location, Road>.Edge> path,
            Graph<Location, Road>.Vertex from) {
        ArrayList<Graph<Location, Road>.Edge> condensed =
            new ArrayList<Graph<Location, Road>.Edge>();
        ArrayList<String> directions = new ArrayList<String>();
        for (int i = 0; i < path.size(); i++) {
            directions.add(path.get(i).getLabel().getDirection(from));
            from = path.get(i).getV(from);
            Graph<Location, Road>.Edge currEdge = path.get(i);
            Road r = currEdge.getLabel();
            double roadLength = r.weight();
            if (i + 1 < path.size() - 1) {
                while (i + 1 < path.size() - 1 && sameRoad(currEdge,
                            path.get(i + 1)) && checkDir(currEdge,
                                path.get(i + 1))) {
                    from = path.get(i + 1).getV(from);
                    roadLength += path.get(i + 1).getLabel().weight();
                    i++;
                }
            }
            if (i + 1 == path.size() - 1) {
                if (sameRoad(currEdge, path.get(i + 1)) && checkDir(currEdge,
                                path.get(i + 1))) {
                    roadLength += path.get(i + 1).getLabel().weight();
                    r.setWeight(roadLength);
                    condensed.add(currEdge);
                    break;
                }
            }
            r.setWeight(roadLength);
            condensed.add(currEdge);
        }
        for (int i = 0; i < condensed.size(); i++) {
            if (i == condensed.size() - 1) {
                printSeg(condensed.get(i), directions.get(i), true);
                System.out.println();
            } else {
                printSeg(condensed.get(i), directions.get(i), false);
                System.out.println();
            }
        }
    }

    /** Print a segment of the route for edge E. If it is the LAST line
     * include the destination. Use DIRECTIONS to get directions.*/
    private static void printSeg(Graph<Location, Road>.Edge e,
            String directions,
            boolean last) {
        if (last) {
            System.out.printf("%d. Take %s %s for %.1f miles to %s.",
                    segmentNum++,
                    e.getLabel(), directions,
                    e.getLabel().weight(),
                    destination);
        } else {

            System.out.printf("%d. Take %s %s for %.1f miles.", segmentNum++,
                    e.getLabel(), directions,
                    e.getLabel().weight());
        }
    }

    /** Returns true if ROAD1 and ROAD2 are the same. */
    private static boolean sameRoad(Graph<Location, Road>.Edge road1,
            Graph<Location, Road>.Edge road2) {
        return road1.getLabel().toString().equals(
                road2.getLabel().toString());
    }


    /** Returns true if the directions for ROAD1 and ROAD2 are the same. */
    private static boolean checkDir(Graph<Location, Road>.Edge road1,
            Graph<Location, Road>.Edge road2) {
        char r1 = road1.getLabel().getDir();
        char r2 = road2.getLabel().getDir();
        return ((r1 == 'N' || r1 == 'S') && (r2 == 'N' || r2 == 'S'))
            || ((r2 == 'E' || r2 == 'W') && (r1 == 'W' || r1 == 'E'));
    }


    /** Returns a distancer that returns the straight line distance. */
    private static final Distancer<Location> PYTH =
        new Distancer<Location>() {
            @Override
            public double dist(Location l0, Location l1) {
                double x = (Math.abs(l0.getX() - l1.getX())
                        * (Math.abs(l0.getX() - l1.getX())));
                double y = (Math.abs(l0.getY() - l1.getY())
                        * Math.abs(l0.getY() - l1.getY()));
                return Math.sqrt(x + y);
            }
        };

    /** Read requests from System.in.*/
    private static void readRequest() {
        requests = new ArrayList<Graph<Location, Road>.Vertex>();
        Scanner in = new Scanner(System.in);
        String curr;
        while (in.hasNext()) {
            curr = in.next();
            curr = curr.replace(",", "");
            if (!locations.containsKey(curr)) {
                System.err.printf("Location %s does not exist.", curr);
                System.out.println();
                usage();
            }
            requests.add(vertices.get(curr));
        }
        if (requests.size() < 2) {
            System.err.printf("Origin and Destination not specified.");
            usage();
        }
    }


    /** Scan the MAPFILENAME and construct the map. */
    private static void buildDB(String mapFileName) {
        String current;
        locations = new HashMap<String, Location>();
        map = new UndirectedGraph<Location, Road>();
        vertices = new HashMap<String, Graph<Location, Road>.Vertex>();
        try {
            Scanner in = new Scanner(new FileReader(mapFileName));
            while (in.hasNextLine()) {
                current = in.nextLine();
                Matcher m = LOCATIONPATTERN.matcher(current);
                Matcher m2 = ROADPATTERN.matcher(current);
                if (m.matches()) {
                    if (locations.containsKey(m.group(1))) {
                        usage();
                    }
                    double x = Double.parseDouble(m.group(2));
                    double y = Double.parseDouble(m.group(3));
                    Location loc = new Location(m.group(1), x, y);
                    locations.put(m.group(1), loc);
                    Graph<Location, Road>.Vertex v = map.add(loc);
                    vertices.put(m.group(1), v);

                } else if (m2.matches()) {
                    if (!locations.containsKey(m2.group(1))
                            || !locations.containsKey(m2.group(5))) {
                        System.err.printf("Map does not have %s or %s",
                                m2.group(1), m2.group(5));
                    }
                    double distance = Double.parseDouble(m2.group(3));
                    Graph<Location, Road>.Vertex c0 = vertices.get(m2.group(1));
                    Graph<Location, Road>.Vertex c1 = vertices.get(m2.group(5));
                    Road newRoad = new Road(m2.group(2), distance,
                            m2.group(4), c0, c1);
                    map.add(c0, c1, newRoad);
                } else if (current.equals("")) {
                    continue;
                } else {
                    System.out.println(current);
                    usage();
                }
            }
        } catch (IOException err) {
            usage();
        }
    }

    /** Print a brief usage message and exit program abnormally. */
    private static void usage() {
        System.out.println(
                "Usage: java trip.Main [ -m MAP ] [ -o OUT ] [ REQUEST ]");
        System.exit(1);
    }

    /** Key - Name of location : Value - Location. */
    private static HashMap<String, Location> locations;
    /** The map. */
    private static Graph<Location, Road> map;
    /** Key - Name of location : Value - Vertex on the map. */
    private static HashMap<String, Graph<Location, Road>.Vertex> vertices;
    /** Stores the request. */
    private static ArrayList<Graph<Location, Road>.Vertex> requests;
    /** The segment number. */
    private static int segmentNum;
    /** The destination. */
    private static String destination;
}

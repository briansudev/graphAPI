package make;

import graph.NoLabel;
import graph.Graph;
import graph.DirectedGraph;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


/** Initial class for the 'make' program.
 *  @author Brian Su
 */
public final class Main {

    /** Used to capture T: P1 P2 P3 etc. */
    static final Pattern TARGETPATTERN = Pattern.compile(
            "([^\\s\\:\\=\\#]+):(.*)");
    /** Used to capture command sets. */
    static final Pattern COMMANDSETPATTERN = Pattern.compile(
            "(\\s+(.*))");
    /** Used to capture T: TIME. */
    static final Pattern FILEINFOPATTERN = Pattern.compile(
            "([^\\s\\:\\=\\#]+)\\s+([0-9]+)");

    /** Entry point for the CS61B make program.  ARGS may contain options
     *  and targets:
     *      [ -f MAKEFILE ] [ -D FILEINFO ] TARGET1 TARGET2 ...
     */
    public static void main(String... args) {
        String makefileName;
        String fileInfoName;

        if (args.length == 0) {
            usage();
        }

        makefileName = "Makefile";
        fileInfoName = "fileinfo";

        int a;
        for (a = 0; a < args.length; a += 1) {
            if (args[a].equals("-f")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    makefileName = args[a];
                }
            } else if (args[a].equals("-D")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    fileInfoName = args[a];
                }
            } else if (args[a].startsWith("-")) {
                usage();
            } else {
                break;
            }
        }

        ArrayList<String> targets = new ArrayList<String>();

        for (; a < args.length; a += 1) {
            targets.add(args[a]);
        }

        make(makefileName, fileInfoName, targets);
    }

    /** Carry out the make procedure using MAKEFILENAME as the makefile,
     *  taking information on the current file-system state from FILEINFONAME,
     *  and building TARGETS, or the first target in the makefile if TARGETS
     *  is empty.
     */
    private static void make(String makefileName, String fileInfoName,
                             List<String> targets) {
        HashMap<String, Integer> mappings = new HashMap<String, Integer>();
        List<Rule> rList = new ArrayList<Rule>();
        try {
            Scanner in = new Scanner(new FileReader(makefileName));
            String current = "";
            int ruleCounter = 0;
            Rule currRule = new Rule(current, current);
            int counter = 0;
            while (in.hasNextLine()) {
                current = in.nextLine();
                Matcher m = TARGETPATTERN.matcher(current);
                Matcher m2 = COMMANDSETPATTERN.matcher(current);
                if (m.matches()) {
                    if (ruleCounter != 0) {
                        currRule.convert();
                        if (ruleExists(rList, currRule) != null) {
                            Rule exRule = ruleExists(rList, currRule);
                            if (!currRule.getCS().equals("")
                                    && !exRule.getCS().equals("")) {
                                usage();
                            } else {
                                exRule.merge(currRule.getPrereqs());
                                if (exRule.getCS().equals("")) {
                                    exRule.setCS(currRule.getCS());
                                }
                            }
                        } else {
                            rList.add(currRule);
                        }
                    }
                    String s1 = m.group(1);
                    String s2 = m.group(2);
                    currRule = new Rule(s1, s2);
                    ruleCounter++;
                } else if (m2.matches()) {
                    String s3 = m2.group(1);
                    currRule.add(s3);
                    ruleCounter++;
                }
                if (!in.hasNextLine()) {
                    currRule.convert();
                    rList.add(currRule);
                }
                counter += 1;
            }
            in.close();
            int currentTime = infoScan(fileInfoName, mappings);
            make2(rList, targets, currentTime, mappings);
        } catch (IOException e) {
            System.err.println("IO Exception");
            usage();
        }
    }

    /** Returns the rule if R already exists in RULES. */
    private static Rule ruleExists(List<Rule> rules, Rule r) {
        for (Rule rule : rules) {
            if (rule.getTarget().equals(r.getTarget())) {
                return rule;
            }
        }
        return null;
    }

    /** Returns current time from FILEINFONAME and updates MAPPINGS
     * with objects and their time. */
    private static int infoScan(String fileInfoName, HashMap<String, Integer>
            mappings) {
        Scanner in2;
        int currentTime = 0;
        String current = "";
        try {
            in2 = new Scanner(new FileReader(fileInfoName));
            currentTime = Integer.parseInt(in2.nextLine());
            while (in2.hasNextLine()) {
                current = in2.nextLine();
                Matcher m3 = FILEINFOPATTERN.matcher(current);
                if (m3.matches()) {
                    String s4 = m3.group(1);
                    String s5 = m3.group(2);
                    mappings.put(s4, Integer.parseInt(s5));
                }
            }
            in2.close();
        } catch (IOException err) {
            System.err.println("IO Error.");
            usage();
        } catch (NumberFormatException err) {
            System.err.println("IO Error.");
            usage();
        }
        return currentTime;
    }

    /** Build stuff from RULES, TARGETS, CURRENTTIME, MAPPINGS. */
    private static void make2(List<Rule> rules, List<String> targets,
            int currentTime, HashMap<String, Integer> mappings) {
        int counter = 0;
        HashMap<String, Graph<String, NoLabel>.Vertex> targetList =
            new HashMap<String, Graph<String, NoLabel>.Vertex>();
        Graph<String, NoLabel> g = new DirectedGraph<String, NoLabel>();
        for (Rule r : rules) {
            Graph<String, NoLabel>.Vertex v = g.add(r.getTarget());
            targetList.put(r.getTarget(), v);
        }
        List<Rule> tempRules = new ArrayList<Rule>();
        for (Rule r : rules) {
            Graph<String, NoLabel>.Vertex target =
                targetList.get(r.getTarget());
            if (!(r.getArray() == null)) {
                for (String p : r.getArray()) {
                    if (targetList.containsKey(p)) {
                        Graph<String, NoLabel>.Vertex pre = targetList.get(p);
                        g.add(target, pre);
                    } else if (mappings.containsKey(p)) {
                        Graph<String, NoLabel>.Vertex pre2 = g.add(p);
                        targetList.put(p, pre2);
                        Rule newRule = new Rule(p, "", true);
                        newRule.convert();
                        tempRules.add(newRule);
                        g.add(target, pre2);
                    } else {
                        usage();
                    }
                }
            }
        }
        for (Rule r : tempRules) {
            rules.add(r);
        }
        MakeTraversal trav = new MakeTraversal(rules, targetList, currentTime,
                mappings, g);
        try {
            String start;
            if (targets.size() == 0) {
                start = rules.get(0).getTarget();
                trav.depthFirstTraverse(g, targetList.get(start));
                if (!trav.getString().equals("")) {
                    System.out.println(trav.getString());
                }
            } else {
                for (String s : targets) {
                    start = s;
                    trav.initVisited(targetList);
                    trav.depthFirstTraverse(g, targetList.get(s));
                }
                if (!trav.getString().equals("")) {
                    System.out.println(trav.getString());
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("A cycle exists.");
            usage();
        }
    }

    /** Print a brief usage message and exit program abnormally. */
    private static void usage() {
        System.err.println(
                "java make.Main [ -f MAKEFILE ] [ -D FILEINFO ]"
                + "TARGET1 TARGET2 ...");
        System.exit(1);
    }

}

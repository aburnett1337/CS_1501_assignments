/**
 * A driver for CS1501 Project 4
 * @author	Dr. Farnan
 */
package cs1501_p4;
import java.util.*;

public class App {
    public static void main(String[] args) {
        NetAnalysis na = new NetAnalysis("build/resources/main/network_data2.txt");
        //na.printAdjList();
        //ArrayList<STE> p = na.lowestAvgLatST();
        boolean test = na.connectedTwoVertFail();
        System.out.println(test);

        //int bandwidth = na.bandwidthAlongPath(p);
        //System.out.println("BANDWIDTH ON PATH 0 TO 5 TO 6 TO 0 TO 7 IS " + bandwidth);
        //System.out.println("THE ANSWER TO IF THE GRAPH IS COPPER ONLY CONNECTED IS " + na.copperOnlyConnected());
    }
}

/**
 * Project 4 NetAnalysis object constructor and functions
 * 
 * @author Austin Burnett
 */

package cs1501_p4;

import java.io.*;
import java.util.ArrayList;
import main.java.cs1501_p4.LinkList;
import main.java.cs1501_p4.Node;


public class NetAnalysis implements NetAnalysis_Inter{
    
    private LinkList[] adjList;
    private LinkList[] adjListCopperOnly;
 
    private final double copperCableSpeed = 230000000;
    private final double fiberCableSpeed = 200000000;
    public int numOfVerticies = 0;
    
    // Dijkstra Structures
    private LinkList[] dijkstraAdjList;
    private double[] dijkstraSpeedTo;
    private IndexMinPQ<Double> dijkstraPQ;
    private int[] dijkstraParentOf;
    
    // Prims Structures
    private LinkList[] primsAdjList;
    private double[] primsSpeedTo;
    private IndexMinPQ<Double> primsPQ;
    private boolean[] primsMarked;
    private int[] primsParentOf;

    //Biconnected Structures
    private LinkList[] adjListOneRemoved;
    private int[] low;
    private int[] pre;
    private int cnt;
    private boolean articulation;
    

    //---------------------------------- Constructor ------------------------------------------------------------------------------
    /**
     * Constructor for NetAnalysis. Reads the file and converts all the text input and assigns them to appropriate places
     * @param fname filepath that contains the data
     */
    public NetAnalysis (String fname){
        try{   
            BufferedReader infile = new BufferedReader( new FileReader(fname) );
            boolean isBeginning = true;
            while(infile.ready()){
                String input = infile.readLine();
                if(isBeginning){
                    numOfVerticies = Integer.parseInt(input);
                    adjList = new LinkList[numOfVerticies];
                    adjListCopperOnly = new LinkList[numOfVerticies];
                    isBeginning = false;
                }
                else{
                    String[] networkData = input.split(" ");
                    int v1 = Integer.parseInt(networkData[0]);
                    int v2 = Integer.parseInt(networkData[1]);
                    String wire = networkData[2];
                    int bandwidth = Integer.parseInt(networkData[3]);
                    double length = (double) Integer.parseInt(networkData[4]);
                    double speed = latencySpeed(wire, length);
                    
                    if(adjList[v1] == null) adjList[v1] = new LinkList(v1);
                    if(adjList[v2] == null) adjList[v2] = new LinkList(v2);
                    
                    if(adjListCopperOnly[v1] == null) adjListCopperOnly[v1] = new LinkList(v1);
                    if(adjListCopperOnly[v2] == null) adjListCopperOnly[v2] = new LinkList(v2);
                    
                    adjList[v1].insert(v2, wire, bandwidth, speed);
                    adjList[v2].insert(v1, wire, bandwidth, speed);
                    
                    if(wire.equals("copper")){
                        adjListCopperOnly[v2].insert(v1, wire, bandwidth, speed);
                        adjListCopperOnly[v1].insert(v2, wire, bandwidth, speed);
                    }
                }
            }
            infile.close();
        }
        catch(Exception e){
            System.out.println("FileReading error " + e);
        }
    }
 
    //-------------------------------------- Lowest Latency Path and helper functions ------------------------------------------------
    
    /**
     * Generates a MST that starts from path u. Uses Dijkstra's algorithm to traverse through the graph provided by 
     * adjList and saves the new MST inside of dijkstraAdjList.
     * 
     * Formats dijkstraAdjList to and traverses the list until it reaches u, then populates an ArrayList<Integer> and return that.
     * 
     * @param u the vertex that you are starting the search from
     * @param w the vertex that you are ending the search with
     * 
     * @return list an ArrayList<Integer> that contains the path from u to w
     */
    public ArrayList<Integer> lowestLatencyPath(int u, int w){
        dijkstraAdjList = new LinkList[numOfVerticies];
        dijkstraSpeedTo = new double[numOfVerticies];
        dijkstraParentOf = new int[numOfVerticies];
        dijkstraPQ = new IndexMinPQ<Double>(numOfVerticies);
        for(int i = 0; i < dijkstraSpeedTo.length; i++){
            dijkstraSpeedTo[i] = 0xffffff;
            dijkstraParentOf[i] = -1;
        }
        dijkstra(u);
        for(int i = 0; i < dijkstraAdjList.length; i++){
            if(dijkstraAdjList[i] == null) dijkstraAdjList[i] = new LinkList();
        }
        ArrayList<Integer> list = new ArrayList<Integer>();
        list = search(list, dijkstraAdjList[u].root, w);
        list.add(0, u);
        return list;
    }

    /**
     * helper function to lowestLatencyPath that traverses dijkstraAdjList and returns the path to the finish in list
     * 
     * @param list ArrayList<Integer> that contains the path from u to w
     * @param node The current node you are looking at
     * @param finish the final vertex you are searching for
     * 
     * @return A populated List
     */
    private ArrayList<Integer> search(ArrayList<Integer> list, Node node, int finish){
        if(node == null) return list;
        Node curr = node.next;
        while(curr != null){
            if(curr.data == finish){
                list.add(0, curr.data);
                return list;
            }
            list = search(list, dijkstraAdjList[curr.data].root, finish);

            if(list.contains(finish)){
                list.add(0, curr.data);
                return list;
            }
            curr = curr.next;
        }
        return list;
    }

    /**
     * The heart of the Dijkstra Algorithm. Manages the Priority Queue
     * @param start the vertex you are starting at
     */
    private void dijkstra(int start){
        dijkstraSpeedTo[start] = 0.0;
        dijkstraPQ.insert(start, dijkstraSpeedTo[start]);
        while(!dijkstraPQ.isEmpty()){
            int vertex = dijkstraPQ.delMin();
            dijkScan(vertex);
        }
    }
/**
 * Traverses every child of a node, and checks to see if the path from that node to its children is shorter than
 * the predefined path to the child from another node.
 * 
 * @param vertex the vertex you are currently looking at
 */
    private void dijkScan(int vertex){
        Node root = adjList[vertex].root;
        Node curr = root;
        while(curr != null){
            int other = curr.data;
            if(curr.speedToRoot + dijkstraSpeedTo[vertex] < dijkstraSpeedTo[other]){
                dijkstraSpeedTo[other] = curr.speedToRoot + dijkstraSpeedTo[vertex];
                if(dijkstraAdjList[vertex] == null) dijkstraAdjList[vertex] = new LinkList(vertex);
                dijkstraAdjList[vertex].insert(curr);
                if(dijkstraParentOf[other] != -1){
                    dijkstraAdjList[dijkstraParentOf[other]].remove(curr);
                }
                dijkstraParentOf[other] = vertex;

                if(dijkstraPQ.contains(other)) dijkstraPQ.decreaseKey(other, dijkstraSpeedTo[other]);
                else dijkstraPQ.insert(other, dijkstraSpeedTo[other]);
            }
            curr = curr.next;
        }
    }
    
    //------------------------------------ Bandwidth along path and helper functions --------------------------------------------------

    /**
     * Traverses the ArrayList and follows the path and finds the lowest bandwidth along the path. If the path does not exist in adjList 
     * then it throws an @exception IllegalArgumentException
     * 
     * @param p the ArrayList that contains the path
     * @return the total bandwidth
     */
    public int bandwidthAlongPath(ArrayList<Integer> p) throws IllegalArgumentException{
        int bandwidth;
        int minBandwidth = 0xfffffff;
        int to;
        int from;
        for(int i = 0; i < (p.size()-1); i++){
            to = p.get(i);
            from = p.get(i+1);
            bandwidth = bandwidthFrom(to, from);
            if(bandwidth == -1){ 
                throw new IllegalArgumentException("path does not exist"); 
            }
            if(bandwidth < minBandwidth) minBandwidth = bandwidth;
        }
        return minBandwidth;
    }

    /**
     * helper function to bandwidthAlongPath() takes the two vertexes and returns the bandwidth shared between the two paths
     * 
     * @param to the starting vertex
     * @param from the ending vertex
     * @return the bandwidth between them
     */
    private int bandwidthFrom(int to, int from){
        Node node = adjList[to].contains(from);
        if(node == null) return -1;
        return node.bandwidthToRoot;
    }
    

    // --------------------------------- Copper only Connected and helper functions -------------------------------------------------

    /**
     * Looks at the copper only adjacency list and checks to make sure it is stil a connected graph. In other words, it checks to make sure 
     * it contains the same number of vertexes as the original adjacency list
     * 
     * @return if copperAdjList is a connected graph
     */
    public boolean copperOnlyConnected(){
        boolean[] visited = new boolean[numOfVerticies];
        int size = 0;
        Node start = null;

        for(int i = 0; i < visited.length; i++){
            visited[i] = false;
        }
        for(int i = 0; i < adjListCopperOnly.length; i++){
            if(adjListCopperOnly[i] != null){
                start = adjListCopperOnly[i].root;
                break;
            }
        }
        if(start == null) return false;
        size = tree(start, size, visited);
        return (size == numOfVerticies);
    }

    /**
     * helper function to copperOnlyConnected. Traverses all the connected nodes and keeps count of how many unique 
     * nodes it sees. 
     * 
     * @param node the current root node you are traversing down
     * @param size the current size of all the previously seen nodes
     * @param visited an array that dictates if a node has been seen or not
     * @return the updated size
     */
    private int tree(Node node, int size, boolean[] visited){
        Node curr = node;
        while(curr != null){
            if(visited[curr.data] == false){
                visited[curr.data] = true;
                size++;
            }
            if(curr.next != null && !visited[curr.next.data]) {
                size = tree(adjListCopperOnly[curr.next.data].root, size, visited);
            }
            curr = curr.next;
        }
        return size;

    }

    //------------------------------ connected two vert fail --------------------------------------------------------------------
    /**
     * pretty much copy / paste of Biconnected.java Motifications to now if one articulation point is found then it returns
     * and it loops through every vertex being missing and checks for articulation points. If even one articulation point is 
     * found, then it cannot survive 2 vertex failures
     */
    public boolean connectedTwoVertFail(){
        for(int i = 0; i < numOfVerticies; i++){
            low = new int[numOfVerticies];
            pre = new int[numOfVerticies];
            adjListOneRemoved = new LinkList[numOfVerticies];

            for(int j = 0; j < numOfVerticies; j++){
                adjListOneRemoved[j] = new LinkList();
                Node curr = adjList[j].root;
                while(curr != null){
                    if(curr.data != i) adjListOneRemoved[j].insert(curr);
                    curr = curr.next;
                }
                adjListOneRemoved[i] = null;
                low[j] = -1;
                pre[j] = -1;
            }

            for(int v = 0; v < numOfVerticies; v++){
                if(pre[v] == -1) articulation = dfs(v,v);
            }

            if(articulation == true) return false;      
        }
        return true;
    }

    private boolean dfs(int u, int v){
        int children = 0;
        pre[v] = cnt++;
        low[v] = pre[v];

        if(adjListOneRemoved[v] != null){
            Node w = adjListOneRemoved[v].root;
            while(w != null){
                if(pre[w.data] == -1){
                    children++;
                    dfs(v, w.data);

                    if(low[w.data] < low[v]) low[v] = low[w.data];

                    if(low[w.data] >= pre[v] && u != v) return true;

                }
                else if(w.data != u){
                    if(pre[w.data] < low[v]) low[v] = pre[w.data];
                }
                w = w.next;
            }
            if( u == v && children > 1) return true;

            return false;
        }
        else return false;
    }

    //-----------------------------LowestAveLatST() and helper functions --------------------------------------------------------

    /**
     * Generates a MST that has the least total latency. Behaves similarly to lowestLatencyPath, but instead looks at all nodes 
     * instead of starting at a specific node.
     * 
     * @return an ArrayList<STE> with every vertex pair in the MST
     */
    public ArrayList<STE> lowestAvgLatST(){
        primsAdjList = new LinkList[numOfVerticies];
        primsSpeedTo = new double[numOfVerticies];
        primsMarked = new boolean[numOfVerticies];
        primsPQ = new IndexMinPQ<Double>(numOfVerticies);
        primsParentOf = new int[numOfVerticies];
        for(int i = 0; i < primsSpeedTo.length; i++){
            primsSpeedTo[i] = 0xffffff;
            primsMarked[i] = false;
            primsParentOf[i] = -1;
        }

        for(int i = 0; i < numOfVerticies; i++){
            if(!primsMarked[i]) prims(i);
        }

        for(int i = 0; i < primsAdjList.length; i++){
            if(primsAdjList[i] == null) primsAdjList[i] = new LinkList();
        }

        // printPrimList();
        ArrayList<STE> list = new ArrayList<STE>();
        list = primsAdjToList(list);
        // for(STE i : list){
        //     System.out.print(i + " ");
        // }
        if(list.size() != (numOfVerticies - 1) ) return null;
        return list;
    }

    /**
     * helper function to lowestAvgLatST(). takes the MST generated by Prims Algorithm and converts those vertex into vertex pairs 
     * by looking at the vertex and every child the vertex has.
     * 
     * @param list list that contains all the edges in the MST
     * @return the updated LIst
     */
    private ArrayList<STE> primsAdjToList(ArrayList<STE> list){
        for(int i = 0; i < primsAdjList.length; i++){
            Node curr = primsAdjList[i].root;
            while(curr != null){
                if(curr != primsAdjList[i].root){
                    STE edge = new STE(primsAdjList[i].root.data, curr.data);
                    list.add(edge);
                }
                curr = curr.next;
            }
        }
        return list;
    }

    /**
     * Heart of the Eager Prims Algorithm. Managed the PQ
     * @param start the first node you are looking at.
     */
    private void prims(int start){
        primsSpeedTo[start] = 0.0;
        primsPQ.insert(start, primsSpeedTo[start]);
        while(!primsPQ.isEmpty()){
            int vertex = primsPQ.delMin();
            primsScan(vertex);
        }
    }

    /**
     * The meat of the Eager Prim's Algorithm. Managing if a node was already seen, Checks the existing path to the given vertex against 
     * the path to the vertex from the given node. If it finds a shorter path, it replaces the node.
     * 
     * @param vertex the node you are looking at
     */
    private void primsScan(int vertex){
        primsMarked[vertex] = true;
        Node root = adjList[vertex].root;
        Node curr = root;
        while(curr != null){
            int other = curr.data;
            if(!primsMarked[other]) {
                if(curr.speedToRoot < primsSpeedTo[other]){
                    primsSpeedTo[other] = curr.speedToRoot;
                    if(primsAdjList[vertex] == null) primsAdjList[vertex] = new LinkList(vertex);
                    primsAdjList[vertex].insert(curr);
                    if(primsParentOf[other] != -1){
                        primsAdjList[primsParentOf[other]].remove(curr);
                    }
                    primsParentOf[other] = vertex;

                    if(primsPQ.contains(other)) primsPQ.decreaseKey(other, primsSpeedTo[other]);
                    else primsPQ.insert(other, primsSpeedTo[other]);
                }
            }
            curr = curr.next;
        }
    }

    //================Helper functions======================================

    /**
     * Calculates the latency given the wire and the length of the wire. Speed = length / time
     * 
     * @param wire the wire given. either copper or fiber optic
     * @param length the length of the wire
     * @return the latency of that wire
     */
    
    private double latencySpeed(String wire, double length){
        if( wire.equals("copper") ) return (length / copperCableSpeed);
        else return (length / fiberCableSpeed);
    }

    public void printAdjList(){
        System.out.println("====================  ENTIRE LIST  ============================");
        for(int i = 0; i < adjList.length; i++){
            System.out.print(i + ": ");
            if(adjList[i] != null) {
                Node curr = adjList[i].root;
                System.out.println("PARENT IS " + curr.data );
                System.out.println("WITH CHILDREN:");
                curr = curr.next;
                while (curr != null){
                    System.out.print(curr.data + " ");
                    curr = curr.next;
                }
                System.out.println("\n");
            }
        }
        System.out.println("==================================================================\n\n");
    }

    public void printAdjListOneRemoved(){
        System.out.println("====================  ENTIRE LIST  ============================");
        for(int i = 0; i < adjListOneRemoved.length; i++){
            System.out.print(i + ": ");
            if(adjListOneRemoved[i] != null) {
                Node curr = adjListOneRemoved[i].root;
                System.out.println("PARENT IS " + curr.data );
                System.out.println("WITH CHILDREN:");
                curr = curr.next;
                while (curr != null){
                    System.out.print(curr.data + " ");
                    curr = curr.next;
                }
                System.out.println("\n");
            }
            else{
                System.out.println("is null");
            }
        }
        System.out.println("==================================================================\n\n");
    }
    public void printCopperOnlyList(){
        System.out.println("====================  COPPER ONLY LIST  ============================");
        for(int i = 0; i < adjListCopperOnly.length; i++){
            System.out.print(i + ": ");
            if(adjListCopperOnly[i] != null) {
                Node curr = adjListCopperOnly[i].root;
                System.out.println("PARENT IS " + curr.data );
                System.out.println("WITH CHILDREN:");
                curr = curr.next;
                while (curr != null){
                    System.out.print(curr.data + " ");
                    curr = curr.next;
                }
                System.out.println("\n");
            }
        }
        System.out.println("==================================================================\n\n");

    }

    public void printDijkList(){
        System.out.println("====================  DIJKSTRA LIST  ============================");
        for(int i = 0; i < dijkstraAdjList.length; i++){
            System.out.print(i + ": ");
            if(dijkstraAdjList[i] != null) {
                Node curr = dijkstraAdjList[i].root;
                if(curr != null){
                    System.out.println("PARENT IS " + curr.data );
                    System.out.println("WITH CHILDREN:");
                    curr = curr.next;
                    while (curr != null){
                        System.out.print(curr.data + " ");
                        curr = curr.next;
                    }
                    System.out.println("\n");
                }
            }
        }
        System.out.println("==================================================================\n\n");
    }

    public void printPrimList(){
        System.out.println("====================  PRIMS LIST  ============================");
            for(int i = 0; i < primsAdjList.length; i++){
                System.out.print(i + ": ");
                if(primsAdjList[i] != null) {
                    Node curr = primsAdjList[i].root;
                    if(curr != null){
                        System.out.println("PARENT IS " + curr.data );
                        System.out.println("WITH CHILDREN:");
                        curr = curr.next;
                        while (curr != null){
                            System.out.print(curr.data + " ");
                            curr = curr.next;
                        }
                        System.out.println("\n");
                    }
                }
            }
        System.out.println("==================================================================\n\n");
    }

//EOF
}
/**
 * Basic Node object to be used by LinkList.java
 * 
 * @author Austin Burnett
 */
package main.java.cs1501_p4;

public class Node{
    public int data;
    public Node next;
    public String wireToRoot;
    public int bandwidthToRoot;
    public double speedToRoot;

    public Node(int info){
        data = info;
        wireToRoot = null;
        bandwidthToRoot = 0;
        speedToRoot = 0.0;
    }

    public Node(int info, String wire, int bandwidth, double speed){
        data = info;
        wireToRoot = wire;
        bandwidthToRoot = bandwidth;
        speedToRoot = speed;
    }

    public Node(Node n){
        data = n.data;
        wireToRoot = n.wireToRoot;
        bandwidthToRoot = n.bandwidthToRoot;
        speedToRoot = n.speedToRoot;
    }
}
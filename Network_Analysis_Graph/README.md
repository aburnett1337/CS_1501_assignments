# Network Analysis Project

## What?
This is a project around simulating network flow by being given different paths with different attributes. (bandwidth, wire type, length, etc).

## How?
This project uses multiple data structures and algorithms to simulate a network. For more specfic descriptions, please refer to the specific file and functions.

## Files
  ### App.java 
  Main function. Currently used for compilation and debugging.
  
  ### IndexMinPQ.java
  Indexable Min Priority Queue provided by Sedgewick and Wayne.
  
  ### LinkList.java
  Basic LinkedList class.
  
  ### NetAnalysis.java
  Heart of the program. Contains the functions and constructors for the Network Analysis.
  
  ### NetAnalysis_Inter.java
  The interface for NetAnalysis.java. Contains each main function and a high level description of how it works.
  
  ### Node.java
  Basic Node class used mainly for LinkList.java.
  
  ### 
  STE.java
  Spanning Tree Edge class file. Mainly used for a few functions in NetAnalysis.java.
  
## Main Functions
  ### lowestLatencyPath
  Uses Dijkstra's algorithm to find the shortest path from point A to point B.
  
  ### bandwidthAlongPath
  Goes through an entire path and sums the bandwidth along that path.
  
  ### copperOnlyConnected
  Uses an adjacency list see if the given graph is connected while only looking through copper cables.
  
  ### ConnectedTwoVertFail
  Checks to see if a graph has 2 articulation points. In other words, If you can remove 2 arbitrary vertexes in a graph and see if the graph is still connected.
  
  ### LowestAvgLatST
  Uses Eager Prim's algorithm to create a Minimum Spanning Tree.
  

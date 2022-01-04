/**
 * Basic LinkedList object
 * 
 * @author Austin Burnett
 */
package main.java.cs1501_p4;

public class LinkList{

    public Node root;
    public Node tail;
    public int size;

    public LinkList(int data){
        root = new Node(data);
        tail = root;
        size = 1;
    }

    public LinkList(){
        root = null;
        tail = root;
        size = 0;
    }

    public void insert(int data, String wire, int bandwidth, double length){
        Node curr = new Node(data, wire, bandwidth, length);
        if(root == null){
            root = curr;
            tail = root;
            size++;
            return;
        }
        tail.next = curr;
        tail = curr;
        size++;
        return;
    }

    public void insert(Node n){
        Node curr = new Node(n);
        if(root == null){
            root = curr;
            tail = root;
            size++;
            return;
        }
        tail.next = curr;
        tail = curr;
        size++;
        return;
    }

    public void remove(Node n){
        if(root == null) return;
        if(root.data == n.data){
            root = root.next;
            size--;
            return;
        }
        if(this.contains(n.data) == null) return;
        Node curr = root;
        while(curr.next.data != n.data){
            curr = curr.next;
        }
        curr.next = curr.next.next;
        size--;
        return;
    }
    
    public void deleteAll(){
        if(root == null) return;
        root = null;
        tail = null;
        size = 0;
        return;
    }

    public Node contains(int data){
        if(root == null) return null;

        Node curr = root;
        while(curr != null){
            if(curr.data == data) return curr;
            curr = curr.next;
        }
        return null;
    }

}
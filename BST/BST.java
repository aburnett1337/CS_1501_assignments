// @author Austin Burnett
package cs1501_p1;

import cs1501_p1.BTNode;
import jdk.jfr.BooleanFlag;

public class BST<T extends Comparable<T>> implements BST_Inter<T>{

    /*
    *  Basic BST Constructor. Creates BST with a BTNode<T> of root
    *  that is a null. In other words, it creates an empty tree! :)
    */
    private BTNode<T> root;
    public BST(){
       root = new BTNode<T>(null);
    }

    /*
    * public put(T key)
    *   Arguments: A genertic type T.
    *   Returns: Nothing.
    *
    *   Description:
    *       Basic public put() call, checks for a valid key
    *       then calls the real put() function to insert T key
    *       into the BST
    *
    */

    public void put(T key){
        if(key == null) return;
        root = put(root, key);
        return;
    }

    /*
    * private BTNode put(<BTNode<T> Node, T key)
    *   Arugments: A node of generic type BTNode and a genereic type T
    *   Returns: A genertic type BTNode
    *
    *   Description:
    *       Takes in the root and valid key, and tests for if it is the root.
    *       If it is not, compare key of node to inputted key, and recursively
    *       traverse down the tree until you find where the node is supposed to
    *       be, then insert it.
    */

    private BTNode put(BTNode<T> Node, T key){
        if(Node == null || Node.getKey() == null) return new BTNode<T>(key);
        int val = key.compareTo(Node.getKey());
        if(val < 0) Node.setLeft(put(Node.getLeft(), key));
        if(val > 0) Node.setRight(put(Node.getRight(), key));
        return Node;
    }
  
    /*
    * public boolean contains(T key)
    *   Arguments: A genertic type T.
    *   Returns: boolean value if key was found
    *
    *   Description:
    *       Basic public call to contains(), checks for valid key then calls real
    *       contains() feeding in the root and the inputted key
    */

    public boolean contains(T key){
        if(key == null) return false;
        return get(root,key);

    }

    /*
    * private boolean get(<BTNode<T> Node, T key)
    *   Arugments: A node of generic type BTNode and a genereic type T
    *   Returns: boolean value if Key was in BST
    *
    *   Description:
    *       Take in and check the node to see if it is null. If node is null then 
    *       key is not in the BST. Then compares key to Node.key and recursively 
    *       finds where key is SUPPOSED to be. If it can find it, return true, otherwise
    *       return false.
    */

    private boolean get(BTNode<T> Node, T key){
        if(Node == null) return false;
        int val = key.compareTo(Node.getKey());
        if(val == 0) return true;
        else if(val < 0 && Node.getLeft() != null && get(Node.getLeft(), key) == true)
            return true;
        else if(val > 0 && Node.getRight() != null && get(Node.getRight(), key) == true)
            return true;

        return false;
    }

    /*
    * public delete(T key)
    *   Arguments: A genertic type T.
    *   Returns: Nothing :)
    *
    *   Description:
    *       Basic public call to delete(), checks for valid key in the BST 
    *       that then calls real delete() feeding in the the key the user 
    *       wants to delete.
    */

    public void delete(T key){
        if(key == null || !(contains(key))) return;
        root = delete(root,key);
    }

    /*
    * private BTNode delete(<BTNode<T> Node, T key)
    *   Arugments: A node of generic type BTNode and a genereic type T
    *   Returns: Root with the deleted node
    *
    *   Description:
    *       Check if the inputted Node is null; if it is there's nothing for ya
    *       to do. If its not null, compare inputted key to Node.key and recursively
    *       find where the key is. Once found, set Node to be the smallest value 
    *       that is LARGER than the key, then return the root with the node removed.
    */

    private BTNode delete(BTNode<T> Node, T key){
        if(Node == null) return null;
        int val = key.compareTo(Node.getKey());
        if(val < 0) Node.setLeft(delete(Node.getLeft(), key));
        else if(val > 0) Node.setRight(delete(Node.getRight(), key));
        else{
            if (Node.getRight() == null) return Node.getLeft();
            if (Node.getLeft() == null) return Node.getRight();
            BTNode<T> filler = Node;
            Node = min(filler.getRight());
            Node.setRight(removeMin(filler.getRight()));
            Node.setLeft(filler.getLeft());
        }

        return Node;
    }

    /*
    * private BTNode min(<BTNode<T> Node)
    *   Arugments: A node of generic type BTNode
    *   Returns: The smallest node of that tree
    *
    *   Description:
    *       Recurisvely traverse a tree or subtree to find its smallest
    *       value and return that Node associated with that value.
    */

    private BTNode min(BTNode<T> Node){
        if (Node.getLeft() == null) return Node; 
        else return min(Node.getLeft()); 
    }
 
    /*
    * private BTNode removeMin(<BTNode<T> Node)
    *   Arugments: A node of generic type BTNode
    *   Returns: The smallest node of that tree
    *
    *   Description:
    *       Recurisvely traverse a tree or subtree to find its smallest
    *       value and return that Node associated with that value REMOVED.
    */

    private BTNode removeMin(BTNode<T> Node){
        if (Node.getLeft() == null) return Node.getRight();
        Node.setLeft(removeMin(Node.getLeft()));

        return Node;
    }

    /*
    * public int height()
    *   Arguments: None :)
    *   Returns: the height of the BST
    *
    *   Description:
    *       Basic public call to height(), calls real height() to find the 
    *       height of the BST.
    */

    public int height(){
        return height(root);
    }

    /*
    * private int height(<BTNode<T> Node)
    *   Arugments: A node of generic type BTNode
    *   Returns: the height of the BST
    *
    *   Description:
    *       Since the definition of height we are using is a BST that is just a 
    *       root has height 1, we check if we have a valid root. If we do, take 
    *       the height of each subtree recursively and return whichever subtree
    *       has the greatest height.
    */

    private int height(BTNode<T> Node){
        if (root == null) return 0;
        int leftTreeHeight = 1;
        int rightTreeHeight = 1;

        if(Node.getLeft() != null) leftTreeHeight = 1 + height(Node.getLeft());
        if(Node.getRight() != null) rightTreeHeight = 1 + height(Node.getRight());

        if(leftTreeHeight < rightTreeHeight) return (rightTreeHeight);
        else return (leftTreeHeight);
    }

    /*
    * public boolean isBalanced()
    *   Arguments: None :)
    *   Returns: boolean if tree is balanced
    *
    *   Description:
    *       Basic public call to isBalanced(), simlpy feeds in the root to the 
    *       real isBalanced() method and returns.
    */

    public boolean isBalanced(){
        return (isBalanced(root));
    }

    /*
    * private boolean isBalanced(BTNode<T> Node)
    *   Arugments: A node of generic type BTNode
    *   Returns: boolean if tree is balanced
    *
    *   Description:
    *       Uses the same logic as height(), take the height of each subtree recursively 
    *       and compare those heights. If they are within one of eachother, then the tree is
    *       Balanced, so return true. Otherwise return false.
    */

    private boolean isBalanced(BTNode<T> Node){
        if (root == null) return true;
        int leftTreeHeight = 1;
        int rightTreeHeight = 1;

        if(Node.getLeft() != null) leftTreeHeight = 1 + height(Node.getLeft());
        if(Node.getRight() != null) rightTreeHeight = 1 + height(Node.getRight());

        if(leftTreeHeight - rightTreeHeight <= 1 || leftTreeHeight - rightTreeHeight <= 1) return true;
        else return false;
    }

    /*
    * public String inOrderTraversal()
    *   Arguments: None :)
    *   Returns: String of the traversed BST.
    *
    *   Description:
    *       Basic public call to inOrderTraversal(), if there is no root then there
    *       is no tree to traverse. otherwise return the inOrderTraversal.
    */

    public String inOrderTraversal(){
        if(root == null) return "";
        return(inOrderTraversal(root));
    }

    /*
    * private String inOrderTraversal(BTNode<T> Node)
    *   Arugments: A node of generic type BTNode
    *   Returns: String of the traversed BST.
    *
    *   Description:
    *       This is using the Morris Algorithm for an interative 
    *       In Order BST traversal. Uses a prev and curr pointer to
    *       traverse the tree from left to right, saving the vlaue of 
    *       each node as it passed it FROM left to right. It returns
    *       a substring of traversal except for the last character because
    *       it is a ":" and I did not feel like having a test case just to see if
    *       it was the last node.
    */

    private String inOrderTraversal(BTNode<T> Node){
        String traversal = "";
        BTNode<T> curr = root;
        BTNode<T> prev;
        while(curr != null){
            if(curr.getLeft() == null){
                traversal += curr.getKey().toString() + ":";
                curr = curr.getRight();
            }
            else{
                prev = curr.getLeft();
                while(prev.getRight() != null && prev.getRight() != curr) 
                    prev = prev.getRight();
                if(prev.getRight() == null){
                    prev.setRight(curr);
                    curr = curr.getLeft();
                }
                else{
                    prev.setRight(null);
                    traversal += curr.getKey().toString() + ":";
                    curr = curr.getRight();
                }
            }
        }

        return traversal.substring(0, (traversal.length()-1) );
    }

    /*
    * public String serialize()
    *   Arguments: None :)
    *   Returns: String of the pre-order traversed BST.
    *
    *   Description:
    *       Basic public call to serialize(), if there is no root then there
    *       is no tree to traverse. otherwise set traversal to the serialization
    *       of the root - 1 because we dont want the last ",".
    */

    public String serialize(){
        if(root == null) return "";
        String traversal = serialize(root);
        return traversal.substring(0, (traversal.length()-1) );
    }

    /*
    * private String serialize(BTNode<T> Node)
    *   Arugments: A node of generic type BTNode
    *   Returns: String of the pre-order traversed BST.
    *
    *   Description:
    *       This is a recursive implementation of a pre-order Traversal. First,
    *       you check for the root and save the correct format in traversal. Move 
    *       down the tree and you check if its a leaf node. If it is a leaf node,
    *       save the correct format in traversal and move back up the tree. If it is
    *       an internal node with ONLY a right child, save the node, and its null left
    *       child and continue your recursion down the tree. If its an internal node
    *       with ONLY a left child, save the node, recurse down the tree, and ON THE WAY
    *       BACK UP, save the null right child. If it is an internal node with 2 children,
    *       save the nodea and recurse down both sides of the tree. Finally return the traversal string.
    */

    private String serialize(BTNode<T> Node){
        String traversal = "";
        if(Node == null) return traversal;
        if(Node.getKey() == root.getKey()){
            traversal += "R(" + Node.getKey().toString() + "),";
            traversal += serialize(Node.getLeft());             
            traversal += serialize(Node.getRight());                
        }

        else if(Node.getLeft() == null && Node.getRight() == null){
            traversal += "L(" + Node.getKey().toString() + "),";    
        }

        else if(Node.getLeft() == null && Node.getRight() != null){
            traversal += "I(" + Node.getKey().toString() + "),";
            traversal += "X(NULL),";
            traversal += serialize(Node.getRight());            
        }

        else if(Node.getLeft() != null && Node.getRight() == null){
            traversal += "I(" + Node.getKey().toString() + "),";
            traversal += serialize(Node.getLeft());
            traversal += "X(NULL),";
        }

        else{
            traversal += "I(" + Node.getKey().toString() + "),";
            traversal += serialize(Node.getLeft());
            traversal += serialize(Node.getRight());
        }

        return traversal;
    }
 
    /*
    * public BST_Inter<T> reverse()
    *   Arguments: None :)
    *   Returns: a new reversal of the default tree
    *
    *   Description:
    *       Basic public call to reverse(). Creates a new tree, assigns its
    *       root to be the same as the old tree and populates that new tree
    *       by reversing the old tree and returns the new tree.
    */

    public BST_Inter<T> reverse(){
        BST oldTree = this;
        if (root == null) return oldTree;
        BST newTree = new BST();
        BTNode<T> newRoot = new BTNode<T>(root.getKey());
        newTree.root = newRoot;
        reverse(newTree.root, oldTree.root);
        return newTree;
    }

     /*
    * private void reverse(BTNode<T> newNode, BTNode<T> oldNode)
    *   Arugments: 2 nodes of generic type BTNode, one for newTree and one for oldTree
    *   Returns: Nothing :)
    *
    *   Description:
    *       Assuming oldNode is the node you want to reverse and oldNode has a valid node,
    *       create a new node left that has the key of oldNode and set that to newNodes right.
    *       Do the same process for the newNodes left. Then, recurse down both oldNode.left subtree
    *       and oldNode.right subtree.
    */

    private void reverse(BTNode<T> newNode, BTNode<T> oldNode){
        if(oldNode.getLeft() != null){
            BTNode<T> left = new BTNode<T>(oldNode.getLeft().getKey());
            newNode.setRight(left);
        }
        if(oldNode.getRight() != null){
            BTNode<T> right = new BTNode<T>(oldNode.getRight().getKey());
            newNode.setLeft(right);
        }

        if(oldNode.getRight() != null)
            reverse(newNode.getLeft(), oldNode.getRight());
        if(oldNode.getLeft() != null)
            reverse(newNode.getRight(), oldNode.getLeft());

        return;
    }
}
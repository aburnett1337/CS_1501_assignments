/**
 * DLB class file
 * Written by Austin Burnett
 * Last Edited 10/19/2021
 */
package cs1501_p2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import cs1501_p2.DLBNode;

public class DLB implements Dict{

    /**
     * Global Variables:
     *  root:       the root of the DLB Tree
     *  currString: the string saved in memory that represents the user string inputted
     * count:       number of valid words in the DLB
     */

    private DLBNode root = null;
    public String currString = "";
    private int count;

    /**
     * public DLB constructor. Creates an empty tree with only \0 existing in the root
     */
    public DLB(){
        root = new DLBNode('\0');
    }

    /**
     * Takes in user inputted String, and adds the string into the DLB 1 character at a 
     * time, then a '.' to simulate the end of string and increment count.
     * @param key String key to add into the DLB
     */
    public void add(String key){
        if (root == null) return;
        if (key == null) return;

        char[] word = key.toCharArray();
        DLBNode curr = root;

        for(int i = 0; i < word.length; i++){
            curr = add(curr, word[i], i);
        }
        curr.setDown(new DLBNode('.'));
        count++;
    }

    /**
     * Main logic add function. Takes a character and finds where it is supposted to be entered.
     * @param prev node reference to the previous key. useful for determining which child to follow
     * @param key char to insert into the DLB
     * @param place int to tell which char of the string you are entering. only used for checking first node
     * @return DLBNode that points to the next char position for that string
     */
    private DLBNode add(DLBNode prev, char key, int place){

        if(prev.getLet() == '\0'){
            root = new DLBNode(key);
        }

        if(place == 0){
            if(prev.getLet() == key){
                return root;
            }
            DLBNode curr = nodeInColumn(root, key);
            if(curr.getLet() == key){
                return curr;
            }
            else{
                curr.setRight(new DLBNode(key));
                return curr.getRight();
            }

        }

        if(prev.getDown() != null){
            if(prev.getDown().getLet() == key){
                return prev.getDown();
            }
            else{
                DLBNode curr = nodeInColumn(prev.getDown(), key);
                if(curr.getLet() == key){
                    if(curr.getDown() != null){
                        return curr;
                    }
                    else{
                        curr.setDown(new DLBNode(key));
                        return curr.getDown();
                    }
                }
                else{
                    curr.setRight(new DLBNode(key));
                    return curr.getRight();
                }
            }
        }
        else prev.setDown(new DLBNode(key));
        return prev.getDown();
    }

    /**
     * public contains method. checks for valid root and key and checks if key is 
     * in DLB with root.
     * @param key String key you are searching for
     * @return boolean value if you found the entire string or not.
     */
    public boolean contains(String key){
        if(root == null) return false;
        if(key == null) return false;

        DLBNode cur = contains(root,key);
        if(cur != null && cur.getDown().getLet() == '.') return true;
        else return false;
    }

    /**
     * private contains method. Checks to see if the String is located staring from Node in the DLB
     * @param node DLBNode you are starting the search from
     * @param key String key you are searching for
     * @return The node that corresponds to the last letter in the string
     */
    private DLBNode contains(DLBNode node, String key){
        DLBNode curr = node;
        char[] word = key.toCharArray();
        for(int i = 0; i < word.length; i++){
            curr = nodeInColumn(curr, word[i]);
            if(curr.getLet() != word[i]) return null;
            else if(i != (word.length - 1)) curr = curr.getDown();
        }
        return curr;
    }

    /**
     * private function that searches if key is a node in the same column as input node
     * @param node the node that you are checking of it's siblings
     * @param key the value of node that you are searching for
     * @return DLBNode that either contains key or is the last sibling in the tree
     */
    private DLBNode nodeInColumn(DLBNode node, char key){
        if(node.getLet() == key || node.getRight() == null) return node;
        else return nodeInColumn(node.getRight(), key);
        
    }

    /**
     * pubilc containsPrefix method to check if the string pre is located
     * in the DLB. Uses private contains.
     * @param pre String that we are searching for inside of the DLB
     * @return boolean if pre is inside of the DLB
     */
    public boolean containsPrefix(String pre){
        if(root == null) return false;
        if(pre == null) return false;

        DLBNode cur = contains(root, pre);
        if(cur != null) return true;
        else return false;
    }

    /**
     * takes in a char, adds it to the global currString and checks to see if currString is a valid
     * word, prefix or both
     * @param next char the new char you add to currString
     * @return int between -1 --> 2: -1 = neither, 0 = valid prefix, 1 = valid word, 2 = valid prefix and word  
     */
    public int searchByChar(char next){
        if(root == null || next == '\0') return -1;
        currString += next;

        DLBNode curr = root;
        char[] word = currString.toCharArray();
        for(int i = 0; i < word.length; i++){
            curr = nodeInColumn(curr, word[i]);
            if(curr.getLet() != word[i]) return -1;
            
            else curr = curr.getDown();
        }
        if(curr.getLet() == '.'){
            if(curr.getRight() == null) return 1;
            else return 2;
        }
        else return 0;

    }

    /**
     * resets currString back to an empty string.
     */
    public void resetByChar(){
        currString = "";
        return;
    }

    /**
     * takes the currString and makes a sorted suggestion based off of 5 closest words in the DLB 
     * @return ArrayList that contains the 5 or less suggested words
     */
    public ArrayList<String> suggest(){
        if(root == null) return null;
        if(currString == null) return null;

        ArrayList arrList = new ArrayList<String>();
        DLBNode curr = contains(root, currString);
        if(curr == null) return arrList;
        arrList = traverseOrSuggest(arrList, curr, false);
        Collections.sort(arrList);
        return arrList;
    }

    /**
     * saves the entire DLB into an ArrayList
     * @return ArrayList containing every word in the DLB
     */
    public ArrayList<String> traverse(){
        if(root == null) return null;
        ArrayList<String> arrList = new ArrayList<String>();
        arrList = traverseOrSuggest(arrList, root, true);
        Collections.sort(arrList);
        return arrList;
    }

    /**
     * starter function to create an empty string to add into the ArrayList
     * @param arrList ArrayList containing either the traverse or suggest
     * @param node the DLBNode that you are starting your search from
     * @param isTraversal boolean for if you are traversing or suggesting
     * @return the ArrayList that contains either the traverse or suggest
     */
    private ArrayList<String> traverseOrSuggest(ArrayList<String> arrList, DLBNode node, boolean isTraversal){
        String strToAdd = "";
        DLBToString(arrList, strToAdd, node, isTraversal);
        return arrList;
    }

    /**
     * meat of the traversal and suggestions. recursively goes through tree adding every word in the arraylist
     * @param arrList  ArrayList containing either the traverse or suggest
     * @param s String that contains the word you are adding to
     * @param node DLBNode containing the letter you want
     * @param isTraversal boolean for if you are traversing or suggesting
     * @return the ArrayList that contains either the traverse or suggest
     */
    private ArrayList<String> DLBToString(ArrayList<String> arrList, String s, DLBNode node, boolean isTraversal){
        if( isTraversal || arrList.size() < 5){
            if(node.getLet() != '.') s += node.getLet();
            if(node.getDown() != null) arrList = DLBToString(arrList, s, node.getDown(), isTraversal);

            if(node.getDown() != null && node.getDown().getLet() == '.' && !arrList.contains(s)) {
                arrList.add(s);
            }
            if(node.getLet() != '.') s = s.substring(0, (s.length() - 1) );
            if(node.getRight() != null) arrList = DLBToString(arrList, s, node.getRight(), isTraversal);

            return arrList;
        }
        else return arrList;
    }

    /**
     * count function
     * @return returns the global count.
     */
    public int count(){
        return count;
    } 
}
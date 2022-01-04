/**
 * DLBNode class for CS1501 Project 2
 * @author	Dr. Farnan, edited by Austin Burnett
 */
package cs1501_p2;

import java.io.Serializable;

public class DLBNodeU implements Serializable {
	
	/**
	 * Letter represented by this DLBNode
	 */
	private char let;

	/**
	 * Lead to other alternatives for current letter in the path
	 */
	private DLBNodeU right;

	/**
	 * Leads to keys with prefixed by the current path
	 */	
	private DLBNodeU down;

    /**
     * Shows the amount of times a word is added (only applies to '.')
     */
    private int frequency;

	/**
	 * Constructor that accepts the letter for the new node to represent
	 */
	public DLBNodeU(char let) {
		this.let = let;
		
		this.right = null;
		this.down = null;
        this.frequency = 0;
	}

	/**
	 * Getter for the letter this DLBNode represents
	 *
	 * @return	The letter
	 */
	public char getLet() {
		return let;
	}

	/**
	 * Getter for the next linked-list DLBNode
	 *
	 * @return	Reference to the right DLBNode
	 */
	public DLBNodeU getRight() {
		return right;
	}

	/**
	 * Getter for the child DLBNode
	 *
	 * @return	Reference to the down DLBNode
	 */
	public DLBNodeU getDown() {
		return down;
	}

    /**
     * 
     * @return Reference to the frequency element
     */
    public int getFrequency() {
        return frequency;
    }

	/**
	 * Setter for the next linked-list DLBNode
	 *
	 * @param	r DLBNode to set as the right reference
	 */
	public void setRight(DLBNodeU r) {
		right = r;
	}

	/**
	 * Setter for the child DLBNode
	 *
	 * @param	d DLBNode to set as the down reference
	 */
	public void setDown(DLBNodeU d) {
		down = d;
	}

    /**
     * 
     * @param f frequency to set as the new frequency
     */
    public void setFrequency(int f) {
        frequency = f;
    }
}

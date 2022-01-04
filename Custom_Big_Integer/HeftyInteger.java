/**
 * HeftyInteger for CS1501 Project 5
 * @author	Austin Burnett
 */
package cs1501_p5;

import java.util.Random;

public class HeftyInteger {

	private final byte[] ONE = {(byte) 1};
	private final byte[] ZERO = {(byte) 0};
	private final byte BITMASK = (byte) 1;

	private byte[] val;

	/**
	 * Construct the HeftyInteger from a given byte array
	 * @param b the byte array that this HeftyInteger should represent
	 */
	public HeftyInteger(byte[] b) {
		val = b;
	}

	/**
	 * Return this HeftyInteger's val
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	public int lengthInBits(){
		int bits = val.length * 8;
		if(this.getVal()[0] == ZERO[0]) bits++;
		for(int i = 0; i < this.length(); i++){
			for(int j = 7; j >= 0; j--){
				byte bit = (byte) (this.getVal()[i] >> j);
				bit = (byte) (bit & BITMASK);
				if( bit == (byte)0 ) bits--;
				else return bits;
			}
		}
		return bits;
	}

	/**
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other HeftyInteger to sum with this
	 */
	public HeftyInteger add(HeftyInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		HeftyInteger res_li = new HeftyInteger(res);

		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public HeftyInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		HeftyInteger neg_li = new HeftyInteger(neg);

		// add 1 to complete two's complement negation
		return neg_li.add(new HeftyInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other HeftyInteger to subtract from this
	 * @return difference of this and other
	 */
	public HeftyInteger subtract(HeftyInteger other) {
		return this.add(other.negate());
	}

	/**
	 * Compute the product of this and other
	 * @param other HeftyInteger to multiply by this
	 * @return product of this and other
	 */
	public HeftyInteger multiply(HeftyInteger other) {
		HeftyInteger t1 = this;
		HeftyInteger t2 = other;
		byte[] num1, num2;
		byte[] extended = { (byte) 0 };
		boolean negProduct = false;
				
		//if multiplying by 1, return the number thats not one
		if(this.getVal().length == 1 && this.getVal()[0] == (byte)1) return other;
		if(other.getVal().length == 1 && other.getVal()[0] == (byte)1) return this;

		// instead of multiplying by negative numbers, just do the positive multiplication
		// then flip the answer in the end
		// x * y = z = -x * -y
		// x * -y = -x * y = -z
		byte bit1 = (byte) (t1.getVal()[0]);
		bit1 = (byte) ((bit1 >> 7) & BITMASK);

		byte bit2 = (byte) (t2.getVal()[0]);
		bit2 = (byte) ((bit2 >> 7) & BITMASK);
		
		if(bit1 == (byte)1 & bit2 == (byte)1){
			t1 = t1.negate();
			t2 = t2.negate();
		}
		else if(bit1 == (byte)1 & bit2 == (byte)0){
			t1 = t1.negate();
			negProduct = true;
		}
		else if(bit1 == (byte)0 & bit2 == (byte)1){
			t2 = t2.negate();
			negProduct = true;
		}
		
		// take the number with less bytes and iterate through that
		if(this.length() < other.length()){
			num1 = t2.getVal();
			num2 = t1.getVal();
		}
		else{
			num1 = t1.getVal();
			num2 = t2.getVal();
		}
		
		// contains all the sums we will be adding up and its size
		HeftyInteger[] arrOfHeftInts = new HeftyInteger[num1.length*8];
		int heftIntsCount = 0;

		//finds all the numbers you need to add by. Gradeschool Algorithm
		for(int i = 0; i < num2.length; i++){
			for(int j = 7; j >= 0; j--){
				byte bit = (byte) ((num2[i] >> j) & BITMASK);

				if(bit == 1){
					int shift = ( (num2.length - 1 - i) * 8) + j;
					extended = shiftLeft(num1, shift);
					HeftyInteger newInt = new HeftyInteger(extended);
					arrOfHeftInts[heftIntsCount] = newInt;
					heftIntsCount++;
				}
			}
		}
		
		// add all those numbers up
		HeftyInteger sum = new HeftyInteger(ZERO);
		for(int i = 0; i < heftIntsCount; i++){
			sum = sum.add(arrOfHeftInts[i]);
		}
		
		HeftyInteger answer = new HeftyInteger((sum.getVal()));
		// if the product should be negative, negate the sum and make sure you have 
		// all the leading 1's
		if(negProduct){
			answer = answer.negate();
			int length = sum.lengthInBits();
			length = length % 8;
			byte mask = (byte) ((byte)-1 << length);
			sum.getVal()[0] = (byte) (sum.getVal()[0] | mask);
		}
		answer = new HeftyInteger(removeDeadBytes(answer.getVal()));
		return answer;
	}

	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another HeftyInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other 	
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	*/
	public HeftyInteger[] XGCD(HeftyInteger other) {
		HeftyInteger[] answer = new HeftyInteger[3];
		boolean flip = false;
		boolean flipX = false;
		boolean flipY = false;
		HeftyInteger num1 = new HeftyInteger(this.getVal());
		HeftyInteger num2 = new HeftyInteger(other.getVal());

		// GCD(x,y) where x = y
		if(!isGreaterThan(this, other)) flip = true;
		
		// if one/both of the numbers are negative, negate that number
		// negate them and do the GCD of the positive numbers,
		// then just negate the x and y at the end
		byte signNum1 = (byte) ((num1.getVal()[0] >> 7) & BITMASK );
		byte signNum2 = (byte) ((other.getVal()[0] >> 7) & BITMASK );
		if(signNum1 == (byte)1){
			flipX = true;
			num1 = num1.negate();
		}

		if(signNum2 == (byte)1){
			flipY = true;
			num2 = num2.negate();
		}
	
		// recursive call to the GCD function
		if(flip) answer = GCD(num2, num1, answer);
		else answer = GCD(num1, num2, answer);
		
		// do all necessary flips
		if(flip) answer = new HeftyInteger[] {answer[0], answer[2], answer[1]};
		if(flipX) answer = new HeftyInteger[] {answer[0], answer[1].negate(), answer[2]};
		if(flipY) answer = new HeftyInteger[] {answer[0], answer[1], answer[2].negate()};

		return answer;
	}

	/**
	 * Heart of the XGCD function, actually handles the GCD part
	 * @param a HeftyInteger a that holds x 
	 * @param b HeftyInteger b that hold y
	 * @param answer HeftyInteger[] that holds our {GCD, x, y}
	 * @return updated HeftyInteger[] answer
	 */
	public HeftyInteger[] GCD(HeftyInteger a, HeftyInteger b, HeftyInteger[] answer){
		// base case. if B == 0, then you're done!
		if( b.length() == 1 && b.getVal()[0] == (byte)0) return new HeftyInteger[]{a, new HeftyInteger(ONE), new HeftyInteger(ZERO)};

		HeftyInteger[] divide = a.divide(b);
		HeftyInteger quotient = divide[0];
		HeftyInteger remainder = divide[1];
		// recurive GCD call
		answer = GCD(b, remainder, answer);

		// x = y prev
		// y = xprev - (quotient * yprev)
		HeftyInteger gcd = answer[0];
		HeftyInteger x = answer[2];
		HeftyInteger y = answer[1];
		HeftyInteger temp = quotient.multiply(answer[2]);
		y = y.subtract(temp);
		
		return (new HeftyInteger[]{gcd, x, y});
	}

	/**
	 * Basic binary Long Division Algorithm
	 * @param other the number you are dividing this by
	 * @return HeftyInteger[] answer containing [quotient, remainder]
	 */
	public HeftyInteger[] divide(HeftyInteger other){
		HeftyInteger[] answer = new HeftyInteger[2];
		byte[] dividend = this.getVal();
		HeftyInteger remainder = new HeftyInteger(ZERO);
		HeftyInteger quotient = new HeftyInteger(ZERO);
		boolean seenFirstOne = false;
		
		// long division algorithm
		for(int i = 0; i < dividend.length; i++){
			
			for(int j = 7; j >= 0; j--){
				byte bit = (byte) ((dividend[i] >> j) & BITMASK);
				if(seenFirstOne == false && bit == (byte)1){
					seenFirstOne = true;
				}
				if(seenFirstOne == true){
					if( i == 0 && j == 7) {
						if(bit == (byte)0) remainder = new HeftyInteger(ZERO);
						else remainder = new HeftyInteger(ONE);
					}
					else remainder = addOnEnd(remainder, bit);
					
					if( isGreaterThan(other, remainder) ){
						if(i == 0 && j == 7) quotient = new HeftyInteger(ZERO);
						else quotient = addOnEnd(quotient, (byte)0);
					}
					else{
						if(i == 0 && j == 7) quotient = new HeftyInteger(ONE);
						else quotient = addOnEnd(quotient, (byte)1);
						
						remainder = remainder.subtract(other);
					}
				}
			}
		}
		answer[0] = new HeftyInteger(removeDeadBytes(quotient.getVal()));
		answer[1] = new HeftyInteger(removeDeadBytes(remainder.getVal()));

		return answer;
	}

	/**
	 * Simple toString function. Creates a String of the HeftyInteger
	 * in binary. Does not include leading 0s in each byte. 
	 * i.e. 11010101 00000001 = 11010101 1
	 */
	public String toString() {
		String s = "";
		int result;
		byte[] arr = this.getVal();
		for(int i = 0; i < arr.length; i++){
			result = arr[i] & 0xff;
			s += Integer.toBinaryString(result) + " ";
		}
		return s;
	}

	/**
	 * Trimming function to remove any extra bytes of -1 or 0 from the byte[]
	 * @param num byte[] num to be trimmed
	 * @return trimmed byte[]
	 */
	public byte[] removeDeadBytes(byte[] num){
		boolean isPositive = true;
		int fodder = 0;

		byte sign = (byte) ((num[0] >> 7) & BITMASK );
		if(sign == (byte)1) isPositive = false;
	
		// loops through num for all the fodder bytes of either -1 or 0
		// stops when it finds a byte that is not either 0 or -1
		for(int i = 0; i < num.length; i++){
			if(isPositive){
				if(num[i] != (byte) 0) break;
			}
			else{
				if(num[i] != (byte) -1) break;
			}
			fodder++;
		}

		// if there are no fodder bytes
		if (fodder == 0) return num;
		// if every byte is a fodder byte
		if(fodder == num.length) return ZERO;
		
		// all negative numbers get an extra byte. If a positive number
		// has a 1 in the MSB of the byte after the fodder bytes, it gets an 
		// extra byte
		if( isPositive && ((num[fodder] >> 7) & BITMASK) == (byte)1) --fodder;
		if(!isPositive) fodder--;
		
		if (fodder <= 0) return num;
		if(fodder == num.length) return ZERO;
		
		// create new byte[]
		byte[] finalNewNum = new byte[num.length - fodder];
		for(int i = 0; i < finalNewNum.length; i++){
			finalNewNum[i] = num[i + fodder];
		}

		return finalNewNum;
	}

	/**
	 * an inverse of the extension function
	 * Takes the given HeavyInteger and adds a new bit 
	 * in the LSB
	 * @param inter HeavyInteger to be modified
	 * @param addedBit bit you want to add
	 * @return inter << 1 & addedBit
	 */
	public HeftyInteger addOnEnd(HeftyInteger inter, byte addedBit){
		HeftyInteger newInt = new HeftyInteger(shiftLeft(inter.getVal(), 1));
		byte[] nIarr = newInt.getVal();
		nIarr[nIarr.length-1] = (byte) (nIarr[nIarr.length-1] | addedBit);
		HeftyInteger newInter = new HeftyInteger(removeDeadBytes(nIarr));
		return newInter;
	}

	/**
	 * Shifts a byte[] to the left by a certian amount of bits
	 * @param num the byte[] to be shifted
	 * @param totalShifts total number of shifts
	 * @return shifted byte[]
	 */
	public byte[] shiftLeft(byte[] num, int totalShifts) {
		// number of shifts in one byte
		int shift = totalShifts % 8;
		// total number of extra bytes created
		int newByte = totalShifts / 8;
		byte[] newNum = new byte[num.length + 1 + newByte];
		byte sign = (byte) ((num[0] >> 7) & BITMASK );
		for(int i = 0; i < newNum.length; i++){
			newNum[i] = (byte) 0;
		}
		// saves the first part of the byte that would be truncated by the 
		// shift
		byte first = (byte)( num[0] >> (8-shift) );
		// saves the second part of the byte that would be saved
		byte second = (byte) ( num[0] << shift );
		for(int i = 0; i < num.length; i++){
			newNum[i] = (byte) ( newNum[i] | first);
			newNum[i+1] = (byte) ( newNum[i+1] | second );

			if(i != (num.length - 1) ){
				byte mask = (byte) ~(0xFF << shift);
				first = (byte) ( (num[i+1] >> (8-shift)) & mask );
				second = (byte) ( num[i+1] << shift );
			}
		}
		
		// sanity check to make sure if a number started negative, it stays negative
		if(newNum[0] == (byte)0 && sign == (byte)1) newNum[0] = (byte)-1;
		return newNum;
	}

	/**
	 * Shifts a byte[] to the right by a certian amount of bits
	 * @param num the byte[] to be shifted
	 * @param totalShifts total number of shifts
	 * @return shifted byte[]
	 */
	public byte[] shiftRight(byte[] num, int totalShifts) {
		// number of shifts in one byte
		int shift = totalShifts % 8;
		// total number of extra bytes created
		int newByte = totalShifts / 8;
		byte[] newNum = new byte[num.length];
		if( newByte >= num.length) return newNum;

		for(int i = 0; i < newNum.length; i++){
			newNum[i] = (byte) 0;
		}
		byte sign = (byte) ((num[0] >> 7) & BITMASK );
		byte first;
		// saves the first part of the byte that would be truncated by the 
		// shift
		if(sign == (byte) 0) first = (byte)( num[0] >>> shift );
		else first = (byte)( num[0] >> shift );
		
		// saves the second part of the byte that would be saved
		byte second = (byte) ( num[0] << (8-shift) );

		for(int i = newByte; i < num.length; i++){
			newNum[i] = (byte) ( newNum[i] | first);

			if(num.length > 1 && i != (num.length - 1) ){
				newNum[i+1] = (byte) ( newNum[i+1] | second );
				byte mask = (byte) ~(0xFF << (8-shift));
				first = (byte)( (num[i+1 - newByte] >> shift) & mask );
				second = (byte) ( num[i+1 - newByte] << (8-shift) );
			}
		}

		byte[] finalNewNum = removeDeadBytes(newNum);
		
		return finalNewNum;
	}
	
	/**
	 * Comparison function to see if one < two
	 * @param one first HeftyInteger to compare
	 * @param two Second HeftyInteger to comapre
	 * @return boolean value if one < 2
	 */
	public boolean isGreaterThan(HeftyInteger one, HeftyInteger two){
		boolean isPositiveOne = true;
		boolean isPositiveTwo = true;

		byte sign1 = (byte) (one.getVal()[0] >> 7);
		sign1 = (byte) (sign1 & BITMASK);
		if(sign1 == (byte)1) isPositiveOne = false;
		
		byte sign2 = (byte) (two.getVal()[0] >> 7);
		sign2 = (byte) (sign2 & BITMASK);
		if(sign2 == (byte)1) isPositiveTwo = false;

		// compares the signs of each number.
		if(isPositiveOne && isPositiveTwo){
			if(one.lengthInBits() > two.lengthInBits()) return true;
			else if(one.lengthInBits() < two.lengthInBits()) return false;
		}
		else if( !isPositiveOne && isPositiveTwo){
			return false;
		}
		else if( isPositiveOne && !isPositiveTwo){
			return true;
		}

		byte[] oneArr = one.getVal();
		byte[] twoArr = two.getVal();

		// loops through the array and comapres each bit of both numbers
		for(int i = 0; i < oneArr.length; i++){
			for(int j = 7; j >= 0; j--){
				byte bit1 = (byte) ((oneArr[i] >> j) & BITMASK);
				byte bit2 = (byte) ((twoArr[i] >> j) & BITMASK);

				if(isPositiveOne){
					// the first number with a unique 0 is smaller 
					if(bit1 == (byte)1 && bit2 == (byte)0) return true;
					else if(bit1 == (byte)0 && bit2 == (byte)1) return false;
				}
				else{
					// the first number with a unique 1 is smaller
					if(bit1 == (byte)1 && bit2 == (byte)0) return false;
					else if(bit1 == (byte)0 && bit2 == (byte)1) return true;
				}
			}
		}

		// one == two. Since the function is testing strictly GreaterThan,
		// we reurn false
		return false;
	}
}

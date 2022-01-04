/**
 * A driver for CS1501 Project 5
 * @author	Austin Burnett
 */
package cs1501_p5;

import java.math.BigInteger;
import java.util.*;

public class App {
	public static void main(String[] args) {
	
		String a = "-2274";
		String b = "-1926";
		

		// BigInteger TEN = new BigInteger(a);
		// BigInteger TENNY = new BigInteger(b);
		// HeftyInteger t = new HeftyInteger(TEN.toByteArray());
		// HeftyInteger i = new HeftyInteger(TENNY.toByteArray());
		// // HeftyInteger j = new HeftyInteger(new BigInteger(s).toByteArray());
		// // HeftyInteger[] divAns = new HeftyInteger[2];

		// HeftyInteger result = i.multiply(t);
		// BigInteger ANS = TEN.multiply(TENNY);
		// HeftyInteger finals = new HeftyInteger(ANS.toByteArray());
		// System.out.println("t is equal to " + t.toString() + " with a size of " + t.length() + " and " + t.lengthInBits() + " in bits" );
		// System.out.println("i is equal to " + i.toString() + " with a size of " + i.length() + " and " + i.lengthInBits() + " in bits" );
		
		// System.out.println("result is equal to " + result.toString() + " with a size of " + result.length() + " and " + result.lengthInBits() + " in bits" );
		// System.out.println("should be equal to " + finals.toString() + " with a size of " + finals.length() + " and " + finals.lengthInBits() + " in bits" );





		// System.out.println("\n");
		// divAns = result.divide(j);
		// System.out.println("\n");

		// System.out.println(result.toString() + " divided by " + j.toString() + " is " + divAns[0].toString() + " with a remainder of " + divAns[1].toString() );

		// result = result.addOnEnd(result, (byte)1);

		// System.out.println("result adding a 1 bit to the end is now " + result.toString() + " with a size of " + result.length() + " and " + result.lengthInBits() + " in bits");

		// byte[] x = result.shiftRight(result.getVal(), 16);
		// HeftyInteger p = new HeftyInteger(x);

		// System.out.println("result shifted by 16 is " + p.toString());




		// BigInteger biA = new BigInteger(a);
		// BigInteger biB = new BigInteger(b);
		
		// HeftyInteger hiA = new HeftyInteger(biA.toByteArray());
		// HeftyInteger hiB = new HeftyInteger(biB.toByteArray());
	
		

		// HeftyInteger[] div = hiA.divide(hiB);
		// System.out.println("hiA is equal to " + hiA + " with a size of " + hiA.length() + " and " + hiA.lengthInBits() + " in bits" );
		// System.out.println("hiB is equal to " + hiB + " with a size of " + hiB.length() + " and " + hiB.lengthInBits() + " in bits" );
		// System.out.println("div is equal to " + div[0] + " with a remainder of " + div[1] + "\n\n");
		
		// HeftyInteger[] hiRes = hiA.XGCD(hiB);

		// BigInteger biGCD = biA.gcd(biB);
		// HeftyInteger hiGCD = hiRes[0];

		// BigInteger x = new BigInteger(hiRes[1].getVal());
		// BigInteger y = new BigInteger(hiRes[2].getVal());

		// BigInteger check = biA.multiply(x);
		// BigInteger check2 = biB.multiply(y);

		// BigInteger ans = check.add(check2);
		// BigInteger biCheck = biA.multiply(x).add(biB.multiply(y));

		// System.out.println("hiA = " + hiA);
		// System.out.println("hiB = " + hiB);
		// System.out.println("GCD = " + hiGCD);
		
		// System.out.println("x = " + hiRes[1] + " y = " + hiRes[2]);

		
		//  assertEquals(0, biGCD.compareTo(biCheck));


		BigInteger biA = new BigInteger(a);
		BigInteger biB = new BigInteger(b);

		HeftyInteger hiA = new HeftyInteger(biA.toByteArray());
		HeftyInteger hiB = new HeftyInteger(biB.toByteArray());

		HeftyInteger[] hiRes = hiA.XGCD(hiB);

		BigInteger biGCD = biA.gcd(biB);
		HeftyInteger hiGCD = hiRes[0];

		BigInteger x = new BigInteger(hiRes[1].getVal());
		BigInteger y = new BigInteger(hiRes[2].getVal());

		// assertEquals(0, biGCD.compareTo(new BigInteger(hiGCD.getVal())));

		BigInteger xprod = biA.multiply(x);
		BigInteger yprod = biB.multiply(y);
		BigInteger fina = xprod.add(yprod);
		
		BigInteger biCheck = biA.multiply(x).add(biB.multiply(y));
		// System.out.println(" (" + biA + " x " + x + ") + (" + biB + " x " + y + ") = " + fina);

		if(biGCD.compareTo(biCheck) == 0) System.out.println(biGCD + " TRUE " + biCheck);
		else System.out.println(biGCD + " FALSE " + biCheck);

		// HeftyInteger p = new HeftyInteger(new byte[]{(byte)2});
		// HeftyInteger q = new HeftyInteger(new byte[]{(byte)-1});
		// HeftyInteger r = p.multiply(q);
		// System.out.println( p + " x " + q + " = " + r);
	}
}

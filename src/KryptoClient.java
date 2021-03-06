import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.math.*;

public class KryptoClient {
	
	private  BigInteger  choose1;
	private  BigInteger choose2;
	public int rounds;
	public BigInteger n = new BigInteger("0"); //Öffentlicher Schlüssel n
	public BigInteger e;  
	private BigInteger d; 
	private final int[] primeArray = {11093, 11113, 11117, 11119, 11131, 11149, 11159, 11161, 11171, 11173, 11177, 11197, 11213, 11239,	
			  15107, 15121, 15131, 15137, 15139, 15149, 15161, 15173, 15187, 15193, 15199, 15217, 15227, 15233,
			  17011, 17021, 17027, 17029, 17033, 17041, 17047, 17053, 17077, 17093, 17099, 17107, 17117, 17123,
			  18089, 18097, 18119, 18121, 18127, 18131, 18133, 18143, 18149, 18169, 18181, 18191, 18199, 18211,
			  18217, 18223, 18229, 18233, 18251, 18253, 18257, 18269, 18287, 18289, 18301, 18307, 18311, 18313};
	int subKey;
	
	public KryptoClient() {
		
		subKey = 3;
		
	}
	
	public String intToString(int integer) {
		Integer meinInteger = new Integer(integer);
        return meinInteger.toString(); 
	}
	
	public String intArrayToString(int[] integerArray) {
		
		String returnMessage = "";
		
		for(int i = 0; i < integerArray.length; i++) {
			returnMessage += intToString(integerArray[i]);
			//System.out.println(returnMessage);
		}
		
		return returnMessage;
	}
	
	public char[] Parser(String message){
		char[] messageArray = new char[message.length()]; // MessageArray wird konfiguriert
		messageArray =  message.toCharArray();
		
		return messageArray;
	}
	
	private String substituteCharsEncrypt(char[] messageArray) {
		String cipher = "";
		int position;
		for(int i = 0; i < messageArray.length; i++) {
			
			if((255 - (int) messageArray[i] <= subKey)) { // wenn subkey größer als verbleibende Buchstaben im Alphabet
				//System.out.println("subkey1: " + subKey);
				position = subKey -(255 - (int) messageArray[i]);
			}
			else{ position = (int) messageArray[i] + subKey; }
			
			cipher += (char) position;
		}
		return cipher;
	}
	
/*	private String substituteCharsEncrypt(char[] messageArray) {
		int position = 0;
		char[] cipherArray = new char[messageArray.length];
		
		for(int i = 0; i < messageArray.length; i++) {
			
			for(int j = 0; j < alphabet.length; j++) {
				
				if(messageArray[i] ==  alphabet[j]) {
					//calculate position
					if((alphabet.length-1 - j <= subKey)) { // wenn subkey größer als verbleibende Buchstaben im Alphabet
						//System.out.println("subkey1: " + subKey);
						position = subKey -(alphabet.length - j);
					}
					else{ position = j + subKey; }
					cipherArray[i] = alphabet[position]; // -1 -> Verschiebung von Alphabet zu ArrayCounter
				}
			}
		}
		return String.valueOf(cipherArray);
	}
*/	
	
	//Nachricht verschlüsseln (Unterfunktionen starten)
	public String encryptMessage(String message, int repeat) {
		if(repeat-1 > 0) {
			return encryptMessage(substituteCharsEncrypt(Parser(message)), repeat-1);
		}
		return substituteCharsEncrypt(Parser(message));
	}
	
	//String in ein StringArray umwandeln
	public String[] stringToStringArray(String string)  {
		
		char[] charArrayOfString = string.toCharArray();
		String[] message = new String[charArrayOfString.length];
		
		for(int i = 0; i < charArrayOfString.length; i++) {
		
			message[i] = String.valueOf(charArrayOfString[i]);
		}
		return message;
	}
	
	//StringArray in IntegerArray umwandeln
	public int[] stringArrayToIntegerArray(String[] string) {
		int[] compareArray = new int[string.length];
		for(int i = 0; i < string.length; i++) {
			compareArray[i] = Integer.parseInt(string[i]);
		}
		return compareArray;	
	}
	
	//Nachrichten verschlüsseln (Unterfunktionen aufrufen)
	public String decryptMessage(String cipherMessage, int rounds) {
	 if(rounds-1 > 0) {
			return decryptMessage(deSubstitute(cipherMessage.toCharArray()), rounds-1);
		}
	 return deSubstitute(cipherMessage.toCharArray());
	}
	
	private String deSubstitute(char[] cipherArray) {
		int position = 0;
		String clearText = "";
		for(int i = 0; i < cipherArray.length; i++) {	
					//calc position
					if(cipherArray[i] < subKey) {  // wenn subkey größer als verbleibende Buchstaben im Alphabet
						position = 255 -(subKey - cipherArray[i]);
					}
					else{ 
						position = cipherArray[i] - subKey; 
						}
				clearText += (char) position; 				// -1 -> Verschiebung von Alphabet zu ArrayCounter
		}
		return clearText;
	}
		
		
	/*//Positionen in Nachrichten Array korrigieren durch Verschiebung mit subKey
	private String deSubstitute(char[] cipherArray) {

		char[] deSubstitutedArray = new char[cipherArray.length];
		int position = 0;
		for(int i = 0; i < cipherArray.length; i++) {
			
			for(int j = 0; j < alphabet.length; j++) {
				
				if(cipherArray[i] ==  alphabet[j]) {
					//calc position
					if(j < subKey) { // wenn subkey größer als verbleibende Buchstaben im Alphabet
						position = alphabet.length -(subKey - j);
						
					}
					else{ position = j - subKey; }
					deSubstitutedArray[i] = alphabet[position]; // -1 -> Verschiebung von Alphabet zu ArrayCounter
				    j = alphabet.length;
				}
			}//System.out.println("raus " + i + " cA: " + cipherArray[i]);
		}
		return String.valueOf(deSubstitutedArray);
	} 
	*/
	
	public void getTwoRandomPrimes(){
		//Erstelle Zufallszahlen und übergebe an globale Werte von choose1/choose2
		
		do{
			choose1 = new BigInteger(intToString(primeArray[(int) ((Math.random() * primeArray.length))]));
			choose2 = new BigInteger(intToString(primeArray[(int) ((Math.random() * primeArray.length))]));
			}while(choose1 == choose2);	
		//System.out.println(choose1 + "  |  " + choose2);
	}
	
	private void calcN(BigInteger p, BigInteger q){
		n =  p.multiply(q);
	}
	
	public void calcE(BigInteger phi_N) {
		
		BigInteger ggT;
		BigInteger i = new BigInteger("1000");
		
		do{
			i= i.add(BigInteger.ONE);
			ggT = phi_N.gcd(i);
			//System.out.println(i + "  ggT: " + ggT);
		}while(!ggT.equals(BigInteger.ONE));
		e = i; //return E
		
	}
	
public void calcD(BigInteger phi_N, BigInteger e) {
		
		BigInteger result =  null;
		BigInteger i = new BigInteger("2");
		
		while(!(i.multiply(phi_N).add(BigInteger.ONE).remainder(e).equals(BigInteger.ZERO))) {
			
			i = i.add(BigInteger.ONE);
			result = (i.multiply(phi_N).add(BigInteger.ONE)).divide(e);
		}
		d = result; //return D
	}
	
	public BigInteger startRSA(){
		getTwoRandomPrimes();
		calcN(choose1, choose2);
		System.out.println(n);
		BigInteger phi_N = (choose1.subtract(BigInteger.ONE)).multiply( choose2.subtract( BigInteger.ONE) );
		calcE(phi_N);
		//d = (phi_N * s  + 1) / e
		calcD(phi_N, e);
		System.out.println("n: " + n + " e: " + e + " d: " + d);
		//System.out.println("Cipher: " + message.modPow(e, n));
		//System.out.println("Message: " + message.modPow(e,n).modPow(d, n));
		System.out.println("e: " + e + " n: " + n);
		return e;
	
}
	
	public BigInteger publicKeyEncrypt(BigInteger message, BigInteger e, BigInteger n) {
		return message.modPow(e, n); 
	}
	
}//End of File
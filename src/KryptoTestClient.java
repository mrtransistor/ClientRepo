import static org.junit.Assert.*;

import org.junit.Test;


public class KryptoTestClient {

/*@Test
public void testDecrypt() {
Krypto test = new  Krypto();
assertEquals("2345", test.decryptMessage("abcd"));
}*/

@Test
public void testStringArrayToIntegerArray(){
	KryptoClient test = new KryptoClient();
	
	int[] intArray = {2,3,4,5};
	String[] stringArray = {"2","3","4","5"};
	assertArrayEquals(intArray ,test.stringArrayToIntegerArray(stringArray));
	
	int[] intArray2 = {4};  
	String[] stringArray2 = {"4"};
	assertArrayEquals(intArray2 ,test.stringArrayToIntegerArray(stringArray2));
}

@Test
public void testStringToStringArray(){
	KryptoClient test = new KryptoClient();
	
	String testString = "2345";
	String[] stringArray = {"2","3","4","5"};
	assertArrayEquals(stringArray ,test.stringToStringArray(testString));
}

@Test
public void testDecrypted(){
	KryptoClient test = new KryptoClient();
	
	String testString = "abcdefghijklmn";
	assertEquals(testString ,test.decryptMessage("defghijklmnopq", test.rounds));
	System.out.println("2345 wurde zu: "+ testString);
}

/*@Test
public void testDeSubstitute(){
	KryptoClient test = new KryptoClient();
	String proofArray = "123";
	char[] testArray = {'1','2','3'};
	assertEquals(proofArray, test.deSubstitute(testArray));
}*/


@Test
public void testEncryptMessage() {
	KryptoClient test = new KryptoClient();
	assertEquals("defg", test.encryptMessage("abcd",test.rounds));	
}

/*@Test
public void testSubsituteCharsEncrypt(){
	KryptoClient test = new KryptoClient();
	assertEquals("bcde", test.substituteCharsEncrypt("abcd".toCharArray()));	
}*/

@Test
public void testParser() {
	KryptoClient test = new KryptoClient();
	assertArrayEquals("abcd".toCharArray(), test.Parser("abcd"));	
}

@Test
public void testIntArrayToStringArray() {
	
	int[] intArray = {1,2,3,4,5};
	String testString = "12345";
	KryptoClient test = new KryptoClient();
	assertEquals(testString, test.intArrayToString(intArray));	
}

@Test
public void testIntToString() {
	KryptoClient test = new KryptoClient();
	assertEquals("2", test.intToString(2));	
}

}//End of File
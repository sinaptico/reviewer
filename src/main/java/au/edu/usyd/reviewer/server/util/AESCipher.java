package au.edu.usyd.reviewer.server.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import au.edu.usyd.reviewer.client.core.util.Constants;


public class AESCipher {

	private static String cipherAlgorithm = "AES";
	private static AESCipher cipherAES;
	
	// Symmetric key to encrypt and decrypt
	private static String SYMMETRICAL_KEY="S1N4PT1C0R3V13W3R1WR1T3GL0SS3R13";

	private static SecretKeySpec key = new SecretKeySpec(SYMMETRICAL_KEY.getBytes(), cipherAlgorithm);
	
	private AESCipher(){	
	}
	
	public static AESCipher getInstance(){
		if (cipherAES == null){
			cipherAES = new AESCipher();
		}
		return cipherAES;
	}
	
	
	/**
	 * Return the value encrypted using AES algorithm
	 * @param String value to encrypt
	 * @return String encrypted value
	 * @throws Exception message to the logger user
	 */
	public static String encrypt(String value) throws Exception{
		String encryptedValue = null;
		try {
			Cipher cipher = Cipher.getInstance(cipherAlgorithm);
			
			// start to encryp the value
			cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            encryptedValue = new String(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(Constants.EXCEPTION_ENCRYPT);
		}
		return encryptedValue;
	}


	/**
	 * Return the decrypted value of the string received as parameter. 
	 * @param String value to decrypt
	 * @return String decrypted value
	 * @throws Exception message to the logged user
	 */
	public static String decrypt(String value) throws Exception{
		String decryptedValue = null;
		try {
			Cipher cipher = Cipher.getInstance(cipherAlgorithm);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] encryptedValue = value.getBytes();
            byte[] decrypted = cipher.doFinal(encryptedValue);
            decryptedValue = new String(decrypted); 
            
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(Constants.EXCEPTION_DECRYPT);
		} 
		
		return decryptedValue;
	}
}

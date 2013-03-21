package au.edu.usyd.reviewer.server.util;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

/**
 * This class is used to encrypt and decrypt strings.
 * The Algorithm used is AES with mode of operation Cipher-Block Chaining (CBC), it means that each block of plaintext is XORed with 
 * the previous ciphertext block before being encrypted. 		
 * The cipher uses PKCS5Padding so it has only be defined for block ciphers that use a 64 bit (8 byte) block size. 
 * @author mdagraca
 *
 */
public class AESCipher {

	// singleton
	private static AESCipher aesCipher= null;
	
	// Constants
	private static String chartset="UTF-8";
	private static String cipherAlgorithm = "AES";
	private static String cipherTransformation = "AES/CBC/PKCS5Padding";
	
	
	//define hardcoded key as  String. Should be stored in a better way. They must be 16 long
	private static String KEY="R3V13W3RS1N4PT1C";
	private static String IV = "RS31VN143PWT31RC";
	    
	//get key stuff, using given key so we can encrypt and decrypt with it
	private static SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes(), cipherAlgorithm);
	private static IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes());
	
	private AESCipher (){	
	}
	
	/**
	 *  This method returns the only instance of the class
	 * @return AESCipher instance
	 * @throws NoSuchAlgorithmException
	 */
	public static AESCipher getInstance() throws NoSuchAlgorithmException{
		if (aesCipher == null){
			aesCipher = new AESCipher(); 
		}
		return aesCipher;
	}
		
	/**
	 * Return the value encrypted using AES algorithm
	 * @param String value to encrypt
	 * @return String encrypted value
	 * @throws Exception message to the logger user
	 */
	public  String encrypt(String value) throws Exception{
		String encryptedValue = null;
		try {
			//get a cipher object
			Cipher cipher = Cipher.getInstance(cipherTransformation);
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec,ivSpec);
		
			//get the raw bytes to encrypt, UTF8 is needed for having a standard character set
			byte[] raw = value.getBytes(chartset);
			
			//encrypt using the cypher
			byte[] encrypted = cipher.doFinal(raw);
			
			//convert to base64 for easier display (and storing like in a database)
			encryptedValue = DatatypeConverter.printBase64Binary(encrypted);
            
		} catch (Exception e) {
			e.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_ENCRYPT);
		}
		return encryptedValue;
	}


	/**
	 * Return the decrypted value of the string received as parameter. 
	 * @param String value to decrypt
	 * @return String decrypted value
	 * @throws Exception message to the logged user
	 */
	public String decrypt(String value) throws Exception{
		String decryptedValue = null;
		try {
			//get a cipher object.
			Cipher cipher = Cipher.getInstance(cipherTransformation);
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
			
			//decode the BASE64 coded message
			byte[] raw = DatatypeConverter.parseBase64Binary(value);
			
			//decode the message
			byte[] original = cipher.doFinal(raw);
			
			//convert the decoded message to a String
			decryptedValue = new String(original,chartset);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_DECRYPT);
		} 
		
		return decryptedValue;
	}
}

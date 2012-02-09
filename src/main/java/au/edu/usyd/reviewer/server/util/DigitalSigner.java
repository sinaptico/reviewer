package au.edu.usyd.reviewer.server.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

public class DigitalSigner {

	private PublicKey publicKey;
	private PrivateKey privateKey;

	public DigitalSigner(String privateKeyPath, String publicKeyPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		if (privateKeyPath != null) {
			privateKey = loadPrivateKey(privateKeyPath);
		}
		if (publicKeyPath != null) {
			publicKey = loadPublicKey(publicKeyPath);
		}
	}

	private PrivateKey loadPrivateKey(String privateKeyPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		InputStream in = new FileInputStream(privateKeyPath);
		byte[] bytes = new byte[in.available()];
		in.read(bytes);
		in.close();
		PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(bytes);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		return factory.generatePrivate(privSpec);	
	}

	private PublicKey loadPublicKey(String publicKeyPath) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		InputStream in = new FileInputStream(publicKeyPath);
		byte[] bytes = new byte[in.available()];
		in.read(bytes);
		in.close();
		X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(bytes);
		KeyFactory factory = KeyFactory.getInstance("RSA");
		return factory.generatePublic(pubSpec);
	}

	public String sign(byte[] message) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
		Signature sig = Signature.getInstance("SHA1withRSA");
		sig.initSign(privateKey);
		sig.update(message);
		byte[] signature = sig.sign();
		return new String(Base64.encodeBase64(signature));
	}

	public String sign(String message) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
		return sign(message.getBytes());
	}
	
	public boolean verify(byte[] message, String signature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature sig = Signature.getInstance("SHA1withRSA");
		sig.initVerify(publicKey);
		sig.update(message);
		return sig.verify(Base64.decodeBase64(signature.getBytes()));
	}
	
	public boolean verify(String message, String signature) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		return verify(message.getBytes(), signature);
	}
}

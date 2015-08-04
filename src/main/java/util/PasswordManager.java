package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordManager {
	static MessageDigest digest = null; 
	static {
		try { digest = MessageDigest.getInstance("MD5"); }
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
	}
	
	public static String getHash(String key) { 
		digest.update(key.getBytes());
		byte [] bytes = digest.digest(); 
		StringBuffer buff = new StringBuffer(); 
		for (int i = 0; i < bytes.length; i++) { 
			buff.append(String.format("%X", bytes[i])); 
		}
		return buff.toString(); 
	}
}

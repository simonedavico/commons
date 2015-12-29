import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;

public class Hashing {
	
	private static int numOfCharacters = 4;
	
	public static String hashKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		return Hashing.MD5(key);
	}
	
	public static String MD5(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] bytesOfMessage = key.getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] hashBytes = md.digest(bytesOfMessage);
		String hashString = new BigInteger(1,hashBytes).toString(16);
		hashString = hashString.substring(0, numOfCharacters);
		return(hashString+"/"+key);
	}
	
}

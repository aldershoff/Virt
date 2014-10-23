package security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.security.util.Password;

public class Hash {

	private SecureRandom random;
	private static final String CHARSET = "UTF-8";
	private static final String ENCRYPTION_ALGORITHM = "SHA-512";
	private BASE64Decoder decoder = new BASE64Decoder();
	private BASE64Encoder encoder = new BASE64Encoder();

	public byte[] getSalt(int length) {
		random = new SecureRandom();
		byte bytes[] = new byte[length];
		random.nextBytes(bytes);
		return bytes;
	}

	public byte[] hashWithSalt(String password, byte[] salt) {
		byte[] hash = null;
		try {
			byte[] bytesOfMessage = password.getBytes(CHARSET);
			MessageDigest md;
			md = MessageDigest.getInstance(ENCRYPTION_ALGORITHM);
			md.reset();
			md.update(salt);
			md.update(bytesOfMessage);
			hash = md.digest();

		} catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
			Logger.getLogger(Password.class.getName()).log(Level.SEVERE,
					"Encoding Problem", ex);
		}
		return hash;
	}

	public String base64FromBytes(byte[] text) {
		return encoder.encode(text);
	}

	public byte[] bytesFrombase64(String text) {
		byte[] textBytes = null;
		try {
			textBytes = decoder.decodeBuffer(text);
		} catch (IOException ex) {
			Logger.getLogger(Password.class.getName()).log(Level.SEVERE,
					"Encoding failed!", ex);
		}
		return textBytes;
	}
}
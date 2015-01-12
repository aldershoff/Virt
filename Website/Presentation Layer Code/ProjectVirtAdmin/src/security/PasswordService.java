package security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.naming.ServiceUnavailableException;

import org.apache.commons.codec.binary.Base64;

/**
 * Class for making a new hashed password for storing in the database
 * 
 * @author KjellZijlemaker
 *
 */

public final class PasswordService {
	private static PasswordService instance;

	public PasswordService() {
	}

	/**
	 * Method for making the password and return the hash
	 * 
	 * @param plaintext
	 * @return
	 * @throws ServiceUnavailableException
	 */
	public synchronized String encrypt(String plaintext)
			throws ServiceUnavailableException {
		MessageDigest md = null;

		/**
		 * Try to make SHA instance
		 */
		try {
			md = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		/**
		 * Try to update the SHA instance
		 */
		try {
			md.update(plaintext.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		/**
		 * Making bytes for the base64 code to encode with
		 */
		byte raw[] = md.digest();
		String hash = new Base64().encodeToString(raw);

		return hash;
	}

	/**
	 * Make the hash and return the instance
	 * 
	 * @return
	 */
	public synchronized PasswordService getInstance() {
		if (instance == null) {
			instance = new PasswordService();
		}
		return instance;
	}
}

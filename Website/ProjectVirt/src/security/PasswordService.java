package security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.naming.ServiceUnavailableException;

import org.apache.commons.codec.binary.Base64;

public final class PasswordService {
	private static PasswordService instance;

	public PasswordService() {
	}

	public synchronized String encrypt(String plaintext)
			throws ServiceUnavailableException {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA"); // step 2
		} catch (NoSuchAlgorithmException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}
		try {
			md.update(plaintext.getBytes("UTF-8")); // step 3
		} catch (UnsupportedEncodingException e) {
			throw new ServiceUnavailableException(e.getMessage());
		}

		byte raw[] = md.digest(); // step 4
		String hash = new Base64().encodeToString(raw); // step 5
		return hash; // step 6
	}

	public synchronized PasswordService getInstance() // step 1
	{
		if (instance == null) {
			instance = new PasswordService();
		}
		return instance;
	}
}

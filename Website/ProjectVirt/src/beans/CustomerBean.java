package beans;

/**
 * Bean for getting all the user information from the database
 * 
 * @author kjellzijlemaker
 *
 */

public class CustomerBean {

	/**
	 * Setting the variables needed
	 */
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	public boolean valid;

	/**
	 * Returning firstname
	 * 
	 * @return
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Setting firstname
	 * 
	 * @param newFirstName
	 */
	public void setFirstName(String newFirstName) {
		firstName = newFirstName;
	}

	/**
	 * Returning lastname
	 * 
	 * @return
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Setting lastname
	 * 
	 * @param newLastName
	 */
	public void setLastName(String newLastName) {
		lastName = newLastName;
	}

	/**
	 * Getting the userpassword
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Setting the userpassword
	 * 
	 * @param newPassword
	 */
	public void setPassword(String newPassword) {
		password = newPassword;
	}

	/**
	 * Getting the username
	 * 
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Setting the username
	 * 
	 * @param newUsername
	 */
	public void setUserName(String newUsername) {
		username = newUsername;
	}

	/**
	 * Check if the user is valid
	 * 
	 * @return
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Set if the user is valid
	 * 
	 * @param newValid
	 */
	public void setValid(boolean newValid) {
		valid = newValid;
	}
}

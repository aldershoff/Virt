package beans;

import com.google.gson.annotations.Expose;

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
	@Expose private int userID;
	@Expose private String username;
	private String password;
	@Expose private String firstName;
	@Expose private String lastName;
	@Expose private String dateOfBirth;
	@Expose private String email;
	@Expose private String phone;
	@Expose private String address;
	@Expose private String zipCode;
	@Expose private String error;
	@Expose private boolean remember;
	@Expose public boolean valid;

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
	 * Check if the user is valid
	 * 
	 * @return
	 */
	public boolean isRememberMe() {
		return remember;
	}
	
	/**
	 * set rememberme parameter
	 * 
	 * @return
	 */
	public void setRememberMe(boolean remember) {
		this.remember = remember;
	}
	
	
	/**
	 * Set if the user is valid
	 * 
	 * @param newValid
	 */
	public void setValid(boolean newValid) {
		valid = newValid;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int i) {
		this.userID = i;
	}
	
	public String getError() {
		return this.error;
	}

	public void setError(String error) {
		this.error = error;
	}
}

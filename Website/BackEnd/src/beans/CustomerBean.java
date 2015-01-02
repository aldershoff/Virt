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
	@Expose private String email;
	@Expose private String phone;
	@Expose private String address;
	@Expose private String zipcode;
	@Expose private boolean remember;
	@Expose private int twoFactor;
	private String company;
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
		return zipcode;
	}

	public void setZipCode(String zipCode) {
		this.zipcode = zipCode;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int i) {
		this.userID = i;
	}

	public int isTwoFactor() {
		return twoFactor;
	}

	public void setTwoFactor(int twoFactor) {
		this.twoFactor = twoFactor;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
}

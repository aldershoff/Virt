package customers;

import javax.servlet.http.Cookie;

/**
 * Future Java Bean to store data in object for the customerdata
 * that is needed in a later stadium
 * 
 * @author kjellzijlemaker
 *
 */

public class Customer {

	private int id;
	private String frontName;
	private String surName;
	private String userName;
	private Cookie loginCookie;
	
	public Customer(){
		
	}
	
	public void setCustomerID(){
		
	}
	
	public void setCustomerFrontname(){
		
	}
	
	public void setCustomerSurname(){
		
	}
	
	public void setCustomerUsername(String username){
		this.userName = username;
	}
	
	public String getCustomerUsername(){
		return this.userName;
	}
	
}

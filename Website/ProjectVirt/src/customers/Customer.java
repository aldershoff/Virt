package customers;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Future Java Bean to store data in object for the customerdata
 * that is needed in a later stadium
 * http://www2.sys-con.com/itsg/virtualcd/java/archives/0302/darby/index.html
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
	
	public Customer(HttpServletRequest request){
//		this.id = request.getParameter(arg0);
//		this.frontName = request.getParameter(arg0);
//		this.surName = request.getParameter(arg0);
//		this.userName = request.getParameter(arg0);
//		this.loginCookie = request.getParameter(arg0);

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

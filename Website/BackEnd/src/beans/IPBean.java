package beans;

import java.util.ArrayList;

/**
 * Bean for getting all the user virtual machine information from database
 * 
 * @author willemWesterhof
 *
 */

public class IPBean {
	
	/**
	 * Setting the variables needed
	 */
	private String IPAddress;

	/**
	 * Returning Available IP
	 * 
	 * @return
	 */
	public String getIPs() {
		return IPAddress;
	}
}

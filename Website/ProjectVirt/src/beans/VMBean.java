package beans;

/**
 * Bean for getting all the user virtual machine information from database
 * 
 * @author kjellzijlemaker
 *
 */

public class VMBean {
	
	/**
	 * Setting the variables needed
	 */
	private String vmName;
	private String vmCPU;
	private String vmMemory;
	private String vmDiskSpace;
	private String vmIP;
	public boolean valid;

	/**
	 * Returning vmName
	 * 
	 * @return
	 */
	public String getVMName() {
		return vmName;
	}

	/**
	 * Setting vmName
	 * 
	 * @param newFirstName
	 */
	public void setVMName(String vmName) {
		this.vmName = vmName;
	}
	
	/**
	 * Returning vmName
	 * 
	 * @return
	 */
	public String getVMCPU() {
		return vmCPU;
	}

	/**
	 * Setting vmName
	 * 
	 * @param newFirstName
	 */
	public void setVMCPU(String vmCPU) {
		this.vmCPU = vmCPU;
	}
	
	/**
	 * Returning vmName
	 * 
	 * @return
	 */
	public String getVMDiskSpace() {
		return vmDiskSpace;
	}

	/**
	 * Setting vmName
	 * 
	 * @param newFirstName
	 */
	public void setVMDiskSpace(String vmDiskSpace) {
		this.vmDiskSpace = vmDiskSpace;
	}

	/**
	 * Returning vmName
	 * 
	 * @return
	 */
	public String getVMMemory() {
		return vmMemory;
	}

	/**
	 * Setting vmName
	 * 
	 * @param newFirstName
	 */
	public void setVMMemory(String vmMemory) {
		this.vmMemory = vmMemory;
	}
	
	/**
	 * Returning vmName
	 * 
	 * @return
	 */
	public String getVMIP() {
		return vmIP;
	}

	/**
	 * Setting vmName
	 * 
	 * @param newFirstName
	 */
	public void setVMIP(String vmIP) {
		this.vmIP = vmIP;
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

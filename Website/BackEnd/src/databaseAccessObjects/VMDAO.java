package databaseAccessObjects;

import infrastructure.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import beans.VMBean;

public class VMDAO {
	/**
	 * Setting the database connection and connection This will be static,
	 * because only one connection will need instantiation
	 */
	private static DBConnection dbConn = null;
	private static Connection conn = null;
	
	/**
	 * Executes the login query, but will also add information to the Bean
	 * 
	 * @return
	 */
	public ArrayList<VMBean> getVMs(String userID) {

		ArrayList<VMBean> VMBeanArray = new ArrayList<VMBean>();
		
		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		ResultSet rs = null;
		final String SELECT_ALL_VM = "SELECT VMID, VMName, VMOS, VMState FROM vm WHERE user_UserID = ? && VMIsActive = 1";

		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {				
				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(SELECT_ALL_VM);

				pstm.setString(1, userID);
				
				// Execute the query
				rs = pstm.executeQuery();

				/**
				 * While the resultset will go to the next result, store the
				 * variables. rs.next will only
				 */
				
				while (rs.next()) {
					VMBean bean = new VMBean();
					
					// Setting the information inside the Bean, from the
					// database information
					bean.setVMID(rs.getInt("VMID"));
					bean.setVMName(rs.getString("VMName"));
					bean.setVMOS(rs.getString("VMOS"));
					bean.setVMState(rs.getString("VMState"));
					
					bean.setValid(true);
					VMBeanArray.add(bean);
					
				}
			}

			/**
			 * Catch exception SQL
			 */
			catch (SQLException ex) {

				// handle any errors
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());

			}

			/**
			 * Finally, close the connection and check if the password from the
			 * form equals the password inside the bean
			 */
			finally {
				closeConn(conn);
			}

		}

		// If the connection was not made, return nothing
		else {
			return null;
		}

		// Return the bean for using the data
		return VMBeanArray;
	}
	
	
	/**
	 * Executes the login query, but will also add information to the Bean
	 * 
	 * @return
	 */
	public VMBean getSpecificVM(VMBean bean, String userID, String vmID) {

		
		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		ResultSet rs = null;
		final String GET_SPECIFIC_VM = "SELECT * FROM vm WHERE vm.user_UserID = ? AND vm.vmid = ?";
		final String GET_VM_IPADDRESS = "SELECT * FROM netaddress WHERE netaddress.VM_VMID = ?";

		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {				
				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(GET_SPECIFIC_VM);
				PreparedStatement pstm2 = conn.prepareStatement(GET_VM_IPADDRESS);
				
				// Setting the parameters (places where the "?" exist)
				pstm.setString(1, userID);
				pstm.setString(2, vmID);

				// Execute the query
				rs = pstm.executeQuery();

				/**
				 * While the resultset will go to the next result, store the
				 * variables. rs.next will only
				 */
				
				while (rs.next()) {
					
					// Setting the information inside the Bean, from the
					// database information
					bean.setVMName(rs.getString("VMName"));
					bean.setVMID(rs.getInt("VMID"));
					bean.setVMCPU(rs.getString("VMCpu"));
					bean.setVMOS(rs.getString("VMOS"));
					bean.setVMDiskSpace(rs.getString("VMHDD"));
					bean.setVMMemory(rs.getString("VMMemory"));
					bean.setVMSLA(rs.getString("VMSLA"));
					bean.setVMMonthlyPrice(rs.getString("VMMonthlyPrice"));
					bean.setVMState(rs.getString("VMState"));
					bean.setValid(true);
					
					
					/**
					 * Execute query inside query for getting the IPaddress and active state
					 */
					pstm2.setString(1, vmID);
					ResultSet rs2 = pstm2.executeQuery();
					while(rs2.next()){
						bean.setVmIPIsActive(rs2.getInt("IsActive"));
						bean.setVMIP(rs2.getString("IpAddress"));
					}
					
				}
			}

			/**
			 * Catch exception SQL
			 */
			catch (SQLException ex) {

				// handle any errors
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());

			}

			/**
			 * Finally, close the connection and check if the password from the
			 * form equals the password inside the bean
			 */
			finally {
				closeConn(conn);
			}

		}

		// If the connection was not made, return nothing
		else {
			return null;
		}

		// Return the bean for using the data
		return bean;
	}
	
	
	/**
	 * Executes the login query, but will also add information to the Bean
	 * 
	 * @return
	 */
	public int startSpecificVMState(String vmID, String userID, int state) {

		
		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		int rs = 0;
		
		final String START_SPECIFIC_VM = "UPDATE vm SET VMState = ? WHERE VMID = ? AND vm.user_UserID = ?";

		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {				
				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(START_SPECIFIC_VM);
				
				// Setting the parameters (places where the "?" exist)
				if(state == 1){
					pstm.setString(1, "Running");
				}
				else{
					pstm.setString(1, "Stopped");
				}
				pstm.setString(2, vmID);
				pstm.setString(3, userID);


				// Execute the query
				rs = pstm.executeUpdate();

				
			}

			/**
			 * Catch exception SQL
			 */
			catch (SQLException ex) {

				// handle any errors
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());

			}

			/**
			 * Finally, close the connection and check if the password from the
			 * form equals the password inside the bean
			 */
			finally {
				closeConn(conn);
				if (rs == 1) {
					return 1;
				}
			}

		}

		// If the connection was not made, return nothing
		else {
			return 2;
		}

		// Return 0 if not successful
		return 0;
	}
	
	/**
	 * Executes the login query, but will also add information to the Bean
	 * 
	 * @return
	 */
	public int stopSpecificVM(String vmID, String userID, int state) {

		
		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		int rs = 0;
		
		final String STOP_SPECIFIC_VM = "UPDATE vm SET VMState = ? WHERE VMID = ? AND vm.user_UserID = ?";

		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {				
				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(STOP_SPECIFIC_VM);
				
				// Setting the parameters (places where the "?" exist)
				if(state == 1){
					pstm.setString(1, "Running");
				}
				else{
					pstm.setString(1, "Stopped");
				}
				pstm.setString(2, vmID);
				pstm.setString(3, userID);


				// Execute the query
				rs = pstm.executeUpdate();

				
			}

			/**
			 * Catch exception SQL
			 */
			catch (SQLException ex) {

				// handle any errors
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());

			}

			/**
			 * Finally, close the connection and check if the password from the
			 * form equals the password inside the bean
			 */
			finally {
				closeConn(conn);
				if (rs == 1) {
					return 1;
				}
			}

		}

		// If the connection was not made, return nothing
		else {
			return 2;
		}

		// Return 0 if not successful
		return 0;
	}
	
	/**
	 * Executes the login query, but will also add information to the Bean
	 * 
	 * @return
	 */
	public int editSpecificVM(String userID, VMBean bean) {

		
		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		int rs = 0;
		
		final String EDIT_SPECIFIC_VM = "UPDATE vm SET VMCpu = ?, VMMemory = ?, VMHDD = ?, VMSLA = ? WHERE VMID = ? AND vm.user_UserID = ?";

		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {				
				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(EDIT_SPECIFIC_VM);
				
				// Setting the parameters (places where the "?" exist)
				pstm.setString(1, bean.getVMCPU());
				pstm.setString(2, bean.getVMMemory());
				pstm.setString(3, bean.getVMDiskSpace());
				pstm.setString(4, bean.getVMSLA());
				pstm.setInt(5, (int) bean.getVMID());
				pstm.setString(6, userID);


				// Execute the query
				rs = pstm.executeUpdate();

				
			}

			/**
			 * Catch exception SQL
			 */
			catch (SQLException ex) {

				// handle any errors
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());

			}

			/**
			 * Finally, close the connection and check if the password from the
			 * form equals the password inside the bean
			 */
			finally {
				closeConn(conn);
				if (rs == 1) {
					return 1;
				}
			}

		}

		// If the connection was not made, return nothing
		else {
			return 2;
		}

		// Return 0 if not successful
		return 0;
	}
	
	
	/**
	 * Executes the login query, but will also add information to the Bean
	 * 
	 * @return
	 */
	public int refreshSpecificVM(String vmID, int state) {
		
		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		int rs = 0;
		final String REFRESH_SPECIFIC_VM = "UPDATE vm SET VMState = ? WHERE vm.VMID = ?";

		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {				
				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(REFRESH_SPECIFIC_VM);
				
				// Setting the parameters (places where the "?" exist)
				if(state == 1){
					pstm.setString(1, "Running");
				}
				else{
					pstm.setString(1, "Stopped");
				}
				pstm.setString(2, vmID);

				// Execute the query
				rs = pstm.executeUpdate();

			
			}

			/**
			 * Catch exception SQL
			 */
			catch (SQLException ex) {

				// handle any errors
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());

			}

			/**
			 * Finally, close the connection and check if the password from the
			 * form equals the password inside the bean
			 */
			finally {
				closeConn(conn);
				if (rs == 1) {
					return 1;
				}
			}

		}

		// If the connection was not made, return nothing
		else {
			return 0;
		}

		// Return the bean for using the data
		return 0;
	}
	
	
	/**
	 * Executes the login query, but will also add information to the Bean
	 * 
	 * @return
	 */
	public int deleteSpecificVM(String vmID, String userID) {

		
		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		int rs = 0;
		
		final String DELETE_SPECIFIC_VM = "UPDATE vm SET VMIsActive = 0 WHERE VMID = ? AND vm.user_UserID = ?";
		final String UNASSIGN_SPECIFIC_VM_IP = "UPDATE netaddress SET IsActive = 0, VM_VMID = 0 WHERE VM_VMID = ?";
		
		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {				
				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(DELETE_SPECIFIC_VM);
				PreparedStatement pstm2 = conn.prepareStatement(UNASSIGN_SPECIFIC_VM_IP);
				
				// Setting the parameters (places where the "?" exist)
				pstm.setString(1, vmID);
				pstm.setString(2, userID);


				// Execute the query
				rs = pstm.executeUpdate();
				
				/**
				 * Execute second query
				 */
				pstm2.setInt(1, Integer.parseInt(vmID));
				pstm2.executeUpdate();
				
			}

			/**
			 * Catch exception SQL
			 */
			catch (SQLException ex) {

				// handle any errors
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());

			}

			/**
			 * Finally, close the connection and check if the password from the
			 * form equals the password inside the bean
			 */
			finally {
				closeConn(conn);
				if (rs == 1) {
					return 1;
				}
			}

		}

		// If the connection was not made, return nothing
		else {
			return 2;
		}

		// Return 0 if not successful
		return 0;
	}
	
	
	public String getVMUUID(String vmID){
		
		// Set the UUID
		String UUID = null;
		
		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		ResultSet rs;
		
		final String GET_VM_UUID = "SELECT VMUUID FROM vm WHERE VMID = ?";
		
		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {				
				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(GET_VM_UUID);
				
				// Setting the parameters (places where the "?" exist)
				pstm.setString(1, vmID);
				

				// Execute the query
				rs = pstm.executeQuery();
				while (rs.next()) {
					UUID = rs.getString("VMUUID");
					
				}
				
			}

			/**
			 * Catch exception SQL
			 */
			catch (SQLException ex) {

				// handle any errors
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());

			}

			/**
			 * Finally, close the connection and check if the password from the
			 * form equals the password inside the bean
			 */
			finally {
				closeConn(conn);
			}

		}

		// If the connection was not made, return nothing
		else {
			return null;
		}

		// Return 0 if not successful
		return UUID;
	}
	
public String getStorKey(String vmID){
		
		// Set the UUID
		String storageKey = null;
		
		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		ResultSet rs;
		
		final String GET_VM_UUID = "SELECT VMStorKey FROM vm WHERE VMID = ?";
		
		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {				
				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(GET_VM_UUID);
				
				// Setting the parameters (places where the "?" exist)
				pstm.setString(1, vmID);
				

				// Execute the query
				rs = pstm.executeQuery();
				while (rs.next()) {
					storageKey = rs.getString("VMStorKey");
					
				}
				
			}

			/**
			 * Catch exception SQL
			 */
			catch (SQLException ex) {

				// handle any errors
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());

			}

			/**
			 * Finally, close the connection and check if the password from the
			 * form equals the password inside the bean
			 */
			finally {
				closeConn(conn);
			}

		}

		// If the connection was not made, return nothing
		else {
			return null;
		}

		// Return 0 if not successful
		return storageKey;
	}
	
	/**
	 * Make the connection with the database and return it
	 * 
	 * @return
	 */
	private static Connection makeConn() {
		dbConn = new DBConnection();
		return conn = dbConn.returnConnection();
	}

	
	/**
	 * Close the connection
	 * 
	 * @param conn
	 */
	private static void closeConn(Connection conn) {
		// Close connection
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	
	
}

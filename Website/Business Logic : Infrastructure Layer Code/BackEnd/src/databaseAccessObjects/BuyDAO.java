package databaseAccessObjects;

import infrastructure.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import beans.VMBean;

/**
 * Class for making a Database Access object. It will get all the data needed
 * from the database and return the object to the back-end servlet for
 * processing the data and sending it to the presentation layer
 * 
 * @author kjellzijlemaker
 *
 */
public class BuyDAO {

	/**
	 * Setting the database connection and connection This will be static,
	 * because only one connection will need instantiation
	 */
	private static DBConnection dbConn = null;
	private static Connection conn = null;



	/**
	 * Executes the login query, but will also add information to the Bean
	 * @return
	 */
	public VMBean addVM(VMBean vmBean, String userID, UUID uuid) {

		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		int rs = 0;
		
		// Adding the VM
		final String ADD_VM = "INSERT INTO vm(VMName, VMCPU, VMOS, VMHDD, VMMemory, VMSLA, VMMonthlyPrice, VMUUID, VMIsActive, VMOrderDate, user_userID, VMState)"
						+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
		final String GET_VM_ID = "select last_insert_id() as last_id from vm";
		
		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {
				
				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(ADD_VM);
				PreparedStatement pstm2 = conn.prepareStatement(GET_VM_ID);

				
				/**
				 * Making timestamp for buying date
				 */
				java.util.Date dt = new java.util.Date();
				java.text.SimpleDateFormat sdf = 
				     new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String currentTime = sdf.format(dt);

				

				// Setting the parameters (places where the "?" exist)
				pstm.setString(1, vmBean.getVMName());
				pstm.setString(2, vmBean.getVMCPU());
				pstm.setString(3, vmBean.getVMOS());
				pstm.setString(4, vmBean.getVMDiskSpace());
				pstm.setString(5, vmBean.getVMMemory());
				pstm.setString(6, vmBean.getVMSLA());
				pstm.setString(7, "8");
				pstm.setString(8, uuid.toString());
				pstm.setInt(9, (int) vmBean.getVMIsActive());
				pstm.setString(10, currentTime);
				pstm.setString(11, userID);
				pstm.setString(12, "Pending");
				
				// Execute the query
				rs = pstm.executeUpdate();

				ResultSet rs2 = pstm2.executeQuery();
				if(rs2.next()){
				vmBean.setVMID(Integer.parseInt(rs2.getString(1)));
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
				if (rs == 1) {
					vmBean.setValid(true);
				} else {
					vmBean.setValid(false);
				}
			}

		}

		// If the connection was not made, return nothing
		else {
			return null;
		}

		// Return the bean for using the data
		return vmBean;
	}

	
	/**
	 * Executes the login query, but will also add information to the Bean
	 * @return
	 */
	public int AssignVMIP(long vmID) {

		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		ResultSet hasIP;
		int addedIP = 0;
		
		// Searching and returning possible IP
		final String GET_VM_IP = "SELECT ID FROM `netaddress` WHERE IsActive = 0";
				
		// Setting the IP active
		final String ADD_VM_IP = "UPDATE netaddress SET IsActive= 1, VM_VMID = ? WHERE ID= ?";
		
		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {
				
				final int IPID;
				
				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(GET_VM_IP);
				PreparedStatement pstm2 = conn.prepareStatement(ADD_VM_IP);
	
				hasIP = pstm.executeQuery();
				
				if(hasIP.next()){
					IPID = hasIP.getInt("ID");
					pstm2.setInt(1, (int) vmID);
					pstm2.setInt(2, IPID);
					addedIP = pstm2.executeUpdate();
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
				if (addedIP == 1) {
					return 1;
				} 
			}

		}

		// If the connection was not made, return nothing
		else {
			return 2;
		}

		// Return the bean for using the data
		return 0;
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

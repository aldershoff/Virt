package databaseAccessObjects;

import infrastructure.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
		final String ADD_VM = "insert into VM(VMName, VMCPU, VMOS, VMHDD, VMMemory, VMSLA, VMMonthlyPrice, VMUUID, VMIsActive, user_userID)"
				+ "values(?,?,?,?,?,?,?,?,?,?)";

		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {

				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(ADD_VM);

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
				pstm.setString(10, userID);
				
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

package databaseAccessObjects;

import infrastructure.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import beans.CustomerBean;
import beans.OverviewBean;
import beans.VMBean;

public class AdminDAO {

	private static DBConnection dbConn = null;
	private static Connection conn = null;

	public ArrayList<CustomerBean> getUserOverview() {

		// Making the arrayList
		ArrayList<CustomerBean> customerBeanArray = new ArrayList<CustomerBean>();

		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		ResultSet rs = null;

		final String GET_USER_OVERVIEW = "SELECT * FROM users";
		// final String GET_USER_OVERVIEW =
		// "SELECT VMid, UserID, VMSLA FROM vm";

		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {

				// Make prepared statement with the desired query
				PreparedStatement pstm = conn
						.prepareStatement(GET_USER_OVERVIEW);

				// Execute the query
				rs = pstm.executeQuery();

				/**
				 * While the resultset will go to the next result, store the
				 * variables. rs.next will only
				 */
				while (rs.next()) {
					CustomerBean bean = new CustomerBean();

					// Setting the information inside the Bean, from the
					// database information
					bean.setUserID(rs.getInt("UserID"));
					bean.setUserName(rs.getString("UserName"));
					bean.setFirstName(rs.getString("UserFirstname"));
					bean.setLastName(rs.getString("UserLastname"));
					bean.setCompany(rs.getString("UserCompany"));
					bean.setEmail(rs.getString("UserEmail"));
					bean.setPhone(rs.getString("UserPhoneNumber"));
					bean.setAddress(rs.getString("UserAddress"));
					bean.setZipCode(rs.getString("UserZipcode"));
					bean.setTwoFactor(rs.getInt("UserTwoFactor"));
					bean.setUserType(rs.getString("UserType"));
					bean.setValid(true);

					customerBeanArray.add(bean);
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
		return customerBeanArray;

	}
	
	
	/**
	 * Executes the login query, but will also add information to the Bean
	 * 
	 * @return
	 */
	public ArrayList<VMBean> getAllVMs(String userID) {

		ArrayList<VMBean> VMBeanArray = new ArrayList<VMBean>();
		
		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		ResultSet rs = null;
		final String SELECT_ALL_VM = "SELECT * FROM vm WHERE user_UserID = ?";

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
					bean.setVMCPU(rs.getString("VMCpu"));
					bean.setVMOS(rs.getString("VMOS"));
					bean.setVMDiskSpace(rs.getString("VMHDD"));
					bean.setVMMemory(rs.getString("VMMemory"));
					bean.setVMSLA(rs.getString("VMSLA"));
					bean.setVMMonthlyPrice(rs.getString("VMMonthlyPrice"));
					bean.setVMState(rs.getString("VMState"));
					bean.setVMIsActive(rs.getInt("VMIsActive"));
					bean.setVMOrderDate(rs.getTimestamp("VMOrderDate").toString());
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
		return VMBeanArray;
		}

	public int setAdmin(String userID) {

		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		int rs = 0;

		final String SET_ADMIN = "UPDATE users SET UserType = 'admin' WHERE UserID = ?";

		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {
				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(SET_ADMIN);

				// Setting the parameters (places where the "?" exist)
				pstm.setString(1, userID);

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

	public int setAdminToUser(String userID) {

		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		int rs = 0;

		final String DEL_ADMIN = "UPDATE users SET UserType = 'user' WHERE UserID = ?";

		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {
				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(DEL_ADMIN);

				// Setting the parameters (places where the "?" exist)
				pstm.setString(1, userID);

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
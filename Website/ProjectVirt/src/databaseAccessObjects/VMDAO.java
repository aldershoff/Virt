package databaseAccessObjects;

import infrastructure.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	public VMBean getVMs(VMBean bean, String userID) {


		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		ResultSet rs = null;
		final String VERIFY_USER = "SELECT vmName FROM vm WHERE userid = " + userID;

		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {

				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(VERIFY_USER);


				// Execute the query
				rs = pstm.executeQuery();

				/**
				 * While the resultset will go to the next result, store the
				 * variables. rs.next will only
				 */
				while (rs.next()) {

					// Setting the information inside the Bean, from the
					// database information
					bean.setVMName(rs.getString("vmName"));
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

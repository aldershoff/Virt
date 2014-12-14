package databaseAccessObjects;

import infrastructure.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class for making a Database Access object. It will get all the data needed
 * from the database and return the object to the back-end servlet for
 * processing the data and sending it to the presentation layer
 * 
 * @author kjellzijlemaker
 *
 */
public class TwoFactorDAO {

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
		public int getMobileID(String regID, String userID) {

			// Making the connection
			conn = makeConn();

			// Setting the resultset and query
			ResultSet rs = null;
			final String GET_MOBILE_ID = "SELECT UserMobileID FROM users WHERE UserID = ?";
			String databaseRegID = null;
			
			/**
			 * If connection is not null, the query can proceed
			 */
			if (conn != null) {

				try {

					// Make prepared statement with the desired query
					PreparedStatement pstm = conn.prepareStatement(GET_MOBILE_ID);

					// Setting the parameters (places where the "?" exist)
					pstm.setString(1, userID);

					// Execute the query
					rs = pstm.executeQuery();

					/**
					 * While the resultset will go to the next result, store the
					 * variables. rs.next will only
					 */
					while (rs.next()) {

						// Setting the information inside the Bean, from the
						// database information
						databaseRegID = rs.getString("UserMobileID");

					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally{
					/**
					 * Check if userRegID equals the one got by the user
					 */
					if (!regID.equals("") || !databaseRegID.equals("")) {
						if (regID.equals(databaseRegID)) {
							return 1;
						} else {
							return 0;
						}

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
	 * Executes the login query, but will also add information to the Bean
	 * 
	 * @return
	 */
	public int registerMobileID(String regID) {

		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		int rs = 0;
		final String INSERT_MOBILE_ID = "insert into users(UserMobileID)"
				+ "values(?)";

		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {

				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(INSERT_MOBILE_ID);

				// Setting the parameters (places where the "?" exist)
				pstm.setString(1, regID);

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
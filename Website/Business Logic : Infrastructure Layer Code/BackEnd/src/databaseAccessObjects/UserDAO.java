package databaseAccessObjects;

import infrastructure.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.CustomerBean;

/**
 * Class for making a Database Access object. It will get all the data needed
 * from the database and return the object to the back-end servlet for
 * processing the data and sending it to the presentation layer
 * 
 * @author kjellzijlemaker
 *
 */
public class UserDAO {

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
	public CustomerBean login(CustomerBean bean) {

		String getFormUsername = bean.getUsername();
		String getFormPassword = bean.getPassword();

		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		ResultSet rs = null;
		final String VERIFY_USER = "SELECT UserName, UserPassword, UserID, UserTwoFactor, UserType FROM users WHERE UserName = ?";

		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {

				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(VERIFY_USER);

				// Setting the parameters (places where the "?" exist)
				pstm.setString(1, getFormUsername);

				// Execute the query
				rs = pstm.executeQuery();

				/**
				 * While the resultset will go to the next result, store the
				 * variables. rs.next will only
				 */
				while (rs.next()) {

					// Setting the information inside the Bean, from the
					// database information
					bean.setUserName(rs.getString("UserName"));
					bean.setPassword(rs.getString("UserPassword"));
					bean.setUserID(rs.getInt("UserID"));
					bean.setTwoFactor(rs.getInt("UserTwoFactor"));
					bean.setUserType(rs.getString("UserType"));

					/**
					 * Check if username and password is correct
					 */
					if (bean.getUsername() != "" || bean.getPassword() != "") {
						if (getFormUsername.equals(bean.getUsername())
								&& getFormPassword.equals(bean.getPassword())) {
							bean.setValid(true);
						} else {
							bean.setValid(false);
						}

					} else {
						bean.setValid(false);
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
	public CustomerBean register(CustomerBean bean) {

		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		int rs = 0;
		final String VERIFY_USER = "insert into users(UserName, UserPassword, UserType, UserFirstname, UserLastname, UserCompany, UserEmail, UserPhoneNumber, UserAddress, UserZipcode, UserMobileID, UserTwoFactor)"
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";

		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {

				// Make prepared statement with the desired query
				PreparedStatement pstm = conn.prepareStatement(VERIFY_USER);

				// Setting the parameters (places where the "?" exist)
				pstm.setString(1, bean.getUsername());
				pstm.setString(2, bean.getPassword());
				pstm.setString(3, "user");
				pstm.setString(4, bean.getFirstName());
				pstm.setString(5, bean.getLastName());
				if (!bean.getCompany().isEmpty()) {
					pstm.setString(6, bean.getCompany());
				} else {
					pstm.setString(6, "");
				}

				pstm.setString(7, bean.getEmail());
				pstm.setString(8, bean.getPhone());
				pstm.setString(9, bean.getAddress());
				pstm.setString(10, bean.getZipCode());
				pstm.setString(11, "");
				pstm.setInt(12, bean.isTwoFactor());

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
					bean.setValid(true);
				} else {
					bean.setValid(false);
				}
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
	 * Getting the user details for profile
	 * 
	 * @return
	 */
	public CustomerBean getUserDetails(CustomerBean bean) {

		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		ResultSet rs = null;
		final String GET_USER_DETAILS = "SELECT * FROM users WHERE UserID = ?";

		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {

				// Make prepared statement with the desired query
				PreparedStatement pstm = conn
						.prepareStatement(GET_USER_DETAILS);

				// Setting the parameters (places where the "?" exist)
				pstm.setInt(1, bean.getUserID());

				// Execute the query
				rs = pstm.executeQuery();

				/**
				 * While the resultset will go to the next result, store the
				 * variables. rs.next will only
				 */
				while (rs.next()) {

					// Setting the information inside the Bean, from the
					// database information
					bean.setUserName(rs.getString("UserName"));
					bean.setFirstName(rs.getString("UserFirstname"));
					bean.setLastName(rs.getString("UserLastname"));
					bean.setCompany(rs.getString("UserCompany"));
					bean.setEmail(rs.getString("UserEmail"));
					bean.setPhone(rs.getString("UserPhoneNumber"));
					bean.setAddress(rs.getString("UserAddress"));
					bean.setZipCode(rs.getString("UserZipcode"));
					bean.setTwoFactor(rs.getInt("UserTwoFactor"));
					bean.setValid(true);
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
	 * Executes the query for changing the information for the user, but will
	 * also add information to the Bean
	 * 
	 * @return
	 */
	public CustomerBean updateUserProfileDetails(CustomerBean bean) {

		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		int rs = 0;
		final String UPDATE_PROFILE_DETAILS_URL = "UPDATE users SET UserFirstName = ?, UserLastName = ?, UserCompany = ?, UserEmail = ?, UserPhoneNumber = ?, UserAddress = ?, UserZipcode = ?, UserTwoFactor = ? WHERE UserID = ?";

		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {

				// Make prepared statement with the desired query
				PreparedStatement pstm = conn
						.prepareStatement(UPDATE_PROFILE_DETAILS_URL);

				// Setting the parameters (places where the "?" exist)
				pstm.setString(1, bean.getFirstName());
				pstm.setString(2, bean.getLastName());
				pstm.setString(3, bean.getCompany());
				pstm.setString(4, bean.getEmail());
				pstm.setString(5, bean.getPhone());
				pstm.setString(6, bean.getAddress());
				pstm.setString(7, bean.getZipCode());
				pstm.setInt(8, bean.isTwoFactor());
				pstm.setInt(9, bean.getUserID());

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
					bean.setValid(true);
				} else {
					bean.setValid(false);
				}
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
	 * Executes the query for changing the information for the user, but will
	 * also add information to the Bean
	 * 
	 * @return
	 */
	public CustomerBean updateUserAccountDetails(CustomerBean bean) {

		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		int rs = 0;
		final String UPDATE_ACCOUNT_DETAILS_URL = "UPDATE users SET UserPassword = ? WHERE UserID = ?";

		/**
		 * If connection is not null, the query can proceed
		 */
		if (conn != null) {

			try {

				// Make prepared statement with the desired query
				PreparedStatement pstm = conn
						.prepareStatement(UPDATE_ACCOUNT_DETAILS_URL);

				// Setting the parameters (places where the "?" exist)
				pstm.setString(1, bean.getPassword());
				pstm.setInt(2, bean.getUserID());

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
					bean.setValid(true);
				} else {
					bean.setValid(false);
				}
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

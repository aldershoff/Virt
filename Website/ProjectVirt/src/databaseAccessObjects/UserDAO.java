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
		final String VERIFY_USER = "SELECT username, password, userid FROM users WHERE username = ?";

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
					bean.setUserName(rs.getString("username"));
					bean.setPassword(rs.getString("password"));
					bean.setUserID(rs.getInt("userid"));

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
		final String VERIFY_USER = "insert into users(username, password, dateofbirth, firstname, lastname, email, phone, address, zipcode)"
				+ "values(?,?,?,?,?,?,?,?,?)";

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
				pstm.setString(3, bean.getDateOfBirth());
				pstm.setString(4, bean.getFirstName());
				pstm.setString(5, bean.getLastName());
				pstm.setString(6, bean.getEmail());
				pstm.setString(7, bean.getPhone());
				pstm.setString(8, bean.getAddress());
				pstm.setString(9, bean.getZipCode());

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

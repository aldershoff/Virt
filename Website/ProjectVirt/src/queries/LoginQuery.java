package queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Class for executing one query for now. Prepared statements are implemented
 * 
 * @author kjellzijlemaker
 *
 */
public class LoginQuery {

	// Variables for making the connection and query
	private String getUsername;
	private String getPassword;
	private final String VERIFY_USER = "SELECT username, password FROM users WHERE username = ? AND password = ?";

	/**
	 * Constructor for initializing the connection, username and password
	 * 
	 * @param conn
	 * @param username
	 * @param password
	 */
	public LoginQuery(String username, String password) {

		this.getUsername = username;
		this.getPassword = password;
	}

	/**
	 * Run the query and return the result
	 * 
	 * @return
	 */
	public int runQuery() {
		return executeQuery();
	}

	/**
	 * Execute query inside the class. This query will get the username and
	 * password for logging in
	 * 
	 * @return
	 */
	private int executeQuery() {
		/**
		 * Setting the query variables needed for execution ResultSet is for
		 * getting the results DBConnection is for making new Databaseconnection
		 * from the DBConnection class The Connection is for getting the
		 * connection from the database and use it locally
		 */
		ResultSet rs = null;
		String checkUsername = null;
		String checkPassword = null;
		DBConnection dbConn = null;
		Connection conn = null;

		try {
			// Initializing the dbconn and conn for use
			dbConn = new DBConnection();
			conn = dbConn.returnConnection();

			// Make prepared statement with the desired query
			PreparedStatement pstm = conn.prepareStatement(VERIFY_USER);

			// Setting the parameters (places where the "?" exist)
			pstm.setString(1, getUsername);
			pstm.setString(2, getPassword);

			// Execute the query
			rs = pstm.executeQuery();

			/**
			 * While the resultset will go to the next result, store the
			 * variables
			 */
			while (rs.next()) {
				checkUsername = rs.getString("username");
				checkPassword = rs.getString("password");
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

		// Close connection
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;

		// Return 1 or 0 for checking if username and password combination were
		// correct or not
		return checkIfCorrect(checkUsername, checkPassword);

	}

	/**
	 * Small method for checking if the query results are matching with the
	 * given username and password. This is very important! When succes, give 1.
	 * When failed, give 0.
	 * 
	 * @param checkUsername
	 * @param checkPassword
	 * @return
	 */
	private int checkIfCorrect(String checkUsername, String checkPassword) {
		if (checkUsername != null) {
			if (checkPassword.equals(getPassword)) {
				return 1;
			} else {
				return 0;
			}
		}
		return 0;
	}
}

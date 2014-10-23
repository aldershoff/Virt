package queries;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CheckLogin {

	private Connection conn;
	private String getUsername;
	private String getPassword;

	public CheckLogin(Connection conn, String username, String password) {
		this.conn = conn;
		this.getUsername = username;
		this.getPassword = password;
	}

	public int runQuery() {
		return executeQuery();
	}

	private int executeQuery() {
		Statement stmt = null;
		ResultSet rs = null;
		String checkUsername = null;
		String checkPassword = null;
		try{
			stmt = conn.createStatement();
			rs = stmt
					.executeQuery("SELECT username, password FROM Users WHERE username = "
							+ "\"" + getUsername + "\"" + " AND id > 0");
			while (rs.next()) {
				checkUsername = rs.getString("username");
				checkPassword = rs.getString("password");
			}

			if (checkUsername != null) {
				if (checkPassword.equals(getPassword)) {
					return 1;
				} else {
					return 0;
				}
			}
		}

		catch (SQLException ex){

			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());

		}
		closeConn();
		return 0;

	}

	private void closeConn() {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

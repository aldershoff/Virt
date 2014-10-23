package queries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

	private Connection connection;

	public Connection returnConnection() {
		try {
			String driverName = "org.gjt.mm.mysql.Driver"; // MySQL MM JDBC
															// driver
			Class.forName(driverName);
			String serverName = "localhost";
			String mydatabase = "ProjectVirt";

			String url = "jdbc:mysql://" + serverName + "/" + mydatabase; // a
																			// JDBC
																			// url
			String sqlusername = "root";
			String sqlpassword = "";

			connection = DriverManager.getConnection(url, sqlusername,
					sqlpassword);

		} catch (ClassNotFoundException e) {

			// Could not find the database driver
			System.err.print("Could not connect to db driver");
		}

		catch (SQLException e) {

			// Could not connect to the database
			System.err.print("Could not connect to db");
		}

		return connection;
	}
	
	public void closeConnection() throws SQLException{
		connection.close();
	}
}

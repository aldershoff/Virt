package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class for making new database connections It's also possible to give SSL
 * connection property with the connection
 * 
 * @author kjellzijlemaker
 *
 */
public class DBConnection {
	// Global connection for returning
	private Connection connection;

	/**
	 * Making the connection and return it
	 * 
	 * @return
	 */
	public Connection returnConnection() {
		try {

			// Driver name (in jar packet)
			String driverName = "org.gjt.mm.mysql.Driver"; // MySQL MM JDBC
			// driver
			// Setting the driver
			Class.forName(driverName);

			// Setting server and database names
			String serverName = "localhost";
			String mydatabase = "projectVirt";

			// Setting the url with all the above variables
			String url = "jdbc:mysql://" + serverName + "/" + mydatabase; // a
			// JDBC
			// url
			// Setting login and password for database
			String sqlusername = "root";
			String sqlpassword = "UNFAH2Ww6waA";

			// Initiazing the connection with the drivermanager for making a
			// real connection
			connection = DriverManager.getConnection(url, sqlusername,
					sqlpassword);
		}

		/**
		 * Catch sql exception
		 */
		catch (SQLException e) {

			// Could not connect to the database
			System.err.print("Could not connect to db!");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Eventually return the connection
		return connection;
	}

	/**
	 * Close connection when done
	 * 
	 * @throws SQLException
	 */
	public void closeConnection() throws SQLException {
		connection.close();
	}
}

package queries;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public final class QueryWithContext {

	public static PrintWriter query(PrintWriter out) throws NamingException {
		Context context = null;
		Connection connect = null;
		Statement statement = null;

		try {

			// Get the context and create a connection
			context = new InitialContext();
			connect = new DBConnection().returnConnection();

			// Create the statement to be used to get the results.
			statement = connect.createStatement();
			String query = "SELECT * FROM the_classics";

			// Execute the query and get the result set.
			ResultSet resultSet = statement.executeQuery(query);
			out.println("<strong>Printing result using context file...</strong><br>");
			while (resultSet.next()) {
				String albumName = resultSet.getString("name");
				String artist = resultSet.getString("artist");
				int year = resultSet.getInt("year");

				out.println("Album: " + albumName + ", by Artist: " + artist
						+ ", released in: " + year + "<br>");
			}
		} catch (SQLException e) {
			e.printStackTrace(out);
		} finally {
			// Close the connection and release the resources used.
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace(out);
			}
			try {
				connect.close();
			} catch (SQLException e) {
				e.printStackTrace(out);
			}
		}
		return out;
	}
}
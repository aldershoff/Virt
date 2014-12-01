package databaseAccessObjects;

import infrastructure.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import com.mysql.fabric.xmlrpc.base.Array;

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
	public ArrayList<VMBean> getVMs(int i) {

		ArrayList<VMBean> VMBeanArray = new ArrayList<VMBean>();
		
		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		ResultSet rs = null;
		final String VERIFY_USER = "SELECT vmName, vmid FROM vm WHERE userid = " + i;

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
					VMBean bean = new VMBean();
					
					// Setting the information inside the Bean, from the
					// database information
					bean.setVMName(rs.getString("vmName"));
					bean.setVmID(rs.getInt("vmid"));
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

		// If the connection was not made, return nothing
		else {
			return null;
		}

		// Return the bean for using the data
		return VMBeanArray;
	}
	
	
	/**
	 * Executes the login query, but will also add information to the Bean
	 * 
	 * @return
	 */
	public VMBean getSpecificVM(VMBean bean, int userID, String vmID) {

		
		// Making the connection
		conn = makeConn();

		// Setting the resultset and query
		ResultSet rs = null;
		final String VERIFY_USER = "SELECT vmName, vmid FROM vm WHERE userid = " + userID + " AND vmid = " + vmID;

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
					bean.setVmID(rs.getInt("vmid"));
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

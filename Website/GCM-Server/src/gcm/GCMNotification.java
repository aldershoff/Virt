package gcm;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import json.JsonPOSTParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

/**
 * Servlet implementation class GCMNotification
 */
@WebServlet("/GCMNotification")
public class GCMNotification extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	// Put your Google API Server Key here
		private static final String GOOGLE_SERVER_KEY = "AIzaSyBkFbdB7QLS5xiqe6kgLGhcbZN1PRTbg4c";
		static final String MESSAGE_KEY = "message";
		static final String REG_ID_STORE = "GCMRegId.txt";



		public GCMNotification() {
			super();
		}

		protected void doGet(HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {
			doPost(request, response);

		}

		@SuppressWarnings("unchecked")
		protected void doPost(HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {


		String regID = "";
		String share = request.getParameter("shareRegId");

		/**
		 * When registering Android device
		 */
		if (request.getParameterMap().containsKey("shareRegId")) {

			if (request.getParameter("shareRegId").contains("1")) {

				// GCM RedgId of Android device to send push notification

				// If the adnroid device wants to share id:
				if (share != null && !share.isEmpty()) {
					regID = request.getParameter("regId");
					String userID = request.getParameter("userId");
					
					// Write the ID to the database
					long result = writeToDatabase(regID, userID, request);
					
					JSONObject jobj = new JSONObject();
					try{
					if(result == 1){
						jobj.put("success", "1");
					}
					else if(result == 2){
						jobj.put("success", "2");
					}
					else{
						jobj.put("success", "0");
					}
					}
					finally{
						response.setContentType("application/json");
						response.getWriter().write(jobj.toString());
					}
				}
			}

		}
		


		/**
		 * Else, the user requested to send message to Android device
		 */

		else {
			JSONObject jobj = new JSONObject();
			try {

				String userID = request.getParameter("userID");

				// Here, we will check if the database contains the regID with
				// userID
				long result = readFromDatabase(userID, request);

				// if the user has a regID, corresponding with the user, the
				// user
				// will get message
				if (result == 1) {
					try {

						String userMessage = request.getParameter("message");
						Sender sender = new Sender(GOOGLE_SERVER_KEY);
						Message message = new Message.Builder().timeToLive(30)
								.delayWhileIdle(true)
								.addData(MESSAGE_KEY, userMessage).build();

						System.out.println("regId: " + regID);

						sender.send(message, regID, 1);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}

					// JSONObject obj = new JSONObject();
					jobj.put("success",
							"Message underway! Please check your mobile frequently.");
			
				} else if (result == 2) {
					jobj.put("error", "Something went really wrong...");
					response.getWriter().write(jobj.toString());
					this.log("Something went wrong");
				} else {
					jobj.put("error",
							"No android device found. Please download the app and login.");

					this.log("please log in");
				}

			}

			finally {
				response.setContentType("application/json");
				response.getWriter().write(jobj.toString());

			}
		}

	}
				
//		writeToDatabase(regID, request);
//		request.setAttribute("pushStatus", "GCM RegId Received.");
//		request.getRequestDispatcher("index.jsp")
//				.forward(request, response);

				
		

		private long writeToDatabase(String regId, String userID, HttpServletRequest request) throws IOException {
			
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("regID", regId));
			postParameters.add(new BasicNameValuePair("userID", userID));

			JSONObject json = JsonPOSTParser.postJsonFromUrl(request,
					"http://localhost:8080/BackEnd/customer/insertUserMobileID",
					postParameters);

			return (long) json.get("result");
			

		}

		/**
		 * Read and check the mobileID from the database..
		 * @param regId
		 * @param userID
		 * @param request
		 * @return
		 * @throws IOException
		 */
		private long readFromDatabase(String userID, HttpServletRequest request) throws IOException {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("userID", userID));
			
			JSONObject json = JsonPOSTParser.postJsonFromUrl(request,
					"http://localhost:8080/BackEnd/customer/getUserMobileID",
					postParameters);

			return (long) json.get("result");
			
		}
		
		
		
//		/**
//		 * Android sends a message back
//		 */
//		else if (request.getParameterMap().containsKey("androidConfirm")) {
//			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
//
//			try {
//				if (request.getParameter("androidConfirm").contains("1")) {
//
//					String callBackRegID = request.getParameter("regID");
//
//					postParameters.add(new BasicNameValuePair("regID",
//							callBackRegID));
//					postParameters.add(new BasicNameValuePair("result", "1"));
//				} else {
//					postParameters.add(new BasicNameValuePair("result", "0"));
//				}
//			} finally {
//				JsonPOSTParser
//						.postJsonFromUrl(
//								request,
//								"http://localhost:8080/BackEnd/customer/insertUserMobileID",
//								postParameters);
//			}
//
//		}
	
}

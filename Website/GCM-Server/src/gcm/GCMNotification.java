package gcm;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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


		String regID = request.getParameter("regId");

		String userMessage = request.getParameter("message");
		Sender sender = new Sender(GOOGLE_SERVER_KEY);
		Message message = new Message.Builder().timeToLive(30)
								.delayWhileIdle(true)
								.addData(MESSAGE_KEY, userMessage).build();

		sender.send(message, regID, 1);


		JSONObject jobj = new JSONObject();
		jobj.put("success",
					"Message underway! Please check your mobile frequently.");
		response.setContentType("application/json");
		response.getWriter().write(jobj.toString());
		}

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
	


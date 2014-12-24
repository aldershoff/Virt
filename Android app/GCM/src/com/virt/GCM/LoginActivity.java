package com.virt.GCM;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity for logging into the server of PlainTech
 * 
 * @author KjellZijlemaker
 * 
 */
public class LoginActivity extends Activity {

	ShareExternalServer appUtil;
	String regId;
	Button btnLogin;
	AsyncTask<Void, Void, String> shareRegidTask;
	EditText username;
	EditText password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		appUtil = new ShareExternalServer();

		// Setting ID
		regId = getIntent().getStringExtra("regId");
		Log.d("MainActivity", "regId: " + regId);

		// Setting username / password fields
		username = (EditText) findViewById(R.id.setUsername);
		password = (EditText) findViewById(R.id.setPassword);

		/**
		 * Setting button and listening on the click
		 */
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				/**
				 * Getting username and password when clicked the button
				 */
				String getUsername = username.getText().toString();
				String getPassword = password.getText().toString();

				// Send the message to the server
				runRegisterRegID(getUsername, getPassword);
			}
		});

	}

	/**
	 * Method for sending the log-in message to the PlainTech server
	 * 
	 * @param username
	 * @param password
	 */
	private void runRegisterRegID(final String username, final String password) {
		final Context context = this;
		final ProgressDialog mDialog = new ProgressDialog(context);
		shareRegidTask = new AsyncTask<Void, Void, String>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();

				/**
				 * Setting loading message and show it
				 */
				mDialog.setMessage("Logging in...");
				mDialog.show();
			}

			@Override
			protected String doInBackground(Void... params) {

				/**
				 * Sending the message
				 */
				String result = appUtil.shareRegIdWithAppServer(context, regId,
						username, password);
				return result;
			}

			@Override
			protected void onPostExecute(String result) {

				/**
				 * Get message result and dismiss the loading screen
				 */
				shareRegidTask = null;
				Toast.makeText(getApplicationContext(), result,
						Toast.LENGTH_LONG).show();

				if (mDialog.isShowing()) {
					mDialog.dismiss();
				}
			}

		};
		shareRegidTask.execute(null, null, null);

	}
}

package com.virt.GCM;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Activity when opening the app. Here, the user will choose for registering to
 * the GCM server or logging into the PlainTech server, and/or both.
 * 
 * @author KjellZijlemaker
 * 
 */
public class MainActivity extends Activity {

	Button btnGCMRegister;
	Button btnAppShare;
	GoogleCloudMessaging gcm;
	Context context;
	String regId;

	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";

	static final String TAG = "Register Activity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = getApplicationContext();

		/**
		 * Setting button for registering and listen to it This is for the GCM
		 * server
		 */
		btnGCMRegister = (Button) findViewById(R.id.btnGCMRegister);
		btnGCMRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (TextUtils.isEmpty(regId)) {
					regId = registerGCM();
					Log.d("RegisterActivity", "GCM RegId: " + regId);
				} else {
					Toast.makeText(getApplicationContext(),
							"Already Registered with GCM Server!",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		/**
		 * Setting the button for registering and listen to it This is for the
		 * PlainTech server
		 */
		btnAppShare = (Button) findViewById(R.id.btnAppShare);
		btnAppShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (TextUtils.isEmpty(regId)) {
					Toast.makeText(getApplicationContext(), "RegId is empty!",
							Toast.LENGTH_LONG).show();
				} else {
					Intent i = new Intent(getApplicationContext(),
							LoginActivity.class);
					i.putExtra("regId", regId);
					Log.d("RegisterActivity",
							"onClick of Share: Before starting main activity.");
					startActivity(i);
					Log.d("RegisterActivity", "onClick of Share: After finish.");
				}
			}
		});
	}

	/**
	 * Method for registering to the GCM server. It will also give the regID if
	 * successful and
	 * 
	 * @return
	 */
	public String registerGCM() {

		gcm = GoogleCloudMessaging.getInstance(this);
		regId = getRegistrationId(context);

		if (TextUtils.isEmpty(regId)) {

			registerInBackground();

			Log.d("RegisterActivity",
					"registerGCM - successfully registered with GCM server!");
		} else {
			Toast.makeText(getApplicationContext(),
					"ID already available! You can now log into PlainTech!",
					Toast.LENGTH_LONG).show();
		}
		return regId;
	}

	/**
	 * Method for getting the registration ID that was saved
	 * 
	 * @param context
	 * @return
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getSharedPreferences(
				LoginActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * Get the app version that was saved
	 * 
	 * @param context
	 * @return
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.d("RegisterActivity",
					"I never expected this! Going down, going down!" + e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Registering to the GCM server and getting the new RegID This is also done
	 * with the Project ID for authentication
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(Config.GOOGLE_PROJECT_ID);
					Log.d("RegisterActivity", "registerInBackground - regId: "
							+ regId);
					msg = "Device successfully registered!";

					storeRegistrationId(context, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
				}
				Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG)
						.show();
			}
		}.execute(null, null, null);
	}

	/**
	 * Storing the ID inside the app
	 * 
	 * @param context
	 * @param regId
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getSharedPreferences(
				LoginActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}
}

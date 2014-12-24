package com.virt.GCM;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Activity for showing the pin in more detail
 * 
 * @author KjellZijlemaker
 * 
 */
public class ShowPinDetailedActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_pin_detailed);

		// When intent was triggered, activate the method
		onNewIntent(getIntent());

	}

	@Override
	public void onNewIntent(Intent intent) {
		Bundle extras = intent.getExtras();

		/**
		 * If the extras inside the intent are not null, get the message
		 */
		if (extras != null) {
			if (extras.containsKey("pinMessage")) {

				/**
				 * Print the message to the screen
				 */
				setContentView(R.layout.activity_show_pin_detailed);
				String msg = extras.getString("pinMessage");
				TextView txtView = (TextView) findViewById(R.id.pin);
				txtView.setText(msg);
			}
		}
	}
}

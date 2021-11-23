package com.taiwanmobile.volunteers.v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.taiwanmobile.volunteers.R;
import com.crashlytics.android.Crashlytics;
import com.taiwanmobile.volunteers.v1.loginview.V1RegisterFragment;

import io.fabric.sdk.android.Fabric;

/*
 * Showing splash screen with a timer. This will be useful when you want to show
 * case your app logo / company
 */
public class SplashScreenActivity extends Activity {
	private static int SPLASH_TIME_OUT = 1000;
	public static Bundle bundle = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_splash);

		onNewIntent(getIntent());

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// This method will be executed once the timer is over
				// Start your app main activity
				Intent i = new Intent(SplashScreenActivity.this,
						MainActivity.class);

				startActivity(i);

				// close this activity
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

	@Override
	public void onNewIntent(Intent intent) {
		bundle = intent.getExtras();
	}
}

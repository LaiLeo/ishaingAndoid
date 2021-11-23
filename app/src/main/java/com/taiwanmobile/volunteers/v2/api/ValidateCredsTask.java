package com.taiwanmobile.volunteers.v2.api;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ValidateCredsTask extends AsyncTask<Void, Void, Boolean> {

	Context mContext;
	int returnStatusCode;

	public ValidateCredsTask(Context c) {
		mContext = c;
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		if (!MyPreferenceManager.hasCredentials(mContext)) {
			return false;
		}
		try {
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
					.url(BackendContract.V2_LOGIN_URL)
					.addHeader("Authorization", "Token " + MyPreferenceManager.getAuthToken(mContext))
					.build();

			Log.d("ValidateCredsTask", BackendContract.V2_LOGIN_URL);
			Log.d("ValidateCredsTask", "Token:" + MyPreferenceManager.getAuthToken(mContext));

			Response response = client.newCall(request).execute();
			returnStatusCode = response.code();

			Log.d("ValidateCredsTask","returnStatusCode = "+ returnStatusCode);
			Log.d("ValidateCredsTask","Response = "+ response.body().string());
			if (returnStatusCode == 200) {
				return true;
			}
		} catch (Exception e) {
			Log.e("Error", Log.getStackTraceString(e));
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		Log.e("test", "code = " + returnStatusCode);
		if (returnStatusCode == 401) {
			MyPreferenceManager.ForceLogout((Activity) mContext);
			// Toast.makeText(mContext,
			// mContext.getString(R.string.login_on_other_device),
			// Toast.LENGTH_LONG).show();
		}
	}
}

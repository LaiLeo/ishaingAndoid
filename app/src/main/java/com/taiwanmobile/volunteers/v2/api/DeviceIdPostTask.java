package com.taiwanmobile.volunteers.v2.api;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.gcm.GCMUtils;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DeviceIdPostTask extends AsyncTask<Void, Void, Boolean> {
	private final String TAG = getClass().getSimpleName();

	public static final String DEVICE_NAME = "device_name";
	public static final String DEVICE_UUID = "uuid";
	public static final String DEVICE_TYPE = "android";

	Context mContext;
	JSONObject jsonObject = null;
	String reportId;
	String deviceId;

	public DeviceIdPostTask(Context c) {
		mContext = c;
		deviceId = MyPreferenceManager.getDeviceId(mContext);
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		if (!GCMUtils.checkPlayServices((Activity) mContext)) {
			return false;
		} else {
			if (StringUtils.isBlank(deviceId)) {
				try {
					GoogleCloudMessaging gcm = GoogleCloudMessaging
							.getInstance(mContext);
					deviceId = gcm.register(BackendContract.SENDER_ID);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
				Log.i(TAG, "registered: " + deviceId);
			} else {
				return true;
			}
		}

		try {

			OkHttpClient client = new OkHttpClient();
			FormBody.Builder formBuilder = new FormBody.Builder()
					.add(DEVICE_NAME, DEVICE_TYPE)
					.add(DEVICE_UUID, deviceId);

			Request.Builder requestBuilder = new Request.Builder()
					.url(BackendContract.DEVICE_URL)
					.post(formBuilder.build());

			if (MyPreferenceManager.hasCredentials(mContext)) {
				requestBuilder.addHeader("Authorization", "Token " + MyPreferenceManager.getAuthToken(mContext));
			}

			Response response = client.newCall(requestBuilder.build()).execute();
			ResponseBody body = response.body();

			int returnStatusCode = response.code();
			if (returnStatusCode == 201) {
				MyPreferenceManager.setDeviceId(mContext, deviceId);
				return true;
			}
			String responseString = body.string();

			Log.e(TAG, "" + returnStatusCode);
			Log.e(TAG, responseString);
		} catch (Exception e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}
}

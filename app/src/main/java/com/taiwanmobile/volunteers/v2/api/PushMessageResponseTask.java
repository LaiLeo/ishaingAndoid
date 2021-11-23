package com.taiwanmobile.volunteers.v2.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.taiwanmobile.volunteers.v2.BackendContract;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PushMessageResponseTask extends AsyncTask<Void, Void, Boolean> {
	private final String TAG = getClass().getSimpleName();

	private static final String PUSH_MESSAGE_ID = "id";

	private final Context mContext;
	private final String messageId;

	public PushMessageResponseTask(Context c, String id) {
		mContext = c;
		messageId = id;
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			OkHttpClient client = new OkHttpClient();
			FormBody.Builder formBuilder = new FormBody.Builder()
					.add(PUSH_MESSAGE_ID, messageId);

			Request request = new Request.Builder()
					.url(BackendContract.MESSAGE_READ_URL)
					.post(formBuilder.build())
					.build();

			Response response = client.newCall(request).execute();
			ResponseBody body = response.body();

			int returnStatusCode = response.code();
			if (returnStatusCode == 200) {
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

}

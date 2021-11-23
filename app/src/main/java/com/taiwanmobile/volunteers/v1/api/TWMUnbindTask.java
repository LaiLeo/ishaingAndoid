package com.taiwanmobile.volunteers.v1.api;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.taiwanmobile.volunteers.v1.util.WebServiceParams;
import com.taiwanmobile.volunteers.v1.util.WebServiceUtil;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.userprofileview.UserProfileEditFragment;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TWMUnbindTask extends BaseRequestTask{
	public static final String TAG = "TWMUnbindTask";

	public TWMUnbindTask(Context c) {
		super(c);

		Log.d(TAG, "create TWMUnbindTask");
		mContext = c;

		setProgressDialog(DialogFactory.createBusyDialog(c, "解綁中"));
		setSuccessDialog(DialogFactory.createDummyDialog(c, this));
		setFailDialog(DialogFactory.createDummyDialog(c, this));
	}

	private Request buildTWMUnbindRequest(){
		String accessToken = MyPreferenceManager.getAuthToken(mContext);

		String url = BackendContract.V1_USER_TWM;
		Log.d(TAG, "set url = " + url);


		Request.Builder requestBuilder = new Request.Builder()
				.url(url).delete();

		Map<String, String> headers = WebServiceUtil.getWebServiceHeaders(mContext,
				WebServiceParams.CommonValues.Headers_Content_Type_APPLICATION_JSON, accessToken);

		Set<String> keySets = headers.keySet();
		for(String key : keySets){
			String value = headers.get(key);
			Log.d(TAG, "addHeader, key = " + key + ", value = "+ value);
			requestBuilder.addHeader(key, value);
		}

		return requestBuilder.build();
	}

	public Response executeRequest(Request request){
		OkHttpClient client = new OkHttpClient();
		try {
			Response response = client.newCall(request).execute();
			return response;
		} catch (IOException e) {
			e.printStackTrace();
			sendResult(UserProfileEditFragment.MSG_TWM_UNBIND_FAILED);
		}
		return null;
	}


	@Override
	public Boolean doInBackground(Void... arg0) {

		Log.d(TAG, "doInBackground");

		Request request = buildTWMUnbindRequest();
		Response response = executeRequest(request);
		return handleResponse(response);

	}

	public boolean handleResponse(Response response){

		if(response == null){
			sendResult(UserProfileEditFragment.MSG_TWM_UNBIND_FAILED);
			return false;
		}

		try {
			ResponseBody body = response.body();
			String responseString = body.string();
			Log.d(TAG, responseString);
			int code = response.code();
			Log.d(TAG, "responseCode = " + code);
			if (code == 200) {
				sendResult(UserProfileEditFragment.MSG_TWM_UNBIND_SUCCESS);
				return true;
			}else{
				sendResult(UserProfileEditFragment.MSG_TWM_UNBIND_FAILED);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			sendResult(UserProfileEditFragment.MSG_TWM_UNBIND_FAILED);
		}

		return false;
	}

	@Override
	protected void onPostExecute(Boolean aBoolean) {
		super.onPostExecute(aBoolean);
		Log.d(TAG, "onPostExecute");
	}


	@Override
	public void onDismiss(DialogInterface dialog) {
	}
}

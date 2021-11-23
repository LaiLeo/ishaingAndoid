package com.taiwanmobile.volunteers.v1.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.taiwanmobile.volunteers.v1.loginview.FubonBindFragment;
import com.taiwanmobile.volunteers.v1.loginview.FubonLoginFragment;
import com.taiwanmobile.volunteers.v1.loginview.TWMBindFragment;
import com.taiwanmobile.volunteers.v1.util.WebServiceParams;
import com.taiwanmobile.volunteers.v1.util.WebServiceUtil;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FubonBindTask extends BaseRequestTask{
	public static final String TAG = "FubonBindTask";


	String username;
	String password;

	public FubonBindTask(Context c, String name,
                         String pwd) {
		super(c);

		Log.d(TAG, "create FubonBindTask");
		username = name;
		password = pwd;

		setProgressDialog(DialogFactory.createBusyDialog(c, "綁定中"));
		setSuccessDialog(DialogFactory.createDummyDialog(c, this));
		setFailDialog(DialogFactory.createDummyDialog(c, this));
	}

	private Request buildFubonBindRequest(){
		String accessToken = MyPreferenceManager.getAuthToken(mContext);

		MediaType mediaType = MediaType.parse(WebServiceParams.CommonValues.Headers_Content_Type_APPLICATION_JSON);


		String url = BackendContract.V1_USER_FUBON;
		Log.d(TAG, "set url = " + url);
		Log.d(TAG, "set username = " + username);
		Log.d(TAG, "set password = " + password);


		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("username", username);
		jsonObject.addProperty("password", password);

		Log.d(TAG, "set requestBody = " + jsonObject.toString());

		RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());

		Request.Builder requestBuilder = new Request.Builder()
				.url(url)
				.post(requestBody);

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
			sendResult(FubonBindFragment.MSG_BIND_FAILED);
		}
		return null;
	}


	@Override
	public Boolean doInBackground(Void... arg0) {

		Log.d(TAG, "doInBackground");

		Request request = buildFubonBindRequest();
		Response response = executeRequest(request);
		return handleResponse(response);

	}

	public boolean handleResponse(Response response){

		if(response == null){
			return false;
		}

		try {
			ResponseBody body = response.body();
			String responseString = body.string();
			Log.d(TAG, responseString);
			mStatusCode = response.code();
			Log.d(TAG, "responseCode = " + mStatusCode);
			if (mStatusCode != 200) {
				String reason = mStatusCode + "";
				sendResult(FubonBindFragment.MSG_BIND_FAILED, reason);
				return false;
			}

			JSONObject jsonObject =  new JSONObject(responseString);

			if(!jsonObject.has("errors")){
				sendResult(FubonBindFragment.MSG_BIND_SUCCESS);
				return true;
			}else{
				JSONArray errors = jsonObject.getJSONArray("errors");
				JSONObject error = errors.getJSONObject(0);
				mError = error.getString("error");
				sendResult(FubonBindFragment.MSG_BIND_FAILED, mError);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			sendResult(FubonLoginFragment.MSG_AUTH_FAILED);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			sendResult(FubonLoginFragment.MSG_AUTH_FAILED);
		}
		return false;
	}

}

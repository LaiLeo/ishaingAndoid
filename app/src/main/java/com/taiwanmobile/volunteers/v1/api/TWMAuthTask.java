package com.taiwanmobile.volunteers.v1.api;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;
import com.j256.ormlite.misc.TransactionManager;
import com.taiwanmobile.volunteers.v1.util.WebServiceParams;
import com.taiwanmobile.volunteers.v1.util.WebServiceUtil;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v1.loginview.FubonLoginFragment;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.utils.AsyncTaskWithDialogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TWMAuthTask extends BaseRequestTask{
	public static final String TAG = "TWMAuthTask";


	public static final String RESPONSE_JSON_KEY_TOKEN = "token";
	public static final String RESPONSE_JSON_KEY_ERRORS = "errors";
	public static final String RESPONSE_JSON_KEY_USER_NAME = "username";
	public static final String RESPONSE_JSON_KEY_ACCESS_CODE = "accessCode";
	public static final String RESPONSE_JSON_KEY_ERR = "error";
	public static final String RESPONSE_JSON_KEY_ACCESS_TOKEN = "accessToken";

	public static final String ERROR_JSON_NOT_REGISTER = "userNotRegister";
	public static final String ERROR_JSON_USER_NOT_FOUND = "userNotFound";

	String code;

	public int returnStatusCode = 0;

	public TWMAuthTask(Context c, String code) {
		super(c);

		Log.d(TAG, "create TWMAuthTask");
		this.code = code;

//		setProgressDialog(DialogFactory.createBusyDialog(c, "登入中"));
//		setSuccessDialog(DialogFactory.createDummyDialog(c, this));
//		setFailDialog(DialogFactory.createDummyDialog(c, this));
	}

	private Request buildAuthRequest(){

		MediaType mediaType = MediaType.parse(WebServiceParams.CommonValues.Headers_Content_Type_APPLICATION_JSON);


		String url = BackendContract.V1_AUTH_TWM;
		Log.d(TAG, "set url = " + url);
		Log.d(TAG, "set code = " + code);


		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("code", code);

		Log.d(TAG, "set requestBody = " + jsonObject.toString());

		RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());

		Request.Builder requestBuilder = new Request.Builder()
				.url(url)
				.post(requestBody);

		Map<String, String> headers = WebServiceUtil.getWebServiceHeaders(mContext,
				WebServiceParams.CommonValues.Headers_Content_Type_APPLICATION_JSON);

		Set<String> keySets = headers.keySet();
		for(String key : keySets){
			String value = headers.get(key);
			Log.d(TAG, "addHeader, key = " + key + ", value = "+ value);
			requestBuilder.addHeader(key, value);
		}

		return requestBuilder.build();
	}


	@Override
	public Boolean doInBackground(Void... arg0) {

		Log.d(TAG, "doInBackground");

		Request request = buildAuthRequest();
		Response response = executeRequest(request);

		if(response != null){
			try {
				ResponseBody body = response.body();
				String responseString = body.string();
				Log.d(TAG, responseString);
				returnStatusCode = response.code();
				Log.d(TAG, "responseCode = " + code);
				if (returnStatusCode == 200) {
					return handleAuthResult(responseString);
				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
			return false;
		}else{
			return false;
		}

	}

	@Override
	protected void onPostExecute(Boolean aBoolean) {
		super.onPostExecute(aBoolean);
		Log.d(TAG, "onPostExecute");
	}

		@Override
	public void onDismiss(DialogInterface arg0) {
		if (returnStatusCode == 200) {
//			MyPreferenceManager.clearInitialzed(mContext);
//			MainActivity.MyDbHelper.close();
//			MainActivity.mMainActivity.isExit = true;
//			parentFragment.getActivity().finish();
//			parentFragment.startActivity(parentFragment.getActivity()
//					.getIntent());
		} else if (returnStatusCode == 403) {
//			new DialogFactory.TitleMessageDialog(mContext, "電子信箱已存在",
//					"該電子信箱已註冊過，請重新輸入或使用忘記密碼功能").show();
		} else {
//			new DialogFactory.TitleMessageDialog(mContext, "上傳失敗",
//					"請確認手機上網功能已開啟").show();
		}
	}



	public boolean handleAuthResult(String responseString){
		try {
			JSONObject jsonObject = new JSONObject(responseString);

			if(jsonObject.has(RESPONSE_JSON_KEY_ERRORS)){//Error handling

				JSONArray errors = jsonObject.getJSONArray(RESPONSE_JSON_KEY_ERRORS);

				//Print error log
				Log.d(TAG, "errors = " + errors);

				if(errors == null || errors.length() == 0){
					return false;
				}

				JSONObject error = errors.getJSONObject(0);
				String errStr = error.getString(RESPONSE_JSON_KEY_ERR);


				switch (errStr){
					case ERROR_JSON_NOT_REGISTER:
						Log.d(TAG, "handle not register case ");
						String accessCode = jsonObject.getString(RESPONSE_JSON_KEY_ACCESS_CODE);

						Bundle data = new Bundle();
						data.putString(RESPONSE_JSON_KEY_ACCESS_CODE, accessCode);


						if(jsonObject.has(RESPONSE_JSON_KEY_USER_NAME)){
							data.putString(RESPONSE_JSON_KEY_USER_NAME, jsonObject.getString(RESPONSE_JSON_KEY_USER_NAME));
						}else{
							data.putString(RESPONSE_JSON_KEY_USER_NAME, "");
						}
						sendResult(FubonLoginFragment.MSG_ACCOUNT_NOT_REGISTER, data);
						break;
					case ERROR_JSON_USER_NOT_FOUND:
						Log.d(TAG, "handle login failed case ");

						sendResult(FubonLoginFragment.MSG_ACCOUNT_VALIDATE_ERROR);
						break;
					default:
						Log.d(TAG, "handle other failed case ");
						sendResult(FubonLoginFragment.MSG_AUTH_FAILED);
						break;
				}

			}else if(jsonObject.has(RESPONSE_JSON_KEY_TOKEN)){
				JSONObject tokenObj = jsonObject.getJSONObject(RESPONSE_JSON_KEY_TOKEN);
				Log.d(TAG, "tokenObj = " + tokenObj);
				String accessToken = tokenObj.getString(RESPONSE_JSON_KEY_ACCESS_TOKEN);

				Bundle data = new Bundle();
				data.putString(RESPONSE_JSON_KEY_ACCESS_TOKEN, accessToken);

				String userName = jsonObject.getString(RESPONSE_JSON_KEY_USER_NAME);

				Log.d(TAG, "accessToken = " + accessToken);
				handleLoginSuccess(accessToken, userName);
				sendResult(FubonLoginFragment.MSG_AUTH_SUCCESS, data);
				return true;
			}


		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void handleLoginSuccess(String accessToken, String userName){

		Log.d(TAG,"handleLoginSuccess:"+ accessToken);

		MyPreferenceManager.setAuthToken(mContext, accessToken);

//		Crashlytics.setUserIdentifier(accessToken);
//		Crashlytics.setUserEmail(userName);
//		Crashlytics.setUserName(userName);
//		MyPreferenceManager.setUserName(mContext, userName);
	}

}

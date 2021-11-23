package com.taiwanmobile.volunteers.v1.api;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;
import com.j256.ormlite.misc.TransactionManager;
import com.taiwanmobile.volunteers.v1.util.WebServiceParams;
import com.taiwanmobile.volunteers.v1.util.WebServiceUtil;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v1.loginview.FubonLoginFragment;
import com.taiwanmobile.volunteers.v2.utils.AsyncTaskWithDialogs;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class FubonAuthTask extends AsyncTaskWithDialogs<Void, Void>
implements  OnDismissListener{
	public static final String TAG = "FubonAuthTask";

	public static final String RESPONSE_JSON_KEY_TOKEN = "token";
	public static final String RESPONSE_JSON_KEY_ERRORS = "errors";
	public static final String RESPONSE_JSON_KEY_ACCESS_CODE = "accessCode";
	public static final String RESPONSE_JSON_KEY_ERR = "error";
	public static final String RESPONSE_JSON_KEY_ACCESS_TOKEN = "accessToken";

	public static final String ERROR_JSON_NOT_REGISTER = "userNotRegister";
	public static final String ERROR_JSON_USER_NOT_FOUND = "userNotFound";

	Context mContext;
//	RegisterFragment parentFragment;

	String username;
	String password;


	public FubonAuthTask(Context c, String name,
                         String pwd) {
		super(c);

		Log.d(TAG, "create FubonAuthTask");
		mContext = c;
		username = name;
		password = pwd;

		setProgressDialog(DialogFactory.createBusyDialog(c, "登入中"));
		setSuccessDialog(DialogFactory.createDummyDialog(c, this));
		setFailDialog(DialogFactory.createDummyDialog(c, this));
	}

	private Request buildFubonAuthRequest(){

		MediaType mediaType = MediaType.parse(WebServiceParams.CommonValues.Headers_Content_Type_APPLICATION_JSON);


		String url = BackendContract.V1_AUTH_FUBON;
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
				WebServiceParams.CommonValues.Headers_Content_Type_APPLICATION_JSON);

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
			sendResult(FubonLoginFragment.MSG_AUTH_FAILED);
		}
		return null;
	}


	@Override
	public Boolean doInBackground(Void... arg0) {

		Log.d(TAG, "doInBackground");

		Request fubonAuthReq = buildFubonAuthRequest();
		Response fubonAuthResp = executeRequest(fubonAuthReq);
		return handleResponse(fubonAuthResp);

	}

	public boolean handleResponse(Response response){

		if(response == null){
			return false;
		}

		try {
			ResponseBody body = response.body();
			String responseString = body.string();
			Log.d(TAG, responseString);
			int code = response.code();
			Log.d(TAG, "responseCode = " + code);
			if (code == 200) {
				return handleAuthResult(responseString);
			}else{
				sendResult(FubonLoginFragment.MSG_AUTH_FAILED);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			sendResult(FubonLoginFragment.MSG_AUTH_FAILED);
		}
		return false;
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
						sendResult(FubonLoginFragment.MSG_ACCOUNT_NOT_REGISTER, accessCode);
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

				boolean isBind = false;
				if(tokenObj.has("bind")){
					isBind = tokenObj.getBoolean("bind");
				}

				Log.d(TAG, "accessToken = " + accessToken + ", isBind = " + isBind);
				handleLoginSuccess(accessToken);
				sendResult(FubonLoginFragment.MSG_AUTH_SUCCESS, isBind);
				return true;
			}


		} catch (JSONException e) {
			e.printStackTrace();
			sendResult(FubonLoginFragment.MSG_AUTH_FAILED);
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
		if (!MyPreferenceManager.hasCredentials(mContext)) {
//			TextView tv = (TextView) fragment.getView().findViewById(
//					R.id.fragment_login_editText_username);
//			TextView tv2 = (TextView) fragment.getView().findViewById(
//					R.id.fragment_login_editText_password);
//
//			tv.setText("");
//			tv2.setText("");
//			tv.setHint("帳號或密碼錯誤, 再次輸入或按忘記密碼");
//			tv.requestFocus();
//			tv.setHintTextColor(0xFFFF8C37);
//			fragment.getView()
//					.findViewById(R.id.fragment_login_btn_register)
//					.setVisibility(View.GONE);
//			fragment.getView()
//					.findViewById(R.id.fragment_login_btn_forgetpassword)
//					.setVisibility(View.VISIBLE);
		} else {
//			if (needRestart) {
				MyPreferenceManager.clearInitialzed(mContext);
				MainActivity.MyDbHelper.close();
				MainActivity.mMainActivity.isExit = true;

				AppCompatActivity activity = (AppCompatActivity) mContext;
				activity.finish();
			    activity.startActivity(activity.getIntent());
//			}
		}
	}


	public void handleLoginSuccess(String accessToken){

		Log.d(TAG,"handleLoginSuccess:"+ accessToken);

		MyPreferenceManager.setAuthToken(mContext, accessToken);
//		Crashlytics.setUserIdentifier(accessToken);
//		Crashlytics.setUserEmail(username);
//		Crashlytics.setUserName(username);
//		MyPreferenceManager.setUserName(mContext, username);
	}

	Handler mCallback;

	public void setCallback(Handler callback){
		this.mCallback = callback;
	}

	public void sendResult(int msg){
		if(mCallback != null){
			mCallback.sendMessage(mCallback.obtainMessage(msg));
		}
	}

	public void sendResult(int msg, Object object){
		if(mCallback != null){
			Message message = mCallback.obtainMessage(msg);
			message.obj = object;
			mCallback.sendMessage(message);
		}
	}
}

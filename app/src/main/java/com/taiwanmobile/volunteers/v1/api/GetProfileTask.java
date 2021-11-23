package com.taiwanmobile.volunteers.v1.api;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.j256.ormlite.misc.TransactionManager;
import com.taiwanmobile.volunteers.v1.loginview.FubonLoginFragment;
import com.taiwanmobile.volunteers.v1.util.WebServiceParams;
import com.taiwanmobile.volunteers.v1.util.WebServiceUtil;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.utils.AsyncTaskWithDialogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class GetProfileTask extends BaseRequestTask{
	public static final String TAG = "GetProfileTask";

	public static final String RESPONSE_JSON_KEY_ERRORS = "errors";

	String accessToken;


	public GetProfileTask(Context c, String accessToken) {
		super(c);
		Log.d(TAG, "create GetProfileTask:  accessToken = " + accessToken);
		this.accessToken = accessToken;

//		setProgressDialog(DialogFactory.createBusyDialog(c, "登入中"));
//		setSuccessDialog(DialogFactory.createDummyDialog(c, this));
//		setFailDialog(DialogFactory.createDummyDialog(c, this));
	}


	@Override
	public Boolean doInBackground(Void... arg0) {

		Log.d(TAG, "doInBackground");

		Request request = buildGetProfileRequest(accessToken);
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
			if (mStatusCode == 200) {
				return handleProfileResult(responseString);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		return false;
	}

	public boolean handleProfileResult(String responseString){
		try {
			JSONObject jsonObject = new JSONObject(responseString);

			if(jsonObject.has(RESPONSE_JSON_KEY_ERRORS)){//Error handling

//				{"error":"invalidToken","description":"Token was either missing or invalid."}

				JSONArray errors = jsonObject.getJSONArray(RESPONSE_JSON_KEY_ERRORS);

				//Print error log
				Log.d(TAG, "errors = " + errors);

				if(errors == null || errors.length() == 0){
					return false;
				}

			}else{
				updateUserProfile(jsonObject);
				sendResult(FubonLoginFragment.MSG_GET_PROFILE_SUCCESS);
				return true;
			}


		} catch (JSONException e) {
			e.printStackTrace();
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


//	    {
//		    "id":8448,
//			"username":"rebecca.lai@fubon.com",
//			"uid":"ee4ad7f93bf74e8b902ab30df26e4473",
//			"aboutMe":"",
//			"skillsDescription":"",
//			"interest":"",
//			"npoId":601,
//			"score":0,
//			"ranking":0,
//			"eventNum":0,
//			"eventEnterpriseHour":0,
//			"eventGeneralHour":0,
//			"icon":"http://isharing.fihcloud.com/resources/default_userprofile_image.png",
//			"isStaff":false,
//			"isNpo":false,
//			"isFubon":true,
//			"isTwm":false
//	    }

	public void updateUserProfile(final JSONObject userObj){

		Log.d(TAG,"updateUserProfile:"+ userObj);

		try {
			String userName = userObj.getString("username");
			Crashlytics.setUserEmail(userName);
		    Crashlytics.setUserName(userName);
			MyPreferenceManager.setUserName(mContext, userName);

			Boolean isFubon = userObj.getBoolean("isFubon");
			Boolean isTwm = userObj.getBoolean("isTwm");
			MyPreferenceManager.setIsFubon(mContext, isFubon);
			MyPreferenceManager.setIsTwm(mContext, isTwm);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		try {
			TransactionManager.callInTransaction(
					MainActivity.MyDbHelper.getConnectionSource(),
					new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							new UserAccountDAO(userObj, true).save(TAG);
							return null;
						}
					});
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// FragHelper.showActionBar(getActivity());
	}


	private Request buildGetProfileRequest(String accessToken){

		String url = BackendContract.V1_USER_PROFILE;
		Log.d(TAG, "set url = " + url);

		Request.Builder requestBuilder = new Request.Builder().url(url).get();

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

}

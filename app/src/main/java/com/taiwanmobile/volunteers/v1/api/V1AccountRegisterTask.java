package com.taiwanmobile.volunteers.v1.api;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;
import com.j256.ormlite.misc.TransactionManager;
import com.taiwanmobile.volunteers.v1.loginview.FubonLoginFragment;
import com.taiwanmobile.volunteers.v1.loginview.V1RegisterFragment;
import com.taiwanmobile.volunteers.v1.util.WebServiceParams;
import com.taiwanmobile.volunteers.v1.util.WebServiceUtil;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.loginview.LoginFragment;
import com.taiwanmobile.volunteers.v2.loginview.RegisterFragment;
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

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class V1AccountRegisterTask extends AsyncTaskWithDialogs<Void, Void>
		implements OnDismissListener {
	private final String TAG = getClass().getSimpleName();

	Context mContext;

	String username;
	String password1;
	String password2;
	boolean ifSuccess = false;

	String accessCode;

	private String twmAccount;

	public int returnStatusCode = 0;

	public static final String RESPONSE_JSON_KEY_TOKEN = "token";
	public static final String RESPONSE_JSON_KEY_ACCOUNT = "account";
	public static final String RESPONSE_JSON_KEY_ERRORS = "errors";
	public static final String RESPONSE_JSON_KEY_ACCESS_CODE = "accessCode";
	public static final String RESPONSE_JSON_KEY_ERR = "error";
	public static final String RESPONSE_JSON_KEY_ACCESS_TOKEN = "accessToken";

	public static final String ERROR_JSON_NOT_REGISTER = "userNotRegister";
	public static final String ERROR_JSON_USER_NOT_FOUND = "userNotFound";

	public V1AccountRegisterTask(Context c, EditText name, String accessCode,
                                 EditText pwd1, EditText pwd2) {
		super(c);
		this.mContext = c;
		this.username = name.getText().toString();
		this.password1 = pwd1.getText().toString();
		this.password2 = pwd2.getText().toString();
		this.accessCode = accessCode;

		setProgressDialog(DialogFactory.createBusyDialog(c, "註冊中"));
		setSuccessDialog(DialogFactory.createDummyDialog(c, this));
		setFailDialog(DialogFactory.createDummyDialog(c, this));
	}

	public void setTWMAccount(String account){
		twmAccount = account;
	}


	@Override
	protected Boolean doInBackground(Void... arg0) {

		//debug code
//		String responseString = "[{\"index\":0,\"account\":\"rebecca.lai@fubon.com\",\"id\":8717,\"token\":{\"accessToken\":\"e5e5579ee2c0022022c4053362eb349885f9cbc0\",\"expiresIn\":3600,\"refreshToken\":\"afafb4a53b4547f38c999277881867eb\",\"resetPassword\":false}}]";
//		return parseRegisterResult(responseString);

		try {
			OkHttpClient client = new OkHttpClient();

			Request request = buildRegisterAuthRequest();
			try {
				Response response = client.newCall(request).execute();
				ResponseBody body = response.body();

				String responseString = body.string();
				Log.e(TAG, responseString);
				returnStatusCode = response.code();

				if (returnStatusCode == 200) {
					parseRegisterResult(responseString);

				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean parseRegisterResult(String responseString){
		try {

			JSONArray jsonArray = new JSONArray(responseString);
			JSONObject jsonObject = jsonArray.getJSONObject(0);

			if(jsonObject.has(RESPONSE_JSON_KEY_ERRORS)){//Error handling

				JSONArray errors = jsonObject.getJSONArray(RESPONSE_JSON_KEY_ERRORS);

				//Print error log
				Log.d(TAG, "errors = " + errors);

				if(errors == null || errors.length() == 0){
					return false;
				}

			}else if(jsonObject.has(RESPONSE_JSON_KEY_TOKEN)){
				JSONObject tokenObj = jsonObject.getJSONObject(RESPONSE_JSON_KEY_TOKEN);
				Log.d(TAG, "tokenObj = " + tokenObj);
				String accessToken = tokenObj.getString(RESPONSE_JSON_KEY_ACCESS_TOKEN);


				Log.d(TAG, "accessToken = " + accessToken);
				handleLoginSuccess(accessToken);
				sendResult(V1RegisterFragment.MSG_AUTH_SUCCESS);
				return true;
			}


		} catch (JSONException e) {
			e.printStackTrace();
		}

		return false;
	}



	private Request buildRegisterAuthRequest(){

		MediaType mediaType = MediaType.parse(WebServiceParams.CommonValues.Headers_Content_Type_APPLICATION_JSON);


		String url = BackendContract.V1_USER_REGISTER;
		Log.d(TAG, "set url = " + url);
		Log.d(TAG, "set username = " + username);
		Log.d(TAG, "set password = " + password1);


		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("username", username);
		jsonObject.addProperty("password", password1);
		jsonObject.addProperty("firstName", "");
		jsonObject.addProperty("lastName", "");
		jsonObject.addProperty("email", username);

		if(!TextUtils.isEmpty(accessCode)){
			jsonObject.addProperty("accessCode", accessCode);
		}

		if(!TextUtils.isEmpty(twmAccount)){
			jsonObject.addProperty("twmAccount", username);
		}

		String content = "[" + jsonObject.toString()+ "]";
		Log.d(TAG, "set requestBody = " + content);

		RequestBody requestBody = RequestBody.create(mediaType, content);

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
	public void onDismiss(DialogInterface arg0) {
//		if (returnStatusCode == 201) {
//			MyPreferenceManager.clearInitialzed(mContext);
//			MainActivity.MyDbHelper.close();
//			MainActivity.mMainActivity.isExit = true;
////			parentFragment.getActivity().finish();
////			parentFragment.startActivity(parentFragment.getActivity()
////					.getIntent());
//		} else if (returnStatusCode == 403) {
//			new DialogFactory.TitleMessageDialog(mContext, "電子信箱已存在",
//					"該電子信箱已註冊過，請重新輸入或使用忘記密碼功能").show();
//		} else {
//			new DialogFactory.TitleMessageDialog(mContext, "上傳失敗",
//					"請確認手機上網功能已開啟").show();
//		}
	}

	public void handleLoginSuccess(String accessToken){

		Log.d(TAG,"handleLoginSuccess:"+ accessToken);

		MyPreferenceManager.setAuthToken(mContext, accessToken);

		Crashlytics.setUserIdentifier(accessToken);
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

package com.taiwanmobile.volunteers.v1.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
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

public class TWMBindTask extends BaseRequestTask{
	public static final String TAG = "TWMBindTask";




	String enterpriseSerialNumber;
	String enterpriseSerialEmail;
	String enterpriseSerialDepartment;
	String enterpriseSerialName;
	String enterpriseSerialId;
	String enterpriseSerialPhone;
	String enterpriseSerialType;
	String enterpriseSerialGroup;

	public TWMBindTask(Context c,
					   String enterpriseSerialNumber,
							   String enterpriseSerialEmail,
							   String enterpriseSerialDepartment,
							   String enterpriseSerialName,
							   String enterpriseSerialId,
							   String enterpriseSerialPhone,
							   String enterpriseSerialType,
							   String enterpriseSerialGroup) {
		super(c);

		Log.d(TAG, "create TWMBindTask");

		this.enterpriseSerialNumber = enterpriseSerialNumber;
		this.enterpriseSerialEmail = enterpriseSerialEmail;
		this.enterpriseSerialDepartment = enterpriseSerialDepartment;
		this.enterpriseSerialName = enterpriseSerialName;
		this.enterpriseSerialId = enterpriseSerialId;
		this.enterpriseSerialPhone = enterpriseSerialPhone;
		this.enterpriseSerialType = enterpriseSerialType;
		this.enterpriseSerialGroup = enterpriseSerialGroup;

		setProgressDialog(DialogFactory.createBusyDialog(c, "綁定中"));
		setSuccessDialog(DialogFactory.createDummyDialog(c, this));
		setFailDialog(DialogFactory.createDummyDialog(c, this));
	}

	private Request buildFubonBindRequest(){
		String accessToken = MyPreferenceManager.getAuthToken(mContext);

		MediaType mediaType = MediaType.parse(WebServiceParams.CommonValues.Headers_Content_Type_APPLICATION_JSON);

		Log.d(TAG, "set accessToken = " + accessToken);

		String url = BackendContract.V1_USER_TWM;
		Log.d(TAG, "set url = " + url);
		Log.d(TAG, "set enterpriseSerialNumber = " + enterpriseSerialNumber);
		Log.d(TAG, "set enterpriseSerialEmail = " + enterpriseSerialEmail);
		Log.d(TAG, "set enterpriseSerialDepartment = " + enterpriseSerialDepartment);
		Log.d(TAG, "set enterpriseSerialName = " + enterpriseSerialName);
		Log.d(TAG, "set enterpriseSerialId = " + enterpriseSerialId);
		Log.d(TAG, "set enterpriseSerialPhone = " + enterpriseSerialPhone);
		Log.d(TAG, "set enterpriseSerialType = " + enterpriseSerialType);
		Log.d(TAG, "set enterpriseSerialGroup = " + enterpriseSerialGroup);


		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("enterpriseSerialNumber", enterpriseSerialNumber);
		jsonObject.addProperty("enterpriseSerialEmail", enterpriseSerialEmail);
		jsonObject.addProperty("enterpriseSerialDepartment", enterpriseSerialDepartment);
		jsonObject.addProperty("enterpriseSerialName", enterpriseSerialName);
		jsonObject.addProperty("enterpriseSerialId", enterpriseSerialId);
		jsonObject.addProperty("enterpriseSerialPhone", enterpriseSerialPhone);
		jsonObject.addProperty("enterpriseSerialType", enterpriseSerialType);
		jsonObject.addProperty("enterpriseSerialGroup", enterpriseSerialGroup);

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
			sendResult(FubonLoginFragment.MSG_AUTH_FAILED);
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
				sendResult(TWMBindFragment.MSG_BIND_FAILED, reason);
				return false;
			}

			JSONObject jsonObject =  new JSONObject(responseString);

			if(!jsonObject.has("errors")){
				sendResult(TWMBindFragment.MSG_BIND_SUCCESS);
				return true;
			}else{
				JSONArray errors = jsonObject.getJSONArray("errors");
				JSONObject error = errors.getJSONObject(0);
				mError = error.getString("error");
				sendResult(TWMBindFragment.MSG_BIND_FAILED, mError);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			sendResult(TWMBindFragment.MSG_BIND_FAILED);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage());
			sendResult(TWMBindFragment.MSG_BIND_FAILED);
		}
		return false;
	}

}

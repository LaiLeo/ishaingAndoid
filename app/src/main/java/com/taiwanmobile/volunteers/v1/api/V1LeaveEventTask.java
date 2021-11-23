package com.taiwanmobile.volunteers.v1.api;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.gson.JsonObject;
import com.j256.ormlite.misc.TransactionManager;
import com.taiwanmobile.volunteers.v1.util.WebServiceParams;
import com.taiwanmobile.volunteers.v1.util.WebServiceUtil;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.database.RegisteredEventDAO;
import com.taiwanmobile.volunteers.v2.utils.AsyncTaskWithDialogs;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class V1LeaveEventTask extends AsyncTaskWithDialogs<Void, Void> implements
		OnDismissListener {
	private final String TAG = getClass().getSimpleName();

	Context mContext;
	// private final EventDAO mEventObject;
	String eventUid;
	String url;
	Fragment mEventDetailFragment;

	public int returnStatusCode = 0;
	public String mError;

	long mEventId;

	public V1LeaveEventTask(Context c, Fragment pFrag, String uid, long eventId) {
		super(c);
		mContext = c;
		mEventDetailFragment = pFrag;
		eventUid = uid;
		mEventId = eventId;
		url = BackendContract.V2_EVENT_LEAVE_URL + eventUid + "/";
		setProgressDialog(DialogFactory.createBusyDialog(mContext, "簽退中"));
		setSuccessDialog(DialogFactory.createDummyDialog(mContext, this));
		setFailDialog(DialogFactory.createDummyDialog(mContext, this));
	}


	private Request buildLeaveRequest(){

		String accessToken = MyPreferenceManager.getAuthToken(mContext);

		long userId = MyPreferenceManager.getUserId(mContext);

		String url = BackendContract.V1_EVENT_LEAVE;
		Log.d(TAG, "set url = " + url);

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("uid", eventUid);
		jsonObject.addProperty("userId", userId);

		String jsonStr = "[" + jsonObject.toString() + "]";

		Log.d(TAG, "set requestBody = " + jsonStr);

		MediaType mediaType = MediaType.parse(WebServiceParams.CommonValues.Headers_Content_Type_APPLICATION_JSON);
		RequestBody requestBody = RequestBody.create(mediaType, jsonStr);

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

	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			OkHttpClient client = new OkHttpClient();
			Request request = buildLeaveRequest();

			Response response = client.newCall(request).execute();

			returnStatusCode = response.code();
			ResponseBody body = response.body();

			if (body == null) {
				Log.e(TAG, "server response error");
				return false;
			}

			final String responseString = body.string();;

			Log.d(TAG, "responseString:" + responseString);
			Log.d(TAG, "returnStatusCode:"+returnStatusCode);
			if (returnStatusCode != 200) {
				return false;
			}

			JSONArray jsonArray = new JSONArray(responseString);

			if(jsonArray.length() > 0) {//Error handling

				JSONObject jsonObject = jsonArray.getJSONObject(0);

				if(!jsonObject.has("errors")){
					TransactionManager.callInTransaction(
							MainActivity.MyDbHelper.getConnectionSource(),
							new Callable<Void>() {
								@Override
								public Void call() throws Exception {

									RegisteredEventDAO dao = new RegisteredEventDAO(eventUid, mEventId, true, false);
									dao.save();
									return null;
								}
							});
					return true;
				}else{
					JSONArray errors = jsonObject.getJSONArray("errors");
					JSONObject error = errors.getJSONObject(0);
					mError = error.getString("error");
				}

			}

			return false;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		return false;
	}

	@Override
	public void onDismiss(DialogInterface arg0) {
		if (returnStatusCode == 200) {
//			new DialogFactory.TitleMessageDialog(mContext, "簽退成功",
//					"恭喜您，完成此次志工活動，請給予活動評分和留言").show();

			DialogFactory.TitleMessageButtonSmallDialog dialog = new DialogFactory.TitleMessageButtonSmallDialog(mContext, "簽退成功",
					"恭喜您，完成此次志工活動，請給予活動評分和留言");
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					String url = MyPreferenceManager.getQuestionnaireUrl(mContext);
					openUrl(mContext, url);

					if (mEventDetailFragment != null) {
						FragHelper.removeAndClearBackStack(
								mEventDetailFragment.getActivity(),
								mEventDetailFragment);
					}

				}
			});
			dialog.show();

		} else {
			new DialogFactory.TitleMessageDialog(mContext, "簽退失敗",
					"Error Code:" + returnStatusCode).show();
		}
	}

	public void openUrl(Context context, String url){
		Uri content_url = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, content_url);
		context.startActivity(intent);
	}
}

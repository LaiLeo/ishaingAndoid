package com.taiwanmobile.volunteers.v1.api;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.taiwanmobile.volunteers.BuildConfig;
import com.taiwanmobile.volunteers.v1.util.WebServiceParams;
import com.taiwanmobile.volunteers.v1.util.WebServiceUtil;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.Map;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class V1VersionCheckTask extends BaseRequestTask {

	public static final String TAG = "V1VersionCheckTask";

	public V1VersionCheckTask(Context c) {
		super(c);
	}

	private boolean checkVersionInGooglePlay(){
		try {
			String curVersion = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0).versionName;
			String newVersion = curVersion;
			newVersion = Jsoup
					.connect(
							"https://play.google.com/store/apps/details?id="
									+ mContext.getPackageName() + "&hl=en")
					.timeout(30000)
					.userAgent(
							"Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
					.referrer("http://www.google.com").get()
					.select("div[itemprop=softwareVersion]").first().ownText();
			// Log.e("v", curVersion + ":" + newVersion);
			return (value(curVersion) < value(newVersion)) ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	private boolean checkVersionInServer(){
		try {
			OkHttpClient client = new OkHttpClient();
			Request request = buildRequest();

			Response response = client.newCall(request).execute();

			mStatusCode = response.code();
			Log.e(TAG, "returnStatusCode = "+mStatusCode);

			ResponseBody body = response.body();

			if (body == null) {
				Log.e(TAG, "server response error");
				return false;
			}

			final String responseString = body.string();

			Log.e(TAG, "Response = "+responseString);
			if (mStatusCode != 200) {
//                sendResult(ForgotPasswordFragment.MSG_ACCOUNT_NOT_FOUND);
				return false;
			}


			JSONObject jsonObject = new JSONObject(responseString);

			//Test code
//            JSONObject jsonObject = new JSONObject("{\"count\":1,\"results\":[{\"id\":1,\"iosVersion\":\"5.1.5\",\"androidVersion\":\"5.1.6\",\"forcedUpgrade\":true,\"questionnaireUrl\":\"https://www.google.com\"}]}");

			if(jsonObject.has("results")){

				JSONArray results = jsonObject.getJSONArray("results");

				if(results != null && results.length() > 0){
					JSONObject result = results.getJSONObject(0);
					Log.e(TAG, "result = "+result);

					String androidVer = result.getString("androidVersion");
					Boolean forcedUpgrade = result.getBoolean("forcedUpgrade");
					String questionnaireUrl = result.getString("questionnaireUrl");

					MyPreferenceManager.setQuestionnaireUrl(mContext, questionnaireUrl);


					String localVersion = BuildConfig.VERSION_NAME;

					if(androidVer.compareTo(localVersion) > 0 && forcedUpgrade){
						return true;
					}

				}
			}

			return false;


		} catch (Exception e) {
			Log.e("Error", e.getMessage());
//            sendResult(ForgotPasswordFragment.MSG_ACCOUNT_NOT_FOUND);
		}
		return false;
	}

	private Request buildRequest(){

		String url = BackendContract.V1_APP_CONFIGS;
		Log.d(TAG, "set url = " + url);

		Request.Builder requestBuilder = new Request.Builder()
				.url(url).get();

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
	public Boolean doInBackground(Void... params) {
		return checkVersionInGooglePlay() || checkVersionInServer();
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		if (result) {

			final String packageName = mContext.getPackageName();

			AlertDialog.Builder b = new AlertDialog.Builder(mContext)
					.setTitle("有更新程式 ")
					.setMessage("微樂志工已釋出最新版本囉，為提供您更好的服務，請點選下方「取得最新版本」，謝謝。")
					.setPositiveButton("取得新版本 ",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
													int which) {
									Intent intent = new Intent(
											Intent.ACTION_VIEW,
											Uri.parse("market://details?id="
													+ packageName));

									if(mContext != null){
										mContext.startActivity(intent);
									}
								}
							})
					.setCancelable(false);
			AlertDialog dialog = b.create();
			dialog.setCanceledOnTouchOutside(false);
			dialog.setOnShowListener(DialogFactory.TEXT_SIZE_ADJUSTER);
			dialog.show();
		}
	}

	private long value(String string) {
		string = string.trim();
		if (string.contains(".")) {
			final int index = string.lastIndexOf(".");
			return value(string.substring(0, index)) * 100
					+ value(string.substring(index + 1));
		} else {
			Long value = (long) 0;
			try {
				value = Long.valueOf(string);
			} catch (NumberFormatException ex) {
				Log.e("version check", "version data fetch error");
			}
			return value;
		}
	}
}

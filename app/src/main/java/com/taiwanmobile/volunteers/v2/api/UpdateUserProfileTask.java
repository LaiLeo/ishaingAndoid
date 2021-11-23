package com.taiwanmobile.volunteers.v2.api;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.app.Fragment;
import android.util.Log;

import com.j256.ormlite.misc.TransactionManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.taiwanmobile.volunteers.v1.util.StaticResUtil;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;
import com.taiwanmobile.volunteers.v2.utils.roundedimageview.RoundedTransformationBuilder;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UpdateUserProfileTask extends AsyncTask<Void, Void, Boolean> {
	private final String TAG = getClass().getSimpleName();
	Context mContext;
	MultipartBody reqEntity;
	Fragment parentFragment;

	Integer returnStatusCode;
	String responseString;

	public UpdateUserProfileTask(Context c, Fragment frag,
			MultipartBody entity) throws UnsupportedEncodingException {
		mContext = c;
		reqEntity = entity;
		parentFragment = frag;
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		if (!MyPreferenceManager.hasCredentials(mContext)) {
			return false;
		}
		try {

			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
					.url(BackendContract.V2_USERPROFILE_UPDATE_URL)
					.post(reqEntity)
					.addHeader("Authorization", "Token " + MyPreferenceManager.getAuthToken(mContext))
					.build();

			Response response = client.newCall(request).execute();

			returnStatusCode = response.code();
			ResponseBody body = response.body();
			responseString = body.string();

			Log.d(TAG, "response: " + responseString);
			if (returnStatusCode == 200) {
				TransactionManager.callInTransaction(
						MainActivity.MyDbHelper.getConnectionSource(),
						new Callable<Void>() {
							@Override
							public Void call() throws Exception {
								new UserAccountDAO(new JSONObject(
										responseString)).save(TAG);
								return null;
							}
						});
				return true;
			}
		} catch (Exception e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (result) {
			// refresh actionbar icon
			try {
				String imageFilePath = UserAccountDAO.queryObjectByMe(mContext).icon;
				if (StringUtils.isNotBlank(imageFilePath)) {
					int image_size = (int) (40 * mContext.getResources()
							.getDisplayMetrics().density);
					Transformation transformation = new RoundedTransformationBuilder()
							.oval(true).build();

					Picasso.with(mContext)
							//.load(BackendContract.DEPLOYMENT_URL
									//+ imageFilePath)
							.load(StaticResUtil.checkUrl(imageFilePath, false, TAG))
							.resize(image_size, image_size).centerCrop()
							.transform(transformation)
							.into(MainActivity.userIcon);
				}
			} catch (Exception ex) {
				Log.e(TAG, Log.getStackTraceString(ex));
			}
			FragHelper.removeAndClearBackStack(mContext, parentFragment);
		} else if (returnStatusCode == 400) {
			try {
				JSONObject responseObject = new JSONObject(responseString);
				Integer code = responseObject.getInt("code");
				if (code == 401) {
					new DialogFactory.TitleMessageDialog(mContext, "更新失敗",
							"您輸入的舊密碼錯誤").show();
				}
			} catch (Exception e) {
				new DialogFactory.TitleMessageDialog(mContext, "更新失敗", "請與客服聯繫")
						.show();
			}
		} else if (returnStatusCode == 401) {
			MyPreferenceManager.ForceLogout((Activity) mContext);
		} else {
			new DialogFactory.TitleMessageDialog(mContext, "更新失敗",
					"請確認手機上網功能已開啟").show();
		}
	}
}

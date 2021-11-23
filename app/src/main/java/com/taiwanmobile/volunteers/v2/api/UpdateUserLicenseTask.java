package com.taiwanmobile.volunteers.v2.api;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.util.Log;

import com.j256.ormlite.misc.TransactionManager;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.database.UserLicenseImageDAO;
import com.taiwanmobile.volunteers.v2.userprofileview.UserProfileEditFragment;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UpdateUserLicenseTask extends AsyncTask<Void, Void, Boolean> {
	private final String TAG = getClass().getSimpleName();
	Context mContext;
	MultipartBody reqEntity;
	UserProfileEditFragment parentFragment;

	Integer returnStatusCode;
	String responseString;
	UserAccountDAO mUserAccountDAO;
	public UpdateUserLicenseTask(Context c, UserProfileEditFragment frag,
								 MultipartBody entity, @NonNull UserAccountDAO userAccountDAO) throws UnsupportedEncodingException {
		mContext = c;
		reqEntity = entity;
		parentFragment = frag;
		mUserAccountDAO = userAccountDAO;
	}

	@Override
	protected Boolean doInBackground(final Void... arg0) {
		if (!MyPreferenceManager.hasCredentials(mContext)) {
			return false;
		}
		try {

			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
					.url(BackendContract.V2_USER_LICENSE_UPDATE_URL)
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
								JSONArray array = new JSONArray(responseString);

								List<UserLicenseImageDAO> imageList = new ArrayList<>();
								for(int i=0; i<array.length();i++){
									UserLicenseImageDAO image = new UserLicenseImageDAO(array.getJSONObject(i), mUserAccountDAO);
									image.save();
									imageList.add(image);
								}
								mUserAccountDAO.images = imageList;
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
			parentFragment.onUploadLicenseComplete();

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

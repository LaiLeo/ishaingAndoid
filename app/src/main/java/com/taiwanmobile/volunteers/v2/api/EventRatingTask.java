package com.taiwanmobile.volunteers.v2.api;

import java.util.concurrent.Callable;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.app.Fragment;
import android.util.Log;

import com.j256.ormlite.misc.TransactionManager;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.database.RegisteredEventDAO;
import com.taiwanmobile.volunteers.v2.utils.AsyncTaskWithDialogs;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;
import com.taiwanmobile.volunteers.v2.utils.ViewHelper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class EventRatingTask extends AsyncTaskWithDialogs<Void, Void> implements
		OnDismissListener {
	private final String TAG = getClass().getSimpleName();

	Context mContext;
	long mEventId;
	String url;
	Float eventScore;
	Fragment parentFragment;

	public int returnStatusCode = 0;

	public EventRatingTask(Context c, Fragment mFrag, long EventId, float score) {
		super(c);
		mContext = c;
		mEventId = EventId;
		eventScore = score;
		parentFragment = mFrag;
		url = BackendContract.setV2EventRatingURL(EventId, score);
		setProgressDialog(DialogFactory.createBusyDialog(mContext, "更新評分中"));
		setSuccessDialog(DialogFactory.createDummyDialog(mContext, this));
		setFailDialog(DialogFactory.createDummyDialog(mContext, this));
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
					.url(url)
					.addHeader("Authorization", "Token " + MyPreferenceManager.getAuthToken(mContext))
					.build();


			Response response = client.newCall(request).execute();

			returnStatusCode = response.code();
			ResponseBody body = response.body();

			if (body == null) {
				Log.e(TAG, "server response error");
				return false;
			}

			final String responseString = body.string();

			if (returnStatusCode != 200) {
				Log.e(TAG, responseString);
				return false;
			}
			TransactionManager.callInTransaction(
					MainActivity.MyDbHelper.getConnectionSource(),
					new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							new RegisteredEventDAO(new JSONObject(
									responseString)).save();
							return null;
						}
					});
			return true;
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
		}
		return false;
	}

	@Override
	public void onDismiss(DialogInterface arg0) {
		if (returnStatusCode == 401) {
			MyPreferenceManager.ForceLogout((Activity) mContext);
		} else if (returnStatusCode != 200) {
			new DialogFactory.TitleMessageDialog(mContext, "評分失敗",
					"請確認手機上網功能已開啟").show();
		} else {
			ViewHelper.refreshEventTabAdapters(mContext);
			if (parentFragment != null) {
				FragHelper.removeAndClearBackStack(
						parentFragment.getActivity(), parentFragment);
			}
		}
	}
}

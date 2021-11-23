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
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.database.RegisteredEventDAO;
import com.taiwanmobile.volunteers.v2.utils.AsyncTaskWithDialogs;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;
import com.taiwanmobile.volunteers.v2.utils.ViewHelper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UnRegisterEventTask extends AsyncTaskWithDialogs<Void, Void>
		implements OnDismissListener {
	private final String TAG = getClass().getSimpleName();

	Context mContext;
	Fragment parentFragment;
	long mEventId;
	String url;
	String title;
	String message;

	public int returnStatusCode = 0;

	public UnRegisterEventTask(Context c, Fragment mFrag, long eventId, String title, String message) {
		super(c);
		mContext = c;
		parentFragment = mFrag;
		mEventId = eventId;
		this.title = title;
		this.message = message;
		url = BackendContract.V2_EVENT_UNREGISTER_URL + mEventId + "/";
		setProgressDialog(DialogFactory.createBusyDialog(mContext, "取消報名中"));
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
							JSONObject obj = new JSONObject(responseString);
							new EventDAO(obj).save();
							RegisteredEventDAO.deleteByEventId(mEventId);
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
		if (returnStatusCode == 200) {

			new DialogFactory.TitleMessageDialog(mContext, title , message)
					.show();
			ViewHelper.refreshEventTabAdapters(mContext);
			FragHelper.removeAndClearBackStack(parentFragment.getActivity(),
					parentFragment);
		} else if (returnStatusCode == 401) {
			MyPreferenceManager.ForceLogout((Activity) mContext);
		} else {
			new DialogFactory.TitleMessageDialog(mContext, "取消報名失敗",
					"請確認手機上網功能已開啟").show();
		}
	}
}

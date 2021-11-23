package com.taiwanmobile.volunteers.v2.api;

import java.util.concurrent.Callable;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.Log;
import android.widget.Button;

import com.j256.ormlite.misc.TransactionManager;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.database.NpoDAO;
import com.taiwanmobile.volunteers.v2.database.SubscribedNpoDAO;
import com.taiwanmobile.volunteers.v2.utils.AsyncTaskWithDialogs;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.ViewHelper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UnSubscribeNPOTask extends AsyncTaskWithDialogs<Void, Void>
		implements OnDismissListener {
	private final String TAG = getClass().getSimpleName();

	Context mContext;
	long mNpoId;
	String url;
	Button mButton;

	public int returnStatusCode = 0;

	public UnSubscribeNPOTask(Context c, long id, Button b) {
		super(c);
		mContext = c;
		mNpoId = id;
		mButton = b;
		url = BackendContract.V2_NPO_UNSUBSCRIBE_URL + mNpoId + "/";
		setProgressDialog(DialogFactory.createBusyDialog(mContext, "取消訂閱中"));
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
							new NpoDAO(new JSONObject(responseString)).save();
							SubscribedNpoDAO.deleteByNpoId(mNpoId);
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
			ViewHelper.refreshEventTabAdapters(mContext);
			mButton.setBackgroundResource(R.drawable.ic_nonsubscribed);
		} else if (returnStatusCode == 401) {
			MyPreferenceManager.ForceLogout((Activity) mContext);
		} else {
			new DialogFactory.TitleMessageDialog(mContext, "取消訂閱失敗",
					"請確認手機上網功能已開啟").show();
		}
	}
}

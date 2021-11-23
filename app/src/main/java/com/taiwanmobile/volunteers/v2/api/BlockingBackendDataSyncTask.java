package com.taiwanmobile.volunteers.v2.api;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.location.Location;
import android.util.Log;

import com.j256.ormlite.misc.TransactionManager;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.database.DonationNpoDAO;
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.database.FocusedEventDAO;
import com.taiwanmobile.volunteers.v2.database.NpoDAO;
import com.taiwanmobile.volunteers.v2.database.RegisteredEventDAO;
import com.taiwanmobile.volunteers.v2.database.SubscribedNpoDAO;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.utils.AsyncTaskWithDialogs;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.ViewHelper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class BlockingBackendDataSyncTask extends
		AsyncTaskWithDialogs<Void, Void> implements OnDismissListener {

	private final String TAG = getClass().getSimpleName();

	private final int TIME_OUT = 60000;

	private static final String RESPONSE_JSON_KEY_USER = "user";
	private static final String RESPONSE_JSON_KEY_NPO = "npo";
	private static final String RESPONSE_JSON_KEY_EVENT = "event";
	private static final String RESPONSE_JSON_KEY_DONATION_NPO = "donation_npo";
	private static final String RESPONSE_JSON_KEY_SUBSCRIBED_NPO = "subscribe_npo";
	private static final String RESPONSE_JSON_KEY_REGISTERED_EVENT = "registered_event";
	private static final String RESPONSE_JSON_KEY_FOCUSED_EVENT = "focused_event";

	private final Context mContext;
	private int returnStatusCode = 0;

	public BlockingBackendDataSyncTask(Context c) {
		super(c);
		mContext = c;

		setProgressDialog(DialogFactory.createBusyDialog(mContext, "初始資料中"));
		setSuccessDialog(DialogFactory.createDummyDialog(mContext, this));
		setFailDialog(DialogFactory.createDummyDialog(mContext, this));
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			OkHttpClient client = new OkHttpClient.Builder().readTimeout(TIME_OUT, TimeUnit.MILLISECONDS).build();
			Request.Builder requestBuilder = new Request.Builder()
					.url(BackendContract.V2_DATA_DUMP_URL);
			if (MyPreferenceManager.hasCredentials(mContext)) {
				requestBuilder.addHeader("Authorization", "Token "
						+ MyPreferenceManager.getAuthToken(mContext));
			}

			Request request = requestBuilder.build();

			Response response = client.newCall(request).execute();
			returnStatusCode = response.code();
			ResponseBody body = response.body();

			if (returnStatusCode != 200) {
				Log.e(TAG, "server response is not 200");
				return false;
			}
			if (body == null) {
				Log.e(TAG, "server response error");
				return false;
			}
			String responseContent = body.string();
			updateData(new JSONObject(responseContent));
			return true;
		} catch (Exception ex) {
			Log.e(TAG, Log.getStackTraceString(ex));
			return false;
		}
	}

	@Override
	public void onDismiss(DialogInterface arg0) {
		if (returnStatusCode == 200) {
			MyPreferenceManager.setInitialzed(mContext);
			ViewHelper.refreshEventTabAdapters(mContext);
		} else {
			new DialogFactory.TitleMessageDialog(mContext, "資料初始失敗",
					"請確認手機上網功能已開啟").show();
		}
	}

	public void updateData(final JSONObject dataObj) {
		try {
			MyPreferenceManager.clearUserData();

			TransactionManager.callInTransaction(
					MainActivity.MyDbHelper.getConnectionSource(),
					new Callable<Void>() {
						@Override
						public Void call() throws Exception {
							if (MyPreferenceManager.hasCredentials(mContext)) {
								JSONObject userObj = dataObj
										.getJSONObject(RESPONSE_JSON_KEY_USER);
//								new UserAccountDAO(userObj).save();

								JSONArray subscribedArray = dataObj
										.getJSONArray(RESPONSE_JSON_KEY_SUBSCRIBED_NPO);
								for (int loop = 0; loop < subscribedArray
										.length(); loop++) {
									JSONObject obj = subscribedArray
											.getJSONObject(loop);
									new SubscribedNpoDAO(obj).save();
								}

								JSONArray registeredArray = dataObj
										.getJSONArray(RESPONSE_JSON_KEY_REGISTERED_EVENT);
								for (int loop = 0; loop < registeredArray
										.length(); loop++) {
									JSONObject obj = registeredArray
											.getJSONObject(loop);
									new RegisteredEventDAO(obj).save();
								}

								JSONArray focusedArray = dataObj
										.getJSONArray(RESPONSE_JSON_KEY_FOCUSED_EVENT);
								for (int loop = 0; loop < focusedArray.length(); loop++) {
									JSONObject obj = focusedArray
											.getJSONObject(loop);
									new FocusedEventDAO(obj).save();
								}
							}

							JSONArray donationNpoArray = dataObj
									.getJSONArray(RESPONSE_JSON_KEY_DONATION_NPO);
							for (int loop = 0; loop < donationNpoArray.length(); loop++) {
								JSONObject obj = donationNpoArray
										.getJSONObject(loop);
								new DonationNpoDAO(obj).save();
							}

							JSONArray npoArray = dataObj
									.getJSONArray(RESPONSE_JSON_KEY_NPO);
							for (int loop = 0; loop < npoArray.length(); loop++) {
								JSONObject obj = npoArray.getJSONObject(loop);
								new NpoDAO(obj).save();
							}

							JSONArray eventArray = dataObj
									.getJSONArray(RESPONSE_JSON_KEY_EVENT);
							for (int loop = 0; loop < eventArray.length(); loop++) {
								JSONObject obj = eventArray.getJSONObject(loop);
								EventDAO dao = new EventDAO(obj);
								if (MainActivity.currentLocation != null) {
									Location eventLlocation = new Location(
											"event");
									eventLlocation.setLatitude(dao.latitude);
									eventLlocation.setLongitude(dao.longtitude);
									dao.distance = (double) (eventLlocation
											.distanceTo(MainActivity.currentLocation) / 1000);
								}
								dao.save();
							}

							return null;
						}
					});
		} catch (Exception e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}
}

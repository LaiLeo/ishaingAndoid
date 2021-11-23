package com.taiwanmobile.volunteers.v2.api;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.misc.TransactionManager;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.VolunteerFragment;
import com.taiwanmobile.volunteers.v2.database.DonationNpoDAO;
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.database.FocusedEventDAO;
import com.taiwanmobile.volunteers.v2.database.NpoDAO;
import com.taiwanmobile.volunteers.v2.database.RegisteredEventDAO;
import com.taiwanmobile.volunteers.v2.database.SubscribedNpoDAO;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.ViewHelper;

public class BackendDataSyncTask extends AsyncTask<Void, Void, Boolean> {

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
	private final PullToRefreshLayout mPullToRefreshLayout;
	private final Boolean isSuccess = false;

	public BackendDataSyncTask(Context c, PullToRefreshLayout ptrl) {
		mContext = c;
		mPullToRefreshLayout = ptrl;
	}

    @Override
    protected void onPreExecute(){
        MainActivity.mViewPager.setSwipeable(false);
        VolunteerFragment.mVolunteerViewPager.setSwipeable(false);
    }

	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d(TAG, "start data sync");
		MainActivity.isDataDownloading = true;
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
			Log.d(TAG, "start data sync fin");

			int returnStatusCode = response.code();
			ResponseBody body = response.body();

			if (returnStatusCode != 200) {
				Log.e(TAG, "server response is not 200, got " + returnStatusCode);
				if(body != null) Log.w(TAG, "response: " + body.string());
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
		} finally {
			MainActivity.isDataDownloading = false;
		}
	}

	@Override
	protected void onPostExecute(final Boolean result) {
        MainActivity.mViewPager.setSwipeable(true);
        VolunteerFragment.mVolunteerViewPager.setSwipeable(true);
		Activity act = (Activity) mContext;
		act.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mPullToRefreshLayout.setRefreshComplete();
				if (result) {
					ViewHelper.refreshEventTabAdapters(mContext);
				} else {
					new DialogFactory.TitleMessageDialog(mContext, "資料更新失敗",
							"請確認手機上網功能已開啟").show();
				}
			}
		});
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

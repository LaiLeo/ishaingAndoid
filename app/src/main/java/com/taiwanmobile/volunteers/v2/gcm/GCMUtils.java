package com.taiwanmobile.volunteers.v2.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class GCMUtils {

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	/**
	 * Tag used on log messages.
	 */
	public static final String TAG = GCMUtils.class.getSimpleName();

	public static final String EXTRA_MESSAGE_KEY = "NotificationMessage";
	public static final String EXTRA_PAGE_TYPE_KEY = "PageType";

    public static final String PAGE_TYPE_GENERAL_VOLUNTEER_LIST = "general_volunteer_list";
    public static final String PAGE_TYPE_GENERAL_VOLUNTEER_DETAIL = "general_volunteer_detail";
    public static final String PAGE_TYPE_ENTERPRISE_VOLUNTEER_LIST = "enterprise_volunteer_list";
    public static final String PAGE_TYPE_ENTERPRISE_VOLUNTEER_DETAIL = "enterprise_volunteer_detail";
    public static final String PAGE_TYPE_ITEM_LIST = "item_list";
    public static final String PAGE_TYPE_ITEM_DETAIL = "item_detail";
    public static final String PAGE_TYPE_5180_LIST = "5180_list";
    public static final String PAGE_TYPE_5180_DETAIL = "5180_detail";

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private static SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static boolean checkPlayServices(Activity activity) {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(activity);
		if (resultCode != ConnectionResult.SUCCESS) {
			// if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
			// GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
			// PLAY_SERVICES_RESOLUTION_REQUEST).show();
			// } else {
			// Log.e(TAG, "This device is not supported.");
			// // finish();
			// }
			Log.e(TAG, "This device is not supported.");
			return false;
		}
		return true;
	}
}

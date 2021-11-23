package com.taiwanmobile.volunteers.v2;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.j256.ormlite.table.TableUtils;
import com.taiwanmobile.volunteers.v2.database.DonationNpoDAO;
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.database.EventResultImageDAO;
import com.taiwanmobile.volunteers.v2.database.FocusedEventDAO;
import com.taiwanmobile.volunteers.v2.database.NpoDAO;
import com.taiwanmobile.volunteers.v2.database.RegisteredEventDAO;
import com.taiwanmobile.volunteers.v2.database.ReplyDAO;
import com.taiwanmobile.volunteers.v2.database.SkillGroupDAO;
import com.taiwanmobile.volunteers.v2.database.SubscribedNpoDAO;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.database.UserLicenseImageDAO;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;

public class MyPreferenceManager {
	public static final String TAG = MyPreferenceManager.class.getSimpleName();

	public static final String KEY_USERNAME = "pref_username";
	public static final String KEY_USER_ID = "pref_user_id";
	public static final String KEY_TOKEN = "pref_token";
	public static final String KEY_DEVICE_ID = "pref_device_id";
	public static final String KEY_APP_VERSION = "pref_app_version";

	public static final String KEY_TIP_SWITCH = "pref_tip";
	public static final String KEY_TIP_TIME = "pref_tip_time";
	public static final String KEY_APP_INITIALIZED = "pref_app_initialized";


	public static final String KEY_IS_FUBON = "pref_is_fubon";
	public static final String KEY_IS_TWM = "pref_is_twm";
	public static final String KEY_QUESTIONNAIRE_URL = "pref_questionnaire_url";




	public static boolean hasInitialzed(Context c) {

		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);

		return StringUtils.isNotEmpty(app_preferences.getString(
				KEY_APP_INITIALIZED, ""));
	}

	public static boolean setInitialzed(Context c) {

		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);

		return app_preferences.edit().putString(KEY_APP_INITIALIZED, "yes")
				.commit();
	}

	public static boolean clearInitialzed(Context c) {

		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);

		return app_preferences.edit().putString(KEY_APP_INITIALIZED, "")
				.commit();
	}

	public static boolean hasCredentials(Context c) {

		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);

		return StringUtils.isNotEmpty(app_preferences.getString(KEY_USERNAME,
				""))
				&& StringUtils.isNotEmpty(app_preferences.getString(KEY_TOKEN,
						""));
	}

	public static String getAuthToken(Context c) {
		if (!hasCredentials(c)) {
			return "";
		} else {
			SharedPreferences app_preferences = PreferenceManager
					.getDefaultSharedPreferences(c);
			return app_preferences.getString(KEY_TOKEN, "");
		}
	}

	public static String getAuthAccessToken(Context c){
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		return app_preferences.getString(KEY_TOKEN, "");
	}

	public static boolean setAuthToken(Context c, String token) {
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);

		MainActivity.hasLogined = true;

		return app_preferences.edit().putString(KEY_TOKEN, token).commit();
	}

	public static String getUserName(Context c) {
		if (!hasCredentials(c)) {
			return "";
		} else {
			SharedPreferences app_preferences = PreferenceManager
					.getDefaultSharedPreferences(c);
			return app_preferences.getString(KEY_USERNAME, "");
		}
	}
	public static Boolean isFubon(Context c) {
		if (!hasCredentials(c)) {
			return false;
		} else {
			SharedPreferences app_preferences = PreferenceManager
					.getDefaultSharedPreferences(c);
			return app_preferences.getBoolean(KEY_IS_FUBON, false);
		}
	}
	public static Boolean isTwm(Context c) {
		if (!hasCredentials(c)) {
			return false;
		} else {
			SharedPreferences app_preferences = PreferenceManager
					.getDefaultSharedPreferences(c);
			return app_preferences.getBoolean(KEY_IS_TWM, false);
		}
	}

	public static boolean setUserName(Context c, String name) {
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		return app_preferences.edit().putString(KEY_USERNAME, name).commit();
	}

	public static boolean setIsFubon(Context c, boolean value) {
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		return app_preferences.edit().putBoolean(KEY_IS_FUBON, value).commit();
	}
	public static boolean setIsTwm(Context c, boolean value) {
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		return app_preferences.edit().putBoolean(KEY_IS_TWM, value).commit();
	}


	public static boolean setQuestionnaireUrl(Context c, String url) {
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		return app_preferences.edit().putString(KEY_QUESTIONNAIRE_URL, url).commit();
	}

	public static String getQuestionnaireUrl(Context c) {
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		return app_preferences.getString(KEY_QUESTIONNAIRE_URL, "");
	}

	@Deprecated
	public static String getUserPassword(Context c) {
		return "";
	}

	public static long getUserId(Context c) {
		if (!hasCredentials(c)) {
			return -1;
		} else {
			try {

				String userName = getUserName(c);
				Log.e(TAG, "getUserName: "+ userName);
				Long id = UserAccountDAO.queryIdByName(userName);
				Log.e(TAG, "queryIdByName: "+ id);

				List<UserAccountDAO> list = MainActivity.MyDbHelper.getUserAccountDAO().queryForAll();
				Log.e(TAG, "UserAccountDAO queryForAll: "+ list);

				if (id == null) {
					if (!TextUtils.isEmpty(userName) && list != null && list.size() > 0) {
						UserAccountDAO userAccountDAO = list.get(0);
						if (userName.equals(userAccountDAO.email)) {
							id = userAccountDAO.id;
						}
					}
				}

				if (id == null){
					return -1;
				} else {
					return id;
				}
			} catch (SQLException e) {
				Log.e(TAG, "no useraccount data");
				return -1;
			}
		}
	}

	public static boolean isTipOn(Context c) {

		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);

		if (StringUtils.isNotEmpty(app_preferences
				.getString(KEY_TIP_SWITCH, ""))) {
			return true;
		}
		return false;
	}

	public static int getTipTime(Context c) {

		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);

		String time = app_preferences.getString(KEY_TIP_TIME, "");
		if (StringUtils.isEmpty(time)) {
			return 24;
		} else {
			return Integer.valueOf(time);
		}

	}

	public static boolean setDeviceId(Context c, String id) {
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);
		int appVersion = getAppVersion(c);
		SharedPreferences.Editor editor = app_preferences.edit();
		editor.putString(KEY_DEVICE_ID, id);
		editor.putInt(KEY_APP_VERSION, appVersion);
		return editor.commit();
	}

	public static String getDeviceId(Context c) {
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);

		String registrationId = app_preferences.getString(KEY_DEVICE_ID, "");
		if (registrationId.isEmpty()) {
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = app_preferences.getInt(KEY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(c);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public static boolean clearCredentials(Context c) {
		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);

		MainActivity.hasLogined = false;
		// ((Activity) c).invalidateOptionsMenu();

		return app_preferences.edit().clear().commit();
	}

	public static void logout(Activity ctx) {

		clearCredentials(ctx);
		try {
			clearUserData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		MainActivity.MyDbHelper.close();
		// Log.e(TAG, "db closed");
		ctx.finish();
		ctx.startActivity(ctx.getIntent());
	}

	public static void ForceLogout(final Activity ctx) {
		AlertDialog.Builder b = new AlertDialog.Builder(ctx)
				.setMessage("您已在其他裝置登入，請重新登入")
				.setPositiveButton(android.R.string.ok, null)
				.setCancelable(false);
		AlertDialog ret = b.create();
		ret.setOnShowListener(DialogFactory.TEXT_SIZE_ADJUSTER);
		ret.show();
	}

	public static void clearUserData() throws SQLException {
//		TableUtils.clearTable(MainActivity.MyDbHelper.getConnectionSource(),
//				UserAccountDAO.class);
		TableUtils.clearTable(MainActivity.MyDbHelper.getConnectionSource(),
				NpoDAO.class);
		TableUtils.clearTable(MainActivity.MyDbHelper.getConnectionSource(),
				EventDAO.class);
		TableUtils.clearTable(MainActivity.MyDbHelper.getConnectionSource(),
				SubscribedNpoDAO.class);
		TableUtils.clearTable(MainActivity.MyDbHelper.getConnectionSource(),
				RegisteredEventDAO.class);
		TableUtils.clearTable(MainActivity.MyDbHelper.getConnectionSource(),
				FocusedEventDAO.class);
		TableUtils.clearTable(MainActivity.MyDbHelper.getConnectionSource(),
				SkillGroupDAO.class);
		TableUtils.clearTable(MainActivity.MyDbHelper.getConnectionSource(),
				ReplyDAO.class);
		TableUtils.clearTable(MainActivity.MyDbHelper.getConnectionSource(),
				EventResultImageDAO.class);
		TableUtils.clearTable(MainActivity.MyDbHelper.getConnectionSource(),
				DonationNpoDAO.class);
		TableUtils.clearTable(MainActivity.MyDbHelper.getConnectionSource(),
				UserLicenseImageDAO.class);
	}
}

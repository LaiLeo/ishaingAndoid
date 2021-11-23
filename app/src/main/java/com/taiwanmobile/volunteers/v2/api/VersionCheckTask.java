//package com.taiwanmobile.volunteers.v2.api;
//
//import org.jsoup.Jsoup;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.taiwanmobile.volunteers.R;
//import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
//
//public class VersionCheckTask extends AsyncTask<Void, Void, Boolean> {
//
//	Context mContext;
//
//	public VersionCheckTask(Context c) {
//		mContext = c;
//	}
//
//	@Override
//	protected Boolean doInBackground(Void... params) {
//		try {
//			String curVersion = mContext.getPackageManager().getPackageInfo(
//					mContext.getPackageName(), 0).versionName;
//			String newVersion = curVersion;
//			newVersion = Jsoup
//					.connect(
//							"https://play.google.com/store/apps/details?id="
//									+ mContext.getPackageName() + "&hl=en")
//					.timeout(30000)
//					.userAgent(
//							"Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
//					.referrer("http://www.google.com").get()
//					.select("div[itemprop=softwareVersion]").first().ownText();
//			// Log.e("v", curVersion + ":" + newVersion);
//			return (value(curVersion) < value(newVersion)) ? true : false;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	@Override
//	protected void onPostExecute(Boolean result) {
//		super.onPostExecute(result);
//		if (result) {
//			AlertDialog.Builder b = new AlertDialog.Builder(mContext)
//					.setMessage(
//							mContext.getResources().getString(
//									R.string.app_update))
//					.setPositiveButton(android.R.string.ok,
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									Intent intent = new Intent(
//											Intent.ACTION_VIEW,
//											Uri.parse("market://details?id="
//													+ mContext.getPackageName()));
//									mContext.startActivity(intent);
//								}
//							})
//					.setNegativeButton(
//							mContext.getResources().getString(
//									R.string.app_update_negative), null)
//					.setCancelable(true);
//			AlertDialog ret = b.create();
//			ret.setOnShowListener(DialogFactory.TEXT_SIZE_ADJUSTER);
//			ret.show();
//		}
//	}
//
//	private long value(String string) {
//		string = string.trim();
//		if (string.contains(".")) {
//			final int index = string.lastIndexOf(".");
//			return value(string.substring(0, index)) * 100
//					+ value(string.substring(index + 1));
//		} else {
//			Long value = (long) 0;
//			try {
//				value = Long.valueOf(string);
//			} catch (NumberFormatException ex) {
//				Log.e("version check", "version data fetch error");
//			}
//			return value;
//		}
//	}
//}

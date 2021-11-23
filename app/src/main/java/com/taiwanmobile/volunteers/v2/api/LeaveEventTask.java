//package com.taiwanmobile.volunteers.v2.api;
//
//import java.util.concurrent.Callable;
//
//import org.json.JSONObject;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.DialogInterface.OnDismissListener;
//import android.app.Fragment;
//import android.util.Log;
//
//import com.j256.ormlite.misc.TransactionManager;
//import com.taiwanmobile.volunteers.v2.BackendContract;
//import com.taiwanmobile.volunteers.v2.MainActivity;
//import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
//import com.taiwanmobile.volunteers.v2.database.RegisteredEventDAO;
//import com.taiwanmobile.volunteers.v2.utils.AsyncTaskWithDialogs;
//import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
//import com.taiwanmobile.volunteers.v2.utils.FragHelper;
//
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//import okhttp3.ResponseBody;
//
//public class LeaveEventTask extends AsyncTaskWithDialogs<Void, Void> implements
//		OnDismissListener {
//	private final String TAG = getClass().getSimpleName();
//
//	Context mContext;
//	// private final EventDAO mEventObject;
//	String eventUid;
//	String url;
//	Fragment mEventDetailFragment;
//
//	public int returnStatusCode = 0;
//
//	public LeaveEventTask(Context c, Fragment pFrag, String uid) {
//		super(c);
//		mContext = c;
//		mEventDetailFragment = pFrag;
//		eventUid = uid;
//		url = BackendContract.V2_EVENT_LEAVE_URL + eventUid + "/";
//		setProgressDialog(DialogFactory.createBusyDialog(mContext, "簽退中"));
//		setSuccessDialog(DialogFactory.createDummyDialog(mContext, this));
//		setFailDialog(DialogFactory.createDummyDialog(mContext, this));
//	}
//
//	@Override
//	protected Boolean doInBackground(Void... arg0) {
//		try {
//			OkHttpClient client = new OkHttpClient();
//			Request request = new Request.Builder()
//					.url(url)
//					.addHeader("Authorization", "Token " + MyPreferenceManager.getAuthToken(mContext))
//					.build();
//
//			Response response = client.newCall(request).execute();
//
//			returnStatusCode = response.code();
//			ResponseBody body = response.body();
//
//			if (body == null) {
//				Log.e(TAG, "server response error");
//				return false;
//			}
//			final String responseString = body.string();
//
//			if (returnStatusCode != 200) {
//				Log.e(TAG, responseString);
//				return false;
//			}
//			TransactionManager.callInTransaction(
//					MainActivity.MyDbHelper.getConnectionSource(),
//					new Callable<Void>() {
//						@Override
//						public Void call() throws Exception {
//							new RegisteredEventDAO(new JSONObject(
//									responseString)).save();
//							return null;
//						}
//					});
//			return true;
//		} catch (Exception e) {
//			Log.e("Error", e.getMessage());
//		}
//		return false;
//	}
//
//	@Override
//	public void onDismiss(DialogInterface arg0) {
//		if (returnStatusCode == 200) {
//			new DialogFactory.TitleMessageDialog(mContext, "簽退成功",
//					"恭喜您，完成此次志工活動，請給予活動評分和留言").show();
//			if (mEventDetailFragment != null) {
//				FragHelper.removeAndClearBackStack(
//						mEventDetailFragment.getActivity(),
//						mEventDetailFragment);
//			}
//		} else if (returnStatusCode == 404) {
//			new DialogFactory.TitleMessageDialog(mContext, "簽退失敗",
//					"您掃描的QR code似乎不是活動簽退條碼喔~").show();
//		} else if (returnStatusCode == 401) {
//			MyPreferenceManager.ForceLogout((Activity) mContext);
//		} else {
//			new DialogFactory.TitleMessageDialog(mContext, "簽退失敗",
//					"請確認手機上網功能已開啟").show();
//		}
//	}
//}

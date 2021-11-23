package com.taiwanmobile.volunteers.v2.api;

import java.util.List;
import java.util.concurrent.Callable;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.app.Fragment;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.j256.ormlite.misc.TransactionManager;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.ItemEventRegisterFragment2;//FIH-modify for new 物資報名頁面
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.database.FocusedEventDAO;
import com.taiwanmobile.volunteers.v2.database.RegisteredEventDAO;
import com.taiwanmobile.volunteers.v2.utils.AsyncTaskWithDialogs;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;
import com.taiwanmobile.volunteers.v2.utils.ViewHelper;

import kotlin.Pair;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RegisterEventTask extends AsyncTaskWithDialogs<Void, Void>
		implements OnDismissListener {
	final String TAG = getClass().getSimpleName();

	private static String RESPONSE_JSON_KEY_REGISTERED_EVENT = "register_event";
	private static String RESPONSE_JSON_KEY_FOCUSED_EVENT = "focus_event";

	Context mContext;
	List<Pair<String, String>> postPairs;
	Fragment parentFragment;

	public int returnStatusCode = 0;

	public RegisterEventTask(Context c, Fragment mFrag,
			List<Pair<String, String>> pairs) {
		super(c);
		mContext = c;
		parentFragment = mFrag;
		postPairs = pairs;
		setProgressDialog(DialogFactory.createBusyDialog(mContext, "報名中"));
		setSuccessDialog(DialogFactory.createDummyDialog(mContext, this));
		setFailDialog(DialogFactory.createDummyDialog(mContext, this));
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			OkHttpClient client = new OkHttpClient();
			FormBody.Builder formBuilder = new FormBody.Builder();
			for(Pair<String, String> pair: postPairs) {
				formBuilder.add(pair.getFirst(), pair.getSecond());
			}

			Request.Builder requestBuilder = new Request.Builder()
					.url(BackendContract.V2_EVENT_REGISTER_URL)
					.post(formBuilder.build())
					.addHeader("Authorization", "Token " + MyPreferenceManager.getAuthToken(mContext));

			Response response = client.newCall(requestBuilder.build()).execute();
			ResponseBody body = response.body();

			returnStatusCode = response.code();

			final String responseString = body.string();
			if (returnStatusCode == 200) {
				TransactionManager.callInTransaction(
						MainActivity.MyDbHelper.getConnectionSource(),
						new Callable<Void>() {
							@Override
							public Void call() throws Exception {
								JSONObject obj = new JSONObject(responseString);
								new RegisteredEventDAO(
										obj.getJSONObject(RESPONSE_JSON_KEY_REGISTERED_EVENT))
										.save();
								new FocusedEventDAO(
										obj.getJSONObject(RESPONSE_JSON_KEY_FOCUSED_EVENT))
										.save();
								return null;
							}
						});
				return true;
			}
			Log.e(TAG, responseString);
			return false;

		} catch (Exception e) {
			Log.e("Error", Log.getStackTraceString(e));
		}
		return false;
	}

	@Override
	public void onDismiss(DialogInterface arg0) {
		if (returnStatusCode == 200) {
			new EventRegisterSuccessDialog(mContext).show();

			ViewHelper.refreshEventTabAdapters(mContext);

			if(parentFragment instanceof ItemEventRegisterFragment2) {//FIH-modify for new 物資報名頁面
				((ItemEventRegisterFragment2) parentFragment).nextPage();//FIH-modify for new 物資報名頁面
			}

			FragHelper.removeAndClearBackStack(parentFragment.getActivity(),
					parentFragment);


		} else if (returnStatusCode == 405) {
			new DialogFactory.TitleMessageDialog(mContext, "該組別已滿",
					"請參加其他組別，謝謝您!").show();
		} else if (returnStatusCode == 401) {
			MyPreferenceManager.ForceLogout((Activity) mContext);
		} else {

			new DialogFactory.TitleMessageDialog(mContext, "報名失敗",
					"請確認手機上網功能已開啟").show();
			Log.e(TAG,"gengqiang->onDismiss()->returnStatusCode= "+returnStatusCode);
		}

	}

	public static class EventRegisterSuccessDialog extends Dialog {
		public EventRegisterSuccessDialog(Context context) {
			super(context, R.style.message_dialog);
			setContentView(R.layout.dialog_with_title);

			TextView tv = (TextView) this
					.findViewById(R.id.title_dialog_tv_title);
//			tv.setText("");
			tv = (TextView) this.findViewById(R.id.title_dialog_tv_content);
			// 建立一個ImageSpan元件並帶入要插入的圖片
			ImageSpan mImageSpan = new ImageSpan(context,
					R.drawable.ic_people_dialog, ImageSpan.ALIGN_BOTTOM);

			// 建立一個SpannableString元件並帶入要顯示的文字字串
			SpannableString mSpannableString = new SpannableString(
					"麻煩請至報名信箱查看「通知信」了解相關資訊");

			// 插入mImageSpan圖片，並指定在字串裡的第8個位置到9個位置進行插入
//			mSpannableString.setSpan(mImageSpan, 8, 9, 0);

			// 將組合後的文字圖片放入TextView裡
			tv.setText(mSpannableString);
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent event) {
			this.dismiss();
			return false;
		}
	}
}

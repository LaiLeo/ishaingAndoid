package com.taiwanmobile.volunteers.v2.api;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;


import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.EventResultFragment;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.database.ReplyDAO;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.ViewUtils;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ReplyPostTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "ReplyPostTask";
	Context mContext;
	MultipartBody reqEntity;
	JSONObject jsonObject = null;
	EventDAO mEventObject;
	EventResultFragment mEventResultFragment;
	int returnStatusCode;

	public ReplyPostTask(Context c, EventResultFragment frag, EventDAO event,
			String message, String imageFilePath)
			throws UnsupportedEncodingException {
		mContext = c;
		mEventObject = event;
		mEventResultFragment = frag;

		MultipartBody.Builder builder = new MultipartBody.Builder()
				.setType(MultipartBody.FORM);

		builder.addFormDataPart(ReplyDAO.POST_KEY_REPORT_ID, String.valueOf(mEventObject.id));
		builder.addFormDataPart(ReplyDAO.POST_KEY_MESSAGE, String.valueOf(message));

		File imageFile = new File(imageFilePath);
		Uri uris = Uri.fromFile(imageFile);
		String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uris.toString());
		String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());

		builder.addFormDataPart(
				ReplyDAO.POST_KEY_ICON,
				ReplyDAO.POST_KEY_ICON,
				RequestBody.create(imageFile, MediaType.parse(mime))
		);

		reqEntity = builder.build();

	}

	public ReplyPostTask(Context c, EventResultFragment frag, EventDAO event,
			String message) throws UnsupportedEncodingException {
		mContext = c;
		mEventResultFragment = frag;
		mEventObject = event;

		MultipartBody.Builder builder = new MultipartBody.Builder()
				.setType(MultipartBody.FORM);

		builder.addFormDataPart(ReplyDAO.POST_KEY_REPORT_ID, String.valueOf(mEventObject.id));
		builder.addFormDataPart(ReplyDAO.POST_KEY_MESSAGE, String.valueOf(message));

		reqEntity = builder.build();
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		if (!MyPreferenceManager.hasCredentials(mContext)) {
			return false;
		}
		try {

			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
					.url(BackendContract.V2_REPLY_URL)
					.post(reqEntity)
					.addHeader("Authorization", "Token " + MyPreferenceManager.getAuthToken(mContext))
					.build();

			Response response = client.newCall(request).execute();
			ResponseBody body = response.body();

			returnStatusCode = response.code();
			if (returnStatusCode == 201) {
				String responseString = body.string();
				jsonObject = new JSONObject(responseString);
				return true;
			}
		} catch (Exception e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (!result) {
			if (returnStatusCode == 401) {
				MyPreferenceManager.ForceLogout((Activity) mContext);
			} else {
				new DialogFactory.TitleMessageDialog(mContext, "留言失敗",
						"請確認手機上網功能已開啟").show();
			}
		} else {
			try {
				ReplyDAO reply = new ReplyDAO(jsonObject, mEventObject);
				reply.save();
				mEventObject.replyNum += 1;
				mEventObject.save();

				TextView tv = (TextView) mEventResultFragment.getView()
						.findViewById(R.id.fragment_event_result_tv_reply_num);
				tv.setText(mEventObject.replyNum + "人回覆。");
				mEventResultFragment.adapter.changeCursor(ReplyDAO
						.queryByEventId(mEventObject.id));
				mEventResultFragment.adapter.notifyDataSetChanged();
				ViewUtils.setListViewSize(mEventResultFragment.replyListView);
			} catch (Exception e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
	}
}

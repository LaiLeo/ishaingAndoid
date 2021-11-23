package com.taiwanmobile.volunteers.v2.api;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.Log;
import android.widget.EditText;

import com.j256.ormlite.misc.TransactionManager;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.loginview.LoginFragment;
import com.taiwanmobile.volunteers.v2.loginview.RegisterFragment;
import com.taiwanmobile.volunteers.v2.utils.AsyncTaskWithDialogs;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AccountRegisterTask extends AsyncTaskWithDialogs<Void, Void>
		implements OnDismissListener {
	private final String TAG = getClass().getSimpleName();

	Context mContext;
	RegisterFragment parentFragment;

	String username;
	String password1;
	String password2;
	boolean ifSuccess = false;

	public int returnStatusCode = 0;

	public AccountRegisterTask(Context c, RegisterFragment frag, EditText name,
			EditText pwd1, EditText pwd2) {
		super(c);
		mContext = c;
		parentFragment = frag;
		username = name.getText().toString();
		password1 = pwd1.getText().toString();
		password2 = pwd2.getText().toString();

		setProgressDialog(DialogFactory.createBusyDialog(c, "註冊中"));
		setSuccessDialog(DialogFactory.createDummyDialog(c, this));
		setFailDialog(DialogFactory.createDummyDialog(c, this));
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {

		try {
			OkHttpClient client = new OkHttpClient();
			FormBody.Builder formBuilder = new FormBody.Builder()
					.add("username", username)
					.add("password", password1)
					.add("password2", password2);

			Request.Builder requestBuilder = new Request.Builder()
					.url(BackendContract.V2_ACCOUNT_REGISTER_URL)
					.post(formBuilder.build());

			try {
				Response response = client.newCall(requestBuilder.build()).execute();
				ResponseBody body = response.body();

				String responseString = body.string();
				Log.e(TAG, responseString);
				returnStatusCode = response.code();
				if (returnStatusCode == 201) {
					final JSONObject jsonObject = new JSONObject(responseString);
					MyPreferenceManager
							.setAuthToken(
									mContext,
									jsonObject
											.getString(LoginFragment.LoginTask.RESPONSE_JSON_KEY_TOKEN));

					MyPreferenceManager.setUserName(mContext, username);
					TransactionManager.callInTransaction(
							MainActivity.MyDbHelper.getConnectionSource(),
							new Callable<Void>() {
								@Override
								public Void call() throws Exception {
									JSONObject userObj = jsonObject
											.getJSONObject(LoginFragment.LoginTask.RESPONSE_JSON_KEY_USER);
									new UserAccountDAO(userObj).save(TAG);
									return null;
								}
							});
					return true;
				}
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void onDismiss(DialogInterface arg0) {
		if (returnStatusCode == 201) {
			MyPreferenceManager.clearInitialzed(mContext);
			MainActivity.MyDbHelper.close();
			MainActivity.mMainActivity.isExit = true;
			parentFragment.getActivity().finish();
			parentFragment.startActivity(parentFragment.getActivity()
					.getIntent());
		} else if (returnStatusCode == 403) {
			new DialogFactory.TitleMessageDialog(mContext, "電子信箱已存在",
					"該電子信箱已註冊過，請重新輸入或使用忘記密碼功能").show();
		} else {
			new DialogFactory.TitleMessageDialog(mContext, "上傳失敗",
					"請確認手機上網功能已開啟").show();
		}
	}
}

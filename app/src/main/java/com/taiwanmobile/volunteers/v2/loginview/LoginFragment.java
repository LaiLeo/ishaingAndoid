package com.taiwanmobile.volunteers.v2.loginview;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Paint;
import android.os.Bundle;
import android.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.j256.ormlite.misc.TransactionManager;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.loginview.FubonLoginFragment;
import com.taiwanmobile.volunteers.v1.loginview.TWMLoginFragment;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.SupportFragment;
import com.taiwanmobile.volunteers.v2.api.DeviceIdPostTask;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.utils.AsyncTaskWithDialogs;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;

import kotlin.Pair;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginFragment extends SupportFragment implements OnClickListener {
	private final String TAG = getClass().getSimpleName();

	private Boolean needRestart;

	public EditText usernameEditText;
	public EditText passwordEditText;




	public LoginFragment() {
		needRestart = false;
	}

	public LoginFragment restartOnLogined() {
		needRestart = true;
		return this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        hideActionBarTab();

		View head = getActivity().getLayoutInflater().inflate(
				R.layout.fragment_login, null);

		head.findViewById(R.id.fragment_login_btn_ok).setOnClickListener(this);

		TextView registerTV = head.findViewById(R.id.fragment_login_btn_register);
		registerTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		registerTV.getPaint().setAntiAlias(true);
		registerTV.setOnClickListener(this);

		TextView forgetPWDTV = head.findViewById(R.id.fragment_login_btn_forgetpassword);
		forgetPWDTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		forgetPWDTV.getPaint().setAntiAlias(true);
		forgetPWDTV.setOnClickListener(this);


		head.findViewById(R.id.fragment_login_btn_fubon)
				.setOnClickListener(this);
		head.findViewById(R.id.fragment_login_btn_twm)
				.setOnClickListener(this);

		usernameEditText = (EditText) head
				.findViewById(R.id.fragment_login_editText_username);
		passwordEditText = (EditText) head
				.findViewById(R.id.fragment_login_editText_password);
		usernameEditText
				.setText(MyPreferenceManager.getUserName(getActivity()));

		// TODO: test
		// usernameEditText.setText("b");

		// passwordTextView.setText("b");

		return head;
	}

	@Override
	public void onClick(View arg0) {
		View parent = arg0.getRootView();
		String userName = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		switch (arg0.getId()) {
		case R.id.fragment_login_btn_ok:
			// check if the user input username & password is correct
			boolean shouldBreak = false;
			if (StringUtils.isBlank(userName)) {
				usernameEditText.setHintTextColor(0xFFFF8C37);
				usernameEditText.setHint("請輸入電子信箱");
				shouldBreak = true;
			}
			if (StringUtils.isBlank(password)) {
				passwordEditText.setHint("請輸入密碼");
				passwordEditText.setHintTextColor(0xFFFF8C37);
				shouldBreak = true;
			}
			if (shouldBreak) {
				return;
			}
			new LoginTask(getActivity(), this, userName, password).execute();
			return;
		case R.id.fragment_login_btn_fubon:
			FragHelper.replace(this, new FubonLoginFragment());
			return;
		case R.id.fragment_login_btn_twm:
			TWMLoginFragment twmLoginFragment = new TWMLoginFragment();
			twmLoginFragment.setRequestTWMLoginPage(true);
			FragHelper.replace(this, twmLoginFragment);
			return;
		case R.id.fragment_login_btn_forgetpassword:

			// user click '忘記密碼'
			FragHelper.replace(this, new ForgotPasswordFragment());

			return;

		case R.id.fragment_login_btn_register:
            RegisterFragment fragment = new RegisterFragment();
            fragment.setUserName(usernameEditText.getText().toString());
            fragment.setPassword(passwordEditText.getText().toString());
			FragHelper.replace(this, fragment);
			return;
		default:
			Log.e(TAG, "unknow click");
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
        showActionBarTab();
	}

	public class LoginTask extends AsyncTaskWithDialogs<Void, Void> implements
			OnDismissListener {
		private final String TAG = getClass().getSimpleName();

		public static final String POST_KEY_LOGIN_USERNAME = "username";
		public static final String POST_KEY_LOGIN_PASSWORD = "password";

		public static final String RESPONSE_JSON_KEY_TOKEN = "token";
		public static final String RESPONSE_JSON_KEY_USER = "user";

		private final Context mContext;
		private final Fragment fragment;
		private final String username;
		private final List<Pair<String, String>> pairs;

		public LoginTask(Context c, Fragment frag, String name, String passwd) {
			super(c);
			mContext = c;
			fragment = frag;
			username = name;

			pairs = new ArrayList<>();
			pairs.add(new Pair<>(POST_KEY_LOGIN_USERNAME, name));
			pairs.add(new Pair<>(POST_KEY_LOGIN_PASSWORD, passwd));

			if (StringUtils.isNotEmpty(MyPreferenceManager.getDeviceId(c))) {
				pairs.add(new Pair<>(DeviceIdPostTask.DEVICE_UUID,
						MyPreferenceManager.getDeviceId(c)));
				pairs.add(new Pair<>(DeviceIdPostTask.DEVICE_NAME,
						DeviceIdPostTask.DEVICE_TYPE));
			}
			setProgressDialog(DialogFactory.createBusyDialog(mContext, "登入中"));
			setSuccessDialog(DialogFactory.createDummyDialog(mContext, this));
			setFailDialog(DialogFactory.createDummyDialog(mContext, this));
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				OkHttpClient client = new OkHttpClient();
				FormBody.Builder formBuilder = new FormBody.Builder();
				for(Pair<String, String> pair: pairs) {
					formBuilder.add(pair.getFirst(), pair.getSecond());
				}
				Request request = new Request.Builder()
						.url(BackendContract.V2_LOGIN_URL)
						.post(formBuilder.build())
						.build();

				Response response = client.newCall(request).execute();
				int statusCode = response.code();
				ResponseBody body = response.body();


				if (statusCode == 200) {
					final JSONObject jsonObject = new JSONObject(body.string());
					MyPreferenceManager.setAuthToken(mContext,
							jsonObject.getString(RESPONSE_JSON_KEY_TOKEN));

					Crashlytics.setUserIdentifier(jsonObject.getString(RESPONSE_JSON_KEY_TOKEN));
					Crashlytics.setUserEmail(username);
					Crashlytics.setUserName(username);
					MyPreferenceManager.setUserName(mContext, username);
					TransactionManager.callInTransaction(
							MainActivity.MyDbHelper.getConnectionSource(),
							new Callable<Void>() {
								@Override
								public Void call() throws Exception {
									JSONObject userObj = jsonObject
											.getJSONObject(RESPONSE_JSON_KEY_USER);
									new UserAccountDAO(userObj).save(TAG);
									return null;
								}
							});

					// FragHelper.showActionBar(getActivity());
					return true;
				}
			} catch (Exception e) {
				Log.e("Error", Log.getStackTraceString(e));
			}
			return false;
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			if (!MyPreferenceManager.hasCredentials(mContext)) {
				TextView tv = (TextView) fragment.getView().findViewById(
						R.id.fragment_login_editText_username);
				TextView tv2 = (TextView) fragment.getView().findViewById(
						R.id.fragment_login_editText_password);

				tv.setText("");
				tv2.setText("");
				tv.setHint("帳號或密碼錯誤, 再次輸入或按忘記密碼");
				tv.requestFocus();
				tv.setHintTextColor(0xFFFF8C37);
				fragment.getView()
						.findViewById(R.id.fragment_login_btn_register)
						.setVisibility(View.GONE);
				fragment.getView()
						.findViewById(R.id.fragment_login_btn_forgetpassword)
						.setVisibility(View.VISIBLE);
			} else {
				if (needRestart) {
					MyPreferenceManager.clearInitialzed(mContext);
					MainActivity.MyDbHelper.close();
					MainActivity.mMainActivity.isExit = true;
					getActivity().finish();
					getActivity().startActivity(getActivity().getIntent());
				}
			}
		}
	}

    private void hideActionBarTab(){
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar!= null) {
            actionBar.setNavigationMode(
                    ActionBar.NAVIGATION_MODE_STANDARD);
        }
    }
    private void showActionBarTab(){
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if(actionBar!= null) {
            actionBar.setNavigationMode(
                    ActionBar.NAVIGATION_MODE_TABS);
        }

    }


}

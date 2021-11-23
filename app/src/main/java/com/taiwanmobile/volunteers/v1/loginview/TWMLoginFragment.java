package com.taiwanmobile.volunteers.v1.loginview;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.Toast;

import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.api.GetProfileTask;
import com.taiwanmobile.volunteers.v1.api.TWMAuthTask;
import com.taiwanmobile.volunteers.v1.util.FihWebViewClient;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.SupportFragment;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class TWMLoginFragment extends SupportFragment implements OnClickListener {
	private final String TAG = getClass().getSimpleName();

	WebView mLoginView;
	FihWebViewClient mFihWebViewClient;


	public static final int MSG_AUTH_FAILED = 0x1231;
	public static final int MSG_ACCOUNT_VALIDATE_ERROR = 0x1232;
	public static final int MSG_ACCOUNT_NOT_REGISTER = 0x1233;
	public static final int MSG_ACCOUNT_NOT_BIND = 0x1234;
	public static final int MSG_AUTH_SUCCESS = 0x1235;
	public static final int MSG_GET_PROFILE_SUCCESS = 0x1236;
	private Handler mAuthCallbackHandler;

	public TWMLoginFragment() {
		mAuthCallbackHandler = new AuthHandler();
	}

	public TWMLoginFragment restartOnLogined() {
		return this;
	}


	boolean needRequestTWMLoginPage = false;


	public void setRequestTWMLoginPage(boolean value){
		needRequestTWMLoginPage = value;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        hideActionBarTab();

		View head = getActivity().getLayoutInflater().inflate(
				R.layout.fragment_login_twm, null);

		mLoginView = head.findViewById(R.id.twmobile_login_view);

		mFihWebViewClient = new FihWebViewClient();

		mFihWebViewClient.setTWMCodeCallback(new FihWebViewClient.TWMCodeCallback() {
			@Override
			public void onTWMCodeReturn(String code) {

				Log.d(TAG, "onTWMCodeReturn:" + code);

				if(!TextUtils.isEmpty(code)){
					TWMAuthTask twmAuthTask = new TWMAuthTask(getActivity(), code);
					twmAuthTask.setCallback(mAuthCallbackHandler);
					twmAuthTask.execute();
				}

			}
		});

		mLoginView.setWebViewClient(mFihWebViewClient);

		loadUrl();
		return head;
	}


	private void loadUrl() {

		if(!needRequestTWMLoginPage){
			Log.d(TAG,"skip loadUrl");
			return;
		}

		Log.d(TAG,"loadUrl");

		clearCookies();

		mLoginView.getSettings().setJavaScriptEnabled(true);
//		mLoginView.clearCache(true);
//		mLoginView.clearFormData();
		mLoginView.loadUrl(BackendContract.TWM_LOGIN_URL);

		needRequestTWMLoginPage = false;
	}


	public void clearCookies(){
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		cookieManager.removeSessionCookie();
		cookieManager.removeAllCookies(null);
		cookieManager.flush();

		WebStorage.getInstance().deleteAllData();
	}

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		default:
			Log.e(TAG, "unknow click");
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
        showActionBarTab();
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



	private class AuthHandler extends Handler {

		@Override
		public void handleMessage(@NonNull Message msg) {

			if(getActivity() == null || getActivity().isFinishing() || isDetached()){
				return;
			}


			switch(msg.what){

				case MSG_ACCOUNT_NOT_BIND:

					break;
				case MSG_ACCOUNT_NOT_REGISTER:{

					Bundle data = msg.getData();

					String accessCode = data.getString(TWMAuthTask.RESPONSE_JSON_KEY_ACCESS_CODE);
					String userName = data.getString(TWMAuthTask.RESPONSE_JSON_KEY_USER_NAME);
					onAccountNotRegistered(accessCode, userName);
					break;}
				case MSG_ACCOUNT_VALIDATE_ERROR:
					onAccountValidateError();
					break;
				case MSG_AUTH_FAILED:
					onFailed();
					break;
				case MSG_AUTH_SUCCESS:{

					Bundle data = msg.getData();

					String accessToken = data.getString(TWMAuthTask.RESPONSE_JSON_KEY_ACCESS_TOKEN);
					String userName = data.getString(TWMAuthTask.RESPONSE_JSON_KEY_USER_NAME);
					getProfile(accessToken);
					break;}
				case MSG_GET_PROFILE_SUCCESS:
					reloadAct();
					break;
				default:
					break;
			}
		}
	}


	public void onFailed() {
		Toast.makeText(getActivity(), "系統忙碌中",Toast.LENGTH_SHORT).show();
	}

	public void onAccountValidateError() {

		DialogFactory.TitleMessageButtonSmallDialog dialog = new DialogFactory.TitleMessageButtonSmallDialog(getActivity(), "密碼錯誤",
				"您輸入的賬號或密碼錯誤，請重新輸入");
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {

				setRequestTWMLoginPage(true);
				loadUrl();
			}
		});
		dialog.show();
	}

	public void onAccountNotRegistered(String accessCode, String userName) {
		V1RegisterFragment fragment = new V1RegisterFragment();
		if(!TextUtils.isEmpty(userName)){
			fragment.setUserName(userName);
			fragment.setNameEditable(false);
		}

		fragment.setAccessCode(accessCode);
//		fragment.setPassword(passwordEditText.getText().toString());
		FragHelper.replace(this, fragment);
	}


	public void getProfile(String accessToken){
		GetProfileTask task = new GetProfileTask(getActivity(), accessToken);
		task.setCallback(mAuthCallbackHandler);
		task.execute();
	}

	public void reloadAct(){
		Toast.makeText(getActivity(), "登入成功",Toast.LENGTH_SHORT).show();
		MyPreferenceManager.clearInitialzed(getActivity());
		MainActivity.MyDbHelper.close();
		MainActivity.mMainActivity.isExit = true;
		getActivity().finish();
		getActivity().startActivity(getActivity().getIntent());
	}

}

package com.taiwanmobile.volunteers.v1.loginview;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.api.FubonAuthTask;
import com.taiwanmobile.volunteers.v1.api.GetProfileTask;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.SupportFragment;
import com.taiwanmobile.volunteers.v2.loginview.LoginFragment;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;

import org.apache.commons.lang3.StringUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class FubonLoginFragment extends SupportFragment implements OnClickListener {
	private final String TAG = getClass().getSimpleName();

	private Boolean needRestart;

	public EditText usernameEditText;
	public EditText passwordEditText;

	private FubonAuthTask mFubonAuthTask;

	public static final int MSG_AUTH_FAILED = 0x1231;
	public static final int MSG_ACCOUNT_VALIDATE_ERROR = 0x1232;
	public static final int MSG_ACCOUNT_NOT_REGISTER = 0x1233;
	public static final int MSG_ACCOUNT_NOT_BIND = 0x1234;
	public static final int MSG_AUTH_SUCCESS = 0x1235;
	public static final int MSG_GET_PROFILE_SUCCESS = 0x1236;
	private Handler mAuthCallbackHandler;



	public FubonLoginFragment() {
		needRestart = false;
		mAuthCallbackHandler = new FubonAuthHandler();
	}

	public FubonLoginFragment restartOnLogined() {
		needRestart = true;
		return this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        hideActionBarTab();

		View head = getActivity().getLayoutInflater().inflate(
				R.layout.fragment_login_fubon, null);

		head.findViewById(R.id.btn_login).setOnClickListener(this);
		head.findViewById(R.id.btn_back).setOnClickListener(
				this);
		TextView registerTV = head.findViewById(R.id.fragment_login_btn_register);
		registerTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		registerTV.getPaint().setAntiAlias(true);
		registerTV.setOnClickListener(this);

		TextView forgetPWDTV = head.findViewById(R.id.fragment_login_btn_forgetpassword);
		forgetPWDTV.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		forgetPWDTV.getPaint().setAntiAlias(true);
		forgetPWDTV.setOnClickListener(this);
		usernameEditText = (EditText) head
				.findViewById(R.id.fragment_login_editText_username);
		passwordEditText = (EditText) head
				.findViewById(R.id.fragment_login_editText_password);
		usernameEditText
				.setText(MyPreferenceManager.getUserName(getActivity()));

		return head;
	}

	@Override
	public void onClick(View arg0) {
		View parent = arg0.getRootView();
		String userName = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		switch (arg0.getId()) {
		case R.id.btn_login:
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
			mFubonAuthTask = new FubonAuthTask(getActivity(), userName, password);
			mFubonAuthTask.setCallback(mAuthCallbackHandler);
			mFubonAuthTask.execute();
			return;
		case R.id.btn_back:
			FragHelper.replace(this, new LoginFragment());
			return;
			case R.id.fragment_login_btn_forgetpassword:

				openUrl("https://volunteer.fuboncharity.org.tw/password");

				return;

			case R.id.fragment_login_btn_register:

				openUrl("https://volunteer.fuboncharity.org.tw/register");

				return;
		default:
			Log.e(TAG, "unknow click");
		}
	}

	public void openUrl(String url){
		Uri content_url = Uri.parse(url);
		//		Intent intent = new Intent();
//		intent.setAction("android.intent.action.VIEW");
//		intent.setData(content_url);

		Intent intent = new Intent(Intent.ACTION_VIEW, content_url);
		startActivity(intent);
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


    private class FubonAuthHandler extends Handler{

		@Override
		public void handleMessage(@NonNull Message msg) {

			if(getActivity() == null || getActivity().isFinishing() || isDetached()){
				return;
			}


			switch(msg.what){

				case MSG_ACCOUNT_NOT_BIND:

					break;
				case MSG_ACCOUNT_NOT_REGISTER:

					String accessCode = (String)msg.obj;
					onAccountNotRegistered(accessCode);
					break;
				case MSG_ACCOUNT_VALIDATE_ERROR:
					onAccountValidateError();
					break;
				case MSG_AUTH_FAILED:
					onFailed();
					break;
				case MSG_AUTH_SUCCESS:
					boolean isBind = (Boolean) msg.obj;
					onAuthSuccess(isBind);
					break;
				case MSG_GET_PROFILE_SUCCESS:
					reloadAct();
					break;
				default:
					break;
			}
		}
	}


	public void onFailed() {
		DialogFactory.createMessageDialog(getActivity(), "登入失敗",
				new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
//						usernameEditText.setText("");
//						passwordEditText.setText("");
					}
				}).show();
	}

	public void onAccountValidateError() {

		DialogFactory.TitleMessageButtonSmallDialog dialog = new DialogFactory.TitleMessageButtonSmallDialog(getActivity(), "密碼錯誤",
				"您輸入的賬號或密碼錯誤，請重新輸入");
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				usernameEditText.setText("");
				passwordEditText.setText("");
			}
		});
		dialog.show();
	}

	public void onAccountNotRegistered(String accessCode) {
		V1RegisterFragment fragment = new V1RegisterFragment();
		fragment.setUserName(usernameEditText.getText().toString());
		fragment.setNameEditable(false);
		fragment.setAccessCode(accessCode);
//		fragment.setPassword(passwordEditText.getText().toString());
		FragHelper.replace(this, fragment);
	}

	public void onAuthSuccess(boolean isBind) {


		if(isBind){
			DialogFactory.TitleMessageButtonSmallDialog dialog = new DialogFactory.TitleMessageButtonSmallDialog(getActivity(), "登入成功",
					"您的賬號已經成功綁定富邦企業賬號！");
			dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
//					reloadAct();
					getProfile();
				}
			});
			dialog.show();
		}else{
//			reloadAct();
			getProfile();
		}

	}

	public void getProfile(){
		String accessToken = MyPreferenceManager.getAuthAccessToken(getActivity());
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

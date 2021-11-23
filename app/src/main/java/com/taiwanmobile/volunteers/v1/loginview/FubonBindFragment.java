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
import com.taiwanmobile.volunteers.v1.api.FubonBindTask;
import com.taiwanmobile.volunteers.v1.api.GetProfileTask;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.SupportFragment;
import com.taiwanmobile.volunteers.v2.loginview.LoginFragment;
import com.taiwanmobile.volunteers.v2.userprofileview.UserProfileFragment;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;

import org.apache.commons.lang3.StringUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class FubonBindFragment extends SupportFragment implements OnClickListener {
	private final String TAG = getClass().getSimpleName();

	private Boolean needRestart;

	public EditText usernameEditText;
	public EditText passwordEditText;

	private FubonBindTask mFubonBindTask;


	public static final int MSG_BIND_SUCCESS = 0x423;
	public static final int MSG_BIND_FAILED = 0x434;
	private Handler mHandler;


	public FubonBindFragment() {
		needRestart = false;
		mHandler = new BindStateHandler();
	}

	public FubonBindFragment restartOnLogined() {
		needRestart = true;
		return this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        hideActionBarTab();

		View head = getActivity().getLayoutInflater().inflate(
				R.layout.fragment_bind_fubon, null);

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
			mFubonBindTask = new FubonBindTask(getActivity(), userName, password);
			mFubonBindTask.setCallback(mHandler);
			mFubonBindTask.execute();
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

	private class BindStateHandler extends Handler{

		@Override
		public void handleMessage(@NonNull Message msg) {

			if(getActivity() == null || getActivity().isFinishing() || isDetached()){
				return;
			}

			switch(msg.what){
				case MSG_BIND_SUCCESS:
					onBindSuccess();
					break;
				case MSG_BIND_FAILED:
					String reason = String.valueOf(msg.obj);
					onBindFailed(reason);
					break;
				case FubonLoginFragment.MSG_GET_PROFILE_SUCCESS:
					FragHelper.replace(getActivity(), UserProfileFragment.newInstance(
							MyPreferenceManager.getUserId(getActivity())));
					break;
				default:
					break;
			}
		}
	}
	public void onBindSuccess(){
		DialogFactory.TitleMessageButtonSmallDialog dialog = new DialogFactory.TitleMessageButtonSmallDialog(getActivity(), "成功",
				"賬號已綁定！");
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				String accessToken = MyPreferenceManager.getAuthToken(getActivity());
				GetProfileTask task = new GetProfileTask(getActivity(), accessToken);
				task.setCallback(mHandler);
				task.execute();
			}
		});
		dialog.show();
	}

	public void onBindFailed(String reason){
		DialogFactory.TitleMessageButtonSmallDialog dialog = new DialogFactory.TitleMessageButtonSmallDialog(getActivity(), "錯誤",
				"綁定失敗："+ reason);
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				FragHelper.replace(getActivity(), UserProfileFragment.newInstance(
						MyPreferenceManager.getUserId(getActivity())));

			}
		});
		dialog.show();
	}


}

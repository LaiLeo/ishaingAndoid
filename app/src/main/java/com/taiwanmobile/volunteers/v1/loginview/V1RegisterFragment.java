package com.taiwanmobile.volunteers.v1.loginview;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.api.GetProfileTask;
import com.taiwanmobile.volunteers.v1.api.V1AccountRegisterTask;
import com.taiwanmobile.volunteers.v1.util.PatternUtil;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.SupportFragment;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;

public class V1RegisterFragment extends SupportFragment implements OnClickListener {


	public static final int MSG_AUTH_FAILED = 0x1231;
	public static final int MSG_ACCOUNT_VALIDATE_ERROR = 0x1232;
	public static final int MSG_AUTH_SUCCESS = 0x1235;
	public static final int MSG_GET_PROFILE_SUCCESS = 0x1236;


	public static boolean isEmailValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	private String userName;
	private String password;

	private boolean isNameEditable = true;
    private String accessCode = null;
    private String twmAccount = null;


	private Handler mCallbackHandler;


	// public LoginFragment loginFragment;

	 public V1RegisterFragment() {
	 	mCallbackHandler = new RegisterHandler();
	 }


    public void setUserName(String name){
        userName = name;
    }

    public void setPassword(String pass){
        password = pass;
    }

    public void setAccessCode(String accessCode){
    	this.accessCode = accessCode;
	}

	public void setTWMAccount(String twmAccount){
	 	this.twmAccount = twmAccount;
	}

    public void setNameEditable(boolean enable){
    	this.isNameEditable = enable;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		hideActionBarTabs();
		View head = getActivity().getLayoutInflater().inflate(
				R.layout.fragment_login_register_v1, null);

		head.findViewById(R.id.fragment_login_register_btn_ok)
				.setOnClickListener(this);

		head.findViewById(R.id.fragment_login_register_tv_policy)
				.setOnClickListener(this);

		((EditText) head.findViewById(R.id.fragment_login_register_et_username))
				.setText(userName);
		((EditText) head.findViewById(R.id.fragment_login_register_et_password))
				.setText(password);

		((EditText) head.findViewById(R.id.fragment_login_register_et_username))
				.setEnabled(isNameEditable);

		return head;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.fragment_login_register_btn_ok:
			EditText username = (EditText) arg0.getRootView().findViewById(
					R.id.fragment_login_register_et_username);
			EditText password = (EditText) arg0.getRootView().findViewById(
					R.id.fragment_login_register_et_password);
			EditText password2 = (EditText) arg0.getRootView().findViewById(
					R.id.fragment_login_register_et_password_confirm);
			CheckBox policyCheckBox = (CheckBox) arg0.getRootView()
					.findViewById(R.id.fragment_login_register_cb_policy);

			boolean shouldBreak = false;
			// check if the above data is filled
			if (username.getText().toString().isEmpty()) {
				username.setHint("請輸入電子信箱");
				username.setHintTextColor(0xFFFF8C37);
				shouldBreak = true;
			}
			if (!isEmailValid(username.getText().toString())) {
				username.setText("");
				username.setHint("請輸入正確的電子信箱(不可有空格)");
				username.setHintTextColor(0xFFFF8C37);
				shouldBreak = true;
			}

			if (password.getText().toString().isEmpty()) {
				password.setHint("請輸入密碼");
				password.setHintTextColor(0xFFFF8C37);
				shouldBreak = true;
			}

			if (!PatternUtil.checkPwd(password.getText().toString())) {
				password.setHint("請輸入8-12碼英數字混合密碼");
				password.setHintTextColor(0xFFFF8C37);
				shouldBreak = true;
			}

			if (password2.getText().toString().isEmpty()) {
				password2.setHint("請輸入確認密碼");
				password2.setHintTextColor(0xFFFF8C37);
				shouldBreak = true;
			}
			if (shouldBreak) {
				break;
			}

			// check if the two passwords are equal
			if (password.getText().toString()
					.compareTo(password2.getText().toString()) != 0) {
				password2.setText("");
				password2.setHint("兩密碼不相符");
				password2.setHintTextColor(0xFFFF8C37);
				return;
			} else if (!policyCheckBox.isChecked()) {
				arg0.getRootView()
						.findViewById(
								R.id.fragment_login_register_tv_nonchecked)
						.setVisibility(View.VISIBLE);

				break;
			}

			V1AccountRegisterTask task = new V1AccountRegisterTask(getActivity(), username, accessCode, password,
						password2);
			if(twmAccount != null){
				task.setTWMAccount(twmAccount);
			}
			task.setCallback(mCallbackHandler);
			task.execute();
			break;
		case R.id.fragment_login_register_tv_policy:
			new DialogFactory.TitleMessageButtonDialog(getActivity(),
					"微樂志工隱私權政策", getResources()
							.getString(R.string.twmf_privacy)).show();
			break;
		default:
			break;
		}
	}


	private class RegisterHandler extends Handler{

		@Override
		public void handleMessage(@NonNull Message msg) {

			if(getActivity() == null || getActivity().isFinishing() || isDetached()){
				return;
			}


			switch(msg.what){

				case MSG_ACCOUNT_VALIDATE_ERROR:
					onAccountValidateError();
					break;
				case MSG_AUTH_FAILED:
					onFailed();
					break;
				case MSG_AUTH_SUCCESS:
					onAuthSuccess(true);
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
		Toast.makeText(getActivity(), "系統忙碌中",Toast.LENGTH_SHORT).show();
	}

	public void onAccountValidateError() {

//		DialogFactory.createMessageDialog(getActivity(), "您輸入的賬號或密碼錯誤，請重新輸入",
//				new DialogInterface.OnCancelListener() {
//					@Override
//					public void onCancel(DialogInterface dialog) {
//						usernameEditText.setText("");
//						passwordEditText.setText("");
//					}
//				}).show();

//		new DialogFactory.TitleMessageDialog(getActivity(),"密碼錯誤",
//				"您輸入的賬號或密碼錯誤，請重新輸入").show();


		DialogFactory.TitleMessageButtonSmallDialog dialog = new DialogFactory.TitleMessageButtonSmallDialog(getActivity(), "密碼錯誤",
				"您輸入的賬號或密碼錯誤，請重新輸入");
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
//				usernameEditText.setText("");
//				passwordEditText.setText("");
			}
		});
		dialog.show();
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
		String accessToken = MyPreferenceManager.getAuthToken(getActivity());
		GetProfileTask task = new GetProfileTask(getActivity(), accessToken);
		task.setCallback(mCallbackHandler);
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


	@Override
	public void onDestroyView() {
		super.onDestroyView();
        showActionBarTabs();
	}
}
package com.taiwanmobile.volunteers.v2.loginview;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.util.PatternUtil;
import com.taiwanmobile.volunteers.v2.SupportFragment;
import com.taiwanmobile.volunteers.v2.api.AccountRegisterTask;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;

public class RegisterFragment extends SupportFragment implements OnClickListener {

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

	// public LoginFragment loginFragment;

	// public RegisterFragment() {
	// loginFragment = null;
	// }

//	public RegisterFragment(String name, String pass) {
//		userName = name;
//	}

    public void setUserName(String name){
        userName = name;
    }

    public void setPassword(String pass){
        password = pass;
    }

    public void setAccessCode(String accessCode){
    	this.accessCode = accessCode;
	}

    public void setNameEditable(boolean enable){
    	this.isNameEditable = enable;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		hideActionBarTabs();
		View head = getActivity().getLayoutInflater().inflate(
				R.layout.fragment_login_register, null);

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
				password.setText("");
				password.setHint("請輸入8-12碼英數字混合密碼");
				password.setHintTextColor(0xFFFF8C37);
				shouldBreak = true;
			}

			if (password2.getText().toString().isEmpty()) {
				password2.setHint("請輸入確認密碼");
				password2.setHintTextColor(0xFFFF8C37);
				shouldBreak = true;
			}

			if (!PatternUtil.checkPwd(password2.getText().toString())) {
				password2.setText("");
				password2.setHint("請輸入8-12碼英數字混合密碼");
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

			new AccountRegisterTask(getActivity(), this, username, password,
					password2).execute();

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

	@Override
	public void onDestroyView() {
		super.onDestroyView();
        showActionBarTabs();
	}
}
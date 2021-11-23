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
import android.widget.EditText;

import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.api.GetProfileTask;
import com.taiwanmobile.volunteers.v1.api.TWMBindTask;
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

public class TWMBindFragment extends SupportFragment implements OnClickListener {
	private final String TAG = getClass().getSimpleName();


	public EditText mETSerialName;
	public EditText mETSerialEmail;
	public EditText mETSerialId;
	public EditText mETSerialNumber;
	public EditText mETSerialPhone;
	public EditText mETSerialType;
	public EditText mETSerialGroup;
	public EditText mETSerialDepartment;

	private TWMBindTask mBindTask;

	public static final int MSG_BIND_SUCCESS = 0x423;
	public static final int MSG_BIND_FAILED = 0x434;
	private Handler mHandler;



	public TWMBindFragment() {
		mHandler = new BindStateHandler();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		hideActionBarTab();

		View head = getActivity().getLayoutInflater().inflate(
				R.layout.fragment_bind_twm, null);

		head.findViewById(R.id.btn_bind).setOnClickListener(this);
		head.findViewById(R.id.btn_cancel).setOnClickListener(this);

		mETSerialName = head.findViewById(R.id.et_serial_name);
		mETSerialEmail = head.findViewById(R.id.et_serial_email);
		mETSerialId = head.findViewById(R.id.et_serial_id);
		mETSerialNumber = head.findViewById(R.id.et_serial_number);
		mETSerialPhone = head.findViewById(R.id.et_serial_phone);
		mETSerialType = head.findViewById(R.id.et_serial_type);
		mETSerialGroup = head.findViewById(R.id.et_serial_group);
		mETSerialDepartment = head.findViewById(R.id.et_serial_department);

		return head;
	}

	@Override
	public void onClick(View arg0) {
		View parent = arg0.getRootView();
		String enterpriseSerialNumber = mETSerialNumber.getText().toString();
		String enterpriseSerialEmail = mETSerialEmail.getText().toString();
		String enterpriseSerialDepartment = mETSerialDepartment.getText().toString();
		String enterpriseSerialName = mETSerialName.getText().toString();
		String enterpriseSerialId = mETSerialId.getText().toString();
		String enterpriseSerialPhone = mETSerialPhone.getText().toString();
		String enterpriseSerialType = mETSerialType.getText().toString();
		String enterpriseSerialGroup = mETSerialGroup.getText().toString();

		switch (arg0.getId()) {
			case R.id.btn_bind:
				// check if the user input username & password is correct
				boolean shouldBreak = false;
				if (StringUtils.isBlank(enterpriseSerialNumber)) {
					mETSerialNumber.setHintTextColor(0xFFFF8C37);
					mETSerialNumber.setHint("請輸入員工編號");
					shouldBreak = true;
				}

				if (StringUtils.isBlank(enterpriseSerialEmail)) {
					mETSerialEmail.setHintTextColor(0xFFFF8C37);
					mETSerialEmail.setHint("請輸入電子信箱");
					shouldBreak = true;
				}

				if (StringUtils.isBlank(enterpriseSerialDepartment)) {
					mETSerialDepartment.setHintTextColor(0xFFFF8C37);
					mETSerialDepartment.setHint("請輸入部門資訊");
					shouldBreak = true;
				}

				if (StringUtils.isBlank(enterpriseSerialName)) {
					mETSerialName.setHintTextColor(0xFFFF8C37);
					mETSerialName.setHint("請輸入真實姓名");
					shouldBreak = true;
				}

				if (StringUtils.isBlank(enterpriseSerialId)) {
					mETSerialId.setHintTextColor(0xFFFF8C37);
					mETSerialId.setHint("請輸入身份證字號");
					shouldBreak = true;
				}

				if (StringUtils.isBlank(enterpriseSerialPhone)) {
					mETSerialPhone.setHintTextColor(0xFFFF8C37);
					mETSerialPhone.setHint("請輸入電話");
					shouldBreak = true;
				}

				if (StringUtils.isBlank(enterpriseSerialType)) {
					mETSerialType.setHintTextColor(0xFFFF8C37);
					mETSerialType.setHint("請輸入公司別");
					shouldBreak = true;
				}

				if (StringUtils.isBlank(enterpriseSerialGroup)) {
					mETSerialGroup.setHintTextColor(0xFFFF8C37);
					mETSerialGroup.setHint("請輸入企業志工分組");
					shouldBreak = true;
				}
				if (shouldBreak) {
					return;
				}
				mBindTask = new TWMBindTask(getActivity(),
						enterpriseSerialNumber,
						enterpriseSerialEmail,
						enterpriseSerialDepartment,
						enterpriseSerialName,
						enterpriseSerialId,
						enterpriseSerialPhone,
						enterpriseSerialType,
						enterpriseSerialGroup);

				mBindTask.setCallback(mHandler);
				mBindTask.execute();
				return;
			case R.id.btn_cancel:
				FragHelper.replace(this, new LoginFragment());
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

package com.taiwanmobile.volunteers.v2.loginview;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.api.V1PWDRestTask;
import com.taiwanmobile.volunteers.v2.SupportFragment;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;

import androidx.annotation.NonNull;

public class ForgotPasswordFragment extends SupportFragment implements OnClickListener {

	public int returnStatusCode = 0;
    static final String TAG = "ForgotPasswordFragment";


    private Handler mHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		hideActionBarTabs();

		View head = getActivity().getLayoutInflater().inflate(
				R.layout.fragment_login_forgot_password, null);

		head.findViewById(R.id.forgot_passwod_btn_send)
				.setOnClickListener(this);

		// if (StringUtils.isNotEmpty(LoginFragment.userName)) {
		// EditText et = (EditText) head
		// .findViewById(R.id.forgot_passwod_et_email);
		// et.setText(LoginFragment.userName);
		// }

		mHandler = new StateHandler();
		return head;
	}

	public static final int MSG_SUCCESS = 200;
	public static final int MSG_ACCOUNT_NOT_FOUND = 404;
	public static final int MSG_ERROR= 500;

	private class StateHandler extends Handler{
		@Override
		public void handleMessage(@NonNull Message msg) {

			if(getActivity() == null || getActivity().isFinishing() || isDetached()){
				return;
			}

			switch (msg.what){
				case MSG_SUCCESS:
					handleSuccess();
					break;
				case MSG_ACCOUNT_NOT_FOUND:
					handleAccountNotFound();
					break;
				case MSG_ERROR:
					String reason = msg.obj.toString();
					handlerError(reason);
					break;
				default:
					break;
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		EditText et = (EditText) arg0.getRootView().findViewById(
				R.id.forgot_passwod_et_email);
		String email = et.getText().toString();

		V1PWDRestTask task = new V1PWDRestTask(getActivity(), email);
		task.setCallback(mHandler);
		task.execute();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
        showActionBarTabs();
	}

//	private class PasswordResetingReportTask extends
//			AsyncTaskWithDialogs<String, String> implements OnDismissListener {
//
//		Fragment parentFragment;
//
//		private PasswordResetingReportTask(Fragment frag) {
//			super(getActivity());
//			parentFragment = frag;
//			setProgressDialog(DialogFactory.createBusyDialog(getActivity(),
//					"傳送中"));
//			setSuccessDialog(DialogFactory.createDummyDialog(getActivity(),
//					this));
//			setFailDialog(DialogFactory.createDummyDialog(getActivity(), this));
//		}
//
//		@Override
//		protected Boolean doInBackground(String... uri) {
//			try {
//				OkHttpClient client = new OkHttpClient();
//				Request request = new Request.Builder()
//						.url(uri[0])
//						.build();
//
//				try {
//					Response response = client.newCall(request).execute();
//
//					returnStatusCode = response.code();
//					if (returnStatusCode == 200) {
//						return true;
//					}
//				} catch (IOException e) {
//					Log.e(TAG, e.getMessage());
//				}
//				return false;
//			} catch (Exception e) {
//				return false;
//			}
//		}
//
//		@Override
//		public void onDismiss(DialogInterface arg0) {
//			if (returnStatusCode == 200) {
//				new DialogFactory.TitleMessageButtonDialog(getActivity(),
//						"密碼重新設定信件已送出",
//						"密碼重新設定信件已送至該電子信箱，請至電子信箱收信，點擊信件中的網頁連結進行密碼修改。").show();
//
//				// FragHelper.showActionBar(getActivity());
//				FragHelper.removeAndClearBackStack(
//						parentFragment.getActivity(), parentFragment);
//			} else if (returnStatusCode == 404) {
//				new DialogFactory.TitleMessageDialog(getActivity(), "系統無此帳號",
//						"請註冊帳號或重新輸入").show();
//			}
//
//		}
//
//	}

	public void handleSuccess(){
		new DialogFactory.TitleMessageButtonDialog(getActivity(),
				"密碼重新設定信件已送出",
				"密碼重新設定信件已送至該電子信箱，請至電子信箱收信，點擊信件中的網頁連結進行密碼修改。").show();

		// FragHelper.showActionBar(getActivity());
		FragHelper.removeAndClearBackStack(
				getActivity(), this);
	}

	public void handleAccountNotFound(){
		new DialogFactory.TitleMessageDialog(getActivity(), "系統無此帳號",
				"請註冊帳號或重新輸入").show();
	}
	public void handlerError(String reason){
		new DialogFactory.TitleMessageDialog(getActivity(), "錯誤",
				reason).show();
	}

}

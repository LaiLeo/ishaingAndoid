package com.taiwanmobile.volunteers.v2.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class AsyncTaskWithDialogs<Params, Progress> extends
		AsyncTask<Params, Progress, Boolean> {

	protected AsyncTaskWithDialogs(Context c) {
		mDlgFail = mDlgSuccess = mDlgProgress = DialogFactory
				.createDummyDialog(c);
	}

	public AsyncTaskWithDialogs<Params, Progress> setProgressDialog(Dialog d) {
		mDlgProgress = d;
		return this;
	}

	public AsyncTaskWithDialogs<Params, Progress> setSuccessDialog(Dialog d) {
		mDlgSuccess = d;
		return this;
	}

	public AsyncTaskWithDialogs<Params, Progress> setFailDialog(Dialog d) {
		mDlgFail = d;
		return this;
	}

	private Dialog mDlgSuccess, mDlgFail;

	@Override
	protected void onPostExecute(Boolean result) {
		mDlgProgress.dismiss();
		if (result) {
			mDlgSuccess.show();
		} else {
			mDlgFail.show();
		}
	}

	private Dialog mDlgProgress;

	@Override
	protected void onPreExecute() {
		mDlgProgress.show();
	}

}

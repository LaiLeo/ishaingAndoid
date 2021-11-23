package com.taiwanmobile.volunteers.v1.api;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.taiwanmobile.volunteers.v2.utils.AsyncTaskWithDialogs;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BaseRequestTask extends AsyncTaskWithDialogs<Void, Void>
implements  OnDismissListener{
	public static final String TAG = "BaseRequestTask";

	Context mContext;
	int mStatusCode;
	String mError;


	public BaseRequestTask(Context c) {
		super(c);
		Log.d(TAG, "create BaseRequestTask");
		mContext = c;
	}



	@Override
	public Boolean doInBackground(Void... arg0) {

		return true;
	}


	@Override
	protected void onPostExecute(Boolean aBoolean) {
		super.onPostExecute(aBoolean);
		Log.d(TAG, "onPostExecute");
	}


	@Override
	public void onDismiss(DialogInterface dialog) {
	}


	Handler mCallback;

	public void setCallback(Handler callback){
		this.mCallback = callback;
	}

	public void sendResult(int msg){
		if(mCallback != null){
			mCallback.sendMessage(mCallback.obtainMessage(msg));
		}
	}

	public void sendResult(int msg, Object object){
		if(mCallback != null){
			Message message = mCallback.obtainMessage(msg);
			message.obj = object;
			mCallback.sendMessage(message);
		}
	}
	public void sendResult(int msg, Bundle data){
		if(mCallback != null){
			Message message = mCallback.obtainMessage(msg);
			message.setData(data);
			mCallback.sendMessage(message);
		}
	}

	public static final long TIME_OUT_MILLIS = 6000;

	public Response executeRequest(Request request){
		OkHttpClient client = new OkHttpClient().newBuilder()
				.connectTimeout(TIME_OUT_MILLIS, TimeUnit.MILLISECONDS)
				.readTimeout(TIME_OUT_MILLIS, TimeUnit.MILLISECONDS)
				.build();
		try {
			Response response = client.newCall(request).execute();
			return response;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

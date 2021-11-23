package com.taiwanmobile.volunteers.v2.tasks;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import androidx.core.app.ActivityCompat;

import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.utils.CommonUtils;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.LocationUtils;

public class CheckUserPositionTask extends AsyncTask<Void, Void, Boolean>
		implements OnDismissListener, ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener, OnCancelListener {
	private final String TAG = getClass().getSimpleName();

	Context mContext;
	String mEventUuid;
	String url;
	Fragment mEventDetailFragment;
	EventDAO mEventObject;
	static final int registerRange = 1000;

	private final ProgressDialog busyDialog;

	private Location mLocation;
	private final GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;

	public CheckUserPositionTask(Context c, Fragment frag, EventDAO event) {
		mContext = c;
		mEventDetailFragment = frag;
		mEventObject = event;

		mGoogleApiClient = LocationUtils.buildGoogleApiClient(c, this, this);
		busyDialog = new ProgressDialog(mContext,
				R.style.DatePickerDialog_big_font);
		busyDialog.setCancelable(true);
		busyDialog.setOnCancelListener(this);
		busyDialog.setMessage("定位中");
		busyDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}

	@Override
	protected void onPreExecute() {
		busyDialog.show();
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		mGoogleApiClient.connect();
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onConnected(Bundle arg0) {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(10000);
		mLocationRequest.setFastestInterval(5000);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location arg0) {
		mLocation = arg0;
		busyDialog.dismiss();
		LocationServices.FusedLocationApi.removeLocationUpdates(
				mGoogleApiClient, this);
		// check location
		Log.e(TAG, "" + mLocation.getLatitude());
		Location eventLlocation = new Location("event");
		eventLlocation.setLatitude(mEventObject.latitude);
		eventLlocation.setLongitude(mEventObject.longtitude);
		float distance = eventLlocation.distanceTo(mLocation);
		Log.i(TAG, "distance = " + distance);
		if (distance < registerRange) {
			CommonUtils.startQRScanner(mEventDetailFragment);
		} else {
//			new DialogFactory.TitleMessageDialog(mContext, "",
//					"無法找到您的位置，是否打開了衛星定位?[設定>位置]打開權限。或尋求現場工作人員協助，謝謝！").show();
			//Test only
			CommonUtils.startQRScanner(mEventDetailFragment);

		}
	}


	@Override
	public void onCancel(DialogInterface arg0) {
		busyDialog.dismiss();
		LocationServices.FusedLocationApi.removeLocationUpdates(
				mGoogleApiClient, this);
	}

	@Override
	public void onDismiss(DialogInterface arg0) {
		// TODO Auto-generated method stub
		mGoogleApiClient.disconnect();
	}
}

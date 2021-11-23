package com.taiwanmobile.volunteers.v2.utils;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

public class LocationUtils {

	public static synchronized GoogleApiClient buildGoogleApiClient(Context c,
			ConnectionCallbacks cc, OnConnectionFailedListener cfl) {
		return new GoogleApiClient.Builder(c).addConnectionCallbacks(cc)
				.addOnConnectionFailedListener(cfl)
				.addApi(LocationServices.API).build();
	}
}

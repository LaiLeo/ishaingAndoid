package com.taiwanmobile.volunteers.v2;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import android.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.squareup.picasso.Picasso;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.util.StaticResUtil;
import com.taiwanmobile.volunteers.v2.api.BackendDataSyncTask;
import com.taiwanmobile.volunteers.v2.api.FocusEventTask;
import com.taiwanmobile.volunteers.v2.api.UnfocusEventTask;
import com.taiwanmobile.volunteers.v2.database.DatabaseHelper;
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.database.FocusedEventDAO;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;

public class LocaionalItemEventListFragment extends Fragment implements
		OnRefreshListener, OnItemClickListener, OnClickListener {
	private final String TAG = getClass().getSimpleName();

	public static LocaionalEventsAdapter adapter;
	PullToRefreshLayout mPullToRefreshLayout;
	private ListView mListView;
	private View head;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;

	public LocaionalItemEventListFragment() {
		// mLocation = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// check Location settings
		LocationManager mlocManager = (LocationManager) getActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		if (!mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			View ret = inflater.inflate(
					R.layout.fragment_location_service_is_disabled_view, null);
			ret.findViewById(
					R.id.fragment_location_service_is_disabled_view_btn)
					.setOnClickListener(this);
			return ret;
		}

		adapter = new LocaionalEventsAdapter();

		head = inflater.inflate(R.layout.fragment_pull_to_refresh_list, null);

		mPullToRefreshLayout = (PullToRefreshLayout) head
				.findViewById(R.id.fragment_pull_to_refresh_list_ptrl);

		mListView = (ListView) head
				.findViewById(R.id.fragment_pull_to_refresh_list_listview);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(this);
		if (mListView.getCount() == 0) {
			head.findViewById(R.id.fragment_pull_to_refresh_list_tv_empty)
					.setVisibility(View.VISIBLE);
		} else {
			head.findViewById(R.id.fragment_pull_to_refresh_list_tv_empty)
					.setVisibility(View.GONE);
		}

		// Now setup the PullToRefreshLayout
		ActionBarPullToRefresh.from(getActivity())
		// Mark All Children as pullable
				.allChildrenArePullable()
				// Set the OnRefreshListener
				.listener(this)
				// Finally commit the setup to our PullToRefreshLayout
				.setup(mPullToRefreshLayout);
		// mGoogleApiClient = LocationUtils.buildGoogleApiClient(getActivity(),
		// this, this);
		// mGoogleApiClient.connect();

		return head;
	}

	@Override
	public void onRefreshStarted(View view) {
		if (!MainActivity.isDataDownloading) {
			new BackendDataSyncTask(getActivity(), mPullToRefreshLayout)
					.execute();
		} else {
			mPullToRefreshLayout.setRefreshComplete();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// ignore header click
		if (arg3 < 0) {
			return;
		} else {
			FragHelper.replace(getActivity(),
					new ItemEventDetailFragment().setEventId(arg3));
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		getFragmentManager().beginTransaction().detach(this).attach(this)
				.commit();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.fragment_location_service_is_disabled_view_btn:
			Intent callGPSSettingIntent = new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(callGPSSettingIntent, 0);
			break;
		}
	}

	public class LocaionalEventsAdapter extends SimpleCursorAdapter implements
			SimpleCursorAdapter.ViewBinder, OnClickListener {
		Context mContext = getActivity().getBaseContext();
		public LocaionalEventsAdapter() {
			super(getActivity(), R.layout.listview_event_list, EventDAO
					.queryAllItemEventsAndSortByDistance(),
					new String[] { EventDAO.DATABASE_COLUMN_IMAGE,
							EventDAO.DATABASE_COLUMN_SUBJECT,
							EventDAO.DATABASE_COLUMN_HAPPEN_DATE,
							EventDAO.DATABASE_COLUMN_REGISTER_DEADLINE_DATE,
							EventDAO.DATABASE_COLUMN_CLOSE_DATE,
							EventDAO.DATABASE_COLUMN_ADDRESS_CITY,
							EventDAO.DATABASE_COLUMN_ADDRESS,
							EventDAO.DATABASE_COLUMN_REQUIRED_VOLUNTEER_NUMBER,
							EventDAO.DATABASE_COLUMN_IS_URGENT,
							EventDAO.DATABASE_COLUMN_LAT,
							EventDAO.DATABASE_COLUMN_ID }, new int[] {
							R.id.listview_event_list_iv_image,
							R.id.listview_event_list_tv_subject,
							R.id.listview_event_list_tv_time,
							R.id.listview_event_list_tv_time_countdown,
							R.id.listview_event_list_tv_end_time,
							R.id.listview_event_list_tv_address_city,
							R.id.listview_event_list_tv_address,
							R.id.listview_event_list_tv_volunteer,
							R.id.listview_event_list_iv_isneedensurance,
							R.id.listview_event_list_tv_distance,
							R.id.listview_event_list_btn_is_focused },
					SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			setViewBinder(this);
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View v = super.getView(arg0, arg1, arg2);

			((TextView) v.findViewById(R.id.listview_event_list_tv_subject))
					.setMaxWidth((int) (180 * getActivity().getResources()
							.getDisplayMetrics().density));

			if (MyPreferenceManager.hasCredentials(mContext)) {
				v.findViewById(R.id.listview_event_list_btn_is_focused)
						.setOnClickListener(this);
			} else {
				v.findViewById(R.id.listview_event_list_btn_is_focused)
						.setVisibility(View.GONE);
			}
			return v;
		}

		@Override
		public boolean setViewValue(View arg0, Cursor arg1, int arg2) {
			if (arg2 == arg1.getColumnIndex(EventDAO.DATABASE_COLUMN_ID)) {
				Button b = (Button) arg0;
				long eventId = arg1.getLong(arg2);
				b.setTag(eventId);
				try {
					Boolean isRegistered = FocusedEventDAO
							.isUserFocused(eventId);
					Boolean isFocused = FocusedEventDAO.isUserFocused(eventId);
					if (isRegistered) {
						b.setBackgroundResource(R.drawable.ic_registered);
					} else if (isFocused) {
						b.setBackgroundResource(R.drawable.ic_subscribed);
					} else {
						b.setBackgroundResource(R.drawable.ic_nonsubscribed);
					}
				} catch (SQLException e) {
					Log.e(TAG, Log.getStackTraceString(e));
				}
				return true;
			} else if (arg2 == arg1
					.getColumnIndex(EventDAO.DATABASE_COLUMN_IMAGE)) {
				String link = arg1.getString(arg2);
				ImageView iv = (ImageView) arg0;
				int image_size = (int) (getResources().getInteger(
						R.integer.listview_image_size) * getResources()
						.getDisplayMetrics().density);
				Picasso.with(mContext)
						//.load(BackendContract.DEPLOYMENT_STATIC_URL + link)
						.load(StaticResUtil.checkUrl(link, true, TAG))
						.resize(image_size, image_size).centerCrop().into(iv);
				return true;
			} else if (arg2 == arg1
					.getColumnIndex(EventDAO.DATABASE_COLUMN_IS_URGENT)) {
				Integer needEnsurance = arg1.getInt(arg2);
				if (needEnsurance == 1) {
					arg0.setVisibility(View.VISIBLE);
					// TODO: test
				} else {
					arg0.setVisibility(View.GONE);
				}
				return true;
			} else if (arg2 == arg1
					.getColumnIndex(EventDAO.DATABASE_COLUMN_HAPPEN_DATE)) {
				TextView tv = (TextView) arg0;
				try {
					Calendar cal = Calendar.getInstance();
					cal.setTime(DatabaseHelper.DATETIME_FORMATTER.parse(arg1
							.getString(arg2)));
					tv.setText(cal.get(Calendar.YEAR) + "/"
							+ (cal.get(Calendar.MONTH) + 1) + "/"
							+ cal.get(Calendar.DATE));
				} catch (ParseException e) {
					Log.e("twmf", "subscribe list date format issue");
				}

				return true;
			} else if (arg2 == arg1
					.getColumnIndex(EventDAO.DATABASE_COLUMN_REGISTER_DEADLINE_DATE)) {
				TextView tv = (TextView) arg0;
				try {
					int isShort = arg1.getInt(arg1
							.getColumnIndex(EventDAO.DATABASE_COLUMN_IS_SHORT));
					int isUrgent = arg1
							.getInt(arg1
									.getColumnIndex(EventDAO.DATABASE_COLUMN_IS_URGENT));
					// set countdown
					Date today = Calendar.getInstance().getTime();
					Date date = DatabaseHelper.DATETIME_FORMATTER
							.parse(arg1.getString(arg1
									.getColumnIndex(EventDAO.DATABASE_COLUMN_HAPPEN_DATE)));
					int numberOfHours = (int) ((date.getTime() - today
							.getTime()) / (3600 * 1000));
					if (numberOfHours < 24 && numberOfHours >= 0) {
						tv.setText("倒數" + String.valueOf(numberOfHours) + "小時");
					} else if (numberOfHours >= 24) {
						numberOfHours = numberOfHours / 24;
						tv.setText("倒數" + String.valueOf(numberOfHours) + "天");
					} else {
						if (isShort != 1 && isUrgent != 1) {
							tv.setText("長期招募");
						} else {
							tv.setText("招募已開始");
						}
					}
				} catch (ParseException e) {
					Log.e("twmf", "subscribe list date format issue");
				}

				return true;
			} else if (arg2 == arg1
					.getColumnIndex(EventDAO.DATABASE_COLUMN_REQUIRED_VOLUNTEER_NUMBER)) {
				Long requiredVolunteerNumber = arg1.getLong(arg2);
				Long currentVolunteerNumber = arg1
						.getLong(arg1
								.getColumnIndex(EventDAO.DATABASE_COLUMN_CURRENT_VOLUNTEER_NUMBER));
				TextView tv = (TextView) arg0;
				if (requiredVolunteerNumber == 0) {
					tv.setText("招募無上限");
				} else if (currentVolunteerNumber >= requiredVolunteerNumber) {
					tv.setText("已額滿");
				} else {
					tv.setText("尚缺"
							+ String.valueOf(requiredVolunteerNumber
									- currentVolunteerNumber) + "份");
				}
				return true;
			} else if (arg2 == arg1
					.getColumnIndex(EventDAO.DATABASE_COLUMN_LAT)) {
				if (MainActivity.currentLocation != null) {
					try {
						double distance = arg1
								.getDouble(arg1
										.getColumnIndex(EventDAO.DATABASE_COLUMN_DISTANCE));
						if (distance > 500) {
							((TextView) arg0).setText("> 500公里");
						} else {
							((TextView) arg0).setText(String.format("%.1f公里",
									distance));
						}
					} catch (Exception ex) {
						Log.e(TAG, Log.getStackTraceString(ex));
					}
				}
				return true;
			}
			return false;
		}

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case R.id.listview_event_list_btn_is_focused:
				Button b = (Button) arg0;
				Drawable subscribed = arg0.getResources().getDrawable(
						R.drawable.ic_subscribed);
				Drawable nonsubscribed = arg0.getResources().getDrawable(
						R.drawable.ic_nonsubscribed);
				Long EventId = (Long) b.getTag();
				if (b.getBackground().getConstantState()
						.equals(subscribed.getConstantState())) {
					new UnfocusEventTask(getActivity(), EventId, b).execute();//FIH-modify for 點擊愛心會crash
				} else if (b.getBackground().getConstantState()
						.equals(nonsubscribed.getConstantState())) {
					new FocusEventTask(getActivity(), EventId, b).execute();//FIH-modify for 點擊愛心會crash
				}
				return;

			case R.id.listview_event_list_tv_address:
				TextView tv = (TextView) arg0;
				String uri = "geo:0,0?q=" + tv.getText().toString();
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse(uri));
				intent.setClassName("com.google.android.apps.maps",
						"com.google.android.maps.MapsActivity");
				mContext.startActivity(intent);
				return;
			}
		}

		@Override
		public void notifyDataSetChanged() {
			if (mListView.getCount() == 0) {
				head.findViewById(R.id.fragment_pull_to_refresh_list_tv_empty)
						.setVisibility(View.VISIBLE);
			} else {
				head.findViewById(R.id.fragment_pull_to_refresh_list_tv_empty)
						.setVisibility(View.GONE);
			}
			super.notifyDataSetChanged();
		}
	}
}

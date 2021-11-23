package com.taiwanmobile.volunteers.v2;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.widget.SimpleCursorAdapter;
//import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
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

import com.squareup.picasso.Picasso;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.util.StaticResUtil;
import com.taiwanmobile.volunteers.v2.api.BackendDataSyncTask;
import com.taiwanmobile.volunteers.v2.api.FocusEventTask;
import com.taiwanmobile.volunteers.v2.api.UnfocusEventTask;
import com.taiwanmobile.volunteers.v2.database.DatabaseHelper;
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.database.FocusedEventDAO;
import com.taiwanmobile.volunteers.v2.database.RegisteredEventDAO;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;
import com.taiwanmobile.volunteers.v2.utils.ViewHelper;

public class SubscribedEventListFragment extends Fragment implements
		OnRefreshListener, OnItemClickListener {
	private final String TAG = "SubEventListFragment";

	public SubscribedEventsAdapter adapter;
	private ListView mListView;
	private View head;
	PullToRefreshLayout mPullToRefreshLayout;

    public static final String BUNDLE_KEY_TARGET = "target";
    static final String KEY_GENERAL_VOLUNTEER = "general_volunteer";
    static final String KEY_ENTERPRISE_VOLUNTEER = "enterprise_volunteer";
    String target = KEY_GENERAL_VOLUNTEER;


	public SubscribedEventListFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        if(getArguments() != null){
            Bundle bundle = getArguments();
            target = bundle.getString(BUNDLE_KEY_TARGET, KEY_GENERAL_VOLUNTEER);
        }else{
            Log.w(TAG, "create subscribed event list fragment without arguments");
        }
        if(target.equals(KEY_GENERAL_VOLUNTEER)){
            ViewHelper.generalVolunteerSubscribedEventListFragment = this;
        }else if (target.equals(KEY_ENTERPRISE_VOLUNTEER)){
            ViewHelper.enterpriseVolunteerSubscribedEventListFragment = this;
        }
		adapter = new SubscribedEventsAdapter();

		head = inflater.inflate(R.layout.fragment_pull_to_refresh_list, container, false);

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
					new VolunteerEventDetailFragment().setEventId(arg3));
		}
	}

    Cursor getDataCursor(){
        if(target.equals(KEY_GENERAL_VOLUNTEER)){
            return EventDAO.queryGeneralVolunteerEvents();
        }else if(target.equals(KEY_ENTERPRISE_VOLUNTEER)) {
            return EventDAO.queryEnterpriseVolunteerEvents();
        }else{
            return EventDAO.queryGeneralVolunteerEvents();
        }
    }

    public void notifyRefreshEventList() {
        adapter.changeCursor(getDataCursor());
        adapter.notifyDataSetChanged();
    }


    public class SubscribedEventsAdapter extends SimpleCursorAdapter implements
			SimpleCursorAdapter.ViewBinder,
			OnClickListener {
		Context mContext = getActivity().getBaseContext();

		public SubscribedEventsAdapter() {
            super(getActivity(), R.layout.listview_event_list, getDataCursor(),
                    new String[] {
                            EventDAO.DATABASE_COLUMN_IMAGE,
                            EventDAO.DATABASE_COLUMN_SUBJECT,
                            EventDAO.DATABASE_COLUMN_HAPPEN_DATE,
                            EventDAO.DATABASE_COLUMN_REGISTER_DEADLINE_DATE,
                            EventDAO.DATABASE_COLUMN_CLOSE_DATE,
                            EventDAO.DATABASE_COLUMN_ADDRESS_CITY,
                            EventDAO.DATABASE_COLUMN_ADDRESS,
                            EventDAO.DATABASE_COLUMN_REQUIRED_VOLUNTEER_NUMBER,
                            EventDAO.DATABASE_COLUMN_IS_URGENT,
                            EventDAO.DATABASE_COLUMN_ID },
                    new int[] {
                            R.id.listview_event_list_iv_image,
                            R.id.listview_event_list_tv_subject,
                            R.id.listview_event_list_tv_time,
                            R.id.listview_event_list_tv_time_countdown,
                            R.id.listview_event_list_tv_end_time,
                            R.id.listview_event_list_tv_address_city,
                            R.id.listview_event_list_tv_address,
                            R.id.listview_event_list_tv_volunteer,
                            R.id.listview_event_list_iv_isneedensurance,
                            R.id.listview_event_list_btn_is_focused },
                    SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);


            setViewBinder(this);
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View v = super.getView(arg0, arg1, arg2);

			// TODO : test
			v.findViewById(R.id.listview_event_list_tv_distance).setVisibility(
					View.GONE);

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
		public boolean setViewValue(View view, Cursor cursor, int id) {
			if (id == cursor.getColumnIndex(EventDAO.DATABASE_COLUMN_ID)) {
				Button b = (Button) view;
				long eventId = cursor.getLong(id);

                Log.d(TAG, "setViewValue " + eventId);
				b.setTag(eventId);
				try {
					Boolean isRegistered = RegisteredEventDAO
							.isUserRegistered(eventId);
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
			} else if (id == cursor
					.getColumnIndex(EventDAO.DATABASE_COLUMN_IMAGE)) {
				String link = cursor.getString(id);
				ImageView iv = (ImageView) view;
				int image_size = (int) (getResources().getInteger(
						R.integer.listview_image_size) * getResources()
						.getDisplayMetrics().density);
				Picasso.with(mContext)
						//.load(BackendContract.DEPLOYMENT_STATIC_URL + link)
						.load(StaticResUtil.checkUrl(link, true, TAG))
						.resize(image_size, image_size).centerCrop().into(iv);
				return true;
			} else if (id == cursor
					.getColumnIndex(EventDAO.DATABASE_COLUMN_IS_URGENT)) {
				Integer needEnsurance = cursor.getInt(id);
				if (needEnsurance == 1) {
					view.setVisibility(View.VISIBLE);
					// TODO: test
				} else {
					view.setVisibility(View.GONE);
				}
				return true;
			} else if (id == cursor
					.getColumnIndex(EventDAO.DATABASE_COLUMN_HAPPEN_DATE)) {
				TextView tv = (TextView) view;
				try {
					Calendar cal = Calendar.getInstance();
					cal.setTime(DatabaseHelper.DATETIME_FORMATTER.parse(cursor
							.getString(id)));
					tv.setText(String.format(Locale.TAIWAN, "%d/%d/%d",
							cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.DATE)));
				} catch (ParseException e) {
					Log.e(TAG, "subscribe list date format issue");
				}

				return true;
			} else if (id == cursor
					.getColumnIndex(EventDAO.DATABASE_COLUMN_REGISTER_DEADLINE_DATE)) {
				TextView tv = (TextView) view;
				try {
					int isShort = cursor.getInt(cursor
							.getColumnIndex(EventDAO.DATABASE_COLUMN_IS_SHORT));
					int isUrgent = cursor
							.getInt(cursor
									.getColumnIndex(EventDAO.DATABASE_COLUMN_IS_URGENT));

					// set countdown
					Date today = Calendar.getInstance().getTime();
					Date date = DatabaseHelper.DATETIME_FORMATTER
							.parse(cursor.getString(cursor
									.getColumnIndex(EventDAO.DATABASE_COLUMN_HAPPEN_DATE)));
					int numberOfHours = (int) ((date.getTime() - today
							.getTime()) / (3600 * 1000));
					if (numberOfHours < 24 && numberOfHours >= 0) {
						tv.setText(String.format("倒數%s小時", String.valueOf(numberOfHours)));
					} else if (numberOfHours >= 24) {
						numberOfHours = numberOfHours / 24;
						tv.setText(String.format("倒數%s天", String.valueOf(numberOfHours)));
					} else {
						if (isShort != 1 && isUrgent != 1) {
							tv.setText("長期招募");
						} else {
							tv.setText("活動已開始");
						}
					}
				} catch (ParseException e) {
					Log.e(TAG, "subscribe list date format issue");
				}

				return true;
			} else if (id == cursor
					.getColumnIndex(EventDAO.DATABASE_COLUMN_REQUIRED_VOLUNTEER_NUMBER)) {
				Long requiredVolunteerNumber = cursor.getLong(id);
				Long currentVolunteerNumber = cursor
						.getLong(cursor
								.getColumnIndex(EventDAO.DATABASE_COLUMN_CURRENT_VOLUNTEER_NUMBER));
				TextView tv = (TextView) view;
				if (requiredVolunteerNumber == 0) {
					tv.setText("報名無上限");
				} else if (currentVolunteerNumber >= requiredVolunteerNumber) {
					tv.setText("已額滿");
				} else {
					tv.setText(String.format("尚缺%s名",
                            String.valueOf(requiredVolunteerNumber - currentVolunteerNumber)));
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
				break;

			case R.id.listview_event_list_tv_address:
				TextView tv = (TextView) arg0;
				String uri = "geo:0,0?q=" + tv.getText().toString();
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
						Uri.parse(uri));
				intent.setClassName("com.google.android.apps.maps",
						"com.google.android.maps.MapsActivity");
				mContext.startActivity(intent);
                break;
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

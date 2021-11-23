package com.taiwanmobile.volunteers.v2.npoview;

import java.sql.SQLException;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.util.StaticResUtil;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.api.BackendDataSyncTask;
import com.taiwanmobile.volunteers.v2.api.SubscribeNPOTask;
import com.taiwanmobile.volunteers.v2.api.UnSubscribeNPOTask;
import com.taiwanmobile.volunteers.v2.database.NpoDAO;
import com.taiwanmobile.volunteers.v2.database.SubscribedNpoDAO;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;

public class ItemNpoListFragment extends Fragment implements OnRefreshListener,
		OnItemClickListener {
	public static final String TAG = ItemNpoListFragment.class.getSimpleName();

	public static NpoListAdapter adapter;
	private ListView mListView;
	private View head;
	PullToRefreshLayout mPullToRefreshLayout;

	public ItemNpoListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		adapter = new NpoListAdapter(getActivity());

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
					new NpoDetailFragment().setNpoId(arg3));
		}
	}

	public class NpoListAdapter extends SimpleCursorAdapter implements
			SimpleCursorAdapter.ViewBinder, OnClickListener {

		Context mContext;

		public NpoListAdapter(Context c) {
			super(c, R.layout.listview_npo_list, NpoDAO.queryItemNpos(),
					new String[] { NpoDAO.DATABASE_COLUMN_ID,
							NpoDAO.DATABASE_COLUMN_ICON,
							NpoDAO.DATABASE_COLUMN_NAME,
							NpoDAO.DATABASE_COLUMN_RATING_USER_NUM,
							NpoDAO.DATABASE_COLUMN_SUBSCRIBED_USER_NUM,
							NpoDAO.DATABASE_COLUMN_EVENT_NUM,
							NpoDAO.DATABASE_COLUMN_JOINED_USER_NUM,
							NpoDAO.DATABASE_COLUMN_TOTAL_RATING_SCORE,
							NpoDAO.DATABASE_COLUMN_DESCRIPTION }, new int[] {
							R.id.listview_npo_list_tv_npo_id,
							R.id.listview_npo_list_iv_image,
							R.id.listview_npo_list_tv_subject,
							R.id.listview_npo_list_tv_rating_num,
							R.id.listview_npo_list_tv_subscribe_num,
							R.id.listview_npo_list_tv_event_num,
							R.id.listview_npo_list_tv_joined_num,
							R.id.listview_npo_list_rb_rating,
							android.R.id.button1 },
					SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

			mContext = c;
			setViewBinder(this);
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View v = super.getView(arg0, arg1, arg2);

			if (MyPreferenceManager.hasCredentials(mContext)) {
				v.findViewById(android.R.id.button1).setOnClickListener(this);
				v.findViewById(android.R.id.button1).setTag(
						v.findViewById(R.id.listview_npo_list_tv_npo_id));
				v.findViewById(R.id.listview_npo_list_tv_npo_id)
						.setTag(v
								.findViewById(R.id.listview_npo_list_tv_subscribe_num));
			} else {
				v.findViewById(android.R.id.button1).setVisibility(View.GONE);
			}
			RatingBar rb = (RatingBar) v
					.findViewById(R.id.listview_npo_list_rb_rating);
			rb.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
			rb.setFocusable(false);
			return v;
		}

		@Override
		public void onClick(View arg0) {
			switch (arg0.getId()) {
			case android.R.id.button1:
				Button b = (Button) arg0;
				Drawable d = arg0.getResources().getDrawable(
						R.drawable.ic_subscribed);
				TextView tv = (TextView) b.getTag();
				TextView tv2 = (TextView) tv.getTag();
				long NPOId = Long.valueOf(tv.getText().toString()).longValue();
				if (b.getBackground().getConstantState()
						.equals(d.getConstantState())) {
					new UnSubscribeNPOTask(mContext, NPOId, b).execute();
				} else {
					new SubscribeNPOTask(mContext, NPOId, b).execute();
				}
				return;
			}
		}

		@Override
		public boolean setViewValue(View arg0, Cursor arg1, int arg2) {
			if (arg2 == arg1
					.getColumnIndex(NpoDAO.DATABASE_COLUMN_TOTAL_RATING_SCORE)) {
				RatingBar rb = (RatingBar) arg0;
				Float score = arg1.getFloat(arg2)
						/ arg1.getFloat(arg1
								.getColumnIndex(NpoDAO.DATABASE_COLUMN_RATING_USER_NUM));
				rb.setRating(score);
				return true;
			} else if (arg2 == arg1.getColumnIndex(NpoDAO.DATABASE_COLUMN_ICON)) {
				String link = arg1.getString(arg2);
				ImageView iv = (ImageView) arg0;
				int image_size = (int) (getResources().getInteger(
						R.integer.listview_image_size) * getResources()
						.getDisplayMetrics().density);
				Picasso.with(mContext)
						//.load(BackendContract.DEPLOYMENT_STATIC_URL + link)
						.load(StaticResUtil.checkUrl(link, true, TAG))
						.resize(image_size, image_size).centerInside().into(iv);
				return true;
			} else if (arg2 == arg1
					.getColumnIndex(NpoDAO.DATABASE_COLUMN_DESCRIPTION)) {
				Button b = (Button) arg0;
				long npoId = arg1.getLong(arg1
						.getColumnIndex(NpoDAO.DATABASE_COLUMN_ID));
				try {
					Boolean isSubscribed = SubscribedNpoDAO
							.isUserSubscribed(npoId);
					if (!isSubscribed) {
						b.setBackgroundResource(R.drawable.ic_nonsubscribed);
					} else {
						b.setBackgroundResource(R.drawable.ic_subscribed);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return true;
			}
			return false;
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

package com.taiwanmobile.volunteers.v2;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import android.app.Fragment;
//import android.support.v4.app.Fragment;
//import android.support.v4.widget.SimpleCursorAdapter;
//import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.squareup.picasso.Picasso;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.util.StaticResUtil;
import com.taiwanmobile.volunteers.v2.database.DonationNpoDAO;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;

public class DonationFragment extends Fragment implements OnItemClickListener {
	private final String TAG = getClass().getSimpleName();

	public static DonationNPOAdapter adapter;

	public DonationFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_donation, null);

		adapter = new DonationNPOAdapter();
		ListView lv = (ListView) view.findViewById(R.id.fragment_donation_lv);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
		// ignore header click
		if (id < 0) {
			return;
		} else {
			FragHelper.replace(getActivity(), new DonationNpoDetailFragment(
					).setNpoId(id));
		}
	}

	public class DonationNPOAdapter extends SimpleCursorAdapter implements
			SimpleCursorAdapter.ViewBinder {

		Context mContext = getActivity().getBaseContext();
		public DonationNPOAdapter() {
			super(getActivity(), R.layout.listview_donation_npo_list,
					DonationNpoDAO.queryDonationNpos(), new String[] {
							DonationNpoDAO.DATABASE_COLUMN_ICON,
							DonationNpoDAO.DATABASE_COLUMN_NAME }, new int[] {
							R.id.listview_donation_npo_list_iv,
							R.id.listview_donation_npo_list_tv_name },
					SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			setViewBinder(this);
		}

		@Override
		public boolean setViewValue(View arg0, Cursor arg1, int arg2) {
			if (arg2 == arg1
					.getColumnIndex(DonationNpoDAO.DATABASE_COLUMN_ICON)) {
				String link = arg1.getString(arg2);
				ImageView iv = (ImageView) arg0;
				int image_size = (int) (getResources().getInteger(
						R.integer.listview_image_size) * getResources()
						.getDisplayMetrics().density);
				Picasso.with(mContext)
						//.load(BackendContract.DEPLOYMENT_URL + link)
						.load(StaticResUtil.checkUrl(link, false, TAG))
						.resize(image_size, image_size).into(iv);
				return true;
			}
			return false;
		}
	}
}

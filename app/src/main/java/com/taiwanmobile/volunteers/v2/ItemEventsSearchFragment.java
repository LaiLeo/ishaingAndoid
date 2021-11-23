package com.taiwanmobile.volunteers.v2;

import android.database.Cursor;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;

public class ItemEventsSearchFragment extends Fragment implements
		OnClickListener, OnCheckedChangeListener {
	private final String TAG = getClass().getSimpleName();

	enum EventType {
		ALL, UERGENT, LONG, SHORT
	}

	private EventType mEventType = EventType.ALL;
	private String selectedCity = "";
    private String selectedVolunteerType = "";
    private String selectedFullStatus = "";

	public ItemEventsSearchFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_item_search, container, false);
        setViewOnClickListeners(view);
		return view;
	}

	private void setViewOnClickListeners(View view){
        view.findViewById(R.id.fragment_item_search_btn_confirm)
                .setOnClickListener(this);
        ((RadioGroup) view.findViewById(R.id.fragment_item_search_rg))
                .setOnCheckedChangeListener(this);
        ((Spinner) view.findViewById(R.id.fragment_item_search_sp_location))
                .setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView adapterView,
                                               View view, int position, long id) {
                        selectedCity = getResources().getStringArray(
                                R.array.location_choice)[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView arg0) {
                    }
                });
        ((Spinner) view.findViewById(R.id.fragment_item_search_sp_type))
                .setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView adapterView,
                                               View view, int position, long id) {
                        selectedVolunteerType = getResources().getStringArray(
                                R.array.item_category_choice)[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView arg0) {
                    }
                });
        ((Spinner) view.findViewById(R.id.fragment_item_search_sp_full_status))
                .setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView adapterView,
                                               View view, int position, long id) {
                        selectedFullStatus = getResources().getStringArray(
                                R.array.full_status_choice)[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView arg0) {
                    }
                });
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_item_search_btn_confirm:
			try {
				String sql = "select e.* from eventdao e where isVolunteer=0";
				if (mEventType == EventType.UERGENT) {
					sql += " and " + EventDAO.DATABASE_COLUMN_IS_URGENT + "=1";
				} else if (mEventType == EventType.SHORT) {
					sql += " and " + EventDAO.DATABASE_COLUMN_IS_SHORT + "=1";
				} else if (mEventType == EventType.LONG) {
					sql += " and " + EventDAO.DATABASE_COLUMN_IS_SHORT + "=0";
				}

				if (selectedCity.compareTo("不限") != 0) {
					sql += " and " + EventDAO.DATABASE_COLUMN_ADDRESS_CITY
							+ "='" + selectedCity + "'";
				}
				if (selectedVolunteerType.compareTo("不限") != 0) {
					sql += " and " + EventDAO.DATABASE_COLUMN_VOLUNTEER_TYPE
							+ "='" + selectedVolunteerType + "'";
				}

                if (!selectedFullStatus.equals("不限")){
                    if (selectedFullStatus.equals("未額滿")){
                        // requiredVolunteerNum = 0 mean infinite
                        sql += " and (currentVolunteerNum < requiredVolunteerNum or requiredVolunteerNum = 0 )  ";
                    }else if (selectedFullStatus.equals("已額滿")){
                        sql += " and (currentVolunteerNum >= requiredVolunteerNum and requiredVolunteerNum != 0) ";
                    }
                }

				sql += ";";
				Log.e(TAG, sql);

				Cursor c = MainActivity.MyDbHelper.getReadableDatabase()
						.rawQuery(sql, null);

				if (c.getCount() == 0) {
					new DialogFactory.TitleMessageDialog(getActivity(),
							"沒有物資缺", "請重新搜尋").show();
                    c.close();
				} else {
					FilteredItemEventListFragment fragment = new FilteredItemEventListFragment();
					fragment.setCursor(c);
					FragHelper.replace(getActivity(),fragment);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.fragment_item_search_rb_all:
			mEventType = EventType.ALL;
			break;
		case R.id.fragment_item_search_rb_urgent:
			mEventType = EventType.UERGENT;
			break;
		case R.id.fragment_item_search_rb_long:
			mEventType = EventType.LONG;
			break;
		case R.id.fragment_item_search_rb_short:
			mEventType = EventType.SHORT;
			break;
		}
	}
}

package com.taiwanmobile.volunteers.v2;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

import com.google.common.base.Joiner;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;

import java.util.ArrayList;
import java.util.List;

//import android.support.v4.app.Fragment;

public class VolunteerEventsSearchFragment extends Fragment implements
		OnClickListener, OnCheckedChangeListener {
	private final String TAG = getClass().getSimpleName();



    public static final String BUNDLE_KEY_TARGET = "target";
    static final String KEY_GENERAL_VOLUNTEER = "general_volunteer";
    static final String KEY_ENTERPRISE_VOLUNTEER = "enterprise_volunteer";
    String target = KEY_GENERAL_VOLUNTEER;


    enum EventType {
		ALL, UERGENT, LONG, SHORT
	}

	private EventType mEventType = EventType.ALL;
    private String selectedCity = "";
    private String selectedFullStatus = "";
	public static List<String> selectedVolunteerTypes;

	public VolunteerEventsSearchFragment() {
		Log.d(TAG, "VolunteerEventsSearchFragment()");
		selectedVolunteerTypes = new ArrayList<>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        if(getArguments() != null){
            Bundle bundle = getArguments();
            target = bundle.getString(BUNDLE_KEY_TARGET, KEY_GENERAL_VOLUNTEER);
        }else{
            Log.w(TAG, "create subscribed event list fragment without arguments");
        }

        View view = inflater.inflate(R.layout.fragment_volunteer_search, container, false);


        setOnClickListeners(view);
		return view;
	}

    private void setOnClickListeners(View view){
        view.findViewById(R.id.fragment_volunteer_search_btn_confirm)
                .setOnClickListener(this);
        ((RadioGroup) view.findViewById(R.id.fragment_volunteer_search_rg))
                .setOnCheckedChangeListener(this);
        ((Spinner) view
                .findViewById(R.id.fragment_volunteer_search_sp_location))
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
        view.findViewById(R.id.fragment_volunteer_search_btn_type)
                .setOnClickListener(this);
        ((Spinner) view
                .findViewById(R.id.fragment_volunteer_search_sp_is_full))
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
            case R.id.fragment_volunteer_search_btn_confirm:
                try {
                    String sql = "select e.* from eventdao e JOIN npodao n ON e.npo_id = n._id where isVolunteer=1 ";
                    if(target.equals(KEY_ENTERPRISE_VOLUNTEER)){
                        sql += " and isEnterprise=1";
                    }else{
                        sql += " and isEnterprise=0";
                    }

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

                    if (!selectedFullStatus.equals("不限")){
                        if (selectedFullStatus.equals("未額滿")){
                            // requiredVolunteerNum = 0 mean infinite
                            sql += " and (currentVolunteerNum < requiredVolunteerNum or requiredVolunteerNum = 0 )  ";
                        }else if (selectedFullStatus.equals("已額滿")){
                            sql += " and (currentVolunteerNum >= requiredVolunteerNum and requiredVolunteerNum != 0) ";
                        }
                    }

                    if (selectedVolunteerTypes.size() != 0) {
                        String constranit = "(";
                        ArrayList<String> types = new ArrayList<>();
                        for (String s : selectedVolunteerTypes) {
                            types.add("'" + s + "'");
                        }
                        constranit += Joiner.on(',').join(types);
                        Log.e(TAG, Joiner.on(',').join(types));
                        constranit += ")";
                        sql += " and " + EventDAO.DATABASE_COLUMN_VOLUNTEER_TYPE
                                + " in " + constranit + "";
                    }
                    sql += ";";
                    Log.e(TAG, sql);

                    Cursor c = MainActivity.MyDbHelper.getReadableDatabase()
                            .rawQuery(sql, null);

                    if (c.getCount() == 0) {
                        new DialogFactory.TitleMessageDialog(getActivity(), "沒有活動",
                                "請重新搜尋").show();
                        c.close();
                    } else {
                        FragHelper.replace(getActivity(),
                                new FilteredEventListFragment(c));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.fragment_volunteer_search_btn_type:
                DialogFactory.showVolunteerEventTypeDialog((Button) v);
                break;
        }
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.fragment_volunteer_search_rb_all:
			mEventType = EventType.ALL;
			break;
		case R.id.fragment_volunteer_search_rb_urgent:
			mEventType = EventType.UERGENT;
			break;
		case R.id.fragment_volunteer_search_rb_long:
			mEventType = EventType.LONG;
			break;
		case R.id.fragment_volunteer_search_rb_short:
			mEventType = EventType.SHORT;
			break;
		}
	}
}

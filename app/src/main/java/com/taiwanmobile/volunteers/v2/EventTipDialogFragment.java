package com.taiwanmobile.volunteers.v2;

import org.apache.commons.lang3.StringUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager.LayoutParams;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Switch;

import com.taiwanmobile.volunteers.R;

public class EventTipDialogFragment extends Dialog implements OnClickListener,
		OnCheckedChangeListener {

	Context mContext;

	public EventTipDialogFragment(Context context) {
		super(context, android.R.style.Theme_DeviceDefault_NoActionBar);
		setContentView(R.layout.dialog_event_tip);
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mContext = context;

		Spinner sp = (Spinner) this.findViewById(R.id.event_tip_dialog_sp_time);
		switch (getTipTime(context)) {
		case 1:
			sp.setSelection(0);
			break;
		case 24:
			sp.setSelection(1);
			break;
		case 48:
			sp.setSelection(2);
			break;
		}
		sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView adapterView, View view,
					int position, long id) {
				selected_position = position;
			}

			@Override
			public void onNothingSelected(AdapterView arg0) {
			}
		});

		Switch sw = (Switch) this
				.findViewById(R.id.event_tip_dialog_switch_tip);
		if (!isTipOn(context)) {
			sw.setChecked(false);
			tipOn = false;
		} else {
			int time = getTipTime(context);
			switch (time) {
			case 1:
				sp.setSelection(0);
				break;
			case 24:
				sp.setSelection(1);
				break;
			case 48:
				sp.setSelection(2);
				break;
			}
		}
		sw.setOnCheckedChangeListener(this);

		this.findViewById(R.id.event_tip_dialog_btn).setOnClickListener(this);
	}

	boolean tipOn = true;
	int selected_position = 0;

	public static final String KEY_TIP_SWITCH = "pref_tip";
	public static final String KEY_TIP_TIME = "pref_tip_time";

	public static boolean isTipOn(Context c) {

		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);

		return StringUtils.isNotEmpty(app_preferences
				.getString(KEY_TIP_SWITCH, ""));
	}

	public static int getTipTime(Context c) {

		SharedPreferences app_preferences = PreferenceManager
				.getDefaultSharedPreferences(c);

		String time = app_preferences.getString(KEY_TIP_TIME, "");
		if (StringUtils.isEmpty(time)) {
			return 24;
		} else {
			return Integer.valueOf(time);
		}

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.event_tip_dialog_btn:
			SharedPreferences app_preferences = PreferenceManager
					.getDefaultSharedPreferences(mContext);
			if (tipOn) {
				app_preferences.edit().putString(KEY_TIP_SWITCH, "on").apply();
			} else {
				app_preferences.edit().putString(KEY_TIP_SWITCH, "").apply();
			}
			switch (selected_position) {
			case 0:
				app_preferences.edit().putString(KEY_TIP_TIME, "1").apply();
				break;
			case 1:
				app_preferences.edit().putString(KEY_TIP_TIME, "24").apply();
				break;
			case 2:
				app_preferences.edit().putString(KEY_TIP_TIME, "48").apply();
				break;
			}
			this.dismiss();
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
		tipOn = isChecked;
	}
}

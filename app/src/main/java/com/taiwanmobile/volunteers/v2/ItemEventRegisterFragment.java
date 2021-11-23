package com.taiwanmobile.volunteers.v2;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v2.api.RegisterEventTask;
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.database.SkillGroupDAO;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.ui_component.NumberPicker;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;

import kotlin.Pair;

public class ItemEventRegisterFragment extends SupportFragment implements
		OnClickListener {
	final String TAG = getClass().getSimpleName();

	private EventDAO mEventObject;
	private View mRet;
	ViewGroup insertPoint;

	private static final String POST_KEY_EVENT_ID = "event_id";
	private static final String POST_KEY_NAME = "name";
	private static final String POST_KEY_PHONE = "phone";
	private static final String POST_KEY_EMAIL = "email";
	private static final String POST_KEY_SKILL = "skill";
	private static final String POST_KEY_SKILL_GROUP = "skillgroup_list";
	private static final String POST_KEY_NOTE = "note";
	private static final String POST_KEY_SKILL_GROUP_ID = "skillgroup";
	//FIH-modify for 舊的物資報名頁面 start
	private static final String POST_KEY_ENTERPRISE_SERIAL_NUMBER = "enterprise_serial_number";
	private static final String POST_KEY_EMPLOYEE_SERIAL_NUMBER = "employee_serial_number";
	//FIH-modify for 舊的物資報名頁面 end

	List<Pair<String, String>> nameValuePairs = new ArrayList<>(10);
	List<View> skillGroup;
//
//	public ItemEventRegisterFragment(List<View> group, EventDAO event) {
//		mEventObject = event;
//		skillGroup = group;
//	}


    public void setEventObject(EventDAO event){
        mEventObject = event;
    }

    public void setSkillGroup(List<View> group){
        skillGroup = group;
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		hideActionBarTabs();

		View ret = inflater
				.inflate(R.layout.fragment_item_event_register, null);
		mRet = ret;

		ret.findViewById(R.id.fragment_item_event_register_btn_submit)
				.setOnClickListener(this);

		ret.findViewById(R.id.fragment_item_event_register_tv_policy)
				.setOnClickListener(this);

		// bind data
		EditText name = (EditText) mRet
				.findViewById(R.id.fragment_item_event_register_et_name);
		EditText phone = (EditText) mRet
				.findViewById(R.id.fragment_item_event_register_et_phone);
		EditText email = (EditText) mRet
				.findViewById(R.id.fragment_item_event_register_et_email);

		try {
			UserAccountDAO useraccountObject = UserAccountDAO
					.queryObjectByMe(getActivity());

			name.setText(useraccountObject.displayName);
			phone.setText(useraccountObject.phone);
			email.setText(useraccountObject.email);

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return ret;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		insertPoint = view.findViewById(R.id.fragment_item_event_register_skill_group);

		if(mEventObject.isRequiredGroup) {
			for (SkillGroupDAO dao : mEventObject.skillGroups) {
				if(dao.name.isEmpty()) {
					continue;
				}
				NumberPicker picker = new NumberPicker(getActivity());
				picker.setName(dao.name);
				picker.setMaxValue(dao.requiredVolunteerNum - dao.currentVolunteerNum);
				picker.setTag(Long.toString(dao.id));
				insertPoint.addView(picker, 0, new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.fragment_item_event_register_tv_policy:
			new DialogFactory.TitleMessageButtonDialog(getActivity(),
					"微樂志工隱私權政策", getResources()
							.getString(R.string.twmf_privacy)).show();
			break;
		case R.id.fragment_item_event_register_btn_submit:
			EditText name = (EditText) v.getRootView().findViewById(
					R.id.fragment_item_event_register_et_name);
			EditText phone = (EditText) v.getRootView().findViewById(
					R.id.fragment_item_event_register_et_phone);
			EditText email = (EditText) v.getRootView().findViewById(
					R.id.fragment_item_event_register_et_email);
			EditText note = (EditText) v.getRootView().findViewById(
					R.id.fragment_item_event_register_et_note);
			CheckBox policyCheckBox = (CheckBox) v.getRootView().findViewById(
					R.id.fragment_item_event_register_cb_policy);

			// 確認資料是否輸入完整
			if (name.getText().toString().isEmpty()
					|| phone.getText().toString().isEmpty()
					|| email.getText().toString().isEmpty()) {
				DialogFactory.createMessageDialog(getActivity(), "請輸入聯絡資料",
						null).show();
				break;
			}

			if (!policyCheckBox.isChecked()) {
				DialogFactory.createMessageDialog(getActivity(), "請詳閱會員條款並勾選",
						null).show();
				break;
			}

			nameValuePairs.clear();

			if (mEventObject.isRequiredGroup) {
				for (View view : skillGroup) {
					CheckBox cb = (CheckBox) view
							.findViewById(R.id.event_detail_skill_list_item_cb);
					if (cb.isChecked()) {
						Long id = (Long) cb.getTag();
						nameValuePairs.add(new Pair<>(POST_KEY_SKILL_GROUP_ID, Long.toString(id)));
						break;
					}
				}
			}

			String skills = "";
			for (View view : skillGroup) {
				CheckBox cb = (CheckBox) view
						.findViewById(R.id.event_detail_skill_list_item_cb);
				if (cb.isChecked()) {
					TextView textView = (TextView) view
							.findViewById(R.id.event_detail_skill_list_item_tv);
					if (textView.getTag() == null) {
						skills += textView.getText().toString() + ",";
					} else {
						skills += (String) textView.getTag() + ",";
					}
				}
			}
			nameValuePairs.add(new Pair<>(POST_KEY_SKILL, skills));
			nameValuePairs.add(new Pair<>(POST_KEY_EVENT_ID, ""
					+ mEventObject.id));
			nameValuePairs.add(new Pair<>(POST_KEY_NAME, name
					.getText().toString()));
			nameValuePairs.add(new Pair<>(POST_KEY_PHONE, phone
					.getText().toString()));
			nameValuePairs.add(new Pair<>(POST_KEY_EMAIL, email
					.getText().toString()));



			int sum = 0;
			if(mEventObject.isRequiredGroup) {
				StringBuilder stringBuilder = new StringBuilder();
				for(int i = 0; i < insertPoint.getChildCount(); i++) {
					NumberPicker picker = (NumberPicker)insertPoint.getChildAt(i);
					if(i == 0) {
						stringBuilder.append(
								String.format(Locale.TAIWAN, "%s:%d",picker.getTag().toString(), picker.getValue())
						);
						sum += picker.getValue();
					} else {
						stringBuilder.append(
								String.format(Locale.TAIWAN, ",%s:%d",picker.getTag().toString(), picker.getValue())
						);
						sum += picker.getValue();
					}
				}
				nameValuePairs.add(new Pair<>(POST_KEY_SKILL_GROUP, stringBuilder.toString()));
				if(sum == 0) {
					DialogFactory.createMessageDialog(getActivity(), "請輸入正確數量",
							null).show();
					return;
				}
			}

			//FIH-add for "物資報名必須要發送企業代碼和員工編碼才能報名成功" start
			if (mEventObject.npo.isEnterprise){
				Log.d(TAG,"gengqiang->ItemEventRegiserFragment->isEnterprise YES");
				nameValuePairs.add(new Pair<>(
						POST_KEY_ENTERPRISE_SERIAL_NUMBER, "abcdefg"));
				nameValuePairs.add(new Pair<>(
						POST_KEY_EMPLOYEE_SERIAL_NUMBER, "55555"));
			} else {
				Log.d(TAG,"gengqiang->ItemEventRegiserFragment->isEnterprise NO");
			}
			//FIH-add for "物資報名必須要發送企業代碼和員工編碼才能報名成功" end
			
			nameValuePairs.add(new Pair<>(POST_KEY_NOTE, note.getText().toString()));

			new RegisterEventTask(getActivity(), this, nameValuePairs)
					.execute();
			break;
		}
	}

	public void nextPage() {
		new AlertDialog.Builder(getActivity())
				.setMessage("我們已將您的愛心告知受贈單位，接下來請您至報名信箱查看「捐贈通知信」。若有物資需求疑問、寄送問題或需索取捐物收據，請洽受贈單位，謝謝！")
				.setPositiveButton(android.R.string.ok, null)
				.show();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		showActionBarTabs();

	}
}

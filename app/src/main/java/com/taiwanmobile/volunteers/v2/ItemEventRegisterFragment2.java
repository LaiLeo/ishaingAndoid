/**
 * FIH add this instead of ItemEventRegisterFragment.java
 */
package com.taiwanmobile.volunteers.v2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v2.api.RegisterEventTask;
import com.taiwanmobile.volunteers.v2.database.Db;
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.database.SkillGroupDAO;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.dialog.UserSkillDialog;
import com.taiwanmobile.volunteers.v2.ui_component.NumberPicker;
import com.taiwanmobile.volunteers.v2.utils.CommonUtils;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;

import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.Pair;

public class ItemEventRegisterFragment2 extends SupportFragment implements
		OnClickListener, OnDateSetListener {
	final String TAG = getClass().getSimpleName();

	private EventDAO mEventObject;
	private View mRet;
	private Date mBirthDate = null;

	ViewGroup insertPoint;

	private static final String POST_KEY_EVENT_ID = "event_id";
	private static final String POST_KEY_NAME = "name";
	private static final String POST_KEY_PHONE = "phone";
	private static final String POST_KEY_EMAIL = "email";
	private static final String POST_KEY_BIRTHDAY = "birthday";
    private static final String POST_KEY_GUARDIAN_NAME = "guardian_name";
    private static final String POST_KEY_GUARDIAN_PHONE = "guardian_phone";
    private static final String POST_KEY_ENTERPRISE_SERIAL_NUMBER = "enterprise_serial_number";
    private static final String POST_KEY_EMPLOYEE_SERIAL_NUMBER = "employee_serial_number";
	private static final String POST_KEY_SKILL = "skill";
	private static final String POST_KEY_UID = "uid";
	private static final String POST_KEY_SKILL_GROUP_ID = "skillgroup";
	private static final String POST_KEY_SKILL_GROUP = "skillgroup_list";
	private static final String POST_KEY_NOTE = "note";

	List<Pair<String, String>> nameValuePairs = new ArrayList<>(10);
	List<View> skillGroup;

//	public VolunteerEventRegisterFragment(List<View> group, EventDAO event) {
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
		Log.d(TAG,"gengqiang->action_bar->ItemEventRegisterFragment2->onCreateView() ready to call hideActionBarTabs()");
		hideActionBarTabs();

		View ret = inflater.inflate(R.layout.fragment_item_event_register2,
				container, false);
		mRet = ret;

		ret.findViewById(R.id.fragment_event_register_btn_birth)
				.setOnClickListener(this);

		ret.findViewById(R.id.fragment_event_register_btn_submit)
				.setOnClickListener(this);

		ret.findViewById(R.id.fragment_event_register_tv_policy)
				.setOnClickListener(this);
		ret.findViewById(R.id.fragment_event_register_et_service_area)
				.setOnClickListener(this);
		ret.findViewById(R.id.fragment_event_register_et_service_item)
				.setOnClickListener(this);

		// bind data
		EditText name = (EditText) mRet
				.findViewById(R.id.fragment_event_register_et_name);
		EditText phone = (EditText) mRet
				.findViewById(R.id.fragment_event_register_et_phone);
		EditText email = (EditText) mRet
				.findViewById(R.id.fragment_event_register_et_email);
//		EditText uid = (EditText) mRet
//				.findViewById(R.id.fragment_event_register_et_uid);
		EditText guardianName = (EditText) mRet
				.findViewById(R.id.fragment_event_register_et_contact_name);
		EditText guardianPhone = (EditText) mRet
				.findViewById(R.id.fragment_event_register_et_contact_phone);
		Button birthdayButton = (Button) mRet
				.findViewById(R.id.fragment_event_register_btn_birth);


		EditText serviceArea = (EditText) mRet
				.findViewById(R.id.fragment_event_register_et_service_area);
		EditText serviceItem = (EditText) mRet
				.findViewById(R.id.fragment_event_register_et_service_item);

		// if (mEventObject.hasInsurance) {
		// uid.setVisibility(View.VISIBLE);
		// mRet.findViewById(R.id.fragment_event_register_tv_tip)
		// .setVisibility(View.VISIBLE);
		// }

        Log.d(TAG, "" + mEventObject.subject);

		try {
			UserAccountDAO useraccountObject = UserAccountDAO
					.queryObjectByMe(getActivity());

			name.setText(useraccountObject.displayName);
			phone.setText(useraccountObject.phone);
			email.setText(useraccountObject.email);
			guardianName.setText(useraccountObject.guardianName);
			guardianPhone.setText(useraccountObject.guardianPhone);
			serviceArea.setText(useraccountObject.interest);
			serviceItem.setText(useraccountObject.skills);

			Calendar cal = Calendar.getInstance();

			if (StringUtils.isNotBlank(useraccountObject.birthDay)) {
				cal.setTime(Db.DATE_FORMATTER.parse(useraccountObject.birthDay));
				mBirthDate = cal.getTime();
				birthdayButton.setText(cal.get(Calendar.YEAR) + "/"
						+ (cal.get(Calendar.MONTH) + 1) + "/"
						+ cal.get(Calendar.DATE));

				if (isUserAdult(cal)) {
					// user is over 18 years-old.
					mRet.findViewById(R.id.fragment_event_register_ll_contact)
							.setVisibility(View.GONE);
					mRet.findViewById(R.id.fragment_event_register_et_uid_parent)
							.setVisibility(View.GONE);//tea-test add
				} else {
					// user is under 18 years-old.
					mRet.findViewById(R.id.fragment_event_register_ll_contact)
							.setVisibility(View.VISIBLE);
					mRet.findViewById(R.id.fragment_event_register_et_uid_parent)
							.setVisibility(View.VISIBLE);//tea-test add
				}
			}

			//tea-test add start
			if (mEventObject.hasInsurance) {
				//Toast.makeText(getActivity(),"Has Insurance",Toast.LENGTH_SHORT).show();
				mRet.findViewById(R.id.fragment_event_register_et_uid_parent)
						.setVisibility(View.VISIBLE);//tea-test add
			} else {
				//Toast.makeText(getActivity(),"No Has Insurance",Toast.LENGTH_SHORT).show();
			}
			//tea-test add end

            if (mEventObject.npo.isEnterprise){
                mRet.findViewById(R.id.fragment_event_register_ll_enterprise)
                        .setVisibility(View.VISIBLE);
            }

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

		case R.id.fragment_event_register_btn_birth:
			DatePickerDialog dialog = new DatePickerDialog(getActivity(), this,
					1996, 1, 1);
			dialog.show();
			break;
		case R.id.fragment_event_register_tv_policy:
			new DialogFactory.TitleMessageButtonDialog(getActivity(),
					"???????????????????????????", getResources()
							.getString(R.string.twmf_privacy)).show();
			break;
		case R.id.fragment_event_register_btn_submit:
			EditText name = (EditText) v.getRootView().findViewById(
					R.id.fragment_event_register_et_name);
			EditText phone = (EditText) v.getRootView().findViewById(
					R.id.fragment_event_register_et_phone);
			EditText email = (EditText) v.getRootView().findViewById(
					R.id.fragment_event_register_et_email);
			 EditText uid = (EditText) v.getRootView().findViewById(
			 R.id.fragment_event_register_et_uid);
			EditText contactName = (EditText) v.getRootView().findViewById(
					R.id.fragment_event_register_et_contact_name);
			EditText contactPhone = (EditText) v.getRootView().findViewById(
					R.id.fragment_event_register_et_contact_phone);
			Button bitrhButton = (Button) v.getRootView().findViewById(
					R.id.fragment_event_register_btn_birth);
			CheckBox policyCheckBox = (CheckBox) v.getRootView().findViewById(
					R.id.fragment_event_register_cb_policy);

            EditText enterpriseSerialNumber = (EditText) v.getRootView().findViewById(
                    R.id.fragment_event_register_et_enterprise_serial_number);
            EditText employeeSerialNumber = (EditText) v.getRootView().findViewById(
                    R.id.fragment_event_register_et_employee_serial_number);


			EditText serviceAreaEditText = (EditText) v.getRootView().findViewById(
					R.id.fragment_event_register_et_service_area);
			EditText serviceItemEditText = (EditText) v.getRootView().findViewById(
					R.id.fragment_event_register_et_service_item);
			EditText note = (EditText) v.getRootView().findViewById(
					R.id.fragment_item_event_register_et_note);

			// ????????????????????????????????????
			if(name.getText().toString().isEmpty()) {
				DialogFactory.createMessageDialog(getActivity(), "???????????????",
						null).show();
				break;
			} else if(phone.getText().toString().isEmpty()) {
				DialogFactory.createMessageDialog(getActivity(), "???????????????/????????????",
						null).show();
				break;
			} else if(email.getText().toString().isEmpty()) {
				DialogFactory.createMessageDialog(getActivity(), "?????????????????????",
						null).show();
				break;
			} else if(this.mBirthDate == null) {
				bitrhButton.setTextColor(0xFFFF8C37);
				bitrhButton.setText("?????????????????????");
				DialogFactory.createMessageDialog(getActivity(), "?????????????????????",
						null).show();
				break;
			}
			// ???????????????
			if(v.getRootView()
					.findViewById(R.id.fragment_event_register_et_uid_parent)
					.getVisibility() == View.VISIBLE) {
				if (uid.getText().toString().isEmpty()) {
					DialogFactory.createMessageDialog(getActivity(), "????????????????????????",
							null).show();
					break;
				} else {
					if(!CommonUtils.isValidUIDNumber(uid.getText().toString())) {
						DialogFactory.createMessageDialog(getActivity(), "?????????????????????????????????",
								null).show();
						break;
					}
				}
			}
			// ???????????????
			if (v.getRootView()
					.findViewById(R.id.fragment_event_register_ll_contact)
					.getVisibility() == View.VISIBLE) {
				// user is under 18 years-old.
				if (contactName.getText().toString().isEmpty()) {
					DialogFactory.createMessageDialog(getActivity(), "????????????????????????",
							null).show();
					break;
				} else if(contactPhone.getText().toString().isEmpty()) {
					DialogFactory.createMessageDialog(getActivity(), "????????????????????????",
							null).show();
					break;
				}
			}

			if (mEventObject.npo.isEnterprise){
				if(enterpriseSerialNumber.getText().toString().isEmpty()){
					enterpriseSerialNumber.setHintTextColor(0xFFFF8C37);
					enterpriseSerialNumber.setHint("?????????????????????");
					break;
				}

				if(employeeSerialNumber.getText().toString().isEmpty()){
                    employeeSerialNumber.setHintTextColor(0xFFFF8C37);
                    employeeSerialNumber.setHint("?????????????????????");
					break;
				}

			}

			if (!policyCheckBox.isChecked()) {
				DialogFactory.createMessageDialog(getActivity(), "??????????????????????????????",
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
						nameValuePairs.add(new Pair<>(
								POST_KEY_SKILL_GROUP_ID, Long.toString(id)));
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
			// nameValuePairs.add(new BasicNameValuePair(POST_KEY_SKILL,
			// skills));
			// if (mEventObject.hasInsurance) {
			// nameValuePairs.add(new BasicNameValuePair(POST_KEY_UID, uid
			// .getText().toString()));
			// }

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
					DialogFactory.createMessageDialog(getActivity(), "?????????????????????",
							null).show();
					return;
				}
			}

            if (mEventObject.npo.isEnterprise){
				Log.d(TAG,"gengqiang->ItemEventRegisterFragment2->isEnterprise YES");
                nameValuePairs.add(new Pair<>(
                        POST_KEY_ENTERPRISE_SERIAL_NUMBER, enterpriseSerialNumber.getText()
                        .toString()));
                nameValuePairs.add(new Pair<>(
                        POST_KEY_EMPLOYEE_SERIAL_NUMBER, employeeSerialNumber.getText()
                        .toString()));
            } else {//tea-test add this just for debug
				Log.d(TAG,"gengqiang->ItemEventRegisterFragment2->isEnterprise NO");
			}

			nameValuePairs.add(new Pair<>(
					UserAccountDAO.POST_KEY_SERVICE_AREA,serviceAreaEditText.getText()
					.toString()));
			nameValuePairs.add(new Pair<>(
					UserAccountDAO.POST_KEY_SERVICE_ITEM,serviceItemEditText.getText()
					.toString()));
			nameValuePairs.add(new Pair<>(POST_KEY_NOTE, note.getText().toString()));

			if (v.getRootView()
					.findViewById(R.id.fragment_event_register_ll_contact)
					.getVisibility() == View.VISIBLE) {
				// user is under 18 years-old.
				nameValuePairs.add(new Pair<>(POST_KEY_EVENT_ID, ""
						+ mEventObject.id));
				nameValuePairs.add(new Pair<>(POST_KEY_NAME, name
						.getText().toString()));
				nameValuePairs.add(new Pair<>(POST_KEY_PHONE, phone
						.getText().toString()));
				nameValuePairs.add(new Pair<>(POST_KEY_EMAIL, email
						.getText().toString()));
				nameValuePairs.add(new Pair<>(POST_KEY_BIRTHDAY, ""
						+ mBirthDate.getTime()));
				nameValuePairs.add(new Pair<>(
						POST_KEY_GUARDIAN_NAME, contactName.getText()
								.toString()));
				nameValuePairs.add(new Pair<>(
						POST_KEY_GUARDIAN_PHONE, contactPhone.getText()
								.toString()));

				new RegisterEventTask(getActivity(), this, nameValuePairs)
						.execute();
			} else {
				// user is over 18 years-old.
				nameValuePairs.add(new Pair<>(POST_KEY_EVENT_ID, ""
						+ mEventObject.id));
				nameValuePairs.add(new Pair<>(POST_KEY_NAME, name
						.getText().toString()));
				nameValuePairs.add(new Pair<>(POST_KEY_PHONE, phone
						.getText().toString()));
				nameValuePairs.add(new Pair<>(POST_KEY_EMAIL, email
						.getText().toString()));
				nameValuePairs.add(new Pair<>(POST_KEY_BIRTHDAY, ""
						+ mBirthDate.getTime()));

				new RegisterEventTask(getActivity(), this, nameValuePairs)
						.execute();
			}
			break;
			case R.id.fragment_event_register_et_service_area:
				onServiceAreaDidClick();
				break;
			case R.id.fragment_event_register_et_service_item:
				onServiceItemDidClick();
				break;
		}
	}

	private void onServiceAreaDidClick() {
		String[] mapping = getResources().getStringArray(R.array.county_mapping);
		if(getView() == null){
			return;
		}
		TextView tv = (TextView) getView().findViewById(R.id.fragment_event_register_et_service_area);
		onSkillSelectionDidClick(mapping, "???????????????", tv);
	}

	private void onServiceItemDidClick() {
		String[] mapping = getResources().getStringArray(R.array.service_item_type);
		if(getView() == null){
			return;
		}
		TextView tv = (TextView) getView().findViewById(R.id.fragment_event_register_et_service_item);
		onSkillSelectionDidClick(mapping, "???????????????", tv);
	}

	private void onSkillSelectionDidClick(@NonNull String[] mapping, @NonNull String title, @NonNull final TextView textView){
    	Log.d(TAG, "onServiceAreaDidClick");

//		TextView tv = (TextView) getView().findViewById(R.id.fragment_event_register_et_service_area);
		UserSkillDialog dialog = new UserSkillDialog(getActivity(), title,
				textView.getText().toString().split(","), mapping);
		dialog.setSubTitle("??????????????????");
		dialog.setSelectionLimit(3);
		dialog.setSelectable(true);
		dialog.setOnCompleteListener(new UserSkillDialog.OnCompleteListener() {
			@Override
			public void onComplete(DialogInterface dialog, String[] allItems, boolean[] selectedItems) {
				if(getView() == null){
					return;
				}
//				TextView tv = (TextView) getView().findViewById(R.id.fragment_event_register_et_service_area);
				StringBuilder list = new StringBuilder();
				boolean first = true;
				for(int i = 0; i < allItems.length; i++){
					if(!selectedItems[i]){
						continue;
					}
					if(first){
						first = false;
					}else{
						list.append(", ");
					}
					list.append(allItems[i].trim());
				}
				textView.setText(list);
				dialog.dismiss();
			}
		});
		dialog.show();

	}

	public void nextPage() {
		new AlertDialog.Builder(getActivity())
				.setMessage("?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????")
				.setPositiveButton(android.R.string.ok, null)
				.show();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG,"gengqiang->action_bar->ItemEventRegisterFragment2->onDestroyView() ready to call showActionBarTabs()");
		showActionBarTabs();
	}

	@Override
	public void onDateSet(DatePicker view, int selectedYear, int selectedMonth,
			int selectedDay) {
		Button birthBtn = (Button) mRet
				.findViewById(R.id.fragment_event_register_btn_birth);

		// android's month from date picker is zero-based.
		birthBtn.setText(selectedYear + "???" + (selectedMonth + 1) + "???"
				+ selectedDay + "???");

		Calendar cal = Calendar.getInstance();
		cal.set(selectedYear, selectedMonth, selectedDay);
		this.mBirthDate = cal.getTime();

		if (isUserAdult(cal)) {
			// user is over 18 years-old.
			mRet.findViewById(R.id.fragment_event_register_ll_contact)
					.setVisibility(View.GONE);
			mRet.findViewById(R.id.fragment_event_register_et_uid_parent)
					.setVisibility(View.GONE);//tea-test add
		} else {
			// user is under 18 years-old.
			mRet.findViewById(R.id.fragment_event_register_ll_contact)
					.setVisibility(View.VISIBLE);
			mRet.findViewById(R.id.fragment_event_register_et_uid_parent)
					.setVisibility(View.VISIBLE);//tea-test add
		}

		//tea-test add start
		if (mEventObject.hasInsurance) {
			//Toast.makeText(getActivity(),"Has Insurance",Toast.LENGTH_SHORT).show();
			mRet.findViewById(R.id.fragment_event_register_et_uid_parent)
					.setVisibility(View.VISIBLE);//tea-test add
		} else {
			//Toast.makeText(getActivity(),"No Has Insurance",Toast.LENGTH_SHORT).show();
		}
		//tea-test add end
	}

	private boolean isUserAdult(Calendar cal) {
		Calendar adultCal = Calendar.getInstance();
		adultCal.set(cal.get(Calendar.YEAR) + 20, cal.get(Calendar.MONTH) + 1,
				cal.get(Calendar.DATE));//tea-test modify
		Date currentDate = new Date();

		if (currentDate.before(adultCal.getTime())) {
			// user is under 18 years-old.
			return false;
		} else {
			// user is over 18 years-old.
			return true;
		}
	}
}

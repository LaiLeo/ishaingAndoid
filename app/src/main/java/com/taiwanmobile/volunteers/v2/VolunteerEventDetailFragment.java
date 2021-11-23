package com.taiwanmobile.volunteers.v2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Fragment;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.util.StaticResUtil;
import com.taiwanmobile.volunteers.v2.api.FocusEventTask;
import com.taiwanmobile.volunteers.v1.api.V1JoinEventTask;
import com.taiwanmobile.volunteers.v1.api.V1LeaveEventTask;
import com.taiwanmobile.volunteers.v2.api.TTVolunteerQueryRegisterTask;
import com.taiwanmobile.volunteers.v2.api.UnRegisterEventTask;
import com.taiwanmobile.volunteers.v2.api.UnfocusEventTask;
import com.taiwanmobile.volunteers.v2.database.CooperationNpoDAO;
import com.taiwanmobile.volunteers.v2.database.DatabaseHelper;
import com.taiwanmobile.volunteers.v2.database.Db;
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.database.FocusedEventDAO;
import com.taiwanmobile.volunteers.v2.database.NpoDAO;
import com.taiwanmobile.volunteers.v2.database.RegisteredEventDAO;
import com.taiwanmobile.volunteers.v2.database.SkillGroupDAO;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.loginview.LoginFragment;
import com.taiwanmobile.volunteers.v2.npoview.NpoDetailFragment;
import com.taiwanmobile.volunteers.v2.tasks.CheckUserPositionTask;
import com.taiwanmobile.volunteers.v2.utils.AdapterBuilder;
import com.taiwanmobile.volunteers.v2.utils.AdapterBuilder.StringBinder;
import com.taiwanmobile.volunteers.v2.utils.CommonUtils;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;
import com.taiwanmobile.volunteers.v2.utils.ViewHelper;

import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VolunteerEventDetailFragment extends SupportFragment implements
		OnClickListener, OnRatingBarChangeListener, OnCheckedChangeListener {
	final String TAG = getClass().getSimpleName();

	final int imageViewId = 55665566;

	private long mEventId;
	public static Fragment mEventDetailFragment;
	private Cursor mCursor;
	private EventDAO mEventObject;

	private TextView mTvRating;
	public List<View> skillGroup = new ArrayList<View>();

	boolean canRegister = true;

	enum UserState {
		FULL, EXPIRED, CAN_JOIN, CAN_REGISTER, JOINED, NEEDLEAVE
	}

	UserState mUserState = UserState.CAN_REGISTER;

	public VolunteerEventDetailFragment setEventId(long id) {
		mEventId = id;
		mEventDetailFragment = this;

		mCursor = EventDAO.queryCursorById(mEventId);
		try {
			mEventObject = EventDAO.getObjectById(mEventId);
		} catch (SQLException e) {
			Log.e(TAG, "no such event id");
			mEventObject = null;
		}

		return this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		hideActionBarTabs();

		SimpleCursorAdapter binder = new AdapterBuilder()
				.set(this)
				.set(R.layout.fragment_event_detail)
				.set(mCursor)
				.bind(EventDAO.DATABASE_COLUMN_IMAGE,
						R.id.fragment_event_detail_iv_image,
						new StringBinder() {

							@Override
							public void bind(View v, String val) {
								if (val.length() > 4) {
									v.setTag(val);
									v.setOnClickListener((OnClickListener) mEventDetailFragment);
								}
								Picasso.with(getActivity())
										//.load(BackendContract.DEPLOYMENT_STATIC_URL
												//+ val).into((ImageView) v);
										.load(StaticResUtil.checkUrl(val, true, TAG)).into((ImageView) v);
							}
						})
				.bind(EventDAO.DATABASE_COLUMN_SUBJECT,
						R.id.fragment_event_detail_tv_subject)
				.bind(EventDAO.DATABASE_COLUMN_NPO_ID,
						R.id.fragment_event_detail_tv_npo_name,
						new StringBinder() {

							@Override
							public void bind(View v, String val) {
								TextView tv = (TextView) v;
								tv.setText(mEventObject.getNpoName());
							}
						})
				.bind(EventDAO.DATABASE_COLUMN_HAPPEN_DATE,
						R.id.fragment_event_detail_tv_time, new StringBinder() {

							@Override
							public void bind(View v, String val) {
								try {
									TextView tv = (TextView) v;

									Calendar cal = Calendar.getInstance();
									cal.setTime(DatabaseHelper.DATETIME_FORMATTER
											.parse(val));

									tv.setText(String.format(Locale.TAIWAN, "%d/%d/%d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE)));
									// set countdown
									tv = (TextView) v
											.getRootView()
											.findViewById(
													R.id.fragment_event_detail_tv_time_countdown);
									Date today = Calendar.getInstance()
											.getTime();
									Date date = DatabaseHelper.DATETIME_FORMATTER
											.parse(val);
									int numberOfHours = (int) ((date.getTime() - today
											.getTime()) / (3600 * 1000));
									if (numberOfHours < 24
											&& numberOfHours >= 0) {
										tv.setText(String.format("倒數%s小時", String.valueOf(numberOfHours)));
									} else if (numberOfHours >= 24) {
										numberOfHours = numberOfHours / 24;
										tv.setText(String.format("倒數%s天", String.valueOf(numberOfHours)));
									}
								} catch (ParseException e) {
									e.printStackTrace();
								}
							}
						})
				.bind(EventDAO.DATABASE_COLUMN_REGISTER_DEADLINE_DATE,
						R.id.fragment_event_detail_tv_time_register,
						new StringBinder() {

							@Override
							public void bind(View v, String val) {
								try {
									TextView tv = (TextView) v;

									Calendar cal = Calendar.getInstance();
									cal.setTime(DatabaseHelper.DATETIME_FORMATTER
											.parse(val));
									tv.setText(String.format(Locale.TAIWAN, "%d/%d/%d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE)));
									// set countdown
									tv = (TextView) v
											.getRootView()
											.findViewById(
													R.id.fragment_event_detail_tv_time_countdown_register);
									Date today = Calendar.getInstance()
											.getTime();
									Date date = DatabaseHelper.DATETIME_FORMATTER
											.parse(val);
									int numberOfHours = (int) ((date.getTime() - today
											.getTime()) / (3600 * 1000));
									if (numberOfHours < 24
											&& numberOfHours >= 0) {
										tv.setText(String.format("倒數%s小時", String.valueOf(numberOfHours)));
									} else if (numberOfHours >= 24) {
										numberOfHours = numberOfHours / 24;
										tv.setText(String.format("倒數%s天", String.valueOf(numberOfHours)));
									}
								} catch (ParseException e) {
									e.printStackTrace();
								}
							}
						})
				.bind(EventDAO.DATABASE_COLUMN_ADDRESS_CITY,
						R.id.fragment_event_detail_tv_address_city,
						new StringBinder() {//FIH-add for 活動詳情頁地址沒有顯示全
							@Override
							public void bind(View v, String val) {
								TextView tv = (TextView) v;
								Log.d(TAG,"gengqiang->address_test->fragment_event_detail_tv_address_city= "+mEventObject.addressCity);
								tv.setText(val);
								tv.setVisibility(View.GONE);
							}
						})
				.bind(EventDAO.DATABASE_COLUMN_ADDRESS,
						R.id.fragment_event_detail_tv_address,
						new StringBinder() {//FIH-add for 活動詳情頁地址沒有顯示全
							@Override
							public void bind(View v, String val) {
								TextView tv = (TextView) v;
								Log.d(TAG,"gengqiang->address_test->fragment_event_detail_tv_address= "+mEventObject.address);
								tv.setText(mEventObject.addressCity + mEventObject.address);
							}
						})
				.bind(EventDAO.DATABASE_COLUMN_EVENT_HOUR,
						R.id.fragment_event_detail_tv_event_hour,
						new StringBinder() {

							@Override
							public void bind(View v, String val) {
								TextView tv = (TextView) v;
								tv.setText(String.format("服務 %s 小時", val));
							}
						})
				.bind(EventDAO.DATABASE_COLUMN_DESCRIPTION,
						R.id.fragment_event_detail_tv_description,
						new StringBinder() {

							@Override
							public void bind(View v, String val) {
								TextView tv = (TextView) v;
								tv.setText(val);
								tv.setMovementMethod(LinkMovementMethod
										.getInstance());
							}
						})
				.bind(EventDAO.DATABASE_COLUMN_TOTAL_RATING_SCORE,
						R.id.fragment_event_detail_rb_rating,
						new StringBinder() {

							@Override
							public void bind(View v, String val) {
								RatingBar rb = (RatingBar) v;
								rb.setFocusable(false);
								rb.setOnTouchListener(new OnTouchListener() {
									@Override
									public boolean onTouch(View v,
											MotionEvent event) {
										return true;
									}
								});
								Float score = mEventObject.getAverageScore();
								rb.setRating(score);

								TextView tv = (TextView) rb
										.getRootView()
										.findViewById(
												R.id.fragment_event_detail_tv_rating_score);
								tv.setText(String.format("%s分", val));
							}
						})
				.bind(EventDAO.DATABASE_COLUMN_RATING_USER_NUM,
						R.id.fragment_event_detail_tv_rating_num)
				.bind(EventDAO.DATABASE_COLUMN_ID,
						R.id.fragment_event_detail_bt_focus,
						new StringBinder() {
							@Override
							public void bind(View v, String val) {
								Button b = (Button) v;
								long eventId = mEventObject.id;
								b.setTag(eventId);
								try {
									Boolean isRegistered = RegisteredEventDAO
											.isUserRegistered(eventId);
									Boolean isFocused = FocusedEventDAO
											.isUserFocused(eventId);
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
							}
						})
//                .bind(EventDAO.DATABASE_COLUMN_COOPERATION_NPO,
//                        R.id.fragment_event_detail_tv_cooperation_npo)
				.build();

		// View view = FragHelper.addViewInFragmentBase(getActivity(), inflater,
		// binder.getView(0, null, null));
		View view = binder.getView(0, null, null);

		view.findViewById(R.id.fragment_event_detail_tv_npo_name).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NpoDAO obj = mEventObject.npo;
				if(obj == null) return;

				try {
					FragHelper.replace(getActivity(),
							new NpoDetailFragment().setNpoId(obj.id));
				} catch (Exception e ) {

				}
			}
		});

		view.findViewById(R.id.fragment_event_detail_tv_address)
				.setOnClickListener(this);
		view.findViewById(R.id.fragment_event_detail_tv_address).setTag(
				view.findViewById(R.id.fragment_event_detail_tv_address_city));
		view.findViewById(R.id.fragment_event_detail_bt_focus)
				.setOnClickListener(this);
		view.findViewById(R.id.fragment_event_detail_iv_switch)
				.setOnClickListener(this);

		TextView scoreTextView = (TextView) view
				.findViewById(R.id.fragment_event_detail_tv_rating_num);
		int num = Integer.valueOf(scoreTextView.getText().toString());
		if (num == 0) {
			TextView tv2 = (TextView) view
					.findViewById(R.id.fragment_event_detail_tv_rating_score);
			tv2.setText("無評分");
		}

		// set required volunteer number
		int currentVolunteerNumber = mEventObject.currentVolunteerNum;
		int totalVolunteerNumber = mEventObject.requiredVolunteerNum;
		TextView volunteerTextView = (TextView) view
				.findViewById(R.id.fragment_event_detail_tv_volunteer_number);
		if (totalVolunteerNumber == 0) {
			volunteerTextView.setText("報名無上限");
		} else if (currentVolunteerNumber >= totalVolunteerNumber) {
			volunteerTextView.setText("已額滿");
			canRegister = false;
		} else {
            String volunteerText = String.format(Locale.TAIWAN, "尚缺%d名 / 共需%d名",
                    totalVolunteerNumber - currentVolunteerNumber,
                    totalVolunteerNumber);
			volunteerTextView.setText(volunteerText);
		}

		// set insurance icon
		if (!mEventObject.hasInsurance) {
			view.findViewById(R.id.fragment_event_detail_iv_isneedensurance)
					.setVisibility(View.GONE);
		}

		// set training icon
		if (!mEventObject.hasTraining && !mEventObject.hasInsurance) {
			view.findViewById(R.id.fragment_event_detail_btn_isneedtraining)
					.setVisibility(View.GONE);
		}

		if (!MyPreferenceManager.hasCredentials(getActivity())) {
			view.findViewById(R.id.fragment_event_detail_bt_focus)
					.setVisibility(View.GONE);
		} else {
			// set user state
			try {
				if ((RegisteredEventDAO.isUserJoined(mEventId) && !mEventObject.requiredSignout)
						|| (RegisteredEventDAO.isUserJoined(mEventId)
								&& mEventObject.requiredSignout && RegisteredEventDAO
									.isUserSignouted(mEventId))) {
					mUserState = UserState.JOINED;
					Button actionButton = (Button) view
							.findViewById(R.id.fragment_event_detail_btn_action);
					actionButton.setText("已參與");
					// show 評分按鈕 in 評分 layout
					view.findViewById(R.id.fragment_event_detail_rl_rating)
							.setVisibility(View.VISIBLE);
					TextView tv = (TextView) view
							.findViewById(R.id.fragment_event_detail_tv_2);
					tv.setText("我要評分");
					tv.setOnClickListener(this);
					return ViewHelper
							.setListener(ViewHelper.setListener(view, this,
									ImageView.class), this, Button.class);
				} else if (RegisteredEventDAO.isUserJoined(mEventId)
						&& !RegisteredEventDAO.isUserSignouted(mEventId)
						&& mEventObject.requiredSignout) {
					mUserState = UserState.NEEDLEAVE;
					Button actionButton = (Button) view
							.findViewById(R.id.fragment_event_detail_btn_action);
					actionButton.setText("我要簽退");
//					actionButton.setTextColor(0xFFFEFEFE);
//					actionButton.setBackground(new ColorDrawable(0xFFFF8C37));
					// show 評分按鈕 in 評分 layout
					view.findViewById(R.id.fragment_event_detail_rl_rating)
							.setVisibility(View.VISIBLE);
					TextView tv = (TextView) view
							.findViewById(R.id.fragment_event_detail_tv_2);
					tv.setText("我要評分");
					tv.setOnClickListener(this);
					return ViewHelper
							.setListener(ViewHelper.setListener(view, this,
									ImageView.class), this, Button.class);
				}
			} catch (Exception ex) {
				Log.e(TAG, Log.getStackTraceString(ex));
			}
		}

		try {
			if (RegisteredEventDAO.isUserRegistered(mEventId)) {
				mUserState = UserState.CAN_JOIN;
				view.findViewById(R.id.fragment_event_detail_btn_action)
						.setVisibility(View.GONE);
				view.findViewById(R.id.fragment_event_detail_btn_join)
						.setVisibility(View.VISIBLE);
				view.findViewById(R.id.fragment_event_detail_btn_join)
						.setOnClickListener(this);
				view.findViewById(R.id.fragment_event_detail_btn_cancel)
						.setVisibility(View.VISIBLE);
				view.findViewById(R.id.fragment_event_detail_btn_cancel)
						.setOnClickListener(this);
				if(mEventObject.isTTVolunteerEvent()){
					view.findViewById(R.id.fragment_event_detail_btn_cancel)
							.setVisibility(View.GONE);
					TextView btn = view.findViewById(R.id.fragment_event_detail_tv_join);
					btn.setText("確認狀態中...");
					view.findViewById(R.id.fragment_event_detail_btn_join)
							.setOnClickListener(null);
					new TTVolunteerQueryRegisterTask(getActivity(), this, ""+ mEventObject.id).execute();

				}

				return ViewHelper.setListener(
						ViewHelper.setListener(view, this, ImageView.class),
						this, Button.class);
			}
		} catch (Exception ex) {
			Log.e(TAG, Log.getStackTraceString(ex));
		}

		try {
			Date date = Db.DATETIME_FORMATTER
					.parse(mEventObject.registerDeadlineDate);
			int between = Calendar.getInstance().getTime().compareTo(date);
			if (between > 0) {
				mUserState = UserState.EXPIRED;
				Button actionButton = (Button) view
						.findViewById(R.id.fragment_event_detail_btn_action);
				actionButton.setText("報名已截止");
				// show 評分 layout
				view.findViewById(R.id.fragment_event_detail_rl_rating)
						.setVisibility(View.VISIBLE);
				view.findViewById(R.id.fragment_event_detail_view_line)
						.setVisibility(View.VISIBLE);

				return ViewHelper.setListener(
						ViewHelper.setListener(view, this, ImageView.class),
						this, Button.class);
			}
		} catch (ParseException e) {
			Log.e(TAG, "detail event issue");
		}

		if (!canRegister) {
			mUserState = UserState.FULL;
			Button actionButton = (Button) view
					.findViewById(R.id.fragment_event_detail_btn_action);
			actionButton.setText("報名已滿");

			return ViewHelper.setListener(
					ViewHelper.setListener(view, this, ImageView.class), this,
					Button.class);
		}

		// default: 可報名

		// set skills view
		LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup insertPoint = (ViewGroup) view
				.findViewById(R.id.fragment_event_detail_ll_skill);
		skillGroup.clear();
		TextView SkillTipTextView = (TextView) view
				.findViewById(R.id.fragment_event_detail_tv_tags_label);
		Boolean showSkillTip = false;
		if (!mEventObject.isRequiredGroup) { // no skill group
			String skills = mEventObject.skillDescription;
			String[] values = skills.split(",");
			SkillTipTextView.setText("請選擇您具備的技能");
            for (String value : values) {
                if (value.length() > 0) {
                    View ll = vi.inflate(R.layout.event_detail_skill_list_item,
                            null);
                    TextView textView = (TextView) ll
                            .findViewById(R.id.event_detail_skill_list_item_tv);
                    CheckBox cb = (CheckBox) ll
                            .findViewById(R.id.event_detail_skill_list_item_cb);
                    ((ImageView) ll
                            .findViewById(R.id.event_detail_skill_list_item_iv))
                            .setVisibility(View.GONE);
                    ((TextView) ll
                            .findViewById(R.id.event_detail_skill_list_item_tv_number))
                            .setVisibility(View.GONE);
                    skillGroup.add(ll);
                    showSkillTip = true;
                    cb.setChecked(false);
                    textView.setText(value);

                    insertPoint.addView(ll, 0, new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                }
            }
		} else {
			SkillTipTextView.setText("請選擇您要參加的組別");
			for (SkillGroupDAO dao : mEventObject.skillGroups) {
				View ll = vi.inflate(R.layout.event_detail_skill_list_item,
						null);
				TextView textView = (TextView) ll
						.findViewById(R.id.event_detail_skill_list_item_tv);
				CheckBox cb = (CheckBox) ll
						.findViewById(R.id.event_detail_skill_list_item_cb);
				ImageView iv = (ImageView) ll
						.findViewById(R.id.event_detail_skill_list_item_iv);
				TextView groupNumberTextView = (TextView) ll
						.findViewById(R.id.event_detail_skill_list_item_tv_number);
				skillGroup.add(ll);
				showSkillTip = true;
				cb.setChecked(false);
				cb.setTag(dao.id);
				cb.setOnCheckedChangeListener(this);
				iv.setVisibility(View.VISIBLE);
				iv.setTag(textView);
				iv.setOnClickListener(this);
				textView.setTag(dao.description);
				textView.setText(dao.name);
				if (dao.requiredVolunteerNum == 0) {
					groupNumberTextView.setText("無上限");
				} else if (dao.requiredVolunteerNum - dao.currentVolunteerNum == 0) {
					cb.setClickable(false);
					groupNumberTextView.setText("已額滿");
				} else {
					groupNumberTextView.setText(String.format(Locale.TAIWAN, "剩餘 %d 人", dao.requiredVolunteerNum
                            - dao.currentVolunteerNum));
				}

				insertPoint.addView(ll, 0, new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));
			}
		}
		if (mEventObject.hasTraining || showSkillTip) {
			view.findViewById(R.id.fragment_event_detail_rl_skill)
					.setVisibility(View.VISIBLE);
			view.findViewById(R.id.fragment_event_detail_view_separation_line_3)
					.setVisibility(View.VISIBLE);
		}
		if (showSkillTip == false) {
			SkillTipTextView.setVisibility(View.GONE);
			view.findViewById(R.id.fragment_event_detail_view_separation_line_3)
					.setVisibility(View.GONE);

		}

		Button actionButton = (Button) view
				.findViewById(R.id.fragment_event_detail_btn_action);
		actionButton.setText("報名");
//		actionButton.setTextColor(0xFFFEFEFE);
//		actionButton.setBackground(new ColorDrawable(0xFFFF8C37));
        String cooperationNpoString = "";
        Boolean cooperationNpoStringFirst = true;
        for(CooperationNpoDAO dao : mEventObject.cooperationNpo){
            if(cooperationNpoStringFirst){
                cooperationNpoStringFirst = false;
            }else{
                cooperationNpoString  = cooperationNpoString + "、";
            }
            cooperationNpoString = cooperationNpoString + dao.name;
        }
        if(mEventObject.cooperationNpo.size() == 0 || TextUtils.isEmpty(cooperationNpoString)){
            view.findViewById(R.id.cooperation_npo_block).setVisibility(View.GONE);
        }else {
            ((TextView) view.findViewById(R.id.fragment_event_detail_tv_cooperation_npo)).setText(cooperationNpoString);
        }



		return ViewHelper.setListener(
				ViewHelper.setListener(view, this, ImageView.class), this,
				Button.class);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_event_detail_bt_focus:
			Button b = (Button) v;
			Drawable subscribed = v.getResources().getDrawable(
					R.drawable.ic_subscribed);
			Drawable nonsubscribed = v.getResources().getDrawable(
					R.drawable.ic_nonsubscribed);
			if (b.getBackground().getConstantState()
					.equals(subscribed.getConstantState())) {
				new UnfocusEventTask(getActivity(), mEventId, b).execute();
			} else if (b.getBackground().getConstantState()
					.equals(nonsubscribed.getConstantState())) {
				new FocusEventTask(getActivity(), mEventId, b).execute();
			}
			break;
		case R.id.fragment_event_detail_ib_share:
			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
			shareIntent.setType("text/plain");

			String subject = "";
			if (MyPreferenceManager.hasCredentials(getActivity())) {
				try {
					UserAccountDAO useraccountObject = UserAccountDAO
							.queryObjectByMe(getActivity());
					subject = useraccountObject.displayName;
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			subject = subject + "與你分享<微樂志工> " + mEventObject.subject;

			String content = BackendContract.EVENT_SHARING_URL
					+ String.valueOf(mEventId) + "/\n" + mEventObject.subject
					+ "\n" + "舉辦: " + mEventObject.npo.name + "\n" + "報名截止: "
					+ mEventObject.registerDeadlineDate + "\n" + "活動開始: "
					+ mEventObject.happenDate + "\n" + "活動結束: "
					+ mEventObject.closeDate + "\n" + "活動地點: "
					+ mEventObject.addressCity + mEventObject.address + "\n"
					+ "服務" + mEventObject.eventHour + "小時" + "\n\n"
					+ "- 分享自微樂志工" + "\n" + "https://www.isharing.tw";

			List<Intent> targetedShareIntents = new ArrayList<Intent>();
			List<ResolveInfo> resInfo = getActivity().getPackageManager()
					.queryIntentActivities(shareIntent, 0);
			if (!resInfo.isEmpty()) {
				for (ResolveInfo resolveInfo : resInfo) {
					String packageName = resolveInfo.activityInfo.packageName;

					if (StringUtils
							.equals(packageName, "com.google.android.gm")) {
						Intent targetedShareIntent = new Intent(
								android.content.Intent.ACTION_SEND);
						targetedShareIntent.setType("text/plain");

						targetedShareIntent.putExtra(
								android.content.Intent.EXTRA_SUBJECT, subject);

						targetedShareIntent.putExtra(
								android.content.Intent.EXTRA_TEXT, content);

						targetedShareIntent.setPackage(packageName);
						targetedShareIntents.add(targetedShareIntent);
					} else if (StringUtils.equals(packageName,
							"com.facebook.katana")) {
						Intent targetedShareIntent = new Intent(
								android.content.Intent.ACTION_SEND);
						targetedShareIntent.setType("text/plain");
						targetedShareIntent.putExtra(
								android.content.Intent.EXTRA_TEXT,
								BackendContract.EVENT_SHARING_URL
										+ String.valueOf(mEventId) + "/");

						targetedShareIntent.setPackage(packageName);
						targetedShareIntents.add(targetedShareIntent);
					} else if (StringUtils.equals(packageName,
							"jp.naver.line.android")) {
						Intent targetedShareIntent = new Intent(
								android.content.Intent.ACTION_SEND);
						targetedShareIntent.setType("text/plain");
						targetedShareIntent.putExtra(
								android.content.Intent.EXTRA_TEXT, content);

						targetedShareIntent.setPackage(packageName);
						// line sharing will cause app crash
						targetedShareIntents.add(targetedShareIntent);
					} else {
						Intent targetedShareIntent = new Intent(
								android.content.Intent.ACTION_SEND);
						targetedShareIntent.setType("text/plain");
						targetedShareIntent.putExtra(
								android.content.Intent.EXTRA_SUBJECT, subject);
						targetedShareIntent.putExtra(
								android.content.Intent.EXTRA_TEXT, content);
						targetedShareIntent.setPackage(packageName);
						targetedShareIntents.add(targetedShareIntent);
					}
				}
				Intent chooserIntent = Intent.createChooser(
						targetedShareIntents.remove(0), "選擇分享的App");

				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
						targetedShareIntents.toArray(new Parcelable[] {}));

				startActivity(chooserIntent);
			}
			break;
		case R.id.fragment_event_detail_btn_action:
			if (mUserState == UserState.CAN_REGISTER) {
				if (!MyPreferenceManager.hasCredentials(getActivity())) {
					FragHelper.replace(getActivity(),
							new LoginFragment().restartOnLogined());
					break;
				}
				if (mEventObject.isRequiredGroup) {
					Boolean checked = false;
					for (View view : skillGroup) {
						CheckBox cbox = (CheckBox) view
								.findViewById(R.id.event_detail_skill_list_item_cb);
						if (cbox.isChecked()) {
							checked = true;
						}
					}
					if (!checked) {
						new DialogFactory.TitleMessageDialog(getActivity(),
								"請選擇組別", "請選擇您要報名的組別，謝謝。").show();
						break;
					}
				}
				// TODO: check
                VolunteerEventRegisterFragment volunteerEventRegisterFragment
                        = new VolunteerEventRegisterFragment();
                volunteerEventRegisterFragment.setSkillGroup(skillGroup);
                volunteerEventRegisterFragment.setEventObject(mEventObject);
				FragHelper.replace(getActivity(),
                        volunteerEventRegisterFragment);
				break;
			} else if (mUserState == UserState.NEEDLEAVE) {
//				Intent intent = new Intent(
//						"com.google.zxing.client.android.SCAN");
//				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
//				startActivityForResult(intent, 0);

				CommonUtils.startQRScanner(this);
			}
			break;
		case R.id.fragment_event_detail_iv_join:
		case R.id.fragment_event_detail_btn_join:
			new CheckUserPositionTask(getActivity(), this, mEventObject)
					.execute();
			break;
		case R.id.fragment_event_detail_iv_cancel:
		case R.id.fragment_event_detail_btn_cancel:
			AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
			builder.setTitle("是否確認取消");
			builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					new UnRegisterEventTask(getActivity(), VolunteerEventDetailFragment.this, mEventId, "已取消報名","").execute();
				}
			});
			builder.setNegativeButton("否", null);
			builder.show();
			break;
		case R.id.fragment_event_detail_iv_isneedensurance:
			// if (StringUtils.isNotEmpty(mEventObject.insuranceContent)) {
			// new DialogFactory.TitleMessageButtonDialog(getActivity(),
			// "保險內容", mEventObject.insuranceContent).show();
			// }
			break;
		case R.id.fragment_event_detail_btn_isneedtraining:
			String dialogContent = mEventObject.trainingContent;
			if (StringUtils.isNotBlank(mEventObject.insuranceContent)) {
				dialogContent += "<br /><br />志工保險內容<br />"
						+ mEventObject.insuranceContent;
			}

			new DialogFactory.TitleMessageButtonDialog(getActivity(), "志工須知內容",
					dialogContent).show();
			// if (StringUtils.isNotBlank(dialogContent)) {
			// new DialogFactory.TitleMessageButtonDialog(getActivity(),
			// "志工訓練內容", dialogContent).show();
			// }
			break;
		case R.id.event_detail_skill_list_item_iv:
			TextView tv = (TextView) v.getTag();
			String description = (String) tv.getTag();
			description = StringUtils.replace(description, ",", "\n");
			new DialogFactory.TitleMessageButtonDialog(getActivity(), tv
					.getText().toString(), description).show();
			break;
		case R.id.fragment_event_detail_tv_2:
			// 評分
            RatingBarDialogFragment fragment = new RatingBarDialogFragment();
            fragment.setParentFragment(this);
            fragment.setEventId(mEventId);
            fragment.show(
                    getFragmentManager(), getTag());
			break;
		case R.id.fragment_event_detail_tv_address:
			TextView addressTextView = (TextView) v;
			TextView cityTextView = (TextView) v.getTag();
			String uri = "geo:0,0?q="// + cityTextView.getText().toString()//FIH-modify for 活動詳情頁地址沒有顯示全
					+ addressTextView.getText().toString();
			Intent i = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse(uri));
			i.setClassName("com.google.android.apps.maps",
					"com.google.android.maps.MapsActivity");
			getActivity().startActivity(i);
			break;
		case R.id.fragment_event_detail_iv_image:
			ImageView view = (ImageView) v;
			FragHelper.replace(getActivity(), new DetailImageViewFragment(view,
					(String) view.getTag()));
			break;
		case imageViewId:
			ImageView viewTwo = (ImageView) v;
			FragHelper.replace(getActivity(), new DetailImageViewFragment(
					viewTwo, (String) viewTwo.getTag()));
			break;
		case R.id.fragment_event_detail_iv_switch:
			FragHelper.replace(getActivity(), new EventResultFragment(
					mEventObject));
			break;
		default:
			Log.e(TAG, "unknow click");
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0) {
			if (resultCode == -1) {
				String contents = data.getStringExtra("SCAN_RESULT");
				// String format = data.getStringExtra("SCAN_RESULT_FORMAT");
				// Log.e("code", "" + contents + " / " + format);
				if (mUserState == UserState.NEEDLEAVE) {
					new V1LeaveEventTask(getActivity(), this, contents, mEventId).execute();
				} else {
					new V1JoinEventTask(getActivity(), this, contents, mEventId).execute();
				}
			} else if (resultCode == 0) {
				// Handle cancel
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		showActionBarTabs();
	}

	@Override
	public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
		mTvRating.setText(String.valueOf(arg0.getRating()));

	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		if (!arg1) {
			return;
		}
		CheckBox cb = (CheckBox) arg0;
		for (View v : skillGroup) {
			CheckBox cbox = (CheckBox) v
					.findViewById(R.id.event_detail_skill_list_item_cb);
			if (cb != cbox) {
				cbox.setChecked(false);
			}
		}
	}
}

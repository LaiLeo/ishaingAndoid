package com.taiwanmobile.volunteers.v2;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.util.StaticResUtil;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.dialog.UserSkillDialog;
import com.taiwanmobile.volunteers.v2.utils.roundedimageview.RoundedTransformationBuilder;

public class UserDataFragment extends SupportFragment implements OnClickListener {
	final String TAG = getClass().getSimpleName();

	// for ProfileEventsAdapter onClick() usage
	public static UserDataFragment fragmentObj;
	private long userId;
	public static ListView eventListView;
	private UserAccountDAO mUserAccountObject;

	public UserDataFragment() {
		fragmentObj = this;
	}

	public void setUserAccountObject(UserAccountDAO dao){
		mUserAccountObject = dao;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		hideActionBarTabs();
		fragmentObj = this;
		View head = getActivity().getLayoutInflater().inflate(
				R.layout.fragment_user_data, null);
		if (mUserAccountObject == null) {
			Log.e(TAG, "no user data");
			return head;
		}
		Answers.getInstance().logContentView(new ContentViewEvent().putContentName("user_page")
		.putContentId("user_id" + mUserAccountObject.id)
		.putCustomAttribute("user_id", ""+mUserAccountObject.id));
		ImageView user_icon = (ImageView) head
				.findViewById(R.id.user_data_iv_photo);
		String imageFilePath = mUserAccountObject.icon;
		if (!imageFilePath.isEmpty()) {
			int image_size = (int) (100 * getResources().getDisplayMetrics().density);
			Transformation transformation = new RoundedTransformationBuilder()
					.oval(true).build();

			Picasso.with(getActivity())
					//.load(BackendContract.DEPLOYMENT_URL + imageFilePath)
					.load(StaticResUtil.checkUrl(imageFilePath, false, TAG))
					.resize(image_size, image_size).centerCrop()
					.transform(transformation).into(user_icon);
		}
		head.findViewById(R.id.user_data_btn_service_area).setOnClickListener(this);
		head.findViewById(R.id.user_data_btn_service_item).setOnClickListener(this);
		ImageView badgeImageView = (ImageView) head
				.findViewById(R.id.user_data_iv_badge);
		setBadgeImageByScore(badgeImageView, mUserAccountObject.score);
		((TextView) head.findViewById(R.id.user_data_tv_name))
				.setText(mUserAccountObject.displayName);
		((TextView) head.findViewById(R.id.user_data_tv_email))
				.setText(mUserAccountObject.email);
		((TextView) head.findViewById(R.id.user_data_tv_description))
				.setText(mUserAccountObject.aboutMe);

		((TextView) head.findViewById(R.id.user_data_tv_eventhour))
				.setText("參加時數 " + mUserAccountObject.getGeneralEventHour() + " /小時");
		((TextView) head.findViewById(R.id.user_data_tv_event_joined_num))
				.setText("參加活動 " + mUserAccountObject.eventNum + " /次");
		((TextView) head.findViewById(R.id.user_data_tv_raking))
				.setText("志工排名 " + mUserAccountObject.ranking);

		return head;
	}


	private void setBadgeImageByScore(ImageView badgeImageView, int score){

		Drawable drawable;
		if (score < 15) {
			drawable = getResources().getDrawable(R.drawable.ic_badge_a);
		} else if (score < 30) {
			drawable = getResources().getDrawable(R.drawable.ic_badge_b);
		} else if (score < 45) {
			drawable = getResources().getDrawable(R.drawable.ic_badge_c);
		} else if (score < 60) {
			drawable = getResources().getDrawable(R.drawable.ic_badge_d);
		} else if (score < 75) {
			drawable = getResources().getDrawable(R.drawable.ic_badge_e);
		} else if (score < 90) {
			drawable = getResources().getDrawable(R.drawable.ic_badge_f);
		} else {
			drawable = getResources().getDrawable(R.drawable.ic_badge_g);
		}
		ViewCompat.setBackground(badgeImageView, drawable);
	}

	@Override
	public void onClick(View arg0) {
		Cursor c;
		switch (arg0.getId()) {
		case R.id.user_data_btn_service_item:
			new UserSkillDialog(getActivity(), "可服務項目",
					mUserAccountObject.skills.split(",")).show();
			break;
		case R.id.user_data_btn_service_area:
			new UserSkillDialog(getActivity(), "可服務區域",
					mUserAccountObject.interest.split(",")).show();
			break;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
        showActionBarTabs();
	}

}

package com.taiwanmobile.volunteers.v2.userprofileview;

import java.sql.SQLException;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.api.GetProfileTask;
import com.taiwanmobile.volunteers.v1.util.StaticResUtil;
import com.taiwanmobile.volunteers.v2.FilteredEventListFragment;
import com.taiwanmobile.volunteers.v2.FilteredItemEventListFragment;
import com.taiwanmobile.volunteers.v2.FilteredNpoListFragment;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.database.FocusedEventDAO;
import com.taiwanmobile.volunteers.v2.database.RegisteredEventDAO;
import com.taiwanmobile.volunteers.v2.database.SubscribedNpoDAO;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.database.UserLicenseImageDAO;
import com.taiwanmobile.volunteers.v2.dialog.UserPhotoDialog;
import com.taiwanmobile.volunteers.v2.dialog.UserSkillDialog;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;
import com.taiwanmobile.volunteers.v2.utils.roundedimageview.RoundedTransformationBuilder;

public class UserProfileFragment extends Fragment implements OnClickListener {
	static final String TAG = "UserProfileFragment";
    public final static String BUNDLE_KEY = "user_id";

	// for ProfileEventsAdapter onClick() usage
	public static UserProfileFragment fragmentObj;
//	private long userId;
//	public static ListView eventListView;
	private UserAccountDAO mUserAccountObject;

    public static UserProfileFragment newInstance(long id){
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(BUNDLE_KEY, id);
        fragment.setArguments(bundle);

        return fragment;
    }

    private void fetchUserAccountObject(){

        long id = getArguments().getLong(BUNDLE_KEY);
        Log.d(TAG, "fetchUserAccountObject: user id = " + id);
        try {
            mUserAccountObject = UserAccountDAO.queryObjectById(id);
            if(mUserAccountObject != null) {
                Log.d(TAG, "mUserAccountObject:" + mUserAccountObject.toString());
                mUserAccountObject.images = UserLicenseImageDAO.queryByUserId(mUserAccountObject.id);
            }
        } catch (SQLException e) {
            Log.e(TAG, "no ush user id");
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        String accessToken = MyPreferenceManager.getAuthToken(getActivity());
        GetProfileTask task = new GetProfileTask(getActivity(), accessToken);
        task.execute();

    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
		if(actionBar != null) {
            actionBar.setNavigationMode(
					ActionBar.NAVIGATION_MODE_STANDARD);
		}
        fetchUserAccountObject();
		fragmentObj = this;
		View head = getActivity().getLayoutInflater().inflate(
				R.layout.fragment_user_profile, container, false);
		if (mUserAccountObject == null) {
			Log.e(TAG, "no user data");
			return head;
		}
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentId("user_profile")
                .putContentName("User Profile")
                .putCustomAttribute("userself", "true"));
		ImageView user_icon = (ImageView) head
				.findViewById(R.id.user_profile_iv_photo);
		String imageFilePath = mUserAccountObject.icon;
        Log.e(TAG, "imageFilePath:"+imageFilePath);

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

		head.findViewById(R.id.user_profile_btn_edit).setOnClickListener(this);
		head.findViewById(R.id.user_profile_btn_service_area).setOnClickListener(this);
		head.findViewById(R.id.user_profile_btn_service_item).setOnClickListener(
				this);
		head.findViewById(R.id.user_profile_btn_non_joined_event)
				.setOnClickListener(this);
		head.findViewById(R.id.user_profile_btn_joined_event)
				.setOnClickListener(this);
		head.findViewById(R.id.user_profile_btn_focused_event)
				.setOnClickListener(this);
		head.findViewById(R.id.user_profile_btn_focused_item_event)
				.setOnClickListener(this);
		head.findViewById(R.id.user_profile_btn_donated_item)
				.setOnClickListener(this);
		head.findViewById(R.id.user_profile_btn_focused_npo)
				.setOnClickListener(this);
        head.findViewById(R.id.user_profile_btn_advance_search_volunteer_hours)
                .setOnClickListener(this);
        head.findViewById(R.id.user_profile_btn_professional_licenses)
                .setOnClickListener(this);


        if(mUserAccountObject.email.equals(mUserAccountObject.displayName)){
            ((TextView) head.findViewById(R.id.user_profile_tv_name))
                    .setVisibility(View.GONE);
            ((TextView) head.findViewById(R.id.user_profile_tv_email))
                    .setText(mUserAccountObject.email);
        }else{
            ((TextView) head.findViewById(R.id.user_profile_tv_name))
                    .setText(mUserAccountObject.displayName);
            ((TextView) head.findViewById(R.id.user_profile_tv_email))
                    .setText(mUserAccountObject.email);
        }


		((TextView) head.findViewById(R.id.user_profile_tv_description))
				.setText(mUserAccountObject.aboutMe);
		ImageView badgeImageView = (ImageView) head
				.findViewById(R.id.user_profile_iv_badge);
		badgeImageView.setOnClickListener(this);
        setBadgeImageByScore(badgeImageView, mUserAccountObject.score);

		((TextView) head.findViewById(R.id.user_profile_tv_eventhour))
				.setText(String.format("參加時數 %s /小時", mUserAccountObject.getGeneralEventHour()));
		((TextView) head.findViewById(R.id.user_profile_tv_event_joined_num))
				.setText(String.format("參加活動 %d /次", mUserAccountObject.eventNum));
		((TextView) head.findViewById(R.id.user_profile_tv_raking))
				.setText(String.format("志工排名 %d", mUserAccountObject.ranking));
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
            case R.id.user_profile_btn_edit:
                UserProfileEditFragment fragment = new UserProfileEditFragment();
                fragment.setUserAccountObject(mUserAccountObject);
                FragHelper.replace(getActivity(), fragment);
                break;
            case R.id.user_profile_btn_service_item:
                new UserSkillDialog(getActivity(), "可服務項目",
                        mUserAccountObject.skills.split(","), false).show();
                // FragHelper.replace(fragmentObj, new UserProfileEditFragment());
                break;
            case R.id.user_profile_btn_service_area:
                new UserSkillDialog(getActivity(), "可服務區域",
                        mUserAccountObject.interest.split(","), false).show();
                // FragHelper.replace(fragmentObj, new UserProfileEditFragment());
                break;
            case R.id.user_profile_btn_non_joined_event:
//			c = RegisteredEventDAO.queryNonJoinedEvents();
                c = RegisteredEventDAO.queryNonJoinedAndNotExpiredEvents();
                if (c.getCount() == 0) {
                    new DialogFactory.TitleMessageDialog(getActivity(), "尚未報名活動",
                            "您沒有報名的活動喔").show();
                } else {
                    FragHelper.replace(getActivity(),
                            new FilteredEventListFragment(c));
                }
                break;
            case R.id.user_profile_btn_joined_event:
                c = RegisteredEventDAO.queryJoinedEvents();
                if (c.getCount() == 0) {
                    new DialogFactory.TitleMessageDialog(getActivity(), "尚未參與活動",
                            "您沒有參加的活動喔").show();
                } else {
                    FragHelper.replace(getActivity(),
                            new FilteredEventListFragment(c));
                }
                break;
            case R.id.user_profile_btn_focused_event:
                c = FocusedEventDAO.queryFocusedVolunteerEvents();
                if (c.getCount() == 0) {
                    new DialogFactory.TitleMessageDialog(getActivity(), "尚未收藏活動",
                            "您沒有收藏的活動喔").show();
                } else {
                    FragHelper.replace(getActivity(),
                            new FilteredEventListFragment(c));
                }
                break;
            case R.id.user_profile_btn_focused_item_event:
                c = FocusedEventDAO.queryFocusedItemEvents();
                if (c.getCount() == 0) {
                    new DialogFactory.TitleMessageDialog(getActivity(), "尚未收藏物資缺",
                            "您沒有收藏的物資缺活動喔").show();
                } else {
                    FragHelper.replace(getActivity(),
                            new FilteredEventListFragment(c));
                }
                break;
            case R.id.user_profile_btn_donated_item:
                c = RegisteredEventDAO.queryDonatedItemEvents();
                if (c.getCount() == 0) {
                    new DialogFactory.TitleMessageDialog(getActivity(), "尚未捐贈物資",
                            "您沒有捐贈過的物資喔").show();
                } else {
					FilteredItemEventListFragment filteredItemEventListFragment = new FilteredItemEventListFragment();
                    filteredItemEventListFragment.setCursor(c);
					FragHelper.replace(getActivity(),filteredItemEventListFragment);
                }
                break;
            case R.id.user_profile_btn_focused_npo:
                c = SubscribedNpoDAO.querySubscribedNpos();
                if (c.getCount() == 0) {
                    new DialogFactory.TitleMessageDialog(getActivity(), "尚未訂閱公益品牌",
                            "您沒有訂閱過的公益品牌喔").show();
                } else {
                    FilteredNpoListFragment filteredNpoListFragment = new FilteredNpoListFragment();
                    filteredNpoListFragment.setCursor(c);
                    FragHelper.replace(getActivity(),filteredNpoListFragment);

                }
                break;
            case R.id.user_profile_iv_badge:
                new DialogFactory.TitleMessageDialog(getActivity(), "彩球介紹",
                        "請踴躍參與活動，集點數為彩球上色喔!").show();
                break;
            case R.id.user_profile_btn_advance_search_volunteer_hours:
                AdvanceVolunteerHoursQueryFragment advanceVolunteerHoursQueryFragment = new AdvanceVolunteerHoursQueryFragment();
                advanceVolunteerHoursQueryFragment.setArguments(getArguments());
                FragHelper.replace(getActivity(), advanceVolunteerHoursQueryFragment);
                break;
            case R.id.user_profile_btn_professional_licenses:
                Log.d(TAG, "uao: " + mUserAccountObject);
                Log.d(TAG, "images: " + mUserAccountObject.images);
                for(UserLicenseImageDAO dao : mUserAccountObject.images){
                    Log.d(TAG, dao.image);
                }
                UserPhotoDialog dialog = new UserPhotoDialog(getActivity(), "專業證照",  mUserAccountObject.images);
                dialog.setEditable(false);
                dialog.show();
//                new UserPhotoDialog().show();
                break;
        }
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
		if(actionBar != null) {
            actionBar.setNavigationMode(
					ActionBar.NAVIGATION_MODE_TABS);
		}
	}

}

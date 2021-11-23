package com.taiwanmobile.volunteers.v2.userprofileview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.util.StaticResUtil;
import com.taiwanmobile.volunteers.v2.SupportFragment;
import com.taiwanmobile.volunteers.v2.YearPickerDialogFragment;
import com.taiwanmobile.volunteers.v2.database.RegisteredEventDAO;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.utils.roundedimageview.RoundedTransformationBuilder;

import java.sql.SQLException;

/**
 * Created by pichu on 西元2016/8/23.
 * for show advance volunteer hours search
 * default start time and end time are "不限" show select list after click
 * the list year from 2015(?) to this year
 */
public class AdvanceVolunteerHoursQueryFragment extends SupportFragment implements
        View.OnClickListener {

    static final String TAG = "AdvanceVHQFragment";
    static final int REQUEST_CODE_START_YEAR = 0;
    static final int REQUEST_CODE_END_YEAR = 1;
    public static final int YEAR_NO_LIMITATION = 0;

    int startYear;
    int endYear;

    private UserAccountDAO mUserAccountObject;

//    View mView = null;


    private void fetchUserAccountObject(){
        try {
            mUserAccountObject = UserAccountDAO.queryObjectById(getArguments().getLong(UserProfileFragment.BUNDLE_KEY));
        } catch (SQLException e) {
            Log.e(TAG, "no ush user id");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult()");
        switch(requestCode){
            case REQUEST_CODE_START_YEAR:
                setStartYear(resultCode);
                break;
            case REQUEST_CODE_END_YEAR:
                setEndYear(resultCode);
                break;
        }


    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.search_hours_btn_set_start_year:
                onSetStartYearClick(v);
                break;
            case R.id.search_hours_btn_set_end_year:
                onSetEndYearClick(v);
                break;
            case R.id.search_hours_btn_start_search:
                onSetStartSearchClick(v);
                break;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        hideActionBarTabs();
        View head = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_user_profile_volunteer_hours, container, false);
        fetchUserAccountObject();
        if (mUserAccountObject == null) {
            Log.e(TAG, "no user data");
            return head;
        }

        ImageView user_icon = (ImageView) head
                .findViewById(R.id.user_profile_iv_photo);
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

        head.findViewById(R.id.user_profile_btn_edit).setOnClickListener(this);

        ((TextView) head.findViewById(R.id.user_profile_tv_name))
                .setText(mUserAccountObject.displayName);
        ((TextView) head.findViewById(R.id.user_profile_tv_email))
                .setText(mUserAccountObject.email);
//        ((TextView) head.findViewById(R.id.user_profile_tv_description))
//                .setText(mUserAccountObject.aboutMe);

        ImageView badgeImageView = (ImageView) head
                .findViewById(R.id.user_profile_iv_badge);
        badgeImageView.setOnClickListener(this);
        setBadgeImageByScore(badgeImageView, mUserAccountObject.score);

//        ((TextView) head.findViewById(R.id.user_profile_tv_eventhour))
//                .setText(String.format("參加時數 %s /小時", mUserAccountObject.eventHour));
//        ((TextView) head.findViewById(R.id.user_profile_tv_event_joined_num))
//                .setText(String.format(Locale.TAIWAN, "參加活動 %d /次", mUserAccountObject.eventNum));
//        ((TextView) head.findViewById(R.id.user_profile_tv_raking))
//                .setText(String.format(Locale.TAIWAN, "志工排名 %d", mUserAccountObject.ranking));


        setOnClickListeners(head);
        return head;
    }


    private void setBadgeImageByScore(ImageView badgeImageView, int score){
        if (score < 15) {
            badgeImageView.setBackground(getResources().getDrawable(
                    R.drawable.ic_badge_a));
        } else if (score < 30) {
            badgeImageView.setBackground(getResources().getDrawable(
                    R.drawable.ic_badge_b));
        } else if (score < 45) {
            badgeImageView.setBackground(getResources().getDrawable(
                    R.drawable.ic_badge_c));
        } else if (score < 60) {
            badgeImageView.setBackground(getResources().getDrawable(
                    R.drawable.ic_badge_d));
        } else if (score < 75) {
            badgeImageView.setBackground(getResources().getDrawable(
                    R.drawable.ic_badge_e));
        } else if (score < 90) {
            badgeImageView.setBackground(getResources().getDrawable(
                    R.drawable.ic_badge_f));
        } else {
            badgeImageView.setBackground(getResources().getDrawable(
                    R.drawable.ic_badge_g));
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setAnswerViewVisibility(View.INVISIBLE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        showActionBarTabs();
    }

    private void setAnswerViewVisibility(int visibility){
        if(getView() == null){
            return;
        }
        View av = getView().findViewById(R.id.answer_view);
        if(av == null){
            return;
        }
        av.setVisibility(visibility);
    }

    private void setOnClickListeners(View v){
        Log.d(TAG, "setOnClickListeners");
        v.findViewById(R.id.search_hours_btn_set_start_year).setOnClickListener(this);
        v.findViewById(R.id.search_hours_btn_set_end_year).setOnClickListener(this);
        v.findViewById(R.id.search_hours_btn_start_search).setOnClickListener(this);
    }

    private void onSetStartYearClick(View v){
        YearPickerDialogFragment fragment = new YearPickerDialogFragment();
        fragment.setTargetFragment(this, REQUEST_CODE_START_YEAR);
        Bundle bundle = new Bundle();
        bundle.putInt("value", startYear);
        fragment.setArguments(bundle);
        fragment.show(getFragmentManager(),getTag());
    }

    private void onSetEndYearClick(View v){
        YearPickerDialogFragment fragment = new YearPickerDialogFragment();
        fragment.setTargetFragment(this, REQUEST_CODE_END_YEAR);
        Bundle bundle = new Bundle();
        bundle.putInt("value", startYear);
        fragment.setArguments(bundle);
        fragment.show(getFragmentManager(),getTag());

    }

    private void onSetStartSearchClick(View v){
        Log.d(TAG, "onSetStartSearchClick");
        TextView generalVolunteerHoursTextView = (TextView) getView().findViewById(R.id.general_volunteer_hours);
        TextView volunteerHoursTextView = (TextView) getView().findViewById(R.id.volunteer_hours);
        TextView enterpriseVolunteerHourslTextView = (TextView) getView().findViewById(R.id.enterprise_volunteer_hours);

        try {

            generalVolunteerHoursTextView.setText(String.valueOf( RegisteredEventDAO.getUserGeneralEventHour(startYear, endYear)));
            enterpriseVolunteerHourslTextView.setText(String.valueOf( RegisteredEventDAO.getUserEnterpriseEventHour(startYear, endYear)));
            volunteerHoursTextView.setText(String.valueOf( RegisteredEventDAO.getUserEventHour(startYear, endYear)));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setAnswerViewVisibility(View.VISIBLE);
    }

    private void setStartYear(int year){
        startYear = year;
        if(year == YEAR_NO_LIMITATION){
            ((TextView)getView().findViewById(R.id.start_year)).setText("不限");
        }else{
            ((TextView)getView().findViewById(R.id.start_year)).setText(String.valueOf(year));
        }
    }
    private void setEndYear(int year){
        endYear = year;
        if(year == YEAR_NO_LIMITATION){
            ((TextView)getView().findViewById(R.id.end_year)).setText("不限");
        }else{
            ((TextView)getView().findViewById(R.id.end_year)).setText(String.valueOf(year));
        }
    }

}

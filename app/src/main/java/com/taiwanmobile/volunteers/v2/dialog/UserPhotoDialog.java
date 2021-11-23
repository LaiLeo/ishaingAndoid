package com.taiwanmobile.volunteers.v2.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.squareup.picasso.Picasso;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.util.StaticResUtil;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.database.UserLicenseImageDAO;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * Created by pichu on 西元2017/11/16.
 * Copy from User Photo Dialog
 */

public class UserPhotoDialog extends Dialog implements
        View.OnClickListener {

    private static final String TAG = "UserSkillDialog";
    private OnCompleteListener listener;
    private RelativeLayout[] childViews;

    private boolean deletable;
    private boolean editable = true;
    private int maxSelectableItem;
    private int selectedItemNumber = 0;
    UserLicenseImageDAO[] mLicenses;

    private final static int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 1;

    private OnActionListener actionListener = new OnActionListener() {
        @Override
        public void onDelete(UserLicenseImageDAO dao) {

        }

        @Override
        public void onAdd() {

        }
    };

    public void setEditable(boolean editable){
        this.editable = editable;
        if(!editable){
            findViewById(R.id.dialog_user_skill_btn_add).setVisibility(View.GONE);
        }else{
            findViewById(R.id.dialog_user_skill_btn_add).setVisibility(View.VISIBLE);

        }
    }



    public UserPhotoDialog(final Context context, final String title,
                           Collection<UserLicenseImageDAO> licenses) {
        super(context,
                android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        setContentView(R.layout.dialog_user_skill_or_inrerest);
        mLicenses = new UserLicenseImageDAO[licenses.size()];
        childViews = new RelativeLayout[licenses.size()];
        TextView tv = findViewById(R.id.dialog_user_skill_tv_title);
        tv.setText(title);


        ViewGroup flowContainer = findViewById(R.id.item_user_skill_or_interest_fl);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater == null){
            Crashlytics.log("UserSkillDialog inflater == null");
            return;
        }

        if(!editable || licenses.size() >= 5){
            findViewById(R.id.dialog_user_skill_btn_add).setVisibility(View.GONE);
        }

        LinearLayout.LayoutParams imageMarginParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        // imageMarginParams.height = (int) (100 * imageMarginDP);
        // imageMarginParams.width = (int) (100 * imageMarginDP);
        imageMarginParams.setMargins(8, 8, 8, 8);
        int i = 0;
        for(UserLicenseImageDAO dao: licenses){
            String s = dao.image;
            final int thisIndex = i;
            if (StringUtils.isNotBlank(s)) {
                View subView = inflater.inflate(R.layout.item_license_photo, flowContainer, false);

                ImageView itemImageView = (ImageView) subView.findViewById(R.id.item_iv_image);
                childViews[i] = (RelativeLayout) subView;
                mLicenses[i] = dao;

                //item_iv_image
                int imageWidth = (int) (150 * context.getResources().getDisplayMetrics().density);
                int imageHeight = (int) (100 * context.getResources().getDisplayMetrics().density);

                Picasso.with(context)
                        //.load(BackendContract.DEPLOYMENT_STATIC_URL + dao.image)
                        .load(StaticResUtil.checkUrl(dao.image, true, TAG))
//                        .resize(image_size, image_size)
                        .resize(imageWidth, imageHeight )
                        .centerInside()
                        .into(itemImageView);

                flowContainer.addView(subView);
                final String url = BackendContract.DEPLOYMENT_STATIC_URL + dao.image;

                itemImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Answers.getInstance().logContentView(new ContentViewEvent()
                                .putContentName("user_view_license_full")
                                .putCustomAttribute("imgid", url));
                        Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(intent);
                    }
                });


            }
            i++;
        }

        findViewById(R.id.dialog_user_skill_btn_add).setOnClickListener(this);
        findViewById(R.id.dialog_user_skill_or_inrerest_btn_ok).setOnClickListener(this);
    }


//
    private void refreshAllView(){
        for(int i=0; i < childViews.length ; i++){
            refreshView(i);
        }

    }

    private void refreshView(final int i){
        if(childViews[i] == null)
            return;

        RelativeLayout tv = childViews[i];
        if(deletable){
            tv.findViewById(R.id.item_tv_delete).setVisibility(View.VISIBLE);
            tv.findViewById(R.id.item_tv_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Answers.getInstance().logCustom(new CustomEvent("del_license"));
                    actionListener.onDelete(mLicenses[i]);
                }
            });
        }else{
            tv.findViewById(R.id.item_tv_delete).setVisibility(View.GONE);
            tv.findViewById(R.id.item_tv_delete).setOnClickListener(null);
        }
    }

    public void setOnCompleteListener(@Nullable OnCompleteListener newListener){
        listener = newListener;
    }

    @Override
    public void onClick(View arg0) {

        switch (arg0.getId()){
            case R.id.dialog_user_skill_or_inrerest_btn_ok:
                this.dismiss();
                break;
            case R.id.dialog_user_skill_btn_add:
                Answers.getInstance().logCustom(new CustomEvent("add_license"));
                actionListener.onAdd();
                break;
        }
    }

    public void setActionListener(@NonNull OnActionListener l) {
        actionListener = l;
    }

    public void setDeletable(boolean b){
        deletable = b;
        refreshAllView();
    }


    public void setSelectionLimit(int selectionLimit) {
        this.maxSelectableItem = selectionLimit;
    }

    public void setSubTitle(@Nullable String subTitle) {
        if(subTitle == null){
            TextView tv = (TextView) this
                    .findViewById(R.id.dialog_user_skill_tv_subtitle);
            tv.setVisibility(View.GONE);
            return;
        }

        TextView tv = this
                .findViewById(R.id.dialog_user_skill_tv_subtitle);
        tv.setVisibility(View.VISIBLE);
        tv.setText(subTitle);
    }

//    public void setSelectable(boolean selectable) {
//        this.selectable = selectable;
//        refreshAllView();
//    }

    public interface OnCompleteListener {
        void onComplete(DialogInterface dialog, String[] allItems, boolean[] selectedItems);
    }

    public interface OnActionListener{
        void onDelete(UserLicenseImageDAO dao);
        void onAdd();
    }

}

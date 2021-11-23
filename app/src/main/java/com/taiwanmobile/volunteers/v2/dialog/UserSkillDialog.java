package com.taiwanmobile.volunteers.v2.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.taiwanmobile.volunteers.R;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by pichu on 西元2017/11/16.
 * Copy from User Skill or Interest Dialog
 */

public class UserSkillDialog extends Dialog implements
        android.view.View.OnClickListener {

    private static final String TAG = "UserSkillDialog";
    private OnCompleteListener listener;
    private String[] _allItems;
    private boolean[] _selectedItems;
    private TextView[] childTextViews;

    private boolean selectable;
    private int maxSelectableItem;
    private int selectedItemNumber = 0;


    public UserSkillDialog(Context context, String title,
                           String[] selectedItems) {
        this(context, title,  selectedItems, selectedItems);
    }

    public UserSkillDialog(Context context, String title,
                           String[] selectedItems, boolean modifyEnable) {
        this(context, title,  selectedItems, selectedItems, modifyEnable);
    }

    public UserSkillDialog(final Context context, final String title,
                           String[] selectedItems, String[] allItems) {
        this(context, title,  selectedItems, allItems, true);
    }

    public UserSkillDialog(final Context context, final String title,
                           String[] selectedItems, String[] allItems, boolean modifyEnable) {
        super(context,
                android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        setContentView(R.layout.dialog_user_skill_or_inrerest);
        _allItems = allItems;
        _selectedItems = new boolean[_allItems.length];
        childTextViews = new TextView[_allItems.length];
        List allItemList = Arrays.asList(allItems);
        for (String selectedItem:selectedItems) {
            selectedItem = selectedItem.trim();
            Log.d(TAG, "testing " + selectedItem);
            int index = allItemList.indexOf(selectedItem);
            if(index == -1){
                Crashlytics.log("item " + selectedItem + " is not in array");
                continue;
            }
            selectedItemNumber++;
            _selectedItems[index] = true;

        }

        TextView tv = (TextView) this
                .findViewById(R.id.dialog_user_skill_tv_title);
        tv.setText(title);


        ViewGroup flowContainer = (ViewGroup) findViewById(R.id.item_user_skill_or_interest_fl);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(inflater == null){
            Crashlytics.log("UserSkillDialog inflater == null");
            return;
        }
        LinearLayout.LayoutParams imageMarginParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        // imageMarginParams.height = (int) (100 * imageMarginDP);
        // imageMarginParams.width = (int) (100 * imageMarginDP);
        imageMarginParams.setMargins(8, 8, 8, 8);
        for (int i=0; i < allItems.length; i++){
//        for (String s : allItems) {
//
//        }
            String s = allItems[i];
            final int thisIndex = i;
            if (StringUtils.isNotBlank(s)) {
                TextView itemTextView = (TextView) inflater.inflate(
                        R.layout.item_user_skill_or_interest, flowContainer, false);
                itemTextView.setLayoutParams(imageMarginParams);
                childTextViews[i] = itemTextView;
                itemTextView.setText(s.trim());
                refreshView(thisIndex);
                flowContainer.addView(itemTextView);
                itemTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(selectedItemNumber >= maxSelectableItem && !_selectedItems[thisIndex]){
                            Crashlytics.log("user select item reach limit");
                            Answers.getInstance().logCustom(new CustomEvent("reach_limit")
                                    .putCustomAttribute("title", title));
                            return;
                        }

                        _selectedItems[thisIndex] = !_selectedItems[thisIndex];
                        if(_selectedItems[thisIndex]){
                            selectedItemNumber++;
                        }else{
                            selectedItemNumber--;
                        }
                        refreshView(thisIndex);
                    }
                });
            }
        }

        this.findViewById(R.id.dialog_user_skill_or_inrerest_btn_ok)
                .setOnClickListener(this);


        this.findViewById(R.id.dialog_user_skill_btn_add).setVisibility(modifyEnable ? View.VISIBLE : View.GONE);
    }



    private void refreshAllView(){
        for(int i=0; i < childTextViews.length ; i++){
            refreshView(i);
        }

    }

    private void refreshView(int i){
        if(childTextViews[i] == null)
            return;

        TextView tv = childTextViews[i];

        if(!_selectedItems[i] || !selectable){
            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.twmf_white));
            ViewCompat.setBackground(tv, ContextCompat.getDrawable(getContext(), R.drawable.bg_user_skill_or_interest_item));
        }else{
            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.twmf_orange));
            ViewCompat.setBackground(tv, ContextCompat.getDrawable(getContext(), R.drawable.bg_user_skill_or_interest_item_white));
        }
    }

    public void setOnCompleteListener(@Nullable OnCompleteListener newListener){
        listener = newListener;
    }

    @Override
    public void onClick(View arg0) {
        if(listener != null){
            listener.onComplete(this, _allItems, _selectedItems);
        }else {
            this.dismiss();
        }
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

        TextView tv = (TextView) this
                .findViewById(R.id.dialog_user_skill_tv_subtitle);
        tv.setVisibility(View.VISIBLE);
        tv.setText(subTitle);
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
        refreshAllView();
    }

    public interface OnCompleteListener {
        void onComplete(DialogInterface dialog, String[] allItems, boolean[] selectedItems);
    }

}

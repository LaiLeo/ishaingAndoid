package com.taiwanmobile.volunteers.v2;

//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by pichu on 西元2016/8/26.
 * for implement shared functions
 */
public class SupportFragment extends Fragment {

    protected void hideActionBarTabs() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            Log.d("SupportFragment","gengqiang->action_bar->SupportFragment->hideActionBarTabs() start");
            actionBar.setNavigationMode(
                    ActionBar.NAVIGATION_MODE_STANDARD);
            Log.d("SupportFragment","gengqiang->action_bar->SupportFragment->hideActionBarTabs() end");
        }
    }

    protected void showActionBarTabs(){

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar != null) {
            Log.d("SupportFragment","gengqiang->action_bar->SupportFragment->showActionBarTabs() start");
            actionBar.setNavigationMode(
                    ActionBar.NAVIGATION_MODE_TABS);
            Log.d("SupportFragment","gengqiang->action_bar->SupportFragment->showActionBarTabs() end");
        }
    }

}

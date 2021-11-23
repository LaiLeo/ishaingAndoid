package com.taiwanmobile.volunteers.v2.utils;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.taiwanmobile.volunteers.v2.DonationFragment;
import com.taiwanmobile.volunteers.v2.ItemEventListFragment;
import com.taiwanmobile.volunteers.v2.LocaionalItemEventListFragment;
import com.taiwanmobile.volunteers.v2.LocationalVolunteerEventListFragment;
import com.taiwanmobile.volunteers.v2.SubscribedEventListFragment;
import com.taiwanmobile.volunteers.v2.database.DonationNpoDAO;
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.database.NpoDAO;
import com.taiwanmobile.volunteers.v2.npoview.ItemNpoListFragment;
import com.taiwanmobile.volunteers.v2.npoview.VolunteerNpoListFragment;

public class ViewHelper {

	public static View setListener(View root, OnClickListener l,
			Class<? extends View> clazz) {

		ViewGroup vg = (ViewGroup) root;
		for (int i = 0; i < vg.getChildCount(); i++) {
			View c = vg.getChildAt(i);
			if (ViewGroup.class.isInstance(c)) {
				setListener(c, l, clazz);
				continue;
			}
			if (!clazz.isInstance(c)) {
				continue;
			}
			c.setOnClickListener(l);
		}
		return root;
	}

    public static SubscribedEventListFragment generalVolunteerSubscribedEventListFragment = null;
    public static SubscribedEventListFragment enterpriseVolunteerSubscribedEventListFragment = null;
    public static ItemEventListFragment itemEventListFragment = null;

    public static LocationalVolunteerEventListFragment generalVolunteerLocationalEventListFragment = null;
    public static LocationalVolunteerEventListFragment enterpriseVolunteerLocationalEventListFragment = null;

    public static VolunteerNpoListFragment generalVolunteerNpoListFragment = null;
    public static VolunteerNpoListFragment enterpriseVolunteerNpoListFragment = null;


    public static void refreshEventTabAdapters(Context c) {
        if(generalVolunteerSubscribedEventListFragment != null){
            generalVolunteerSubscribedEventListFragment.notifyRefreshEventList();
        }
        if(enterpriseVolunteerSubscribedEventListFragment != null){
            enterpriseVolunteerSubscribedEventListFragment.notifyRefreshEventList();
        }
        if(generalVolunteerLocationalEventListFragment != null &&
				generalVolunteerLocationalEventListFragment.adapter != null){
            generalVolunteerLocationalEventListFragment.notifyRefreshEventList();
        }
        if(enterpriseVolunteerLocationalEventListFragment != null &&
				enterpriseVolunteerLocationalEventListFragment.adapter != null){
            enterpriseVolunteerLocationalEventListFragment.notifyRefreshEventList();
        }
        if(generalVolunteerNpoListFragment != null){
            generalVolunteerNpoListFragment.notifyRefreshEventList();
        }

        if(enterpriseVolunteerNpoListFragment != null){
            enterpriseVolunteerNpoListFragment.notifyRefreshEventList();
        }

		// item tab
        if(itemEventListFragment != null){
            itemEventListFragment.notifyRefreshEventList();
        }
		if (LocaionalItemEventListFragment.adapter != null) {
			LocaionalItemEventListFragment.adapter.changeCursor(EventDAO
					.queryAllItemEventsAndSortByDistance());
			LocaionalItemEventListFragment.adapter.notifyDataSetChanged();
		}
		if (ItemNpoListFragment.adapter != null) {
			ItemNpoListFragment.adapter.changeCursor(NpoDAO.queryItemNpos());
			ItemNpoListFragment.adapter.notifyDataSetChanged();
		}
		// donation tab
		if (DonationFragment.adapter != null) {
			DonationFragment.adapter.changeCursor(DonationNpoDAO
					.queryDonationNpos());
			DonationFragment.adapter.notifyDataSetChanged();
		}
	}
}

package com.taiwanmobile.volunteers.v2;

import java.util.Locale;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.view.ViewPager;
import androidx.legacy.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v2.npoview.ItemNpoListFragment;
import com.taiwanmobile.volunteers.v2.utils.CustomViewPager;
import com.taiwanmobile.volunteers.v2.utils.ViewUtils;

public class ItemFragment extends Fragment {
	private final String TAG = getClass().getSimpleName();

	SectionsPagerAdapter mItemSectionsPagerAdapter;
	public CustomViewPager mItemViewPager;

	public ItemFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_item_tab_main, null);
		Log.d(TAG, "view: " + view);
		mItemSectionsPagerAdapter = new SectionsPagerAdapter(getActivity()
				.getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mItemViewPager = (CustomViewPager) view
				.findViewById(R.id.fragment_item_tab_main_pager);
		mItemViewPager.setOffscreenPageLimit(5);
		mItemViewPager.setAdapter(mItemSectionsPagerAdapter);

		mItemViewPager.setCurrentItem(1);
		mItemViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int position) {
						if (position == 3) {
							ViewUtils.setSearchBarTagetToNpos(getActivity());
						} else {
							ViewUtils.setSearchBarTagetToEvents(getActivity());
						}
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
					}

					@Override
					public void onPageScrollStateChanged(int arg0) {
					}
				});
		// // Bind the tabs to the ViewPager
		// PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view
		// .findViewById(R.id.fragment_volunteer_main_pager_title_strip);
		// tabs.setViewPager(mItemViewPager);

		return view;
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			switch (position) {
			case 0:
				return new ItemEventsSearchFragment();
			case 1:
				return new ItemEventListFragment();
			case 2:
				return new LocaionalItemEventListFragment();
			case 3:
				return new ItemNpoListFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 4 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.item_tab_inside_1).toUpperCase(l);
			case 1:
				return getString(R.string.item_tab_inside_2).toUpperCase(l);
			case 2:
				return getString(R.string.item_tab_inside_3).toUpperCase(l);
			case 3:
				return getString(R.string.item_tab_inside_4).toUpperCase(l);
			}
			return null;
		}
	}
}

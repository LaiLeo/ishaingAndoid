package com.taiwanmobile.volunteers.v2;

import java.util.Locale;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import androidx.legacy.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v2.npoview.VolunteerNpoListFragment;
import com.taiwanmobile.volunteers.v2.utils.CustomViewPager;
import com.taiwanmobile.volunteers.v2.utils.ViewUtils;


/*
 * 志工訊息頁面
 */
public class VolunteerFragment extends SupportFragment {
	@SuppressWarnings("unused")
	private final String TAG = "VolunteerFragment";

	SectionsPagerAdapter mSectionsPagerAdapter;
	static public CustomViewPager mVolunteerViewPager;

    public static final String BUNDLE_KEY_TARGET = "target";
    static final String KEY_GENERAL_VOLUNTEER = "general_volunteer";
    static final String KEY_ENTERPRISE_VOLUNTEER = "enterprise_volunteer";
    String target = KEY_GENERAL_VOLUNTEER;

	public VolunteerFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView("+inflater+")");

        if(getArguments() != null){
            Bundle bundle = getArguments();
            target = bundle.getString(BUNDLE_KEY_TARGET, KEY_GENERAL_VOLUNTEER);
        }else{
            Log.w(TAG, "create volunteer fragment without arguments");
        }

		View view = null;
        if(target.equals(KEY_ENTERPRISE_VOLUNTEER)){
            view = inflater.inflate(R.layout.fragment_enterprise_volunteer_tab_main, container, false);
        }else {
            view = inflater.inflate(R.layout.fragment_tab_main, container, false);
        }
        Log.d(TAG, "view: " + view);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity()
				.getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        if(target.equals(KEY_ENTERPRISE_VOLUNTEER)){

            mVolunteerViewPager = (CustomViewPager) view
                    .findViewById(R.id.fragment_enterprise_volunteer_tab_main_pager);
        }else {
            mVolunteerViewPager = (CustomViewPager) view
                    .findViewById(R.id.fragment_tab_main_pager);
        }

		mVolunteerViewPager.setOffscreenPageLimit(5);
		mVolunteerViewPager.setAdapter(mSectionsPagerAdapter);
		mVolunteerViewPager.setCurrentItem(1);
		// // Bind the tabs to the ViewPager
		// PagerTabStrip tabs = (PagerTabStrip) view
		// .findViewById(R.id.fragment_tab_main_pager_title_strip);
		// tabs.setViewPager(mViewPager);
		mVolunteerViewPager
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

		return view;
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            Log.d(TAG, "SectionsPagerAdapter("+fm+")");
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
            Fragment returnFragment = null;
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_KEY_TARGET, target);
			switch (position) {
			case 0:
                // 進階搜尋
                returnFragment = new VolunteerEventsSearchFragment();
                break;
			case 1:
                // 全部活動
                returnFragment = new SubscribedEventListFragment();
                break;
			case 2:
                // 附近活動
                returnFragment = new LocationalVolunteerEventListFragment();
				break;
			case 3:
                // 品牌館
                returnFragment = new VolunteerNpoListFragment();
                break;
            default:
                Log.d(TAG, "volunteer tab out of range");
			}
            if(returnFragment != null) {
                returnFragment.setArguments(bundle);
            }
			return returnFragment;
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
				return getString(R.string.volunteer_tab_inside_1)
						.toUpperCase(l);
			case 1:
				return getString(R.string.volunteer_tab_inside_2)
						.toUpperCase(l);
			case 2:
				return getString(R.string.volunteer_tab_inside_3)
						.toUpperCase(l);
			case 3:
				return getString(R.string.volunteer_tab_inside_4)
						.toUpperCase(l);
			}
			return null;
		}
	}
}

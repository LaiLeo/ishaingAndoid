package com.taiwanmobile.volunteers.v2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.legacy.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.util.StaticResUtil;
import com.taiwanmobile.volunteers.v2.api.BlockingBackendDataSyncTask;
import com.taiwanmobile.volunteers.v2.api.DeviceIdPostTask;
import com.taiwanmobile.volunteers.v2.api.ValidateCredsTask;
import com.taiwanmobile.volunteers.v1.api.V1VersionCheckTask;
import com.taiwanmobile.volunteers.v2.database.DatabaseHelper;
import com.taiwanmobile.volunteers.v2.database.DonationNpoDAO;
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.gcm.GCMUtils;
import com.taiwanmobile.volunteers.v2.loginview.LoginFragment;
import com.taiwanmobile.volunteers.v2.userprofileview.UserProfileFragment;
import com.taiwanmobile.volunteers.v2.utils.CustomViewPager;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;
import com.taiwanmobile.volunteers.v2.utils.ViewUtils;
import com.taiwanmobile.volunteers.v2.utils.roundedimageview.RoundedTransformationBuilder;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
		ActionBar.TabListener, OnClickListener, OnMenuItemClickListener, FragmentManager.OnBackStackChangedListener {//FIH-modify for 頁面跳轉錯亂
	final String TAG = getClass().getSimpleName();
	/**
	 * The {@link PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link androidx.fragment.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link FragmentStatePagerAdapter}.
	 */
	public static SectionsPagerAdapter mSectionsPagerAdapter;
	public static MainActivity mMainActivity;
	public static ImageView userIcon;
	public static AutoCompleteTextView searchView;

	public static Boolean hasLogined = false;
	public static CustomViewPager mViewPager;
	public static DatabaseHelper MyDbHelper;
	public static boolean isDataDownloading = false;
	public static Location currentLocation = null;

	private View mCustomView;

    private static final int TAG_GENERAL_VOLUNTEER_TAB = 0;
    private static final int TAG_ENTERPRISE_VOLUNTEER_TAB = 1;
    private static final int TAG_ITEM_TAB = 2;
    private static final int TAG_DONATION_TAB = 3;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
//        setTheme(R.style.Ap);
		setContentView(R.layout.activity_main);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		mMainActivity = this;

		if (MyDbHelper == null || !MyDbHelper.isOpen()) {
			// Log.e(TAG, "db created");
			MyDbHelper = new DatabaseHelper(this);
			MyDbHelper.getReadableDatabase();
		}

		if (MyPreferenceManager.hasCredentials(this)) {
			new ValidateCredsTask(this).execute();
		}
		// initial background task
		new V1VersionCheckTask(this).execute();
		if (StringUtils.isBlank(MyPreferenceManager.getDeviceId(this))) {
			new DeviceIdPostTask(this).execute();
		}

		new BlockingBackendDataSyncTask(this).execute();
		LoadingDialogFragment dialog = LoadingDialogFragment.newInstance();
		dialog.setCancelable(false);
		dialog.show(getSupportFragmentManager(), "LoadingDialogFragment");

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		if(actionBar != null) {
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
					| ActionBar.DISPLAY_SHOW_HOME);

            actionBar.setDisplayShowTitleEnabled(false);
		}

		View homeIcon = findViewById(android.R.id.home);
		if(homeIcon != null) {
			((View) homeIcon.getParent()).setVisibility(View.GONE);
			homeIcon.setVisibility(View.GONE);
		}

		LayoutInflater mInflater = LayoutInflater.from(this);
		mCustomView = mInflater.inflate(R.layout.view_actionbar, null);
		// binding back button
		ImageView iv = (ImageView) mCustomView
				.findViewById(R.id.action_bar_iv_back);
		iv.setOnClickListener(this);

		// binding '個人履歷' button
		userIcon = (ImageView) mCustomView
				.findViewById(R.id.action_bar_iv_user_profile);
		userIcon.setOnClickListener(this);
		if (MyPreferenceManager.hasCredentials(this)) {
			try {
				String imageFilePath = UserAccountDAO.queryObjectByMe(this).icon;
				Log.d(TAG,"imageFilePath= "+imageFilePath);
				if (StringUtils.isNotBlank(imageFilePath) && !imageFilePath.equalsIgnoreCase("/uploads/default_userprofile_image.png")) {
					int image_size = (int) (40 * getResources()
							.getDisplayMetrics().density);
					Transformation transformation = new RoundedTransformationBuilder()
							.oval(true).build();

					Picasso.with(this)
							//.load(BackendContract.DEPLOYMENT_URL
									//+ imageFilePath)
							.load(StaticResUtil.checkUrl(imageFilePath, false, TAG))
							.resize(image_size, image_size).centerCrop()
							.transform(transformation).into(userIcon);
				}
			} catch (Exception ex) {
				Log.e(TAG, Log.getStackTraceString(ex));
			}
		}
		// binding '...' button
		iv = (ImageView) mCustomView.findViewById(R.id.action_bar_iv_option);
		iv.setOnClickListener(this);

		searchView = (AutoCompleteTextView) mCustomView
				.findViewById(R.id.action_bar_actv_search);

		// searchView.setOnKeyListener(new OnKeyListener() {
		//
		// @Override
		// public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
		// // TODO Auto-generated method stub
		//
		// Toast.makeText(Current_Activity.this, arg1+"",
		// Toast.LENGTH_LONG).show();
		// // return true; - if consumed
		// return false;
		// }
		// });

		// http://stackoverflow.com/questions/13135447/setting-onclicklistner-for-the-drawable-right-of-an-edittext

		if(actionBar != null) {

		ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT);
			actionBar.setCustomView(mCustomView, layout);
//            actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.twmf_orange)));

		}
        setToolBarContentInsets();
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
//		mSectionsPagerAdapter = new SectionsPagerAdapter(
//				(FragmentManager)getSupportFragmentManager());

		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (CustomViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(10);
		// mViewPager.setCurrentItem(0);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setSwipeable(false);//FIH-add this for 大分類之間不能跨越滑動，否則看起來比較混亂

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						Log.d(TAG,"gengqiang->onPageSelected() position="+position);
						actionBar.setSelectedNavigationItem(position);//FIH-add enable this for 如果不設置mViewPager.setSwipeable(false) 允許一直向一個方向滑動page，當跳到大分類時tab也可以跟隨切換
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.

			View view = getLayoutInflater().inflate(R.layout.view_custom_tab,
					null);

			// ImageView icon = (ImageView) view
			// .findViewById(R.id.custom_tab_icon);
			// icon.setImageResource(R.drawable.ic_clock);

			TextView title = (TextView) view
					.findViewById(R.id.custom_tab_title);
			title.setText(mSectionsPagerAdapter.getPageTitle(i));
            ActionBar.Tab tab = actionBar.newTab().setCustomView(view).setTabListener(this);
//            tab.set.

			actionBar.addTab(tab);
		}

		onNewIntent(getIntent());
		//FIH-add for 進入子fragment會把tab顯示出來，原因是新launch的fragment的onCreate里hide tab要早與當前fragment destroy里的show tab
		getFragmentManager().addOnBackStackChangedListener(this);

	}

    private void setToolBarContentInsets(){
        if(getSupportActionBar() == null){
            Log.w(TAG, "getSupportActionBar() == null");
            return;
        }
        if(getSupportActionBar().getCustomView() == null){
            Log.w(TAG, "getSupportActionBar().getCustomView() == null");
            return;

        }
        Toolbar toolbar= (Toolbar)getSupportActionBar().getCustomView().getParent();
        toolbar.setContentInsetsAbsolute(0,0);
    }

	@Override
	public void onBackStackChanged() {//FIH-add for 進入子fragment會把tab顯示出來，原因是新launch的fragment的onCreate里hide tab要早與當前fragment destroy里的show tab
		//TODO: 這個方法是最後調用，所以不管前面hide/show了，代碼優化的話可以把之前的SupportFragment里的hide/show拿掉；如果進一步優化的話，除了viewpage里的主fragment，其他launch的fragment都應該放新的Activity
		int count = getFragmentManager().getBackStackEntryCount();
		Log.e(TAG,"gengqiang->action_bar->MainActivity->onBackStackChanged() count="+count);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			if(count == 0) {
				//show
				actionBar.setNavigationMode(
						ActionBar.NAVIGATION_MODE_TABS);
			} else {
				//hide
				actionBar.setNavigationMode(
						ActionBar.NAVIGATION_MODE_STANDARD);
			}

		}

	}

	public class TipDialog extends Dialog {
		public TipDialog(Context context, String title, String content) {
			super(context, R.style.message_dialog);
			setContentView(R.layout.title_dialog);

			TextView tv = (TextView) this
					.findViewById(R.id.title_dialog_tv_title);
			tv.setText(title);
			tv = (TextView) this.findViewById(R.id.title_dialog_tv_content);
			tv.setText(content);
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent event) {
			this.dismiss();
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
		//FIH-add for tab選中後沒有充滿整個tab
		((TextView)(tab.getCustomView().findViewById(R.id.custom_tab_title))).setTextColor(getResources().getColor(R.color.tab_selected));
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
		Boolean isOnNpoListFragment = false;
		if(tab.getPosition() == TAG_ENTERPRISE_VOLUNTEER_TAB) {
			new AlertDialog.Builder(this)
					.setMessage("此專區僅供企業員工報名，欲報名一般志工活動請點選「志工訊息」")
					.setPositiveButton(getString(android.R.string.ok), null)
					.show();
		}
		if (tab.getPosition() == TAG_GENERAL_VOLUNTEER_TAB // 0
				&& VolunteerFragment.mVolunteerViewPager != null) {
			if (VolunteerFragment.mVolunteerViewPager.getCurrentItem() == 3) {
				isOnNpoListFragment = true;
			}
		}
        // FIXME set item search bar
//        else if (tab.getPosition() == TAG_ITEM_TAB // 2
//				&& ItemFragment.mItemViewPager != null) {
//			if (ItemFragment.mItemViewPager.getCurrentItem() == 3) {
//				isOnNpoListFragment = true;
//			}
//		}

		if (tab.getPosition() == TAG_DONATION_TAB) {
			ViewUtils.setSearchBarTagetToDonationNpos(this);
		} else if (isOnNpoListFragment) {
			ViewUtils.setSearchBarTagetToNpos(this);
		} else {
			ViewUtils.setSearchBarTagetToEvents(this);
		}
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
								FragmentTransaction fragmentTransaction) {
		//FIH-add for tab選中後沒有充滿整個tab
		((TextView)(tab.getCustomView().findViewById(R.id.custom_tab_title))).setTextColor(getResources().getColor(R.color.tab_unselected));
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
								FragmentTransaction fragmentTransaction) {
	}

	public interface SecondFragmentListener {
		void onSwitchToNextFragment();
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
            Log.d(TAG, "getItem(" + position + ")");
            switch (position) {
                case TAG_GENERAL_VOLUNTEER_TAB:
                    // 志工訊息(一般志工)
                    return new VolunteerFragment();
                case TAG_ENTERPRISE_VOLUNTEER_TAB:
                    // 企業志工
                    VolunteerFragment fragment = new VolunteerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(VolunteerFragment.BUNDLE_KEY_TARGET, VolunteerFragment.KEY_ENTERPRISE_VOLUNTEER);
                    fragment.setArguments(bundle);
                    return fragment;
                case TAG_ITEM_TAB:
                    // 物資訊息
                    return new ItemFragment();
                case TAG_DONATION_TAB:
                    // 我要捐款
                    return new DonationFragment();
            }
            return null;
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case TAG_GENERAL_VOLUNTEER_TAB: // 0
                    return getString(R.string.title_section1).toUpperCase(l);
                case TAG_ENTERPRISE_VOLUNTEER_TAB: // 1
                    return getString(R.string.title_section_enterprise_volunteer).toUpperCase(l);
                case TAG_ITEM_TAB: // 2
                    return getString(R.string.title_section2).toUpperCase(l);
                case TAG_DONATION_TAB: // 3
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
		}
	}

	@Override
	public void onClick(View arg0) {

		// Toast.makeText(getApplicationContext(), "Refresh Clicked!",
		// Toast.LENGTH_LONG).show();
        switch (arg0.getId()) {
            case R.id.action_bar_iv_back:
                this.onBackPressed();
                break;
            case R.id.action_bar_iv_option:
                onActionBarIvOptionClick(arg0);
                break;
            case R.id.action_bar_iv_user_profile:
                onActionBarIvUserProfileClick(arg0);
                break;
        }
	}

    private void onActionBarIvOptionClick(View arg0){
        PopupMenu popupMenu = new PopupMenu(this, arg0);
        popupMenu.getMenuInflater().inflate(R.menu.popupmenu_settings,
                popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.getMenu().findItem(R.id.settings_menu_add_npo)
                .setVisible(false);
        if (!MyPreferenceManager.hasCredentials(this)) {
            popupMenu.getMenu().findItem(R.id.settings_menu_logout)
                    .setVisible(false);
        }
        popupMenu.show();
    }

    private void onActionBarIvUserProfileClick(View arg0){
        if (!MyPreferenceManager.hasCredentials(this)) { // is not Login
            FragHelper
                    .replace(this, new LoginFragment().restartOnLogined());
        } else {
			FragHelper.replace(this, UserProfileFragment.newInstance(
					MyPreferenceManager.getUserId(this)));
		}
    }

	@Override
	public void onBackPressed() {
		// if (getActionBar().getNavigationMode() ==
		// ActionBar.NAVIGATION_MODE_STANDARD) {
		// getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// }
		((EditText) this.findViewById(R.id.action_bar_actv_search)).setText("");
		super.onBackPressed();
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.settings_menu_tutorial:
			DialogFactory.createFullScreenInfoDialog(this,
					R.layout.dialog_setting_tutorial).show();
			return true;
		case R.id.settings_menu_about:
            PackageInfo pInfo = null;
            String version = "unknown";
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
			new DialogFactory.TitleMessageButtonDialog(this, "關於微樂志工",
					String.format(getResources().getString(R.string.twmf_about), version)).show();
			return true;
			//FIH-add for 成為夥伴 start
			case R.id.settings_menu_partner:
				Intent partnerIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(getResources().getString(R.string.twmf_partner_site)));
				startActivity(partnerIntent);
				return true;
			//FIH-add for 成為夥伴 end
		case R.id.settings_menu_policy:
			new DialogFactory.TitleMessageButtonDialog(this, "服務條款",
					getResources().getString(R.string.twmf_policy)).show();
			return true;
		case R.id.settings_menu_faq:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(getResources().getString(R.string.twmf_faq_site)));
			startActivity(browserIntent);
			return true;
//		case R.id.settings_menu_rate:
//			Intent intent = new Intent(
//					Intent.ACTION_VIEW,
//					Uri.parse(getResources().getString(R.string.twmf_rate_site)));
//			startActivity(intent);
//			return true;
		case R.id.settings_menu_facebook:
			startActivity(
					new Intent(
							Intent.ACTION_VIEW,
							Uri.parse(getResources().getString(R.string.twmf_facebook))
					)
			);
			return true;
		case R.id.settings_menu_add_npo:
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources()
					.getString(R.string.twmf_add_npo_site)));
			startActivity(i);
			return true;
		case R.id.settings_menu_setting:
			new EventTipDialogFragment(this).show();
			return true;
		case R.id.settings_menu_logout:
			MyPreferenceManager.logout(this);
			return true;
		default:
			return false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//FIH-add for 進入子fragment會把tab顯示出來，原因是新launch的fragment的onCreate里hide tab要早與當前fragment destroy里的show tab
		getFragmentManager().removeOnBackStackChangedListener(this);
	}

	public boolean isExit = false;

	@Override
	public void finish() {
		if (isExit) {
			super.finish();
		} else {
			AlertDialog.Builder b = new AlertDialog.Builder(this)
					.setMessage("您確定要關閉APP嗎?")
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									isExit = true;
									MyDbHelper.close();
									// Log.e(TAG, "db closed");
									finish();
								}
							}).setNegativeButton("返回", null)
					.setCancelable(true);
			AlertDialog ret = b.create();
			// TODO
			// ret.setOnShowListener(DialogFactory.TEXT_SIZE_ADJUSTER);
			ret.show();
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent" );
		Bundle extras = intent.getExtras();
		if (extras == null) {
            return;
        }
        if (!extras.containsKey(GCMUtils.EXTRA_MESSAGE_KEY)) {
            return;
        }
        try {
            ActionBar actionBar = getSupportActionBar();
            String event_id = extras.getString(GCMUtils.EXTRA_MESSAGE_KEY);
            String page_type = extras.getString(GCMUtils.EXTRA_PAGE_TYPE_KEY,"");
            if (!StringUtils.isNotBlank(event_id)) {
                return;
            }
            long id = Long.parseLong(event_id);
            Log.d(TAG, "onNewIntent: id: " + event_id);
            if(page_type.equals(GCMUtils.PAGE_TYPE_GENERAL_VOLUNTEER_LIST)) {
                actionBar.selectTab(actionBar.getTabAt(TAG_GENERAL_VOLUNTEER_TAB));
            }else if (page_type.equals(GCMUtils.PAGE_TYPE_ENTERPRISE_VOLUNTEER_LIST)) {
                actionBar.selectTab(actionBar.getTabAt(TAG_ENTERPRISE_VOLUNTEER_TAB));
            }else if (page_type.equals(GCMUtils.PAGE_TYPE_ITEM_LIST)){
                actionBar.selectTab(actionBar.getTabAt(TAG_ITEM_TAB));
            }else if (page_type.equals(GCMUtils.PAGE_TYPE_5180_LIST)) {
                actionBar.selectTab(actionBar.getTabAt(TAG_DONATION_TAB));
            }else if (page_type.equals(GCMUtils.PAGE_TYPE_5180_DETAIL)){
                DonationNpoDAO dao = DonationNpoDAO.queryObjectById(id);
                if(dao == null){
                    return;
                }
                FragHelper.replace(this,new DonationNpoDetailFragment().setNpoId(id));
            }else {
                EventDAO dao = EventDAO.getObjectById(id);
                if (dao == null) {
                    return;
                }

                if (dao.isVolunteer) {
                    if(dao.npo.isEnterprise) {
                        actionBar.selectTab(actionBar.getTabAt(TAG_ENTERPRISE_VOLUNTEER_TAB));
                    }else{
                        // not enterprise
                        actionBar.selectTab(actionBar.getTabAt(TAG_GENERAL_VOLUNTEER_TAB));
                    }
                    FragHelper.replace(this,
                            new VolunteerEventDetailFragment()
                                    .setEventId(id));
                } else {
                    actionBar.selectTab(actionBar.getTabAt(TAG_ITEM_TAB));
                    FragHelper.replace(this,
                            new ItemEventDetailFragment()
                                    .setEventId(id));
                }

            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("GCM", Log.getStackTraceString(e));
        }


	}
}

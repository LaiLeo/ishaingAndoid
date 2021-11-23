package com.taiwanmobile.volunteers.v2.npoview;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.cursoradapter.widget.SimpleCursorAdapter;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.squareup.picasso.Picasso;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.util.StaticResUtil;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.FilteredEventListFragment;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.SupportFragment;
import com.taiwanmobile.volunteers.v2.api.SubscribeNPOTask;
import com.taiwanmobile.volunteers.v2.api.UnSubscribeNPOTask;
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.database.NpoDAO;
import com.taiwanmobile.volunteers.v2.database.SubscribedNpoDAO;
import com.taiwanmobile.volunteers.v2.utils.AdapterBuilder;
import com.taiwanmobile.volunteers.v2.utils.AdapterBuilder.DoubleBinder;
import com.taiwanmobile.volunteers.v2.utils.AdapterBuilder.IntegerBinder;
import com.taiwanmobile.volunteers.v2.utils.AdapterBuilder.StringBinder;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;

public class NpoDetailFragment extends SupportFragment implements OnClickListener {
	final String TAG = getClass().getSimpleName();

	private long mNpoId;
	public static NpoDetailFragment npoObj;

	private Cursor mCursor;
	private NpoDAO mNpoObject;

	public NpoDetailFragment setNpoId(long id) {
		mNpoId = id;
		npoObj = this;

		mCursor = NpoDAO.querCursorById(mNpoId);
		try {
			mNpoObject = NpoDAO.queryObjectById(mNpoId);
		} catch (SQLException e) {
			Log.e(TAG, "no such npo id");
			mNpoObject = null;
		}
		// initialize(API_KEY, this);

		return this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		hideActionBarTabs();

		SimpleCursorAdapter binder = new AdapterBuilder()
				.set(getActivity())
				.set(R.layout.fragment_npo_detail)
				.set(mCursor)
				.bind(NpoDAO.DATABASE_COLUMN_ICON,
						R.id.fragment_npo_detail_iv_npo_icon,
						new StringBinder() {
							@Override
							public void bind(View v, String val) {
								Picasso.with(getActivity())
										//.load(BackendContract.DEPLOYMENT_STATIC_URL
												//+ val)
										.load(StaticResUtil.checkUrl(val, true, TAG))
										.into((ImageView) v);
							}
						})
				.bind(NpoDAO.DATABASE_COLUMN_NAME,
						R.id.fragment_npo_detail_tv_subject)
				.bind(NpoDAO.DATABASE_COLUMN_DESCRIPTION,
						R.id.fragment_npo_detail_tv_description,
						new StringBinder() {

							@Override
							public void bind(View v, String val) {
								TextView tv = (TextView) v;
								tv.setText(val);
								tv.setMovementMethod(LinkMovementMethod
										.getInstance());
							}
						})
				.bind(NpoDAO.DATABASE_COLUMN_CONTACT_WEBSITE,
						R.id.fragment_npo_detail_tv_npo_website,
						new StringBinder() {

							@Override
							public void bind(View v, String val) {
								TextView npo_website = (TextView) v;
								npo_website.setText(Html.fromHtml("<a href=\""
										+ val + "\">" + val + "</a>"));
								npo_website
										.setMovementMethod(LinkMovementMethod
												.getInstance());
							}
						})
				.bind(NpoDAO.DATABASE_COLUMN_TOTAL_RATING_SCORE,
						R.id.fragment_npo_detail_rb_score, new DoubleBinder() {

							@Override
							public void bind(View v, Double val) {
								RatingBar rb = (RatingBar) v;
								rb.setOnTouchListener(new OnTouchListener() {
									@Override
									public boolean onTouch(View v,
											MotionEvent event) {
										return true;
									}
								});
								rb.setFocusable(false);
								Float score = mNpoObject.getAverageScore();
								rb.setRating(score);
							}
						})
				.bind(NpoDAO.DATABASE_COLUMN_RATING_USER_NUM,
						R.id.fragment_npo_detail_tv_score, new IntegerBinder() {

							@Override
							public void bind(View v, Integer val) {
								TextView tv = (TextView) v;
								tv.setText(val + " 人");
							}
						})
				.bind(NpoDAO.DATABASE_COLUMN_EVENT_NUM,
						R.id.fragment_npo_detail_tv_event_num)
				.bind(NpoDAO.DATABASE_COLUMN_JOINED_USER_NUM,
						R.id.fragment_npo_detail_tv_event_joined_num)
				.bind(NpoDAO.DATABASE_COLUMN_SUBSCRIBED_USER_NUM,
						R.id.fragment_npo_detail_tv_subscribed_num)
				.bind(NpoDAO.DATABASE_COLUMN_YOUTUBE,
						R.id.fragment_npo_detail_ytpv, new StringBinder() {
							@Override
							public void bind(View v, final String val) {
								if (StringUtils.isBlank(val)) {
									v.setVisibility(View.GONE);
									return;
								}

								try {
									YouTubePlayerFragment youTubePlayerFragment = YouTubePlayerFragment
											.newInstance();

//                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                        getChildFragmentManager()
                                                .beginTransaction()
                                                .add(R.id.fragment_npo_detail_ytpv,
                                                        youTubePlayerFragment)
                                                .commit();
//                                    }else {
//                                        Log.w(TAG, "versoin not support!!");
//                                    }

									youTubePlayerFragment.initialize(
											BackendContract.YOUTUBE_API_KEY,
											new OnInitializedListener() {

												@Override
												public void onInitializationSuccess(
														Provider arg0,
														final YouTubePlayer youTubePlayer,
														boolean b) {
													youTubePlayer
															.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
													// Tell the player how
													// to
													// control the change
													// youTubePlayer
													// .setOnFullscreenListener(new
													// OnFullscreenListener() {
													// @Override
													// public void onFullscreen(
													// boolean arg0) {
													// if (arg0) {
													// youTubePlayer
													// .setFullscreen(false);
													// Intent intent = new
													// Intent(
													// Intent.ACTION_VIEW,
													// Uri.parse("vnd.youtube:"
													// + val));
													// intent.putExtra(
													// "VIDEO_ID",
													// val);
													// intent.putExtra(
													// "force_fullscreen",
													// true);
													// startActivity(intent);
													// }
													// }
													// });
													if (!b) {
														youTubePlayer
																.loadVideo(val);
													}
													// youTubePlayer.pause();
												}

												@Override
												public void onInitializationFailure(
														Provider arg0,
														YouTubeInitializationResult arg1) {
												}
											});

								} catch (Exception e) {
									Log.e(TAG, Log.getStackTraceString(e));
								}
							}
						})
				.bind(NpoDAO.DATABASE_COLUMN_ID,
						R.id.fragment_npo_detail_bt_subscribed,
						new StringBinder() {

							@Override
							public void bind(View v, String val) {
								Button b = (Button) v;
								if (MyPreferenceManager
										.hasCredentials(getActivity())) {
									try {
										Boolean is_npo_subscribed = SubscribedNpoDAO
												.isUserSubscribed(mNpoId);

										if (is_npo_subscribed) {
											b.setBackgroundResource(R.drawable.ic_subscribed);
										} else {
											b.setBackgroundResource(R.drawable.ic_nonsubscribed);
										}
										b.setOnClickListener(npoObj);
									} catch (SQLException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									b.setVisibility(View.GONE);
								}
							}
						}).build();

		View head = binder.getView(0, null, null);

		head.findViewById(R.id.fragment_npo_detail_btn_events)
				.setOnClickListener(this);
		// ListView lv = (ListView) head
		// .findViewById(R.id.fragment_npo_detail_lv_events);
		// lv.setDivider(new ColorDrawable(getResources().getColor(
		// R.color.twmf_orange))); // argb = transparent
		// lv.setDividerHeight(getResources().getInteger(
		// R.integer.listview_divider_height));
		// lv.setAdapter(new NpoEventsAdapter(getActivity(), mNpoId));

		return head;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_npo_detail_bt_subscribed:
			Button b = (Button) v;
			Drawable d = v.getResources().getDrawable(R.drawable.ic_subscribed);
			if (b.getBackground().getConstantState()
					.equals(d.getConstantState())) {
				new UnSubscribeNPOTask(getActivity(), mNpoId, b).execute();
			} else {
				new SubscribeNPOTask(getActivity(), mNpoId, b).execute();
			}
			break;
		case R.id.fragment_npo_detail_btn_events:
			Cursor c = EventDAO.queryAllVolunteerEventsByNpoId(mNpoId);
			if (c.getCount() == 0) {
				new DialogFactory.TitleMessageDialog(getActivity(), "沒有活動",
						"該組織尚未舉辦活動").show();
			} else {
				FragHelper.replace(getActivity(),
						new FilteredEventListFragment(c));
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		showActionBarTabs();
	}

	// @Override
	// public void onInitializationFailure(Provider arg0,
	// YouTubeInitializationResult arg1) {
	// Toast.makeText(getActivity(), "Failured to Initialize!",
	// Toast.LENGTH_LONG).show();
	// }
	//
	// @Override
	// public void onInitializationSuccess(Provider provider,
	// YouTubePlayer player, boolean wasRestored) {
	// /** Start buffering **/
	// if (!wasRestored) {
	// player.cueVideo(VIDEO_ID);
	// }
	// }

}

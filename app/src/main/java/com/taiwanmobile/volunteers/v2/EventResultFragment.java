package com.taiwanmobile.volunteers.v2;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.util.StaticResUtil;
import com.taiwanmobile.volunteers.v2.api.FocusEventTask;
import com.taiwanmobile.volunteers.v2.api.ReplyPostTask;
import com.taiwanmobile.volunteers.v2.api.UnfocusEventTask;
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.database.EventResultImageDAO;
import com.taiwanmobile.volunteers.v2.database.FocusedEventDAO;
import com.taiwanmobile.volunteers.v2.database.RegisteredEventDAO;
import com.taiwanmobile.volunteers.v2.database.ReplyDAO;
import com.taiwanmobile.volunteers.v2.database.UserAccountDAO;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;
import com.taiwanmobile.volunteers.v2.utils.ImageCache;
import com.taiwanmobile.volunteers.v2.utils.ImageUtils;
import com.taiwanmobile.volunteers.v2.utils.UriUtils;
import com.taiwanmobile.volunteers.v2.utils.ViewUtils;

@SuppressLint("ValidFragment")
public class EventResultFragment extends SupportFragment implements OnClickListener {
	private final String TAG = getClass().getSimpleName();

	private static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 2331;

	private final EventResultFragment fragmentObject;
	private final EventDAO mEventObject;
	public ListView replyListView;
	public ReplyListAdapter adapter;

	private Uri outputFileUri;
	private Pair<Uri, Uri> cached;

	@SuppressLint("ValidFragment")
	public EventResultFragment(EventDAO obj) {
		fragmentObject = this;
		mEventObject = obj;
		cached = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		hideActionBarTabs();

		View rootView = inflater.inflate(R.layout.fragment_event_result_detail,
				container, false);
		((TextView) rootView
				.findViewById(R.id.fragment_event_result_tv_subject))
				.setText(mEventObject.subject);

		rootView.findViewById(R.id.fragment_event_result_iv_switch)
				.setOnClickListener(this);
		rootView.findViewById(R.id.fragment_event_result_btn_focus)
				.setOnClickListener(this);
		rootView.findViewById(R.id.fragment_event_result_iv_share)
				.setOnClickListener(this);
		rootView.findViewById(R.id.fragment_event_result_iv_reply_commit_image)
				.setOnClickListener(this);
		rootView.findViewById(R.id.fragment_event_result_btn_reply_comment)
				.setOnClickListener(this);

		if (!MyPreferenceManager.hasCredentials(getActivity())) {
			rootView.findViewById(R.id.fragment_event_result_btn_focus)
					.setVisibility(View.GONE);
			rootView.findViewById(R.id.fragment_event_result_ll).setVisibility(
					View.GONE);
		}
		TextView replyNumTextView = (TextView) rootView
				.findViewById(R.id.fragment_event_result_tv_reply_num);
		replyNumTextView.setText(mEventObject.replyNum + "人回覆。");

		// set focus button
		Button focusButton = (Button) rootView
				.findViewById(R.id.fragment_event_result_btn_focus);

		try {
			Boolean isRegistered = RegisteredEventDAO
					.isUserRegistered(mEventObject.id);
			Boolean isFocused = FocusedEventDAO.isUserFocused(mEventObject.id);
			if (isRegistered) {
				focusButton.setBackgroundResource(R.drawable.ic_registered);
			} else if (isFocused) {
				focusButton.setBackgroundResource(R.drawable.ic_subscribed);
			} else {
				focusButton.setBackgroundResource(R.drawable.ic_nonsubscribed);
			}
		} catch (SQLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}

		// set description view or resulting images view
		if (mEventObject.images.size() == 0) {
			rootView.findViewById(R.id.fragment_event_result_hsv_images)
					.setVisibility(View.GONE);
			((TextView) rootView
					.findViewById(R.id.fragment_event_result_tv_description))
					.setText(mEventObject.description);
		} else {
			rootView.findViewById(R.id.fragment_event_result_tv_description)
					.setVisibility(View.GONE);
			LinearLayout llResultImage = (LinearLayout) rootView
					.findViewById(R.id.fragment_event_result_ll_images);

			float imageMarginDP = 16 * getActivity().getResources()
					.getDisplayMetrics().density;
			LinearLayout.LayoutParams imageMarginParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			// imageMarginParams.height = (int) (100 * imageMarginDP);
			// imageMarginParams.width = (int) (100 * imageMarginDP);
			imageMarginParams.setMargins(8, 0, 0, 0);
			for (EventResultImageDAO dao : mEventObject.images) {
				ImageView imageView = new ImageView(getActivity());
				imageView.setScaleType(ScaleType.CENTER_CROP);
				imageView.setLayoutParams(imageMarginParams);
				imageView.setTag(dao.image);
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ImageView view = (ImageView) v;
						FragHelper.replace(
								getActivity(),
								new DetailImageViewFragment(view, (String) view
										.getTag()));
					}
				});
				Picasso.with(getActivity())
						//.load(BackendContract.DEPLOYMENT_STATIC_URL + dao.image)
						.load(StaticResUtil.checkUrl(dao.image, true, TAG))
						.into(imageView);
				llResultImage.addView(imageView);

			}
		}

		// set reply view
		replyListView = (ListView) rootView
				.findViewById(R.id.fragment_event_result_lv_reply);
		adapter = new ReplyListAdapter(getActivity(), mEventObject.id);
		replyListView.setAdapter(adapter);
		ViewUtils.setListViewSize(replyListView);

		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
        showActionBarTabs();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_event_result_iv_switch:
			getActivity().onBackPressed();
			break;
		case R.id.fragment_event_result_btn_focus:
			Button b = (Button) v;
			Drawable subscribed = v.getResources().getDrawable(
					R.drawable.ic_subscribed);
			Drawable nonsubscribed = v.getResources().getDrawable(
					R.drawable.ic_nonsubscribed);
			if (b.getBackground().getConstantState()
					.equals(subscribed.getConstantState())) {
				new UnfocusEventTask(getActivity(), mEventObject.id, b)
						.execute();
			} else if (b.getBackground().getConstantState()
					.equals(nonsubscribed.getConstantState())) {
				new FocusEventTask(getActivity(), mEventObject.id, b).execute();
			}
			break;
		case R.id.fragment_event_result_iv_share:
			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			// TextView subjectTextView = (TextView)
			// v.getRootView().findViewById(
			// R.id.fr);
			// TextView descriptionTextView = (TextView) v.getRootView()
			// .findViewById(R.id.fragment_event_detail_tv_description);

			List<Intent> targetedShareIntents = new ArrayList<Intent>();
			List<ResolveInfo> resInfo = getActivity().getPackageManager()
					.queryIntentActivities(shareIntent, 0);
			if (!resInfo.isEmpty()) {
				for (ResolveInfo resolveInfo : resInfo) {
					String packageName = resolveInfo.activityInfo.packageName;

					if (StringUtils
							.equals(packageName, "com.google.android.gm")) {
						Intent targetedShareIntent = new Intent(
								android.content.Intent.ACTION_SEND);
						targetedShareIntent.setType("text/plain");
						targetedShareIntent.putExtra(
								android.content.Intent.EXTRA_SUBJECT,
								mEventObject.subject);
						targetedShareIntent.putExtra(
								android.content.Intent.EXTRA_TEXT,
								mEventObject.description + "\n\n\n 活動網址: "
										+ BackendContract.EVENT_SHARING_URL
										+ mEventObject.id + "/");

						targetedShareIntent.setPackage(packageName);
						targetedShareIntents.add(targetedShareIntent);
					} else if (StringUtils.equals(packageName,
							"com.facebook.katana")) {
						Intent targetedShareIntent = new Intent(
								android.content.Intent.ACTION_SEND);
						targetedShareIntent.setType("text/plain");
						targetedShareIntent.putExtra(
								android.content.Intent.EXTRA_SUBJECT,
								mEventObject.subject);
						targetedShareIntent.putExtra(
								android.content.Intent.EXTRA_TEXT,
								BackendContract.EVENT_SHARING_URL
										+ mEventObject.id);

						targetedShareIntent.setPackage(packageName);
						targetedShareIntents.add(targetedShareIntent);
					} else if (StringUtils.equals(packageName,
							"jp.naver.line.android")) {
						Intent targetedShareIntent = new Intent(
								android.content.Intent.ACTION_SEND);
						targetedShareIntent.setType("text/plain");
						targetedShareIntent.putExtra(
								android.content.Intent.EXTRA_TEXT,
								BackendContract.EVENT_SHARING_URL
										+ mEventObject.id);

						targetedShareIntent.setPackage(packageName);
						// line sharing will cause app crash
						targetedShareIntents.add(targetedShareIntent);
					} else {
						Intent targetedShareIntent = new Intent(
								android.content.Intent.ACTION_SEND);
						targetedShareIntent.setType("text/plain");
						targetedShareIntent.putExtra(
								android.content.Intent.EXTRA_SUBJECT,
								mEventObject.subject);
						targetedShareIntent.putExtra(
								android.content.Intent.EXTRA_TEXT,
								BackendContract.EVENT_SHARING_URL
										+ mEventObject.id);
						targetedShareIntent.setPackage(packageName);
						targetedShareIntents.add(targetedShareIntent);
					}
				}
				Intent chooserIntent = Intent.createChooser(
						targetedShareIntents.remove(0), "選擇分享的App");

				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
						targetedShareIntents.toArray(new Parcelable[] {}));
				startActivity(chooserIntent);
			}
			break;
		case R.id.fragment_event_result_iv_reply_commit_image:
			ImageView iv = (ImageView) v;
			outputFileUri = ImageUtils.createCaptureUri(getActivity());

			final List<Intent> cameraIntents = new ArrayList<Intent>();
			final Intent captureIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			final PackageManager packageManager = getActivity()
					.getPackageManager();
			final List<ResolveInfo> listCam = packageManager
					.queryIntentActivities(captureIntent, 0);
			for (ResolveInfo res : listCam) {
				final String packageName = res.activityInfo.packageName;
				final Intent intent = new Intent(captureIntent);
				intent.setComponent(new ComponentName(
						res.activityInfo.packageName, res.activityInfo.name));
				intent.setPackage(packageName);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

				intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION,
						ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				cameraIntents.add(intent);
			}

			// Filesystem.
			Intent galleryIntent = new Intent(Intent.ACTION_PICK);
			galleryIntent.setType(ImageUtils.INTENT_TYPE_SELECT_IMAGE);

			// Chooser of filesystem options.
			final Intent chooserIntent = Intent.createChooser(galleryIntent,
					this.getString(R.string.image_select_intent));

			// Add the camera options.
			chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
					cameraIntents.toArray(new Parcelable[] {}));

			startActivityForResult(chooserIntent,
					SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
			break;
		case R.id.fragment_event_result_btn_reply_comment:
			String message = ((EditText) v.getRootView().findViewById(
					R.id.fragment_event_result_et_reply_comment)).getText()
					.toString();
			if (StringUtils.isBlank(message) && cached == null) {
				break;
			}
			try {
				if (cached == null) {
					new ReplyPostTask(getActivity(), this, mEventObject,
							message).execute();
				} else {
					new ReplyPostTask(getActivity(), this, mEventObject,
							message, cached.second.getPath()).execute();
				}
				((EditText) v.getRootView().findViewById(
						R.id.fragment_event_result_et_reply_comment))
						.setText("");
				cached = null;
				ImageView mImageView = (ImageView) v
						.getRootView()
						.findViewById(
								R.id.fragment_event_result_iv_reply_commit_image);
				mImageView.setImageBitmap(null);
				mImageView.setBackgroundResource(R.drawable.ic_photo);
				FragHelper.hideKeyboard(getActivity());
			} catch (Exception e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != 0) {
			if (requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE) {
				final boolean isCamera;
				if (data == null) {
					isCamera = true;
				} else {
					final String action = data.getAction();
					if (action == null) {
						isCamera = false;
						Log.e(TAG, "false");
					} else {
						// Log.e(TAG, "action = " + action);
						// Log.e(TAG,
						// android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						// isCamera = action
						// .equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
						isCamera = true;
					}
				}
				Uri selectedImageUri;
				try {
					if (isCamera) {
						selectedImageUri = outputFileUri;
						cached = ImageCache
								.put(getActivity(), selectedImageUri);
						String path = selectedImageUri.getPath();
						new File(path).delete();
						Log.d(TAG, "deleted " + path);
					} else {
						selectedImageUri = data == null ? null : data.getData();
						Uri uri = Uri.fromFile(new File(UriUtils
								.getRealPathFromURI(getActivity(),
										selectedImageUri)));
						cached = ImageCache.put(getActivity(), uri);
					}

					ImageView iv = (ImageView) getView().findViewById(
							R.id.fragment_event_result_iv_reply_commit_image);
					iv.setBackgroundResource(android.R.color.transparent);
					iv.setImageURI(cached.second);
					iv.setTag(cached.second);
					iv.setOnClickListener(mIbDelListener);

				} catch (Exception ex) {
					Log.e(TAG, Log.getStackTraceString(ex));
				}
			}
		}
	}

	private final OnClickListener mIbDelListener = new OnClickListener() {

		@Override
		public void onClick(final View arg0) {
			AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
					.setMessage(
							arg0.getResources().getString(
									R.string.image_delete_dialog_title))
					.setPositiveButton(
							arg0.getResources().getString(
									R.string.image_delete_dialog_ok),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									ImageView iv = (ImageView) arg0;
									iv.setImageBitmap(null);
									iv.setBackgroundResource(R.drawable.ic_photo);
									iv.setOnClickListener(fragmentObject);
									cached = null;
									Uri uri = (Uri) iv.getTag();
									ImageCache.delete(getActivity(), uri
											.getLastPathSegment().toString());
								}
							})
					.setNegativeButton(
							arg0.getResources().getString(
									R.string.image_delete_dialog_cancel), null)
					.setCancelable(true);
			AlertDialog ret = b.create();
			ret.setOnShowListener(DialogFactory.TEXT_SIZE_ADJUSTER);
			ret.show();
		}
	};

	public class ReplyListAdapter extends SimpleCursorAdapter implements
			ViewBinder {

		Context mContext;

		public ReplyListAdapter(Context c, Long eventId) {
			super(c, R.layout.listview_reply_list, ReplyDAO
					.queryByEventId(eventId), new String[] {
					ReplyDAO.DATABASE_COLUMN_USER_ICON,
					ReplyDAO.DATABASE_COLUMN_USER_NAME,
					ReplyDAO.DATABASE_COLUMN_MESSAGE,
					ReplyDAO.DATABASE_COLUMN_IMAGE }, new int[] {
					R.id.listview_reply_list_iv_usericon,
					R.id.listview_reply_list_tv_username,
					R.id.listview_reply_list_tv_content,
					R.id.listview_reply_list_iv_image },
					SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			mContext = c;
			setViewBinder(this);
		}

		@Override
		public boolean setViewValue(View arg0, Cursor arg1, int arg2) {
			if (arg2 == arg1.getColumnIndex(ReplyDAO.DATABASE_COLUMN_USER_ICON)) {
				String link = arg1.getString(arg2);
				ImageView iv = (ImageView) arg0;
				try {
					Long replyId = arg1.getLong(arg1
							.getColumnIndex(ReplyDAO.DATABASE_COLUMN_ID));
					final UserAccountDAO user = ReplyDAO.getObjectById(replyId).user;
					iv.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (user.isPublic) {
                                UserDataFragment fragment = new UserDataFragment();
                                fragment.setUserAccountObject(user);
								FragHelper.replace(getActivity(),
                                        fragment);
							} else {
								new DialogFactory.TitleMessageDialog(mContext,
										"無法顯示", "此帳號沒有開放個人履歷喔").show();
							}
						}
					});
				} catch (SQLException ex) {
					Log.e(TAG, Log.getStackTraceString(ex));
				}
				Picasso.with(mContext)
						//.load(BackendContract.DEPLOYMENT_STATIC_URL + link)
						.load(StaticResUtil.checkUrl(link, true, TAG))
						.placeholder(R.drawable.ic_downloading)
						.error(R.drawable.ic_no_photo).into(iv);
				return true;
			} else if (arg2 == arg1
					.getColumnIndex(ReplyDAO.DATABASE_COLUMN_IMAGE)) {
				String link = arg1.getString(arg2);
				ImageView iv = (ImageView) arg0;
				if (link.length() > 4) {
					iv.setVisibility(View.VISIBLE);
					iv.setTag(link);
					iv.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							ImageView view = (ImageView) v;
							FragHelper.replace(getActivity(),
									new DetailImageViewFragment(view,
											(String) view.getTag()));
						}
					});
					Picasso.with(mContext)
							//.load(BackendContract.DEPLOYMENT_STATIC_URL + link)
							.load(StaticResUtil.checkUrl(link, true, TAG))
							.placeholder(R.drawable.ic_downloading)
							.error(R.drawable.ic_no_photo).into(iv);
				}
				return true;
			} else if (arg2 == arg1
					.getColumnIndex(ReplyDAO.DATABASE_COLUMN_MESSAGE)) {
				String message = arg1.getString(arg2);
				TextView tv = (TextView) arg0;
				if (StringUtils.isBlank(message)) {
					tv.setVisibility(View.GONE);
				} else {
					tv.setText(message);
				}
				return true;

			}
			return false;
		}
		//
		// @Override
		// public void notifyDataSetChanged() {
		//
		// if (replyListView.getCount() == 0) {
		// replyListView.getRootView()
		// .findViewById(R.id.report_detail_ll_replylist)
		// .setVisibility(View.GONE);
		// } else {
		// replyListView.getRootView()
		// .findViewById(R.id.report_detail_ll_replylist)
		// .setVisibility(View.VISIBLE);
		// }
		// super.notifyDataSetChanged();
		// }
	}
}

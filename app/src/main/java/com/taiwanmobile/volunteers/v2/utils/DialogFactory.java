package com.taiwanmobile.volunteers.v2.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import androidx.viewpager.widget.ViewPager.LayoutParams;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v2.VolunteerEventsSearchFragment;

public class DialogFactory {

	public static AlertDialog createBusyDialog(Context c, int id) {
		return createBusyDialog(c, c.getString(id));
	}

	public static AlertDialog createBusyDialog(Context c, String msg) {
		LayoutInflater inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialoglayout = inflater.inflate(R.layout.dialog_busy, null);
		Builder builder = new Builder(c);
		builder.setView(dialoglayout);
		builder.setCancelable(false);
		return builder.create();

		// ProgressDialog dlg = new ProgressDialog(c,
		// R.style.DatePickerDialog_big_font);
		// dlg.setCancelable(false);
		// dlg.setMessage(msg);
		// dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// return dlg;
	}

	private static class FullScreenInfoDialog extends Dialog {
		public FullScreenInfoDialog(Context context, int Rid) {
			super(context,
					android.R.style.Theme_DeviceDefault_Light_NoActionBar);
			setContentView(Rid);
			getWindow().setLayout(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent event) {
			this.dismiss();
			return false;
		}
	}

	public static Dialog createFullScreenInfoDialog(Context c, int Rid) {
		return new FullScreenInfoDialog(c, Rid);
	}

	private static class DummyDialog extends Dialog {
		private OnDismissListener mListener;

		public DummyDialog(Context context, OnDismissListener l) {
			super(context);
			mListener = l;
		}

		public DummyDialog(Context context) {
			super(context);
		}

		@Override
		public void show() {
			if (mListener != null) {
				mListener.onDismiss(this);
			}

		}
	}

	public static Dialog createDummyDialog(Context c, OnDismissListener l) {
		return new DummyDialog(c, l);
	}

	public static Dialog createDummyDialog(Context c) {
		return new DummyDialog(c);
	}

	@SuppressWarnings("deprecation")
	public static AlertDialog createMessageDialog(Context c, int id) {
		return createMessageDialog(c, c.getString(id));
	}

	@Deprecated
	public static AlertDialog createMessageDialog(Context c, String msg) {
		return createMessageDialog(c, msg, null);
	}

	private static <T extends View> List<T> findViewByType(ViewGroup root,
			Class<T> clazz) {
		List<T> ret = new ArrayList<T>();
		int children = root.getChildCount();
		for (int i = 0; i < children; i++) {
			View v = root.getChildAt(i);
			if (clazz.isInstance(v)) {
				ret.add((T) v);
			}
			if (v instanceof ViewGroup) {
				ret.addAll(findViewByType((ViewGroup) v, clazz));
			}
		}
		return ret;
	}

	public static final OnShowListener TEXT_SIZE_ADJUSTER = new OnShowListener() {

		@Override
		public void onShow(DialogInterface arg0) {
			AlertDialog ad = (AlertDialog) arg0;
			Context ctx = ad.getContext();
			ViewGroup root = (ViewGroup) ad.findViewById(android.R.id.custom)
					.getRootView();
			List<TextView> tvs = findViewByType(root, TextView.class);
			for (TextView tv : tvs) {
				tv.setTextAppearance(ctx, R.style.DialogFactory_big_font);
			}

		}

	};

	public static AlertDialog createMessageDialog(Context c, int id,
			OnCancelListener l) {
		return createMessageDialog(c, c.getString(id), l);
	}

	public static AlertDialog createMessageDialog(Context c, String msg,
			OnCancelListener l) {
		AlertDialog.Builder b = new AlertDialog.Builder(c)
				.setMessage(msg)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						}).setCancelable(false);
		if (l != null) {
			b.setOnCancelListener(l);
		}
		AlertDialog ret = b.create();
		ret.setOnShowListener(TEXT_SIZE_ADJUSTER);
		return ret;
	}

	public static class TitleMessageDialog extends Dialog {
		public TitleMessageDialog(Context context, String title, String content) {
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

	public static class TitleMessageButtonDialog extends Dialog implements
			android.view.View.OnClickListener {
		public TitleMessageButtonDialog(Context context, String title,
				String content) {
			super(context,
					android.R.style.Theme_DeviceDefault_Light_NoActionBar);
			setContentView(R.layout.dialog_with_title_button);
			// getWindow().setLayout(LayoutParams.MATCH_PARENT,
			// LayoutParams.MATCH_PARENT);

			TextView tv = (TextView) this
					.findViewById(R.id.title_button_dialog_tv_title);
			tv.setText(title);
			tv = (TextView) this
					.findViewById(R.id.title_button_dialog_tv_content);
			tv.setText(Html.fromHtml(content));
			tv.setMovementMethod(LinkMovementMethod.getInstance());

			this.findViewById(R.id.title_button_dialog_btn_ok)
					.setOnClickListener(this);
		}

		@Override
		public void onClick(View arg0) {
			this.dismiss();
		}
	}

	public static class TitleMessageButtonSmallDialog extends Dialog implements
			android.view.View.OnClickListener {
		public TitleMessageButtonSmallDialog(Context context, String title,
				String content) {
			super(context,
					R.style.message_dialog);
			setContentView(R.layout.dialog_with_title_button_small);
			// getWindow().setLayout(LayoutParams.MATCH_PARENT,
			// LayoutParams.MATCH_PARENT);

			TextView tv = (TextView) this
					.findViewById(R.id.title_button_dialog_tv_title);
			tv.setText(title);
			tv = (TextView) this
					.findViewById(R.id.title_button_dialog_tv_content);
			tv.setText(Html.fromHtml(content));
			tv.setMovementMethod(LinkMovementMethod.getInstance());

			this.findViewById(R.id.title_button_dialog_btn_ok)
					.setOnClickListener(this);
		}

		@Override
		public void onClick(View arg0) {
			this.dismiss();
		}
	}

	private static String[] EVENT_TYPES;
	private static boolean EMPTY_EVENT_TYPE_SELECTION[];

	public static void showVolunteerEventTypeDialog(final Button b) {
		EVENT_TYPES = b.getContext().getResources()
				.getStringArray(R.array.volunteer_category_choice);
		EMPTY_EVENT_TYPE_SELECTION = new boolean[EVENT_TYPES.length];
		showEventTypeDialog(
				b,
				b.getContext().getResources()
						.getStringArray(R.array.volunteer_category_choice),
				null);
	}

	public static void showVolunteerEventIsFullDialog(final Button b) {
		EVENT_TYPES = b.getContext().getResources()
				.getStringArray(R.array.full_status_choice);
		EMPTY_EVENT_TYPE_SELECTION = new boolean[EVENT_TYPES.length];
		showEventTypeDialog(
				b,
				b.getContext().getResources()
						.getStringArray(R.array.full_status_choice),
				null);
	}

	private static class EventTypeDialogListener implements
			DialogInterface.OnClickListener,
			DialogInterface.OnMultiChoiceClickListener,
			DialogInterface.OnCancelListener {

		private final boolean mChoices[] = EMPTY_EVENT_TYPE_SELECTION.clone();
		private final Button mTvTarget;
		private final OnClickListener mListener;

		public boolean[] getChoices() {
			return mChoices;
		}

		public EventTypeDialogListener(Button b,
				DialogInterface.OnClickListener l) {
			mListener = l;
			mTvTarget = b;
			if (b.getTag() == null) {
				b.setTag(Long.valueOf(0));
			}
			onCancel(null);
		}

		@Override
		public void onClick(DialogInterface dialog, int which, boolean isChecked) {
			mChoices[which] = isChecked;
		}

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			ArrayList<String> sel = new ArrayList<String>();

			for (int i = 0; i < mChoices.length; i++) {
				if (mChoices[i]) {
					String val = EVENT_TYPES[i];
					sel.add(EVENT_TYPES[i]);
				}
			}
			VolunteerEventsSearchFragment.selectedVolunteerTypes = sel;

			mTvTarget.setText(sel.size() == 0 ? "" : Joiner.on(',').join(sel));

			if (mListener != null) {
				mListener.onClick(arg0, arg1);
			}

		}

		@Override
		public void onCancel(DialogInterface arg0) {
			// TODO Auto-generated method stub

		}
	}

	public static void showEventTypeDialog(Button b, String[] choices,
			DialogInterface.OnClickListener onClick) {

		EventTypeDialogListener l = new EventTypeDialogListener(b, onClick);
		new AlertDialog.Builder(b.getContext())
				.setMultiChoiceItems(choices, l.getChoices(), l)
				.setPositiveButton(android.R.string.ok, l).create().show();
	}

	// public static AlertDialog createTitleMessageDialog(Context c, String
	// title,
	// String msg, OnCancelListener l) {
	//
	// AlertDialog.Builder b = new AlertDialog.Builder(c)
	// .setTitle(title)
	// .setMessage(msg)
	// .setPositiveButton(android.R.string.ok,
	// new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// dialog.cancel();
	// }
	// }).setCancelable(false);
	// if (l != null) {
	// b.setOnCancelListener(l);
	// }
	// AlertDialog ret = b.create();
	// ret.setOnShowListener(TEXT_SIZE_ADJUSTER);
	// return ret;
	// }
	//
	// public static class DateSetListener implements
	// DatePickerDialog.OnDateSetListener {
	//
	// private final TextView mTvTarget;
	//
	// public DateSetListener(TextView tv) {
	// mTvTarget = tv;
	// }
	//
	// @Override
	// public void onDateSet(DatePicker view, int year, int monthOfYear,
	// int dayOfMonth) {
	//
	// mTvTarget.setText(createDateString(view));
	//
	// }
	//
	// }
	//
	// private static final SimpleDateFormat DATE_FORMATTER = new
	// SimpleDateFormat(
	// "MM/dd/yyyy", Locale.US);
	//
	// private static Calendar createCalendar(DatePicker dp) {
	// Calendar c = Calendar.getInstance();
	// c.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
	// return c;
	// }
	//
	// private static String createDateString(Calendar c) {
	// return DATE_FORMATTER.format(c.getTime());
	// }
	//
	// private static String createDateString(DatePicker dp) {
	// return createDateString(createCalendar(dp));
	// }
}

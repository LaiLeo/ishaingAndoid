package com.taiwanmobile.volunteers.v2.utils;

import android.app.Activity;
import android.content.Context;
import android.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.appcompat.widget.TintContextWrapper;
import android.view.inputmethod.InputMethodManager;

import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v2.MainActivity;

public class FragHelper {

	// public static void add(Context context, Fragment next) {
	// ((FragmentActivity) context).getSupportFragmentManager()
	// .beginTransaction().add(R.id.main_activity_container, next)
	// .addToBackStack(null).commit();
	// }

	public static void replace(Context context, Fragment next) {
		((MainActivity) context).getFragmentManager()
				.beginTransaction().replace(R.id.main_activity_container, next)
				.addToBackStack(null).commit();
	}

	public static void remove(Context context, Fragment next) {
		((MainActivity) context).getFragmentManager()
				.beginTransaction().remove(next).commit();
	}

	public static void remove(Fragment current, Fragment next) {
		current.getFragmentManager().beginTransaction().remove(next).commit();
	}

	public static void replace(Fragment current, Fragment next) {
		current.getFragmentManager().beginTransaction()
				.replace(current.getId(), next).addToBackStack(null).commit();
	}

	public static void removeAndClearBackStack(Context context, Fragment next) {
		((MainActivity) context).getFragmentManager().popBackStack(null,
				FragmentManager.POP_BACK_STACK_INCLUSIVE);
		remove(context, next);
	}

	public static void hideKeyboard(Activity act) {
		try {
			InputMethodManager imm = (InputMethodManager) act
					.getSystemService(Context.INPUT_METHOD_SERVICE);

			imm.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(),
					0);
		} catch (Exception ex) {
		}
	}
}

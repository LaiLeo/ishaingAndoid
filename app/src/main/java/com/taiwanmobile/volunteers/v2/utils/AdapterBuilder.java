package com.taiwanmobile.volunteers.v2.utils;

import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import android.content.Context;
import android.database.Cursor;
import android.app.Fragment;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Pair;
import android.view.View;

public class AdapterBuilder {

	private final ArrayList<String> mFrom = new ArrayList<String>();
	private final ArrayList<Integer> mTo = new ArrayList<Integer>();
	private Context mContext;
	private Cursor mCursor;
	private int mResId;
	private ViewBinder mViewBinder;

	public AdapterBuilder bind(String from, int to) {
		mFrom.add(from);
		mTo.add(to);
		return this;
	}

	public static interface ItemBinder<T> {
		public void bind(View v, T val);
	}

	public static interface StringBinder extends ItemBinder<String> {
	}

	public static interface IntegerBinder extends ItemBinder<Integer> {
	}

	public static interface LongBinder extends ItemBinder<Long> {
	}

	public static interface DoubleBinder extends ItemBinder<Double> {
	}

	public static interface FloatBinder extends ItemBinder<Float> {
	}

	private class Binder implements ViewBinder {
		ImmutableMap<Integer, ItemBinder<?>> mBinderMap;

		public Binder() {
			Builder<Integer, ItemBinder<?>> builder = ImmutableMap
					.<Integer, ItemBinder<?>> builder();
			for (Pair<Integer, ItemBinder<?>> p : mBinders) {
				builder.put(p.first, p.second);
			}
			mBinderMap = builder.build();
		}

		@Override
		public boolean setViewValue(View arg0, Cursor arg1, int arg2) {
			ItemBinder<?> b = mBinderMap.get(arg0.getId());
			if (b != null) {
				if (StringBinder.class.isInstance(b)) {
					StringBinder sb = (StringBinder) b;
					sb.bind(arg0, arg1.getString(arg2));
				} else if (IntegerBinder.class.isInstance(b)) {
					IntegerBinder ib = (IntegerBinder) b;
					ib.bind(arg0, arg1.getInt(arg2));
				} else if (LongBinder.class.isInstance(b)) {
					LongBinder lb = (LongBinder) b;
					lb.bind(arg0, arg1.getLong(arg2));
				} else if (DoubleBinder.class.isInstance(b)) {
					DoubleBinder db = (DoubleBinder) b;
					db.bind(arg0, arg1.getDouble(arg2));
				} else if (FloatBinder.class.isInstance(b)) {
					FloatBinder fb = (FloatBinder) b;
					fb.bind(arg0, arg1.getFloat(arg2));
				} else {
					return false;
				}
				return true;
			}
			return false;
		}

	}

	private final ArrayList<Pair<Integer, ItemBinder<?>>> mBinders = new ArrayList<Pair<Integer, ItemBinder<?>>>();

	public AdapterBuilder bind(String from, int to, ItemBinder<?> b) {

		mBinders.add(Pair.<Integer, ItemBinder<?>> create(to, b));
		return bind(from, to);
	}

	public AdapterBuilder set(Fragment f) {
		return set(f.getActivity());
	}

	public AdapterBuilder set(Context c) {
		mContext = c;
		return this;
	}

	public AdapterBuilder set(Cursor c) {
		mCursor = c;
		return this;
	}

	public AdapterBuilder set(int resId) {
		mResId = resId;
		return this;
	}

	public AdapterBuilder set(ViewBinder vb) {
		mViewBinder = vb;
		return this;
	}

	public SimpleCursorAdapter build() {
		String[] from = mFrom.toArray(new String[0]);
		int[] to = ArrayUtils.toPrimitive(mTo.toArray(new Integer[0]));
		SimpleCursorAdapter ret = new SimpleCursorAdapter(mContext, mResId,
				mCursor, from, to,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		if (mViewBinder != null) {
			ret.setViewBinder(mViewBinder);
		}
		if (mBinders.size() > 0) {
			ret.setViewBinder(new Binder());
		}
		return ret;
	}

}

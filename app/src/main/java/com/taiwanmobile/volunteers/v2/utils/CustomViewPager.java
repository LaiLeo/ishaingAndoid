package com.taiwanmobile.volunteers.v2.utils;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {
	private boolean swipeable = true;

	public CustomViewPager(Context context) {
		super(context);
	}

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// Call this method in your motion events when you want to disable or
	// enable
	// It should work as desired.
	public void setSwipeable(boolean swipeable) {
		this.swipeable = swipeable;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return (this.swipeable) && super.onInterceptTouchEvent(arg0);
	}

}

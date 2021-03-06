/*
 * Copyright 2013 Blaz Solar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taiwanmobile.volunteers.v2.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.taiwanmobile.volunteers.R;

public class FlowLayout extends ViewGroup {

	private int paddingHorizontal;
	private int paddingVertical;

	public FlowLayout(Context context) {
		super(context);
		init();
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		paddingHorizontal = getResources().getDimensionPixelSize(
				R.dimen.flowlayout_horizontal_padding);
		paddingVertical = getResources().getDimensionPixelSize(
				R.dimen.flowlayout_vertical_padding);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int childLeft = getPaddingLeft();
		int childTop = getPaddingTop();
		int lineHeight = 0;
		// 100 is a dummy number, widthMeasureSpec should always be EXACTLY for
		// FlowLayout
		int myWidth = resolveSize(100, widthMeasureSpec);
		int wantedHeight = 0;
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == View.GONE) {
				continue;
			}
			// let the child measure itself
			child.measure(
					getChildMeasureSpec(widthMeasureSpec, 0,
							child.getLayoutParams().width),
					getChildMeasureSpec(heightMeasureSpec, 0,
							child.getLayoutParams().height));
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();
			// lineheight is the height of current line, should be the height of
			// the heightest view
			lineHeight = Math.max(childHeight, lineHeight);
			if (childWidth + childLeft + getPaddingRight() > myWidth) {
				// wrap this line
				childLeft = getPaddingLeft();
				childTop += paddingVertical + lineHeight;
				lineHeight = childHeight;
			}
			childLeft += childWidth + paddingHorizontal;
		}
		wantedHeight += childTop + lineHeight + getPaddingBottom();
		setMeasuredDimension(myWidth,
				resolveSize(wantedHeight, heightMeasureSpec));
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int childLeft = getPaddingLeft();
		int childTop = getPaddingTop();
		int lineHeight = 0;
		int myWidth = right - left;
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() == View.GONE) {
				continue;
			}
			int childWidth = child.getMeasuredWidth();
			int childHeight = child.getMeasuredHeight();
			lineHeight = Math.max(childHeight, lineHeight);
			if (childWidth + childLeft + getPaddingRight() > myWidth) {
				childLeft = getPaddingLeft();
				childTop += paddingVertical + lineHeight;
				lineHeight = childHeight;
			}
			child.layout(childLeft, childTop, childLeft + childWidth, childTop
					+ childHeight);
			childLeft += childWidth + paddingHorizontal;
		}
	}
}
package com.taiwanmobile.volunteers.v2;

import android.os.Bundle;
import android.app.DialogFragment;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RatingBar;

import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v2.api.EventRatingTask;

public class RatingBarDialogFragment extends DialogFragment implements
		OnClickListener {

	long eventId;
	Fragment parentFragment;

//	public RatingBarDialogFragment(Fragment frag, long id) {
//		parentFragment = frag;
//		eventId = id;
//	}

	public void setParentFragment(Fragment frag){
		parentFragment = frag;
	}

	public void setEventId(long id){
		eventId = id;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_ratingbar, container);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		view.findViewById(R.id.ratingbar_dialog_btn).setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View arg0) {
		RatingBar rb = (RatingBar) arg0.getRootView().findViewById(
				R.id.ratingbar_dialog_rb);
		new EventRatingTask(getActivity(), parentFragment, eventId,
				rb.getRating()).execute();
		this.dismiss();
	}
}

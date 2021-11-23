package com.taiwanmobile.volunteers.v2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.util.StaticResUtil;
import com.taiwanmobile.volunteers.v2.utils.TouchImageView;

@SuppressLint("ValidFragment")
public class DetailImageViewFragment extends SupportFragment {

	ImageView view;
	String imageFilePath;
	TouchImageView image;
	ProgressBar progressBar;


	@SuppressLint("ValidFragment")
    public DetailImageViewFragment(ImageView v, String path) {
		view = v;
		imageFilePath = path;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		hideActionBarTabs();
//		getActivity().getActionBar().hide();

		final View rootView = inflater.inflate(
				R.layout.fragment_detail_image_view, container, false);
		image = (TouchImageView) rootView
				.findViewById(R.id.fragment_detail_image_view_tiv);
		Picasso.with(getActivity())
				//.load(BackendContract.DEPLOYMENT_STATIC_URL + imageFilePath)
				.load(StaticResUtil.checkUrl(imageFilePath, true,"DetailImageViewFragment"))
				.error(R.drawable.ic_no_photo).into(image, new Callback() {
					@Override
					public void onSuccess() {
						rootView.findViewById(
								R.id.fragment_detail_image_view_progressBar)
								.setVisibility(View.GONE);
						image.setVisibility(View.VISIBLE);
					}

					@Override
					public void onError() {
						rootView.findViewById(
								R.id.fragment_detail_image_view_progressBar)
								.setVisibility(View.GONE);
					}
				});
		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
        showActionBarTabs();
	}
}

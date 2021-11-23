package com.taiwanmobile.volunteers.v2;

import java.sql.SQLException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v1.util.StaticResUtil;
import com.taiwanmobile.volunteers.v2.database.DonationNpoDAO;

public class DonationNpoDetailFragment extends SupportFragment implements
		OnClickListener {
	private final String TAG = getClass().getSimpleName();

	DonationNpoDAO npoObject;
	private String selectedDonation = "手動選其他金額";
	private String dialCode = "";

    public DonationNpoDetailFragment(){

    }

    public DonationNpoDetailFragment setNpoId(long id) {
        try {
            npoObject = DonationNpoDAO.queryObjectById(id);
        } catch (SQLException e) {
            npoObject = null;
            Log.e(TAG, "DonationNpoDAO id: " + id + " not found.");
        }
        return this;
    }


    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		hideActionBarTabs();

		final View rootView = inflater.inflate(
				R.layout.fragment_donation_npo_detail, container, false);

		if (npoObject != null) {
			rootView.findViewById(
					R.id.fragment_donation_npo_detail_btn_donation)
					.setOnClickListener(this);
			((TextView) rootView
					.findViewById(R.id.fragment_donation_npo_detail_tv_subject))
					.setText(npoObject.name);
			((TextView) rootView
					.findViewById(R.id.fragment_donation_npo_detail_tv_description))
					.setText(npoObject.description);
			Picasso.with(getActivity())
					//.load(BackendContract.DEPLOYMENT_STATIC_URL
							//+ npoObject.iconURL)
					.load(StaticResUtil.checkUrl( npoObject.iconURL,true, TAG))
					.into((ImageView) rootView
							.findViewById(R.id.fragment_donation_npo_detail_iv_icon));
			((Spinner) rootView
					.findViewById(R.id.fragment_item_search_sp_donation))
					.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView adapterView,
								View view, int position, long id) {
							selectedDonation = getResources().getStringArray(
									R.array.donation_choice)[position];
						}

						@Override
						public void onNothingSelected(AdapterView arg0) {
						}
					});


			if(TextUtils.isEmpty(npoObject.payPeriodURL)){
				rootView.findViewById(R.id.btn_donation_period).setVisibility(View.GONE);
			}else{
				rootView.findViewById(R.id.btn_donation_period).setOnClickListener(this);
			}

			if(TextUtils.isEmpty(npoObject.payURL)) {
				rootView.findViewById(R.id.btn_donation_creditcard).setVisibility(View.GONE);
			}else{
				rootView.findViewById(R.id.btn_donation_creditcard).setOnClickListener(this);
			}

		}

//		((RadioGroup)rootView.findViewById(R.id.selection_group)).setO                                                nCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(RadioGroup group, int checkedId) {
//				View phoneGroup = rootView.findViewById(R.id.donate_by_phone_group);
//				switch (checkedId) {
//					case R.id.selection_group_creditcard :{
//						String url = npoObject.payURL;
//						if(url == null || url.length() == 0) {
//							new AlertDialog.Builder(getActivity())
//									.setMessage("此單位不同意使用數位捐款，請選擇語音捐款或洽此單位官網，謝謝！")
//									.setPositiveButton(android.R.string.ok, null)
//									.show();
//							return;
//						}
//						phoneGroup.setVisibility(View.GONE);
//						Intent i = new Intent(Intent.ACTION_VIEW);
//						i.setData(Uri.parse(url));
//						startActivity(i);
//						break;
//					}
//					case R.id.selection_group_phone :{
//						phoneGroup.setVisibility(View.VISIBLE);
//						break;
//					}
//				}
//			}
//		});


		rootView.findViewById(R.id.btn_donation_phone).setOnClickListener(this);

		return rootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
        showActionBarTabs();
	}


	public void startPhoneDonation(){
		dialCode = "tel:5180" + npoObject.code + ",1";
		String dialogMessage = getResources().getString(
				R.string.donation_tip_dialog_message);
		if (selectedDonation.compareTo("捐款100") == 0) {
			dialCode += ",1,1,1,1";
		} else if (selectedDonation.compareTo("捐款200") == 0) {
			dialCode += ",2,1,1,1";
		} else if (selectedDonation.compareTo("捐款300") == 0) {
			dialCode += ",3,1,1,1";
		} else if (selectedDonation.compareTo("捐款500") == 0) {
			dialCode += ",4,1,1,1";
		} else if (selectedDonation.compareTo("捐款1,000") == 0) {
			dialCode += ",5,1,1,1";
		} else if (selectedDonation.compareTo("捐款1,500") == 0) {
			dialCode += ",6,1,1,1";
		} else if (selectedDonation.compareTo("捐款2,000") == 0) {
			dialCode += ",7,1,1,1";
		} else if (selectedDonation.compareTo("捐款3,000") == 0) {
			dialCode += ",8,1,1,1";
		} else if (selectedDonation.compareTo("捐款6,000") == 0) {
			dialCode += ",9,1,1,1";
		} else {
			dialogMessage = getResources().getString(
					R.string.donation_tip_dialog_message_default);
		}

		TextView title = new TextView(getActivity());
		// You Can Customise your Title here
		title.setText(getResources().getString(
				R.string.donation_tip_dialog_title));
		title.setBackgroundColor(Color.WHITE);
		title.setPadding(10, 10, 10, 10);
		title.setGravity(Gravity.CENTER);
		title.setTextColor(Color.BLACK);
		title.setTextSize(20);
		AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
				.setCustomTitle(title)
				.setMessage(dialogMessage)
				.setPositiveButton("繼續",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
												int which) {
								Uri uri = Uri.parse(dialCode);

								Intent dial = new Intent(
										Intent.ACTION_DIAL, uri);
								startActivity(dial);
							}
						}).setNegativeButton("取消", null)
				.setCancelable(true);
		AlertDialog ret = b.create();
		// TODO
		// ret.setOnShowListener(DialogFactory.TEXT_SIZE_ADJUSTER);
		ret.show();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_donation_npo_detail_btn_donation:
		case R.id.btn_donation_phone:
			showSingleChoiceDialog();
			break;
			case R.id.btn_donation_creditcard: {
				String url = npoObject.payURL;
				if (url == null || url.length() == 0) {
					new AlertDialog.Builder(getActivity())
							.setMessage("此單位不同意使用數位捐款，請選擇語音捐款或洽此單位官網，謝謝！")
							.setPositiveButton(android.R.string.ok, null)
							.show();
					return;
				}
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
				break;
			}
			case R.id.btn_donation_period:{
				String url = npoObject.payURL;
				if(url == null || url.length() == 0) {
					new AlertDialog.Builder(getActivity())
							.setMessage("此單位不同意使用數位捐款，請選擇語音捐款或洽此單位官網，謝謝！")
							.setPositiveButton(android.R.string.ok, null)
							.show();
					return;
				}
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
				break;
			}
			default:
				break;
		}
	}


    String[] mDonationItems;

	int mSelectedItems;
	private void showSingleChoiceDialog(){
		final AlertDialog.Builder builder =
				new AlertDialog.Builder(getActivity());
		builder.setTitle("語音捐款");

		mDonationItems = getActivity().getResources().getStringArray(R.array.donation_choice);
		builder.setSingleChoiceItems(mDonationItems, 0,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mSelectedItems = which;
					}
				});
		builder.setPositiveButton(R.string.btn_confirm,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						selectedDonation = mDonationItems[mSelectedItems];
						startPhoneDonation();
					}
				});
		builder.show();
	}
}

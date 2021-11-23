package com.taiwanmobile.volunteers.v2;

import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v2.userprofileview.AdvanceVolunteerHoursQueryFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class YearPickerDialogFragment extends DialogFragment implements
		ListAdapter, AdapterView.OnItemClickListener {

    @SuppressWarnings("unused")
    private static final String TAG = "YearPickerDialogFragment";
    ArrayList<String> mData = new ArrayList<>();
    int mValue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_year_picker, container);
//		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mData.clear();
        mData.add("不限");
        Bundle bundle = getArguments();
        mValue = bundle.getInt("value",AdvanceVolunteerHoursQueryFragment.YEAR_NO_LIMITATION);
        for(int y = 2012; y <= new GregorianCalendar().get(Calendar.YEAR); y++){
            mData.add(String.valueOf(y));
        }
        ListView listView = (ListView)view.findViewById(R.id.year_list);
        ColorDrawable bar = new ColorDrawable(getColor(R.color.twmf_orange));
        listView.setDivider(bar);
        listView.setDividerHeight(1);
        listView.setOnItemClickListener(this);
        listView.setAdapter(this);
		return view;
	}

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public boolean isSelected(int position){
        if(getItem(position).equals("不限") && mValue == AdvanceVolunteerHoursQueryFragment.YEAR_NO_LIMITATION){
            return true;
        }else if(getItem(position).equals(String.valueOf(mValue))){
            return true;
        }
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_year_item, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
        tv.setText((String)getItem(position));

        View background = convertView.findViewById(android.R.id.background);
        if(isSelected(position)){
            background.setBackgroundColor(getColor(R.color.twmf_orange));
            tv.setTextColor(getColor(R.color.twmf_white));
        }else{
            background.setBackgroundColor(getColor(R.color.twmf_white));
            tv.setTextColor(getColor(R.color.twmf_orange));

        }
        return convertView;
    }

    private int getColor(int color){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getResources().getColor(color, null);
        }else{
            //noinspection deprecation
            return getResources().getColor(color);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(getItem(position).equals("不限")){
            getTargetFragment().onActivityResult(getTargetRequestCode(), AdvanceVolunteerHoursQueryFragment.YEAR_NO_LIMITATION, null);
        }else{
            getTargetFragment().onActivityResult(getTargetRequestCode(), Integer.decode((String) getItem(position)), null);
        }
        dismiss();
    }
}

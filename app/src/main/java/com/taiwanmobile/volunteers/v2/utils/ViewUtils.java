package com.taiwanmobile.volunteers.v2.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FilterQueryProvider;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.taiwanmobile.volunteers.v2.DonationNpoDetailFragment;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.VolunteerEventDetailFragment;
import com.taiwanmobile.volunteers.v2.database.DonationNpoDAO;
import com.taiwanmobile.volunteers.v2.database.EventDAO;
import com.taiwanmobile.volunteers.v2.database.NpoDAO;
import com.taiwanmobile.volunteers.v2.npoview.NpoDetailFragment;

public class ViewUtils {
	public static void setListViewSize(ListView myListView) {
		ListAdapter myListAdapter = myListView.getAdapter();
		if (myListAdapter == null) {
			// do nothing return null
			return;
		}
		// set listAdapter in loop for getting final size
		int totalHeight = 0;
		for (int size = 0; size < myListAdapter.getCount(); size++) {
			View listItem = myListAdapter.getView(size, null, myListView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		// setting listview item in adapter
		ViewGroup.LayoutParams params = myListView.getLayoutParams();
		params.height = totalHeight
				+ (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
		myListView.setLayoutParams(params);
		// print height of adapter on log
		// Log.i("height of listItem:", String.valueOf(totalHeight));
	}

	public static void setSearchBarTagetToDonationNpos(final Context c) {
		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(c,
				android.R.layout.simple_dropdown_item_1line,
				DonationNpoDAO.queryDonationNpos(),
				new String[] { DonationNpoDAO.DATABASE_COLUMN_NAME },
				new int[] { android.R.id.text1 }, 0);
		MainActivity.searchView.setHint("搜尋捐款組織...");
		MainActivity.searchView.setAdapter(mAdapter);
		MainActivity.searchView
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> listView, View view,
							int position, long id) {
						// Get the cursor, positioned to the corresponding row
						// in
						// the
						// result set
						// Cursor cursor = (Cursor)
						// listView.getItemAtPosition(position);
						//
						// // Get the state's capital from this row in the
						// database.
						// String capital = cursor.getString(cursor
						// .getColumnIndexOrThrow(EventDAO.DATABASE_COLUMN_SUBJECT));
						// Update the parent class's TextView
						// actv.setText(capital);
						FragHelper.hideKeyboard((Activity) c);
						FragHelper.replace(view.getContext(),
								new DonationNpoDetailFragment().setNpoId(id));
					}
				});

		// Set the CursorToStringConverter, to provide the labels for the
		// choices to be displayed in the AutoCompleteTextView.
		mAdapter.setCursorToStringConverter(new CursorToStringConverter() {
			@Override
			public String convertToString(android.database.Cursor cursor) {
				// Get the label for this row out of the "state" column
				final int columnIndex = cursor
						.getColumnIndexOrThrow(DonationNpoDAO.DATABASE_COLUMN_NAME);
				final String str = cursor.getString(columnIndex);
				return str;
			}
		});

		// Set the FilterQueryProvider, to run queries for choices
		// that match the specified input.
		mAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			@Override
			public Cursor runQuery(CharSequence constraint) {
				// Search for states whose names begin with the specified
				// letters.
				if (constraint != null) {
					String param = "%" + constraint.toString().trim() + "%";
					// Log.e(TAG, param);
					String params[] = { param };
					String sql = "select n.* from donationnpodao n where name like ?;";
					// Log.e(TAG, sql);
					Cursor cursor = MainActivity.MyDbHelper
							.getReadableDatabase().rawQuery(sql, params);
					return cursor;
				}
				return null;
			}
		});
	}

	public static void setSearchBarTagetToNpos(final Context c) {
		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(c,
				android.R.layout.simple_dropdown_item_1line,
				NpoDAO.queryVolunteerNpos(),
				new String[] { NpoDAO.DATABASE_COLUMN_NAME },
				new int[] { android.R.id.text1 }, 0);
		MainActivity.searchView.setHint("搜尋活動單位...");
		MainActivity.searchView.setAdapter(mAdapter);
		MainActivity.searchView
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> listView, View view,
							int position, long id) {
						// Get the cursor, positioned to the corresponding row
						// in
						// the
						// result set
						// Cursor cursor = (Cursor)
						// listView.getItemAtPosition(position);
						//
						// // Get the state's capital from this row in the
						// database.
						// String capital = cursor.getString(cursor
						// .getColumnIndexOrThrow(EventDAO.DATABASE_COLUMN_SUBJECT));
						// Update the parent class's TextView
						// actv.setText(capital);
						FragHelper.hideKeyboard((Activity) c);
						FragHelper.replace(view.getContext(),
								new NpoDetailFragment().setNpoId(id));
					}
				});

		// Set the CursorToStringConverter, to provide the labels for the
		// choices to be displayed in the AutoCompleteTextView.
		mAdapter.setCursorToStringConverter(new CursorToStringConverter() {
			@Override
			public String convertToString(android.database.Cursor cursor) {
				// Get the label for this row out of the "state" column
				final int columnIndex = cursor
						.getColumnIndexOrThrow(NpoDAO.DATABASE_COLUMN_NAME);
				final String str = cursor.getString(columnIndex);
				return str;
			}
		});

		// Set the FilterQueryProvider, to run queries for choices
		// that match the specified input.
		mAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			@Override
			public Cursor runQuery(CharSequence constraint) {
				// Search for states whose names begin with the specified
				// letters.
				if (constraint != null) {
					String param = "%" + constraint.toString().trim() + "%";
					// Log.e(TAG, param);
					String params[] = { param };
					String sql = "select n.* from npodao n where name like ?;";
					// Log.e(TAG, sql);
					Cursor cursor = MainActivity.MyDbHelper
							.getReadableDatabase().rawQuery(sql, params);
					return cursor;
				}
				return null;
			}
		});
	}

	public static void setSearchBarTagetToEvents(final Context c) {
		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(c,
				android.R.layout.simple_dropdown_item_1line,
				EventDAO.queryAllEvents(),
				new String[] { EventDAO.DATABASE_COLUMN_SUBJECT },
				new int[] { android.R.id.text1 }, 0);
		MainActivity.searchView.setHint("搜尋活動...");
		MainActivity.searchView.setAdapter(mAdapter);
		MainActivity.searchView
				.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> listView, View view,
							int position, long id) {
						// Get the cursor, positioned to the corresponding row
						// in
						// the
						// result set
						// Cursor cursor = (Cursor)
						// listView.getItemAtPosition(position);
						//
						// // Get the state's capital from this row in the
						// database.
						// String capital = cursor.getString(cursor
						// .getColumnIndexOrThrow(EventDAO.DATABASE_COLUMN_SUBJECT));
						// Update the parent class's TextView
						// actv.setText(capital);
						FragHelper.hideKeyboard((Activity) c);
						FragHelper.replace((Activity)c,
								new VolunteerEventDetailFragment()
										.setEventId(id));
					}
				});

		// Set the CursorToStringConverter, to provide the labels for the
		// choices to be displayed in the AutoCompleteTextView.
		mAdapter.setCursorToStringConverter(new CursorToStringConverter() {
			@Override
			public String convertToString(android.database.Cursor cursor) {
				// Get the label for this row out of the "state" column
				final int columnIndex = cursor
						.getColumnIndexOrThrow(EventDAO.DATABASE_COLUMN_SUBJECT);
				final String str = cursor.getString(columnIndex);
				return str;
			}
		});

		// Set the FilterQueryProvider, to run queries for choices
		// that match the specified input.
		mAdapter.setFilterQueryProvider(new FilterQueryProvider() {
			@Override
			public Cursor runQuery(CharSequence constraint) {
				// Search for states whose names begin with the specified
				// letters.
				if (constraint != null) {
					String param = "%" + constraint.toString().trim() + "%";
					// Log.e(TAG, param);
					String params[] = { param, param, param };
					String sql = "select e.* from eventdao e where subject like ? or address like ? or addressCity like ?;";
					// Log.e(TAG, sql);
					Cursor cursor = MainActivity.MyDbHelper
							.getReadableDatabase().rawQuery(sql, params);
					return cursor;
				}
				return null;
			}
		});
	}
}
package com.taiwanmobile.volunteers.v2.database;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.taiwanmobile.volunteers.v2.MainActivity;

public class DonationNpoDAO {
	private final String TAG = getClass().getSimpleName();

	public static final String DATABASE_TABLE_NAME = "donationnpo";
	public static final String DATABASE_COLUMN_ID = "_id";
	public static final String DATABASE_COLUMN_NAME = "name";
	public static final String DATABASE_COLUMN_DESCRIPTION = "description";
	public static final String DATABASE_COLUMN_ICON = "iconURL";
	public static final String DATABASE_COLUMN_CODE = "code";

	static final String JSON_GET_KEY_ID = "id";
	static final String JSON_GET_KEY_NAME = "name";
	static final String JSON_GET_KEY_DESCRIPTION = "description";
	static final String JSON_GET_KEY_ICON = "npo_icon";
	static final String JSON_GET_KEY_FILE_THUMB = "thumb_path";
	static final String JSON_GET_KEY_CODE = "code";
	static final String JSON_GET_KEY_PAY_URL = "newebpay_url";
	static final String JSON_GET_KEY_PAY_PERIOD_URL = "newebpayPeriodUrl";//tea-test add

	@DatabaseField(columnName = "_id", id = true)
	public Long id;
	@DatabaseField
	public String name;
	@DatabaseField
	public String description;
	@DatabaseField
	public String code;
	@DatabaseField
	public String iconURL;
	@DatabaseField
	public String payURL;
	@DatabaseField//tea-test add
	public String payPeriodURL;//tea-test add

	DonationNpoDAO() {
	}

	public DonationNpoDAO(JSONObject obj) throws JSONException {
		Log.d(TAG, "DonationNpoDAO()");
		this.id = obj.getLong(JSON_GET_KEY_ID);
		this.name = obj.getString(JSON_GET_KEY_NAME);
		this.description = obj.getString(JSON_GET_KEY_DESCRIPTION);
		if (DatabaseHelper.hasThumbnail(obj.getString(JSON_GET_KEY_FILE_THUMB))) {
			this.iconURL = obj.getString(JSON_GET_KEY_FILE_THUMB);
		} else {
			this.iconURL = obj.getString(JSON_GET_KEY_ICON);
		}
		this.code = obj.getString(JSON_GET_KEY_CODE);
		if(obj.has(JSON_GET_KEY_PAY_URL))
			this.payURL = obj.getString(JSON_GET_KEY_PAY_URL);
		else {
			this.payURL = null;
		}
		//tea-test add start
		if(obj.has(JSON_GET_KEY_PAY_PERIOD_URL)) {
			this.payPeriodURL = obj.getString(JSON_GET_KEY_PAY_PERIOD_URL);
		} else {
			this.payPeriodURL = null;
		}
		//tea-test add end
	}

	public void save() {
		try {
			MainActivity.MyDbHelper.getDonationNpoDAO().createOrUpdate(this);
		} catch (SQLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	public static Cursor queryDonationNpos() {
		//FIH-modify for 捐款排序，支持數位的排在前面 start
		//String sql = "select n.* from donationnpodao n;";
		//String sql = "select n.* from donationnpodao n where n.payURL!='';";
		//String sql = "select n.* from donationnpodao n order by n.payURL desc;";
		String sql = "select n.* from donationnpodao n order by n.payURL='';";
		//FIH-modify for 捐款排序，支持數位的排在前面 end

		return MainActivity.MyDbHelper.getReadableDatabase()
				.rawQuery(sql, null);
	}

	public static Cursor querCursorById(long id) {
		String sql = "select n.* from donationnpodao n where n._id=?;";
		String[] array = DatabaseHelper.fill(id);

		return MainActivity.MyDbHelper.getReadableDatabase().rawQuery(sql,
				array);
	}

	public static DonationNpoDAO queryObjectById(long id) throws SQLException {
		List<DonationNpoDAO> list = MainActivity.MyDbHelper.getDonationNpoDAO()
				.queryForEq(DATABASE_COLUMN_ID, id);
		if (list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}
}

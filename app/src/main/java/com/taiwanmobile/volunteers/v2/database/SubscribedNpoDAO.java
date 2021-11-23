package com.taiwanmobile.volunteers.v2.database;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.taiwanmobile.volunteers.v2.MainActivity;

public class SubscribedNpoDAO {
	private final String TAG = getClass().getSimpleName();

	public static final String DATABASE_TABLE_NAME = "subscribe_npo";
	public static final String DATABASE_COLUMN_ID = "_id";
	public static final String DATABASE_COLUMN_SUBSCRIBED_NPO_ID = "subscribedNPO_id";

	static final String JSON_GET_KEY_ID = "id";
	static final String JSON_GET_KEY_SUBSCRIBED_NPO = "subscribed_NPO";

	@DatabaseField(columnName = "_id", id = true)
	Long id;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	NpoDAO subscribedNPO;

	SubscribedNpoDAO() {
	}

	public SubscribedNpoDAO(JSONObject obj) throws JSONException {
		this.id = obj.getLong(JSON_GET_KEY_ID);
		this.subscribedNPO = new NpoDAO(
				obj.getJSONObject(JSON_GET_KEY_SUBSCRIBED_NPO));
	}

	public void save() {
		try {
			// MainActivity.MyDbHelper.getNpoDAO().createOrUpdate(subscribedNPO);
			MainActivity.MyDbHelper.getSubscribedNpoDAO().createOrUpdate(this);
		} catch (SQLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	public static Boolean isUserSubscribed(long npoId) throws SQLException {
		List<SubscribedNpoDAO> list = MainActivity.MyDbHelper
				.getSubscribedNpoDAO().queryForEq(
						DATABASE_COLUMN_SUBSCRIBED_NPO_ID, npoId);
		if (list.size() == 1) {
			return true;
		} else {
			return false;
		}
	}

	public static Boolean deleteByNpoId(long id) throws SQLException {
		List<SubscribedNpoDAO> list = MainActivity.MyDbHelper
				.getSubscribedNpoDAO().queryForEq(
						DATABASE_COLUMN_SUBSCRIBED_NPO_ID, id);
		if (list.size() == 1) {
			MainActivity.MyDbHelper.getSubscribedNpoDAO().delete(list.get(0));
		}
		return true;
	}

	public static Cursor querySubscribedNpos() {
		String sql = "select n.* from npodao n, subscribednpodao s "
				+ "where n._id=s.subscribedNPO_id;";
		return MainActivity.MyDbHelper.getReadableDatabase()
				.rawQuery(sql, null);
	}
}

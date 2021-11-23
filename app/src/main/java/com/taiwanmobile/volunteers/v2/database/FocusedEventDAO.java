package com.taiwanmobile.volunteers.v2.database;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.taiwanmobile.volunteers.v2.MainActivity;

public class FocusedEventDAO {
	private final String TAG = getClass().getSimpleName();

	public static final String DATABASE_TABLE_NAME = "focusedeventdao";
	public static final String DATABASE_COLUMN_ID = "_id";
	public static final String DATABASE_COLUMN_FOCUSED_EVENT_ID = "focusedEvent_id";

	static final String JSON_GET_KEY_ID = "id";
	static final String JSON_GET_KEY_FOCUSED_EVENT = "focused_event";

	@DatabaseField(columnName = "_id", id = true)
	Long id;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	EventDAO focusedEvent;

	FocusedEventDAO() {
	}

	public FocusedEventDAO(JSONObject obj) throws JSONException {
		this.id = obj.getLong(JSON_GET_KEY_ID);
		this.focusedEvent = new EventDAO(
				obj.getJSONObject(JSON_GET_KEY_FOCUSED_EVENT));
	}

	public void save() {
		try {
			MainActivity.MyDbHelper.getEventDAO().createOrUpdate(focusedEvent);
			MainActivity.MyDbHelper.getFocusedEventDAO().createOrUpdate(this);
		} catch (SQLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	public static Cursor queryFocusedVolunteerEvents() {
		String sql = "select e.* "
				+ "from eventdao e, focusedeventdao f "
				+ "where e._id=f.focusedEvent_id and e.isVolunteer=1 order by isUrgent DESC, isShort DESC, e.happenDate DESC;";
		return MainActivity.MyDbHelper.getReadableDatabase()
				.rawQuery(sql, null);
	}

	public static Cursor queryFocusedItemEvents() {
		String sql = "select e.* "
				+ "from eventdao e, focusedeventdao f "
				+ "where e._id=f.focusedEvent_id and e.isVolunteer=0 order by isUrgent DESC, isShort DESC, e.happenDate DESC;";
		return MainActivity.MyDbHelper.getReadableDatabase()
				.rawQuery(sql, null);
	}

	public static Boolean isUserFocused(long eventId) throws SQLException {
		List<FocusedEventDAO> list = MainActivity.MyDbHelper
				.getFocusedEventDAO().queryForEq(
						DATABASE_COLUMN_FOCUSED_EVENT_ID, eventId);
		if (list.size() == 1) {
			return true;
		} else {
			return false;
		}
	}

	public static Integer getUserFocusedEventNumer() throws SQLException {
		return MainActivity.MyDbHelper.getFocusedEventDAO().queryForAll()
				.size();
	}

	public static Boolean deleteByEventId(long id) throws SQLException {
		List<FocusedEventDAO> list = MainActivity.MyDbHelper
				.getFocusedEventDAO().queryForEq(
						DATABASE_COLUMN_FOCUSED_EVENT_ID, id);
		if (list.size() == 1) {
			MainActivity.MyDbHelper.getFocusedEventDAO().delete(list.get(0));
		}
		return true;
	}
}

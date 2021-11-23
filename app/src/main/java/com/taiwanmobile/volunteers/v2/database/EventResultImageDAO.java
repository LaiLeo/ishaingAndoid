package com.taiwanmobile.volunteers.v2.database;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.taiwanmobile.volunteers.v2.MainActivity;

public class EventResultImageDAO {
	private final String TAG = getClass().getSimpleName();

	public static final String DATABASE_TABLE_NAME = "resultimage";
	public static final String DATABASE_COLUMN_ID = "_id";
	public static final String DATABASE_COLUMN_EVENT = "event";
	public static final String DATABASE_COLUMN_IMAGE = "image";

	static final String JSON_GET_KEY_ID = "id";
	static final String JSON_GET_KEY_REPLY_IMAGE = "image";
	static final String JSON_GET_KEY_REPLY_IMAGE_THUMB = "thumb_path";

	@DatabaseField(columnName = "_id", id = true)
	Long id;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	EventDAO event;
	@DatabaseField
	public String image;

	public EventResultImageDAO() {
	}

	public EventResultImageDAO(JSONObject obj, EventDAO eventDAO)
			throws JSONException {
		this.id = obj.getLong(JSON_GET_KEY_ID);
		this.event = eventDAO;
		if (DatabaseHelper.hasThumbnail(obj
				.getString(JSON_GET_KEY_REPLY_IMAGE_THUMB))) {
			this.image = obj.getString(JSON_GET_KEY_REPLY_IMAGE_THUMB);
		} else {
			this.image = obj.getString(JSON_GET_KEY_REPLY_IMAGE);
		}
	}

	public void save() {
		try {
			MainActivity.MyDbHelper.getEventResultImageDAO().createOrUpdate(
					this);
		} catch (SQLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	public static Cursor queryByEventId(long id) {
		String sql = "select r.* from resultimagedao r where r.event_id=?";
		String[] array = DatabaseHelper.fill(id);

		return MainActivity.MyDbHelper.getReadableDatabase().rawQuery(sql,
				array);
	}
}

package com.taiwanmobile.volunteers.v2.database;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.taiwanmobile.volunteers.v2.MainActivity;

public class ReplyDAO {
	private final String TAG = getClass().getSimpleName();

	public static final String DATABASE_TABLE_NAME = "reply";
	public static final String DATABASE_COLUMN_ID = "_id";
	public static final String DATABASE_COLUMN_EVENT = "event";
	public static final String DATABASE_COLUMN_USER = "user";
	public static final String DATABASE_COLUMN_REPLY_TIME = "replyTime";
	public static final String DATABASE_COLUMN_MESSAGE = "message";
	public static final String DATABASE_COLUMN_IMAGE = "image";

	public static final String DATABASE_COLUMN_USER_NAME = "userName";
	public static final String DATABASE_COLUMN_USER_ICON = "userIcon";

	static final String JSON_GET_KEY_ID = "id";
	static final String JSON_GET_KEY_USER = "user_account";
	static final String JSON_GET_KEY_REPLY_TIME = "reply_time";
	static final String JSON_GET_KEY_REPLY_MESSAGE = "message";
	static final String JSON_GET_KEY_REPLY_IMAGE = "image";
	static final String JSON_GET_KEY_REPLY_IMAGE_THUMB = "thumb_path";

	public static final String POST_KEY_REPORT_ID = "event";
	public static final String POST_KEY_MESSAGE = "message";
	public static final String POST_KEY_ICON = "image";

	@DatabaseField(columnName = "_id", id = true)
	public Long id;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	public EventDAO event;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	public UserAccountDAO user;
	@DatabaseField
	public String userName;
	@DatabaseField
	public String userIcon;
	@DatabaseField
	public String replyTime;
	@DatabaseField
	public String message;
	@DatabaseField
	public String image;

	public ReplyDAO() {
	}

	public ReplyDAO(JSONObject obj, EventDAO eventDAO) throws JSONException {
		this.id = obj.getLong(JSON_GET_KEY_ID);
		this.event = eventDAO;
		this.user = new UserAccountDAO(obj.getJSONObject(JSON_GET_KEY_USER));
		this.userName = this.user.displayName;
		this.userIcon = this.user.icon;
		this.replyTime = obj.getString(JSON_GET_KEY_REPLY_TIME).replace("T",
				" ");
		this.message = obj.getString(JSON_GET_KEY_REPLY_MESSAGE);
		if (DatabaseHelper.hasThumbnail(obj
				.getString(JSON_GET_KEY_REPLY_IMAGE_THUMB))) {
			this.image = obj.getString(JSON_GET_KEY_REPLY_IMAGE_THUMB);
		} else {
			this.image = obj.getString(JSON_GET_KEY_REPLY_IMAGE);
		}
	}

	public void save() {
		try {
//			Log.d(TAG,"getUserAccountDAO().createOrUpdate: "+ this.user);
//			MainActivity.MyDbHelper.getUserAccountDAO().createOrUpdate(
//					this.user);
			MainActivity.MyDbHelper.getReplyDAO().createOrUpdate(this);
		} catch (SQLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	public static Cursor queryByEventId(long id) {
		String sql = "select r.* from replydao r where r.event_id=? order by r.replyTime DESC;";
		String[] array = DatabaseHelper.fill(id);

		return MainActivity.MyDbHelper.getReadableDatabase().rawQuery(sql,
				array);
	}

	public static ReplyDAO getObjectById(long id) throws SQLException {
		List<ReplyDAO> list = MainActivity.MyDbHelper.getReplyDAO().queryForEq(
				DATABASE_COLUMN_ID, id);
		if (list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}
}

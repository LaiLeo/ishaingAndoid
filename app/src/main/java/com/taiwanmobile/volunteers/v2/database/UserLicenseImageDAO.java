package com.taiwanmobile.volunteers.v2.database;

import android.database.Cursor;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.taiwanmobile.volunteers.v2.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

public class UserLicenseImageDAO {
	private final String TAG = getClass().getSimpleName();

	public static final String DATABASE_TABLE_NAME = "user_license";
	public static final String DATABASE_COLUMN_ID = "_id";
	public static final String DATABASE_COLUMN_USER = "user_id";
	public static final String DATABASE_COLUMN_IMAGE = "image";

	static final String JSON_GET_KEY_ID = "id";
	static final String JSON_GET_KEY_REPLY_IMAGE = "image";
	static final String JSON_GET_KEY_REPLY_IMAGE_THUMB = "thumb_path";

	public static final String POST_KEY_ADD_LICENSE = "add_license";
	public static final String POST_KEY_DELETE_ID = "delete_id";

	@DatabaseField(columnName = "_id", id = true)
	public Long id;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	UserAccountDAO user;
	@DatabaseField
	public String image;

	public UserLicenseImageDAO() {
	}

	public UserLicenseImageDAO(JSONObject obj, UserAccountDAO userAccountDAO)
			throws JSONException {
		this.id = obj.getLong(JSON_GET_KEY_ID);
		this.user = userAccountDAO;
		if (DatabaseHelper.hasThumbnail(obj
				.getString(JSON_GET_KEY_REPLY_IMAGE_THUMB))) {
			this.image = obj.getString(JSON_GET_KEY_REPLY_IMAGE_THUMB);
		} else {
			this.image = obj.getString(JSON_GET_KEY_REPLY_IMAGE);
		}
	}

	public void save() {
		try {
			MainActivity.MyDbHelper.getUserLicenseImageDAO().createOrUpdate(
					this);
		} catch (SQLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	public static List<UserLicenseImageDAO> queryByUserId(long id) throws SQLException {
		List<UserLicenseImageDAO> list = MainActivity.MyDbHelper.getUserLicenseImageDAO().queryForAll();
//				.queryForEq(DATABASE_COLUMN_USER, id);
		return list;
	}
}

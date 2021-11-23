package com.taiwanmobile.volunteers.v2.database;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.taiwanmobile.volunteers.v2.MainActivity;

public class NpoDAO {
	private final String TAG = getClass().getSimpleName();

	public static final String DATABASE_TABLE_NAME = "npo";
	public static final String DATABASE_COLUMN_ID = "_id";
	public static final String DATABASE_COLUMN_NAME = "name";
	public static final String DATABASE_COLUMN_DESCRIPTION = "description";
	public static final String DATABASE_COLUMN_ICON = "iconURL";
	public static final String DATABASE_COLUMN_SERVICE_TARGET = "serviceTarget";
	public static final String DATABASE_COLUMN_CONTACT_NAME = "contactName";
	public static final String DATABASE_COLUMN_CONTACT_PHONE = "contactPhone";
	public static final String DATABASE_COLUMN_CONTACT_EMAIL = "contactEmail";
	public static final String DATABASE_COLUMN_CONTACT_ADDRESS = "contactAddress";
	public static final String DATABASE_COLUMN_CONTACT_WEBSITE = "contactWebSite";
	public static final String DATABASE_COLUMN_CONTACT_SITE = "contactSite";
	public static final String DATABASE_COLUMN_RATING_USER_NUM = "ratingUserNum";
	public static final String DATABASE_COLUMN_TOTAL_RATING_SCORE = "totalRatingScore";
	public static final String DATABASE_COLUMN_SUBSCRIBED_USER_NUM = "subscribedUserNum";
	public static final String DATABASE_COLUMN_JOINED_USER_NUM = "joinedUserNum";
	public static final String DATABASE_COLUMN_EVENT_NUM = "eventNum";
    public static final String DATABASE_COLUMN_IS_INVENTORY = "isInventory";
    public static final String DATABASE_COLUMN_IS_ENTERPRISE = "isEnterprise";
	public static final String DATABASE_COLUMN_YOUTUBE = "youtubeCode";

	static final String JSON_GET_KEY_ID = "id";
	static final String JSON_GET_KEY_NAME = "name";
	static final String JSON_GET_KEY_DESCRIPTION = "description";
	static final String JSON_GET_KEY_ICON = "npo_icon";
	static final String JSON_GET_KEY_FILE_THUMB = "thumb_path";
	static final String JSON_GET_KEY_SERVICE_TARGET = "service_target";
	static final String JSON_GET_KEY_CONTACT_NAME = "contact_name";
	static final String JSON_GET_KEY_CONTACT_PHONE = "contact_phone";
	static final String JSON_GET_KEY_CONTACT_EMAIL = "contact_email";
	static final String JSON_GET_KEY_CONTACT_ADDRESS = "contact_address";
	static final String JSON_GET_KEY_CONTACT_WEBSITE = "contact_website";
	static final String JSON_GET_KEY_CONTACT_SITE = "contact_site";
	static final String JSON_GET_KEY_RATING_USER_NUM = "rating_user_num";
	static final String JSON_GET_KEY_TOTAL_RATING_SCORE = "total_rating_score";
	static final String JSON_GET_KEY_SUBSCRIBED_USER_NUM = "subscribed_user_num";
	static final String JSON_GET_KEY_JOINED_USER_NUM = "joined_user_num";
	static final String JSON_GET_KEY_EVENT_NUM = "event_num";
    static final String JSON_GET_KEY_IS_INVENTORY = "is_inventory";
    static final String JSON_GET_KEY_IS_ENTERPRISE = "is_enterprise";
	static final String JSON_GET_KEY_YOUTUBE = "youtube_code";

	@DatabaseField(columnName = "_id", id = true)
	public Long id;
	@DatabaseField
	public String name;
	@DatabaseField
	public String description;
	@DatabaseField
	public String serviceTarget;
	@DatabaseField
	public String iconURL;
	@DatabaseField
	public String contactName;
	@DatabaseField
	public String contactPhone;
	@DatabaseField
	public String contactEmail;
	@DatabaseField
	public String contactAddress;
	@DatabaseField
	public String contactWebSite;
	@DatabaseField
	public String contactSite;
	@DatabaseField
	public Integer ratingUserNum = 0;
	@DatabaseField
	public Double totalRatingScore = 0.0;
	@DatabaseField
	public Integer subscribedUserNum = 0;
	@DatabaseField
	public Integer joinedUserNum = 0;
	@DatabaseField
	public Integer eventNum = 0;
    @DatabaseField
    public Boolean isInventory;
    @DatabaseField
    public Boolean isEnterprise;
	@DatabaseField
	public String youtubeCode;

	NpoDAO() {
	}

	public NpoDAO(JSONObject obj) throws JSONException {
		this.id = obj.getLong(JSON_GET_KEY_ID);
		this.name = obj.getString(JSON_GET_KEY_NAME);
		this.description = obj.getString(JSON_GET_KEY_DESCRIPTION);
		if (DatabaseHelper.hasThumbnail(obj.getString(JSON_GET_KEY_FILE_THUMB))) {
			this.iconURL = obj.getString(JSON_GET_KEY_FILE_THUMB);
		} else {
			this.iconURL = obj.getString(JSON_GET_KEY_ICON);
		}
		this.serviceTarget = obj.getString(JSON_GET_KEY_SERVICE_TARGET);
		this.contactName = obj.getString(JSON_GET_KEY_CONTACT_NAME);
		this.contactPhone = obj.getString(JSON_GET_KEY_CONTACT_PHONE);
		this.contactEmail = obj.getString(JSON_GET_KEY_CONTACT_EMAIL);
		this.contactAddress = obj.getString(JSON_GET_KEY_CONTACT_ADDRESS);
		this.contactWebSite = obj.getString(JSON_GET_KEY_CONTACT_WEBSITE);
		this.contactSite = obj.getString(JSON_GET_KEY_CONTACT_SITE);

		this.ratingUserNum = obj.getInt(JSON_GET_KEY_RATING_USER_NUM);
		this.totalRatingScore = obj.getDouble(JSON_GET_KEY_TOTAL_RATING_SCORE);
		this.subscribedUserNum = obj.getInt(JSON_GET_KEY_SUBSCRIBED_USER_NUM);
		this.joinedUserNum = obj.getInt(JSON_GET_KEY_JOINED_USER_NUM);
		this.eventNum = obj.getInt(JSON_GET_KEY_EVENT_NUM);
        this.isInventory = obj.getBoolean(JSON_GET_KEY_IS_INVENTORY);
        this.isEnterprise = obj.getBoolean(JSON_GET_KEY_IS_ENTERPRISE);
		this.youtubeCode = obj.getString(JSON_GET_KEY_YOUTUBE);
	}

	public void save() {
		try {
			MainActivity.MyDbHelper.getNpoDAO().createOrUpdate(this);
		} catch (SQLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	public float getAverageScore() {
		return (float) (totalRatingScore / ratingUserNum);
	}

	public static Cursor queryVolunteerNpos() {
		String sql = "select n.* from npodao n;";

		return MainActivity.MyDbHelper.getReadableDatabase()
				.rawQuery(sql, null);
	}

    public static Cursor queryVolunteerGeneralNpos() {
        String sql = "select n.* from npodao n where isEnterprise = 0;";

        return MainActivity.MyDbHelper.getReadableDatabase()
                .rawQuery(sql, null);
    }

    public static Cursor queryVolunteerEnterpriseNpos() {
        String sql = "select n.* from npodao n where isEnterprise = 1;";

        return MainActivity.MyDbHelper.getReadableDatabase()
                .rawQuery(sql, null);
    }

	public static Cursor queryItemNpos() {
		String sql = "select n.* from npodao n where isInventory=1;";

//		 sql = "select e.* from nopdao n ,eventdao e  where isVolunteer=1 and n.isEnterprise=1 order by isUrgent DESC, isShort DESC, e.happenDate ASC;";

		return MainActivity.MyDbHelper.getReadableDatabase()
				.rawQuery(sql, null);
	}

	public static Cursor querCursorById(long id) {
		String sql = "select n.* from npodao n where n._id=?;";
		String[] array = DatabaseHelper.fill(id);

		return MainActivity.MyDbHelper.getReadableDatabase().rawQuery(sql,
				array);
	}

	public static NpoDAO queryObjectById(long id) throws SQLException {
		List<NpoDAO> list = MainActivity.MyDbHelper.getNpoDAO().queryForEq(
				DATABASE_COLUMN_ID, id);
		if (list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}
}

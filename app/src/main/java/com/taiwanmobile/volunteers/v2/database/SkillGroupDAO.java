package com.taiwanmobile.volunteers.v2.database;

import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;

public class SkillGroupDAO {
	final String TAG = getClass().getSimpleName();

	public static final String DATABASE_TABLE_NAME = "skillgroup";
	public static final String DATABASE_COLUMN_ID = "_id";
	public static final String DATABASE_COLUMN_NAME = "name";
	public static final String DATABASE_COLUMN_DESCRIPTION = "description";
	public static final String DATABASE_COLUMN_CURRENT_VOLUNTEER_NUMBER = "currentVolunteerNum";
	public static final String DATABASE_COLUMN_REQUIRED_VOLUNTEER_NUMBER = "requiredVolunteerNum";

	static final String JSON_GET_KEY_ID = "id";
	static final String JSON_GET_KEY_NAME = "name";
	static final String JSON_GET_KEY_DESCRIPTION = "skills_description";
	static final String JSON_GET_KEY_CURRENT_VOLUNTEER_NUMBER = "current_volunteer_number";
	static final String JSON_GET_KEY_REQUIRED_VOLUNTEER_NUMBER = "volunteer_number";

	@DatabaseField(columnName = "_id", id = true)
	public long id;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	EventDAO event;
	@DatabaseField
	public String name;
	@DatabaseField
	public String description;
	@DatabaseField
	public Integer currentVolunteerNum;
	@DatabaseField
	public Integer requiredVolunteerNum;

	SkillGroupDAO() {
	}

	public SkillGroupDAO(JSONObject skillGroupObj, EventDAO eventObject)
			throws JSONException {
		this.id = skillGroupObj.getLong(JSON_GET_KEY_ID);
		this.event = eventObject;
		this.name = skillGroupObj.getString(JSON_GET_KEY_NAME);
		this.description = skillGroupObj.getString(JSON_GET_KEY_DESCRIPTION);
		this.currentVolunteerNum = skillGroupObj
				.getInt(JSON_GET_KEY_CURRENT_VOLUNTEER_NUMBER);
		this.requiredVolunteerNum = skillGroupObj
				.getInt(JSON_GET_KEY_REQUIRED_VOLUNTEER_NUMBER);
	}

}

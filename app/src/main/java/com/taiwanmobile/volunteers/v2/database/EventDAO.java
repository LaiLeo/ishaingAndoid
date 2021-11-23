package com.taiwanmobile.volunteers.v2.database;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.util.Log;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.dao.LazyForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.taiwanmobile.volunteers.v2.MainActivity;

public class EventDAO {
    final static String TAG = "EventDAO";

    public static final String DATABASE_TABLE_NAME = "event";
	public static final String DATABASE_COLUMN_ID = "_id";
	public static final String DATABASE_COLUMN_NPO_ID = "npo_id";
	public static final String DATABASE_COLUMN_USERACCOUNT_ID = "user_id";
	public static final String DATABASE_COLUMN_SUBJECT = "subject";
	public static final String DATABASE_COLUMN_DESCRIPTION = "description";
	public static final String DATABASE_COLUMN_IMAGE = "iconURL";
	public static final String DATABASE_COLUMN_PUB_DATE = "pubDate";
	public static final String DATABASE_COLUMN_HAPPEN_DATE = "happenDate";
	public static final String DATABASE_COLUMN_CLOSE_DATE = "closeDate";
	public static final String DATABASE_COLUMN_REGISTER_DEADLINE_DATE = "registerDeadlineDate";
	public static final String DATABASE_COLUMN_EVENT_HOUR = "eventHour";
	public static final String DATABASE_COLUMN_ADDRESS = "address";
	public static final String DATABASE_COLUMN_ADDRESS_CITY = "addressCity";
	public static final String DATABASE_COLUMN_FOCUS_NUM = "focusNum";
	public static final String DATABASE_COLUMN_REPLY_NUM = "replyNum";
	public static final String DATABASE_COLUMN_INSURANCE = "hasInsurance";
	public static final String DATABASE_COLUMN_TRAINING = "hasTraining";
	public static final String DATABASE_COLUMN_TRAINING_DESCRIPTION = "trainingContent";
	public static final String DATABASE_COLUMN_INSURANCE_DESCRIPTION = "insuranceContent";
	public static final String DATABASE_COLUMN_CURRENT_VOLUNTEER_NUMBER = "currentVolunteerNum";
	public static final String DATABASE_COLUMN_REQUIRED_VOLUNTEER_NUMBER = "requiredVolunteerNum";
	public static final String DATABASE_COLUMN_REQUIRED_GROUP = "isRequiredGroup";
	public static final String DATABASE_COLUMN_SKILLS = "skillDescription";
	public static final String DATABASE_COLUMN_LAT = "latitude";
	public static final String DATABASE_COLUMN_LNG = "longtitude";
	public static final String DATABASE_COLUMN_RATING_USER_NUM = "ratingUserNum";
	public static final String DATABASE_COLUMN_TOTAL_RATING_SCORE = "totalRatingScore";
	public static final String DATABASE_COLUMN_IS_VOLUNTEER_EVENT = "isVolunteer";
	public static final String DATABASE_COLUMN_IS_URGENT = "isUrgent";
	public static final String DATABASE_COLUMN_REQUIRED_SIGNOUT = "requiredSignout";
	public static final String DATABASE_COLUMN_IS_SHORT = "isShort";
	public static final String DATABASE_COLUMN_VOLUNTEER_TYPE = "volunteerType";
    public static final String DATABASE_COLUMN_COOPERATION_NPO = "cooperation_npo";

	public static final String DATABASE_COLUMN_DISTANCE = "distance";
	// public static final String DATABASE_COLUMN_LEAVE_UID = "leaveUid";

	public static final String USER_NAME = "username";
	public static final String USER_ICON = "icon";

	static final String JSON_GET_KEY_ID = "id";
	static final String JSON_GET_KEY_SUBJECT = "subject";
	static final String JSON_GET_KEY_DESCRIPTION = "description";
	static final String JSON_GET_KEY_IMAGE = "image_link_1";
	static final String JSON_GET_KEY_IMAGE_THUMB = "thumb_path";
	static final String JSON_GET_KEY_PUB_DATE = "pub_date";
	static final String JSON_GET_KEY_HAPPEN_DATE = "happen_date";
	static final String JSON_GET_KEY_CLOSE_DATE = "close_date";
	static final String JSON_GET_KEY_REGISTER_DEADLINE_DATE = "register_deadline_date";
	static final String JSON_GET_KEY_EVENT_HOUR = "event_hour";
	static final String JSON_GET_KEY_ADDRESS = "address";
	static final String JSON_GET_KEY_ADDRESS_CITY = "address_city";
	static final String JSON_GET_KEY_FOCUS_NUM = "focus_num";
	static final String JSON_GET_KEY_REPLY_NUM = "reply_num";
	static final String JSON_GET_KEY_INSURANCE = "insurance";
	static final String JSON_GET_KEY_TRAINING = "volunteer_training";
	static final String JSON_GET_KEY_TRAINING_DESCRIPTION = "volunteer_training_description";
	static final String JSON_GET_KEY_INSURANCE_DESCRIPTION = "insurance_description";
	static final String JSON_GET_KEY_CURRENT_VOLUNTEER_NUMBER = "current_volunteer_number";
	static final String JSON_GET_KEY_REQUIRED_VOLUNTEER_NUMBER = "required_volunteer_number";
	static final String JSON_GET_KEY_REQUIRED_GROUP = "required_group";
	static final String JSON_GET_KEY_SKILLS = "skills_description";
	static final String JSON_GET_KEY_FOREIGN_THIRD_PARTY_ID = "foreign_third_party_id";
	static final String JSON_GET_KEY_LAT = "lat";
	static final String JSON_GET_KEY_LNG = "lng";
	static final String JSON_GET_KEY_RATING_USER_NUM = "rating_user_num";
	static final String JSON_GET_KEY_TOTAL_RATING_SCORE = "total_rating_score";
	static final String JSON_GET_KEY_IS_VOLUNTEER_EVENT = "is_volunteer_event";
	static final String JSON_GET_KEY_IS_URGENT = "is_urgent";
    static final String JSON_GET_KEY_REQUIRED_SIGNOUT = "require_signout";
    static final String JSON_GET_KEY_COOPERATION_NPO = "cooperation_NPO";
    static final String JSON_GET_KEY_COOPERATION_NPO_NAME = "name";
    static final String JSON_GET_KEY_COOPERATION_NPO_ID = "id";

    public static final String JSON_GET_KEY_IS_SHORT = "is_short";
	public static final String JSON_GET_KEY_VOLUNTEER_TYPE = "volunteer_type";
	// static final String JSON_GET_KEY_UID = "uid";
	// static final String JSON_GET_KEY_LEAVE_UID = "leave_uid";

	static final String JSON_GET_KEY_USER = "user_account";
	static final String JSON_GET_KEY_NPO = "owner_NPO";
	static final String JSON_GET_KEY_SKILL_GROUP = "skill_groups";
	static final String JSON_GET_KEY_REPLIES = "replies";
	static final String JSON_GET_KEY_RESULT_IMAGES = "images";

	@DatabaseField(columnName = "_id", id = true)
	public long id;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	public NpoDAO npo;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	public UserAccountDAO user;
	@DatabaseField
	public String subject;
	@DatabaseField
	public String description;
	@DatabaseField
	public String iconURL;
    @ForeignCollectionField
    public Collection<CooperationNpoDAO> cooperationNpo;
	@DatabaseField
	public String pubDate;
	@DatabaseField
	public String happenDate;
	@DatabaseField
	public String closeDate;
	@DatabaseField
	public String registerDeadlineDate;
	@DatabaseField
	public Double eventHour;
	@DatabaseField
	public String address;
	@DatabaseField
	public String addressCity;
	@DatabaseField
	public Integer focusNum;
	@DatabaseField
	public Integer replyNum;
	@DatabaseField
	public Boolean hasInsurance;
	@DatabaseField
	public String insuranceContent;
	@DatabaseField
	public Boolean hasTraining;
	@DatabaseField
	public String trainingContent;
	@DatabaseField
	public Integer currentVolunteerNum;
	@DatabaseField
	public Integer requiredVolunteerNum;
	@DatabaseField
	public Boolean isRequiredGroup;
	@DatabaseField
	public String skillDescription;
	@DatabaseField
	public String foreignThirdPartyId;
	@DatabaseField
	public Double longtitude;
	@DatabaseField
	public Double latitude;
	@DatabaseField
	public Integer ratingUserNum = 0;
	@DatabaseField
	public Double totalRatingScore = 0.0;
	@DatabaseField
	public Boolean isVolunteer;
	@DatabaseField
	public Boolean isUrgent;
	@DatabaseField
	public Boolean requiredSignout;
	@DatabaseField
	public Boolean isShort;
	@DatabaseField
	public String volunteerType;

	@DatabaseField
	public Double distance;
	// @DatabaseField
	// public String uid;
	// @DatabaseField
	// public String leaveUid;
	@ForeignCollectionField
	public Collection<SkillGroupDAO> skillGroups;

	@ForeignCollectionField
	public Collection<ReplyDAO> replies;

	@ForeignCollectionField
	public Collection<EventResultImageDAO> images;

	public EventDAO() {
	}

	public EventDAO(JSONObject eventObj) throws JSONException {
		this.skillGroups = new ArrayList<>();
		this.replies = new ArrayList<>();
		this.images = new ArrayList<>();
        this.cooperationNpo = new ArrayList<>();

		this.id = eventObj.getLong(JSON_GET_KEY_ID);
		try {
			this.npo = new NpoDAO(eventObj.getJSONObject(JSON_GET_KEY_NPO));
		}catch(Exception e) {

		}
		if (!eventObj.isNull(JSON_GET_KEY_USER)) {
			this.user = new UserAccountDAO(
					eventObj.getJSONObject(JSON_GET_KEY_USER));
		} else {
			this.user = null;
		}
		this.subject = eventObj.getString(JSON_GET_KEY_SUBJECT);
		this.description = eventObj.getString(JSON_GET_KEY_DESCRIPTION);
		if (DatabaseHelper.hasThumbnail(eventObj
				.getString(JSON_GET_KEY_IMAGE_THUMB))) {
			this.iconURL = eventObj.getString(JSON_GET_KEY_IMAGE_THUMB);
		} else {
			this.iconURL = eventObj.getString(JSON_GET_KEY_IMAGE);
		}

        JSONArray npoList = eventObj.getJSONArray(JSON_GET_KEY_COOPERATION_NPO);
        Log.d(TAG, "dao name json: "+ npoList.length() + " event subject: " + subject);
        try {
			for (int i = 0; i < npoList.length(); i++) {
				this.cooperationNpo.add(new CooperationNpoDAO(npoList.getJSONObject(i), this));
			}
		}catch (Exception e) {

		}
        this.pubDate = eventObj.getString(JSON_GET_KEY_PUB_DATE).replace("T",
				" ");
		this.happenDate = eventObj.getString(JSON_GET_KEY_HAPPEN_DATE).replace(
				"T", " ");
		this.closeDate = eventObj.getString(JSON_GET_KEY_CLOSE_DATE).replace(
				"T", " ");
		this.registerDeadlineDate = eventObj.getString(
				JSON_GET_KEY_REGISTER_DEADLINE_DATE).replace("T", " ");
		this.eventHour = eventObj.getDouble(JSON_GET_KEY_EVENT_HOUR);
		this.address = eventObj.getString(JSON_GET_KEY_ADDRESS);
		this.addressCity = eventObj.getString(JSON_GET_KEY_ADDRESS_CITY);
		this.focusNum = eventObj.getInt(JSON_GET_KEY_FOCUS_NUM);
		this.replyNum = eventObj.getInt(JSON_GET_KEY_REPLY_NUM);
		this.hasInsurance = eventObj.getBoolean(JSON_GET_KEY_INSURANCE);
		this.hasTraining = eventObj.getBoolean(JSON_GET_KEY_TRAINING);
		this.trainingContent = eventObj
				.getString(JSON_GET_KEY_TRAINING_DESCRIPTION);
		this.insuranceContent = eventObj
				.getString(JSON_GET_KEY_INSURANCE_DESCRIPTION);
		this.currentVolunteerNum = eventObj
				.getInt(JSON_GET_KEY_CURRENT_VOLUNTEER_NUMBER);
		this.requiredVolunteerNum = eventObj
				.getInt(JSON_GET_KEY_REQUIRED_VOLUNTEER_NUMBER);
		this.isRequiredGroup = eventObj.getBoolean(JSON_GET_KEY_REQUIRED_GROUP);
		this.skillDescription = eventObj.getString(JSON_GET_KEY_SKILLS);
		this.foreignThirdPartyId = eventObj.getString(JSON_GET_KEY_FOREIGN_THIRD_PARTY_ID);
		this.latitude = eventObj.getDouble(JSON_GET_KEY_LAT);
		this.longtitude = eventObj.getDouble(JSON_GET_KEY_LNG);
		this.ratingUserNum = eventObj.getInt(JSON_GET_KEY_RATING_USER_NUM);
		this.totalRatingScore = eventObj
				.getDouble(JSON_GET_KEY_TOTAL_RATING_SCORE);
		this.isUrgent = eventObj.getBoolean(JSON_GET_KEY_IS_URGENT);
		this.isVolunteer = eventObj.getBoolean(JSON_GET_KEY_IS_VOLUNTEER_EVENT);
		this.requiredSignout = eventObj
				.getBoolean(JSON_GET_KEY_REQUIRED_SIGNOUT);
		this.isShort = eventObj.getBoolean(JSON_GET_KEY_IS_SHORT);
		this.volunteerType = eventObj.getString(JSON_GET_KEY_VOLUNTEER_TYPE);
		// this.uid = eventObj.getString(JSON_GET_KEY_UID);
		// this.leaveUid = eventObj.getString(JSON_GET_KEY_LEAVE_UID);

		try {
			List<SkillGroupDAO> skillGroupList = new ArrayList<>();
			JSONArray skillGroupObjs = eventObj
					.getJSONArray(JSON_GET_KEY_SKILL_GROUP);
			for (int loop = 0; loop < skillGroupObjs.length(); loop++) {
				SkillGroupDAO skiiGroup = new SkillGroupDAO(
						skillGroupObjs.getJSONObject(loop), this);
				skillGroupList.add(skiiGroup);
			}
			this.addSkillGroup(skillGroupList);
		}catch(Exception e){

		}


		try {
		List<ReplyDAO> replyList = new ArrayList<>();
		JSONArray replyObjList = eventObj.getJSONArray(JSON_GET_KEY_REPLIES);
		for (int loop = 0; loop < replyObjList.length(); loop++) {
			ReplyDAO reply = new ReplyDAO(replyObjList.getJSONObject(loop), this);
			replyList.add(reply);
		}
		this.addReplies(replyList);
		}catch(Exception e){

		}


		try {
			List<EventResultImageDAO> imageList = new ArrayList<>();
			JSONArray imageObjList = eventObj.getJSONArray(JSON_GET_KEY_RESULT_IMAGES);
			for (int loop = 0; loop < imageObjList.length(); loop++) {
				EventResultImageDAO image = new EventResultImageDAO(
						imageObjList.getJSONObject(loop), this);
				imageList.add(image);
			}
			this.addResultImages(imageList);
		}catch(Exception e){

		}
	}

	public void setUser(UserAccountDAO u) {
		this.user = u;
	}

	public void addSkillGroup(List<SkillGroupDAO> groups) {
		this.skillGroups.addAll(groups);
	}

	public void addReplies(List<ReplyDAO> groups) {
		this.replies.addAll(groups);
	}

	public void addResultImages(List<EventResultImageDAO> groups) {
		this.images.addAll(groups);
	}

	// public void addReply(ReplyDAO... replies) {
	// this.addReply(Arrays.asList(replies));
	// }
	//
	// public void addReply(List<ReplyDAO> replies) {
	// this.replies.addAll(replies);
	// }
	//
	// public Integer getReplyNum() {
	// return this.replyNumber;
	// }
	//
	// public void addReplyNum(Integer num) throws SQLException {
	// this.replyNumber += num;
	// MainActivity.MyDbHelper.getReportDAO().update(this);
	// }

	public Long getId() {
		return id;
	}

	public String getNpoName() {
		return this.npo.name;
	}

	public float getAverageScore() {
		return (float) (totalRatingScore / ratingUserNum);
	}

	public void save() {
		try {
			MainActivity.MyDbHelper.getNpoDAO().createOrUpdate(npo);
			if (user != null) {
//				Log.d(TAG,"getUserAccountDAO().createOrUpdate: "+ this.user);
//				MainActivity.MyDbHelper.getUserAccountDAO()
//						.createOrUpdate(user);
			}
			for (SkillGroupDAO dao : skillGroups) {
				MainActivity.MyDbHelper.getSkillGroupDAO().createOrUpdate(dao);
			}
			for (ReplyDAO dao : replies) {
				dao.save();
			}
			for (EventResultImageDAO dao : images) {
				dao.save();
			}
            Log.d(TAG, "save: dao name: " + cooperationNpo.size());
            for (CooperationNpoDAO dao: cooperationNpo){
                MainActivity.MyDbHelper.getCooperationNpoDAO().createOrUpdate(dao);
            }
			MainActivity.MyDbHelper.getEventDAO().createOrUpdate(this);
		} catch (SQLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	public static Cursor queryAllEvents() {
		String sql = "select e.* from eventdao e order by e.happenDate DESC;";

		return MainActivity.MyDbHelper.getReadableDatabase()
				.rawQuery(sql, null);
	}

	public static Cursor queryAllVolunteerEvents() {
		String sql = "select e.* from eventdao e where isVolunteer=1 order by isUrgent DESC, isShort DESC, e.happenDate ASC;";

		Cursor c = MainActivity.MyDbHelper.getReadableDatabase().rawQuery(sql,
				null);
		return filterActivatedEvents(c);
	}

    public static Cursor queryGeneralVolunteerEvents() {
        /*
		String sql = "select e.* from eventdao e JOIN npodao n ON e.npo_id = n._id " +
                "where isVolunteer=1 AND n.isEnterprise=0 AND e.registerDeadlineDate > datetime() " +
                "order by (requiredVolunteerNum != 0 AND requiredVolunteerNum <= currentVolunteerNum) ASC, isUrgent DESC, isShort DESC, e.happenDate ASC;";
        */
        //FIH-modify for 志工訊息頁面按照上架時間排序： 依照新規則，上架時間來排序，之前的order規則全拿掉，因為order依照前後順序來排優先級，所以依據e.pubDate排後再加上之前的條件也沒有用
		String sql = "select e.* from eventdao e JOIN npodao n ON e.npo_id = n._id " +
				"where isVolunteer=1 AND n.isEnterprise=0 AND e.registerDeadlineDate > datetime() " +
//				"AND (e.requiredVolunteerNum >0 AND e.currentVolunteerNum<e.requiredVolunteerNum OR e.requiredVolunteerNum=0) " +//this for 除去滿額
				"order by e.pubDate DESC;";//this for 以上架時間為准

        Cursor c = MainActivity.MyDbHelper.getReadableDatabase().rawQuery(sql,
                null);
        Log.d(TAG, "queryEnterpriseVolunteerEvents return rows: " + c.getCount());
        return filterActivatedEvents(c);
    }

	public static Cursor queryEnterpriseVolunteerEvents() {
        /*
		String sql = "select e.* from eventdao e JOIN npodao n ON e.npo_id = n._id " +
                "where isVolunteer=1 AND n.isEnterprise=1  AND e.registerDeadlineDate > datetime() " +
                "order by (requiredVolunteerNum != 0 AND requiredVolunteerNum <= currentVolunteerNum) ASC, isUrgent DESC, isShort DESC, e.happenDate ASC;";
        */
		//FIH-modify for 企業志工頁面按照上架時間排序： 依照新規則，上架時間來排序，之前的order規則全拿掉，因為order依照前後順序來排優先級，所以依據e.pubDate排後再加上之前的條件也沒有用
		String sql = "select e.* from eventdao e JOIN npodao n ON e.npo_id = n._id " +
				"where isVolunteer=1 AND n.isEnterprise=1  AND e.registerDeadlineDate > datetime() " +
//				"AND (e.requiredVolunteerNum >0 AND e.currentVolunteerNum<e.requiredVolunteerNum OR e.requiredVolunteerNum=0) " +//this for 除去滿額
				"order by e.pubDate DESC;";//this for 以上架時間為准

		Cursor c = MainActivity.MyDbHelper.getReadableDatabase().rawQuery(sql,
				null);
        Log.d(TAG, "queryEnterpriseVolunteerEvents return rows: " + c.getCount());
		return filterActivatedEvents(c);
	}

    public static Cursor queryAllVolunteerEventsAndSortByDistance() {
        String sql = "select e.* from eventdao e  where isVolunteer=1 order by distance;";

        Cursor c = MainActivity.MyDbHelper.getReadableDatabase().rawQuery(sql,
                null);
        return filterActivatedEvents(c);
    }

    public static Cursor quertGeneralVolunteerEventsAndSortByDistance(){

        String sql = "select e.* from eventdao e JOIN npodao n ON e.npo_id = n._id  where isVolunteer=1 AND n.isEnterprise=0 order by distance;";

        Cursor c = MainActivity.MyDbHelper.getReadableDatabase().rawQuery(sql,
                null);
        return filterActivatedEvents(c);
    }

    public static Cursor queryEnterpriseVolunteerEventsAndSortByDistance() {
        String sql = "select e.* from eventdao e JOIN npodao n ON e.npo_id = n._id  where isVolunteer=1 AND n.isEnterprise=1 order by distance;";

        Cursor c = MainActivity.MyDbHelper.getReadableDatabase().rawQuery(sql,
                null);
        return filterActivatedEvents(c);
    }

	public static Cursor queryAllItemEvents() {
//		String sql = "select e.* from eventdao e  where isVolunteer=0 order by (requiredVolunteerNum != 0 AND requiredVolunteerNum <= currentVolunteerNum)  AND e.registerDeadlineDate > datetime() " +
//				" ASC, isUrgent DESC, isShort DESC, e.happenDate DESC;";
		//FIH-modify for 物資訊息頁面按照上架時間排序： 依照新規則，上架時間來排序，之前的order規則全拿掉，因為order依照前後順序來排優先級，所以依據e.pubDate排後再加上之前的條件也沒有用
		String sql = "select e.* from eventdao e  where isVolunteer=0 AND e.registerDeadlineDate > datetime() "+
//				"AND (e.requiredVolunteerNum >0 AND e.currentVolunteerNum<e.requiredVolunteerNum OR e.requiredVolunteerNum=0) " +//this for 除去滿額
				"order by e.pubDate DESC;";//this for 以上架時間為准

		Cursor c = MainActivity.MyDbHelper.getReadableDatabase().rawQuery(sql,
				null);
		return filterActivatedEvents(c);
	}

	public static Cursor queryAllItemEventsAndSortByDistance() {
		String sql = "select e.* from eventdao e  where isVolunteer=0 order by distance;";

		Cursor c = MainActivity.MyDbHelper.getReadableDatabase().rawQuery(sql,
				null);
		return filterActivatedEvents(c);
	}

	private static Cursor filterActivatedEvents(Cursor cursor) {
		String[] columns = cursor.getColumnNames();
		MatrixCursor newCursor = new MatrixCursor(columns, 1);

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			Date today = Calendar.getInstance().getTime();
			try {
//				// check if is fulled
//				Long requiredVolunteerNumber = cursor
//						.getLong(cursor
//								.getColumnIndex(EventDAO.DATABASE_COLUMN_REQUIRED_VOLUNTEER_NUMBER));
//				Long currentVolunteerNumber = cursor
//						.getLong(cursor
//								.getColumnIndex(EventDAO.DATABASE_COLUMN_CURRENT_VOLUNTEER_NUMBER));
//				if (currentVolunteerNumber >= requiredVolunteerNumber) {
//					continue;
//				}

				Date date = Db.DATETIME_FORMATTER
						.parse(cursor.getString(cursor
								.getColumnIndex(EventDAO.DATABASE_COLUMN_REGISTER_DEADLINE_DATE)));
				if ((date.getTime() - today.getTime()) >= 0) {
					MatrixCursor.RowBuilder b = newCursor.newRow();
					for (String col : columns) {
						b.add(cursor.getString(cursor.getColumnIndex(col)));
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		cursor.close();
		return newCursor;
	}

	private static List<EventDAO> filterActivatedEvents(List<EventDAO> list) {
		List<EventDAO> newList = new ArrayList<>();

		for (EventDAO event : list) {
			Date today = Calendar.getInstance().getTime();
			try {
				// check if is fulled
				Integer requiredVolunteerNumber = event.requiredVolunteerNum;
				Integer currentVolunteerNumber = event.currentVolunteerNum;
				if (currentVolunteerNumber >= requiredVolunteerNumber) {
					continue;
				}

				Date date = Db.DATETIME_FORMATTER
						.parse(event.registerDeadlineDate);
				if ((date.getTime() - today.getTime()) >= 0) {
					newList.add(event);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return newList;
	}

	public static Cursor queryAllVolunteerEventsByNpoId(long npoId) {
		String sql = "select e.* from eventdao e where e.npo_id=? order by isUrgent DESC, isShort DESC, e.happenDate DESC;";
		String[] array = DatabaseHelper.fill(npoId);

		return MainActivity.MyDbHelper.getReadableDatabase().rawQuery(sql,
				array);
	}

	public static Cursor queryCursorById(long id) {
		String sql = "select e.* from eventdao e where e._id=?;";
		String[] array = DatabaseHelper.fill(id);

		return MainActivity.MyDbHelper.getReadableDatabase().rawQuery(sql,
				array);
	}

	public static EventDAO getObjectById(long id) throws SQLException {
		List<EventDAO> list = MainActivity.MyDbHelper.getEventDAO().queryForEq(
				DATABASE_COLUMN_ID, id);
		if (list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	// public static List<EventDAO> getAllEventsObjects() throws SQLException {
	// return MainActivity.MyDbHelper.getEventDAO().queryForAll();
	// }

	public static List<EventDAO> getAllActivatedEventsObjects()
			throws SQLException {
		return filterActivatedEvents(MainActivity.MyDbHelper.getEventDAO()
				.queryForAll());
		// return MainActivity.MyDbHelper.getEventDAO().queryForAll();
	}

	public static List<EventDAO> getAllVolunteerEventsObjects()
			throws SQLException {
		return MainActivity.MyDbHelper.getEventDAO().queryForEq(
				DATABASE_COLUMN_IS_VOLUNTEER_EVENT, true);
	}

	public static List<EventDAO> getVolunteerUergentEventObjects()
			throws SQLException {
		return MainActivity.MyDbHelper.getEventDAO().query(
				MainActivity.MyDbHelper.getEventDAO().queryBuilder().where()
						.eq(DATABASE_COLUMN_IS_VOLUNTEER_EVENT, true).and()
						.eq(DATABASE_COLUMN_IS_URGENT, true).prepare());
	}

	public static List<EventDAO> getVolunteerShortEventObjects()
			throws SQLException {
		return MainActivity.MyDbHelper.getEventDAO().query(
				MainActivity.MyDbHelper.getEventDAO().queryBuilder().where()
						.eq(DATABASE_COLUMN_IS_VOLUNTEER_EVENT, true).and()
						.eq(DATABASE_COLUMN_IS_SHORT, true).prepare());
	}

	public static List<EventDAO> getVolunteerLongObjects() throws SQLException {
		return MainActivity.MyDbHelper.getEventDAO().query(
				MainActivity.MyDbHelper.getEventDAO().queryBuilder().where()
						.eq(DATABASE_COLUMN_IS_VOLUNTEER_EVENT, true).and()
						.eq(DATABASE_COLUMN_IS_SHORT, false).prepare());
	}

	public boolean isTTVolunteerEvent() {
		return foreignThirdPartyId.startsWith("TTVOLUNTEER|");
	}
}

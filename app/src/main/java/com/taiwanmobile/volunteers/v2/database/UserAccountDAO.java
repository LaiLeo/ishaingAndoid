package com.taiwanmobile.volunteers.v2.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;

public class UserAccountDAO {
	private static final String TAG = UserAccountDAO.class.getSimpleName();

	public static final String DATABASE_TABLE_NAME = "user_account";
	public static final String DATABASE_COLUMN_ID = "_id";
	public static final String DATABASE_COLUMN_NAME = "displayName";
	public static final String DATABASE_COLUMN_EMAIL = "email";
	public static final String DATABASE_COLUMN_PHONE = "phone";
	public static final String DATABASE_COLUMN_ICON = "icon";
	public static final String DATABASE_COLUMN_IS_PUBLIC = "isPublic";
	public static final String DATABASE_COLUMN_INTEREST = "interest";
	public static final String DATABASE_COLUMN_ABOUT_ME = "aboutMe";
	public static final String DATABASE_COLUMN_RANKING = "ranking";
	public static final String DATABASE_COLUMN_SCORE = "score";
	public static final String DATABASE_COLUMN_EVENT_HOUR = "eventHour";
	public static final String DATABASE_COLUMN_EVENT_NUM = "eventNum";

	static final String JSON_GET_KEY_ID = "id";
	static final String JSON_GET_KEY_NAME = "name";
	static final String JSON_GET_KEY_EMAIL = "email";
	static final String JSON_GET_KEY_OPEN = "open";
	static final String JSON_GET_KEY_PHONE = "phone";
	static final String JSON_GET_KEY_ICON = "icon";
	static final String JSON_GET_KEY_ICON_THUMB = "thumb_path";
	static final String JSON_GET_KEY_SECURITY_ID = "security_id";
	static final String JSON_GET_KEY_SKILLS = "skills_description";
	static final String JSON_GET_KEY_BIRTHDAY = "birthday";
	static final String JSON_GET_KEY_GUARDIAN_NAME = "guardian_name";
	static final String JSON_GET_KEY_GUARDIAN_PHONE = "guardian_phone";
	static final String JSON_GET_KEY_IS_PUBLIC = "is_public";
	static final String JSON_GET_KEY_INTEREST = "interest";
	static final String JSON_GET_KEY_ABOUT_ME = "about_me";
	static final String JSON_GET_KEY_RANKING = "ranking";
	static final String JSON_GET_KEY_SCORE = "score";
	static final String JSON_GET_KEY_EVENT_HOUR = "event_hour";
	static final String JSON_GET_KEY_EVENT_NUM = "event_num";
	static final String JSON_GET_KEY_IMAGES =  "images";

	static final String JSON_GET_KEY_IS_FUBON =  "isFubon";
	static final String JSON_GET_KEY_IS_TWM =  "isTwm";

	public static final String POST_KEY_NAME = "name";
	public static final String POST_KEY_PHONE = "phone";
	public static final String POST_KEY_PASSWORD = "password";
	public static final String POST_KEY_OLD_PASSWORD = "old_password";
	public static final String POST_KEY_IMAGE = "icon";
	public static final String POST_KEY_INTEREST = "interest";
	public static final String POST_KEY_SERVICE_AREA = "service_area";
	public static final String POST_KEY_SERVICE_ITEM = "service_item";
	public static final String POST_KEY_ABOUT_ME = "about_me";
	public static final String POST_KEY_IS_PUBLIC = "is_public";
	public static final String POST_KEY_SKILLS = "skills_description";

	@DatabaseField(columnName = "_id", id = true)
	public Long id;
	@DatabaseField
	public String displayName;
	@DatabaseField
	public String password;
	@DatabaseField
	public String email;
	@DatabaseField
	public String phone;
	@DatabaseField
	public String securityId;
	@DatabaseField
	public String birthDay;
	@DatabaseField
	public String guardianName;
	@DatabaseField
	public String guardianPhone;
	@DatabaseField
	public String icon;
	@DatabaseField
	public String skills;
	@DatabaseField
	public Boolean isPublic;
	@DatabaseField
	public String interest;
	@DatabaseField
	public String aboutMe;
	@DatabaseField
	public Integer ranking;
	@DatabaseField
	public Integer score;
	@DatabaseField
	public Double eventHour;
	@DatabaseField
	public Integer eventNum;

	@DatabaseField
	public Boolean isFubon;
	@DatabaseField
	public Boolean isTwm;

	@ForeignCollectionField
	public Collection<UserLicenseImageDAO> images;

	public UserAccountDAO() {
	}

	public UserAccountDAO(JSONObject userObj) throws JSONException {

//		Log.d("UserAccountDAO JSON ->", userObj.toString());
		this.images = new ArrayList<>();

		this.id = userObj.getLong(JSON_GET_KEY_ID);
		this.displayName = userObj.getString(JSON_GET_KEY_NAME);
		this.icon = userObj.getString(JSON_GET_KEY_ICON);
		if (DatabaseHelper.hasThumbnail(userObj
				.getString(JSON_GET_KEY_ICON_THUMB))) {
			this.icon = userObj.getString(JSON_GET_KEY_ICON_THUMB);
		} else {
			this.icon = userObj.getString(JSON_GET_KEY_ICON);
		}
		this.email = userObj.getString(JSON_GET_KEY_EMAIL);

		this.phone = userObj.getString(JSON_GET_KEY_PHONE);
		this.securityId = userObj.getString(JSON_GET_KEY_SECURITY_ID);
		this.birthDay = userObj.getString(JSON_GET_KEY_BIRTHDAY);
		this.guardianName = userObj.getString(JSON_GET_KEY_GUARDIAN_NAME);
		this.guardianPhone = userObj.getString(JSON_GET_KEY_GUARDIAN_PHONE);
		this.skills = userObj.getString(JSON_GET_KEY_SKILLS);
		this.isPublic = userObj.getBoolean(JSON_GET_KEY_IS_PUBLIC);
		this.interest = userObj.getString(JSON_GET_KEY_INTEREST);
		this.aboutMe = userObj.getString(JSON_GET_KEY_ABOUT_ME);
		this.ranking = userObj.getInt(JSON_GET_KEY_RANKING);
		this.score = userObj.getInt(JSON_GET_KEY_SCORE);
		this.eventHour = userObj.getDouble(JSON_GET_KEY_EVENT_HOUR);
		this.eventNum = userObj.getInt(JSON_GET_KEY_EVENT_NUM);

		this.isFubon = getBooleanValue(userObj, JSON_GET_KEY_IS_FUBON);
		this.isTwm = getBooleanValue(userObj, JSON_GET_KEY_IS_TWM);

		List<UserLicenseImageDAO> imageList = new ArrayList<>();
		JSONArray imageObjList = userObj.getJSONArray(JSON_GET_KEY_IMAGES);
		for (int loop = 0; loop < imageObjList.length(); loop++) {
			Log.d(TAG, "loop: " + loop);
			UserLicenseImageDAO image = new UserLicenseImageDAO(
					imageObjList.getJSONObject(loop), this);
			imageList.add(image);
		}
		this.addLicenseImages(imageList);

	}
	public UserAccountDAO(JSONObject userObj, boolean safety) throws JSONException {
		this.images = new ArrayList<>();

		this.id = getLongValue(userObj, JSON_GET_KEY_ID);
		this.displayName = getStringValue(userObj, "username");
		this.icon = getStringValue(userObj, JSON_GET_KEY_ICON);
		if (DatabaseHelper.hasThumbnail(getStringValue(userObj, JSON_GET_KEY_ICON_THUMB))) {
			this.icon = getStringValue(userObj, JSON_GET_KEY_ICON_THUMB);
		} else {
			this.icon = getStringValue(userObj, JSON_GET_KEY_ICON);
		}
		this.email = getStringValue(userObj, "username");

		this.phone = getStringValue(userObj, JSON_GET_KEY_PHONE);
		this.securityId = getStringValue(userObj, JSON_GET_KEY_SECURITY_ID);
		this.birthDay = getStringValue(userObj, JSON_GET_KEY_BIRTHDAY);
		this.guardianName = getStringValue(userObj, JSON_GET_KEY_GUARDIAN_NAME);
		this.guardianPhone = getStringValue(userObj, JSON_GET_KEY_GUARDIAN_PHONE);
		this.skills = getStringValue(userObj, "skillsDescription");
		this.isPublic = getBooleanValue(userObj, JSON_GET_KEY_IS_PUBLIC);
		this.interest = getStringValue(userObj, JSON_GET_KEY_INTEREST);
		this.aboutMe = getStringValue(userObj, "aboutMe");
		this.ranking = getIntValue(userObj, JSON_GET_KEY_RANKING);
		this.score = getIntValue(userObj, JSON_GET_KEY_SCORE);
		this.eventHour =getDoubleValue(userObj, JSON_GET_KEY_EVENT_HOUR);
		this.eventNum = getIntValue(userObj, JSON_GET_KEY_EVENT_NUM);

		this.isFubon = getBooleanValue(userObj, JSON_GET_KEY_IS_FUBON);
		this.isTwm = getBooleanValue(userObj, JSON_GET_KEY_IS_TWM);

		List<UserLicenseImageDAO> imageList = new ArrayList<>();

		if(userObj.has(JSON_GET_KEY_IMAGES)){
			JSONArray imageObjList = userObj.getJSONArray(JSON_GET_KEY_IMAGES);
			for (int loop = 0; loop < imageObjList.length(); loop++) {
				Log.d(TAG, "loop: " + loop);
				UserLicenseImageDAO image = new UserLicenseImageDAO(
						imageObjList.getJSONObject(loop), this);
				imageList.add(image);
			}
			this.addLicenseImages(imageList);
		}

	}

	public String getStringValue(JSONObject userObj, String filed) throws JSONException{
		if(userObj.has(filed)){
			return userObj.getString(filed);
		}
		return "";
	}

	public int getIntValue(JSONObject userObj, String filed) throws JSONException{
		if(userObj.has(filed)){
			return userObj.getInt(filed);
		}
		return 0;
	}
	public long getLongValue(JSONObject userObj, String filed) throws JSONException{
		if(userObj.has(filed)){
			return userObj.getLong(filed);
		}
		return 0;
	}
	public double getDoubleValue(JSONObject userObj, String filed) throws JSONException{
		if(userObj.has(filed)){
			return userObj.getDouble(filed);
		}
		return 0;
	}
	public boolean getBooleanValue(JSONObject userObj, String filed) throws JSONException{
		if(userObj.has(filed)){
			return userObj.getBoolean(filed);
		}
		return false;
	}


	public void addLicenseImages(@NonNull List<UserLicenseImageDAO> groups) {
		this.images.addAll(groups);
	}




	public void save(String from) {
		try {
			for (UserLicenseImageDAO dao : images) {
				dao.save();
			}

//			if(TextUtils.isEmpty(email)){
//				Log.e(TAG, "skip saving, because no email ->" + this.toString());
//				return;
//			}

			Log.e(TAG, "save from "+from+" ->" + this.toString());
			MainActivity.MyDbHelper.getUserAccountDAO().createOrUpdate(this);

			Long id = queryIdByName(this.displayName);
			Log.e(TAG, "saved, id ->" + id);

		} catch (SQLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	@Override
	public String toString() {
		return "UserAccountDAO{" +
				"id=" + id +
				", displayName='" + displayName + '\'' +
				", password='" + password + '\'' +
				", email='" + email + '\'' +
				", phone='" + phone + '\'' +
				", securityId='" + securityId + '\'' +
				", birthDay='" + birthDay + '\'' +
				", guardianName='" + guardianName + '\'' +
				", guardianPhone='" + guardianPhone + '\'' +
				", icon='" + icon + '\'' +
				", skills='" + skills + '\'' +
				", isPublic=" + isPublic +
				", interest='" + interest + '\'' +
				", aboutMe='" + aboutMe + '\'' +
				", ranking=" + ranking +
				", score=" + score +
				", eventHour=" + eventHour +
				", eventNum=" + eventNum +
				", images=" + images +
				", isFubon=" + isFubon +
				", isTwm=" + isTwm +
				'}';
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSecurityId() {
		return securityId;
	}

	public void setSecurityId(String securityId) {
		this.securityId = securityId;
	}

	public String getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}

	public String getGuardianName() {
		return guardianName;
	}

	public void setGuardianName(String guardianName) {
		this.guardianName = guardianName;
	}

	public String getGuardianPhone() {
		return guardianPhone;
	}

	public void setGuardianPhone(String guardianPhone) {
		this.guardianPhone = guardianPhone;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public Boolean getPublic() {
		return isPublic;
	}

	public void setPublic(Boolean aPublic) {
		isPublic = aPublic;
	}

	public String getInterest() {
		return interest;
	}

	public void setInterest(String interest) {
		this.interest = interest;
	}

	public String getAboutMe() {
		return aboutMe;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	public Integer getRanking() {
		return ranking;
	}

	public void setRanking(Integer ranking) {
		this.ranking = ranking;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Double getEventHour() {
		return eventHour;
	}

	public void setEventHour(Double eventHour) {
		this.eventHour = eventHour;
	}

	public Integer getEventNum() {
		return eventNum;
	}

	public void setEventNum(Integer eventNum) {
		this.eventNum = eventNum;
	}

	public Collection<UserLicenseImageDAO> getImages() {
		return images;
	}

	public void setImages(Collection<UserLicenseImageDAO> images) {
		this.images = images;
	}


	public Boolean getFubon() {
		return isFubon;
	}

	public void setFubon(Boolean fubon) {
		isFubon = fubon;
	}

	public Boolean getTwm() {
		return isTwm;
	}

	public void setTwm(Boolean twm) {
		isTwm = twm;
	}

	public static Long queryIdByName(String name) throws SQLException {
		List<UserAccountDAO> list = MainActivity.MyDbHelper.getUserAccountDAO()
				.queryForEq(DATABASE_COLUMN_EMAIL, name);
		if (list.size() == 1) {
			return list.get(0).id;
		} else {
			return null;
		}
	}

	public static UserAccountDAO queryObjectById(Long id) throws SQLException {
		List<UserAccountDAO> list = MainActivity.MyDbHelper.getUserAccountDAO()
				.queryForEq(DATABASE_COLUMN_ID, id);
		if (list.size() == 1) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public static UserAccountDAO queryObjectByMe(Context c) throws SQLException {
		if (MyPreferenceManager.hasCredentials(c)) {
			return queryObjectById(MyPreferenceManager.getUserId(c));
		}
		return null;
	}

    public Double getGeneralEventHour(){

        return eventHour;
    }


}

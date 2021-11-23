package com.taiwanmobile.volunteers.v2.database;

import java.io.File;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.database.Schema.CoreEvent;
import com.taiwanmobile.volunteers.v2.database.Schema.CoreUseraccount;

public class Db {

	public static final String DATABASE_NAME = "twmf_local.db";

	public static SQLiteDatabase open(Context c) {
		// File root = new File(c.getFilesDir(), "database");
		File root = new File(c.getExternalFilesDir(null), "database");
		return SQLiteDatabase.openDatabase(root.getAbsolutePath() + "/"
				+ DATABASE_NAME, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS
				| SQLiteDatabase.OPEN_READWRITE);

	}

	public static Cursor dump(Cursor c) {
		Log.e("db", DatabaseUtils.dumpCursorToString(c));
		return c;
	}

	public static String dump(String s) {
		Log.e("db", s);
		return s;
	}

	public static interface AsCols {
		public static String IS_EVENT_REGISTERED = "is_event_registered";
		public static String IS_EVENT_FOCUSED = "is_event_focused";
		public static String FOCUSED_EVENT_COUNT = "focused_event_count";
		public static String JOINED_EVENT_COUNT = "joined_event_count";
		public static String JOINED_EVENT_HOURS = "joined_event_hours";
		public static String EVENT_COUNT = "event_count";
		public static String SUBSCRIBE_COUNT = "subscribe_count";
		public static String COMMENT_COUNT = "comment_count";
		public static String NPO_NAME = "npo_name";
		public static String IS_SUBSCRIBED = "is_subscribed";
		public static String IS_JOINED = "isJoined";
		// for NPO list view
		public static String NPO_SUBSCRIBED_USER_COUNT = "npo_subscribed_user_count";
		public static String NPO_JOINED_USER_COUNT = "npo_joined_user_count";
		public static String NPO_AVERAGE_SCORE = "npo_average_score";
		public static String NPO_RATING_USER_NUM = "npo_rating_user_num";
		public static String EVENT_AVERAGE_SCORE = "event_average_score";
		public static String EVENT_RATING_USER_NUM = "event_rating_user_num";

		// for user profile view
	}

	public static int getUserId(Context c, String username) {
		try {
			Cursor cursor = queryUserId(open(c), username);
			cursor.moveToFirst();
			int id = cursor.getInt(cursor
					.getColumnIndex(CoreUseraccount.USER_ID));
			cursor.close();
			return id;
		} catch (Exception ex) {
			Log.e("twmf", Log.getStackTraceString(ex));
			// LoginFragment.clearCredentials(c);
		}
		return -1;
	}

	public static Cursor queryUserId(Context c, String username) {
		return queryUserId(open(c), username);
	}

	public static Cursor queryUserId(SQLiteDatabase db, String username) {
		String sql = "select u.id as _id, u.* from core_useraccount u "
				+ "where u.email='" + username + "';";
		return db.rawQuery(sql, null);
	}

	public static Cursor queryUserEventState(Context c, long userId,
			long eventId) {
		return queryUserEventState(open(c), userId, eventId);
	}

	public static Cursor queryUserEventState(SQLiteDatabase db, long userId,
			long eventId) {

		String sql = "select e.id as _id, e.*, "
				+ "(r.registered_event_id not null) as is_event_registered, r.isJoined, "
				+ "(f.focused_event_id not null) as is_event_focused "
				+ "from core_event e "
				+ "left join core_userregisteredevent r on e.id=r.registered_event_id and r.user_id=? "
				+ "left join core_userfocusedevent f on e.id=f.focused_event_id and f.user_id=? "
				+ "where e.id=?;";

		return db.rawQuery(sql, array(userId, userId, eventId));
	}

	public static Cursor querySubscribedEvents(Context c, long uid) {
		return querySubscribedEvents(open(c), uid);
	}

	public static Cursor querySubscribedEvents(SQLiteDatabase db, long uid) {

		String sqlSubq = "select e.id as _id, e.*, "
				+ "(select id from core_userregisteredevent where registered_event_id=e.id and user_id=?) not null as is_event_registered, "
				+ "(select name from core_npo where id=e.owner_npo_id) as npo_name, "
				+ "(select id from core_userfocusedevent where focused_event_id=e.id and user_id=?) not null as is_event_focused "
				+ "from core_event e order by e.happen_date ASC;";
		// return db.rawQuery(sqlSubq, fill(2, uid));
		Cursor cursor = db.rawQuery(sqlSubq, fill(2, uid));

		String[] columns = cursor.getColumnNames();
		MatrixCursor newCursor = new MatrixCursor(columns, 1);

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			Date today = Calendar.getInstance().getTime();
			try {
				Date date = Db.DATETIME_FORMATTER.parse(cursor.getString(cursor
						.getColumnIndex(CoreEvent.CLOSE_DATE)));
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

	public static Cursor queryFocusedEvents(Context c, long uid) {
		return queryFocusedEvents(open(c), uid);
	}

	public static Cursor queryFocusedEvents(SQLiteDatabase db, long uid) {

		String sql = "select e.id as _id, e.*, n.name as npo_name, (r.registered_event_id not null) as is_event_registered "
				+ "from core_event e, core_userfocusedevent f, core_npo n "
				+ "left join core_userregisteredevent r on e.id=r.registered_event_id and r.user_id=? "
				+ "where e.owner_npo_id=n.id and e.id=f.focused_event_id and f.user_id=? order by e.happen_date DESC;";
		return db.rawQuery(sql, fill(2, uid));
	}

	public static Cursor queryUserProfile(Context c, long uid) {
		return queryUserProfile(open(c), uid);
	}

	public static Cursor queryUserProfile(SQLiteDatabase db, long uid) {
		String sql = "select u.*, "
				+ "(select count(user_id) from core_userregisteredevent where user_id=u.id and isjoined='true') as joined_event_count, "
				+ "(select count(user_id) from core_userfocusedevent where user_id=u.id) as focused_event_count, "
				+ "(select ifnull(sum(e.event_hour),0) from core_event e, core_userregisteredevent r where r.user_id=u.id and r.registered_event_id=e.id and r.isjoined='true') as joined_event_hours "
				+ "from auth_user u where u.id=?;";
		return db.rawQuery(sql, fill(uid));
	}

	public static Cursor queryUserData(Context c, long uid) {
		return queryUserData(open(c), uid);
	}

	public static Cursor queryUserData(SQLiteDatabase db, long uid) {
		String sql = "select u.* from core_useraccount u where u.user_id=?;";
		return db.rawQuery(sql, fill(uid));
	}

	private static String[] fill(long v) {
		return fill(1, v);
	}

	private static String[] fill(int len, long v) {
		String[] ret = new String[len];
		Arrays.fill(ret, String.valueOf(v));
		return ret;
	}

	private static String[] array(long... vals) {
		ArrayList<String> a = new ArrayList<String>();
		for (long v : vals) {
			a.add(String.valueOf(v));
		}
		return a.toArray(new String[0]);
	}

	public static Cursor querySearchEvents(Context c, long uid,
			String searchText) {
		String sqlSubq = "select e.id as _id, e.*, "
				+ "(select id from core_userregisteredevent where registered_event_id=e.id and user_id=?) not null as is_event_registered, "
				+ "(select name from core_npo where id=e.owner_npo_id) as npo_name, "
				+ "(select id from core_userfocusedevent where focused_event_id=e.id and user_id=?) not null as is_event_focused "
				+ "from core_event e " + "where (e.subject LIKE '%"
				+ searchText + "%' or e.description LIKE '%" + searchText
				+ "%') order by e.happen_date DESC;";

		return open(c).rawQuery(sqlSubq, fill(2, uid));
	}

	public static Cursor querySearchEvents(Context c, String searchText) {
		String sqlSubq = "select e.id as _id, e.*, "
				+ "(select name from core_npo where id=e.owner_npo_id) as npo_name "
				+ "from core_event e " + "where (e.subject LIKE '%"
				+ searchText + "%' or e.description LIKE '%" + searchText
				+ "%') order by e.happen_date DESC;";

		return open(c).rawQuery(sqlSubq, null);
	}

	public static Cursor queryJoinedEvents(Context c, long uid) {
		return queryJoinedEvents(open(c), uid);
	}

	public static Cursor queryJoinedEvents(SQLiteDatabase db, long uid) {
		String sql = "select e.id as _id, e.*, n.name as npo_name "
				+ "from core_event e, core_npo n, core_userregisteredevent r "
				+ "where e.owner_npo_id=n.id and e.id=r.registered_event_id and r.isJoined='true' and r.user_id=? order by e.happen_date DESC";
		return db.rawQuery(sql, fill(uid));
	}

	public static Cursor queryNonJoinedEvents(Context c, long uid) {
		String sql = "select e.id as _id, e.*, n.name as npo_name "
				+ "from core_event e, core_npo n, core_userregisteredevent r "
				+ "where e.owner_npo_id=n.id and e.id=r.registered_event_id and r.isJoined='false' and r.user_id=? order by e.happen_date ASC";
		// return open(c).rawQuery(sql, fill(uid));
		Cursor cursor = open(c).rawQuery(sql, fill(uid));

		String[] columns = cursor.getColumnNames();
		MatrixCursor newCursor = new MatrixCursor(columns, 1);

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			Date today = Calendar.getInstance().getTime();
			try {
				Date date = Db.DATETIME_FORMATTER.parse(cursor.getString(cursor
						.getColumnIndex(CoreEvent.CLOSE_DATE)));
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

	public static Cursor queryUserFilteredNonJoinedEvents(Context c, long uid,
			int day) {
		String sql = "select e.id as _id, e.*, n.name as npo_name "
				+ "from core_event e, core_npo n, core_userregisteredevent r "
				+ "where e.owner_npo_id=n.id and e.id=r.registered_event_id and r.isJoined='false' and r.user_id=? order by e.happen_date ASC";
		// return open(c).rawQuery(sql, fill(uid));
		Cursor cursor = open(c).rawQuery(sql, fill(uid));

		String[] columns = cursor.getColumnNames();
		MatrixCursor newCursor = new MatrixCursor(columns, 1);

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			Date today = Calendar.getInstance().getTime();
			try {
				Date date = Db.DATETIME_FORMATTER.parse(cursor.getString(cursor
						.getColumnIndex(CoreEvent.HAPPEN_DATE)));
				int numberOfDays = (int) ((date.getTime() - today.getTime()) / (3600 * 1000 * 24));
				if (day == 0 || (numberOfDays >= 0 && numberOfDays < day)) {
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

	public static Cursor queryNonJoinedCountdownEvents(Context c, long uid) {
		String sql = "select e.id as _id, e.*, n.name as npo_name "
				+ "from core_event e, core_npo n, core_userregisteredevent r "
				+ "where e.owner_npo_id=n.id and e.id=r.registered_event_id and r.isJoined='false' and r.user_id=? order by e.happen_date ASC";
		// return open(c).rawQuery(sql, fill(uid));
		Cursor cursor = open(c).rawQuery(sql, fill(uid));

		String[] columns = cursor.getColumnNames();
		MatrixCursor newCursor = new MatrixCursor(columns, 1);
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			Date today = Calendar.getInstance().getTime();
			try {
				Date date = Db.DATETIME_FORMATTER.parse(cursor.getString(cursor
						.getColumnIndex(CoreEvent.HAPPEN_DATE)));
				int numberOfHours = (int) ((date.getTime() - today.getTime()) / (3600 * 1000));
				if (numberOfHours < MyPreferenceManager.getTipTime(c)
						&& numberOfHours >= 0) {
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

	// for NPO list view
	public static Cursor queryNpos(SQLiteDatabase db, long uid) {
		return db
				.rawQuery(
						"select n.id as _id, n.*, "
								+ "(SELECT ifnull(AVG(score),0) from (select * from core_userregisteredevent ure join (select * from core_event ev where ev.owner_NPO_id=n.id) as e ON ure.registered_event_id=e.id and ure.score > '0')) as npo_average_score, "
								+ "(SELECT count(user_id) from (select * from core_userregisteredevent ure join (select * from core_event ev where ev.owner_NPO_id=n.id) as e ON ure.registered_event_id=e.id and ure.score > '0')) as npo_rating_user_num, "
								+ "(select count(user_id) from core_usersubscribednpo where subscribed_NPO_id=n.id) as npo_subscribed_user_count, "
								+ "(select count(id) from core_event where owner_NPO_id=n.id) as event_count, "
								+ "(select id from core_usersubscribednpo where user_id=? and subscribed_NPO_id=n.id) not null as is_subscribed, "
								+ "(select count(user_id) from core_userregisteredevent ure join (select * from core_event ev where ev.owner_NPO_id=n.id) as e ON ure.registered_event_id=e.id) as npo_joined_user_count "
								+ "from core_npo n;", fill(uid));
	}

	public static Cursor queryNpos(Context c, long uid) {
		return queryNpos(open(c), uid);
	}

	// for download npo images
	public static Cursor queryNpos(SQLiteDatabase db) {
		return db.rawQuery("select n.id as _id, n.* from core_npo n;", null);
	}

	public static Cursor queryNpos(Context c) {
		return queryNpos(open(c));
	}

	public static Cursor queryNpo(Context c, long nid, long uid) {
		return queryNpo(open(c), nid, uid);
	}

	public static Cursor queryNpo(SQLiteDatabase db, long nid, long uid) {
		return db
				.rawQuery(
						"select n.id as _id, n.*, "
								+ "(SELECT ifnull(AVG(score),0) from (select * from core_userregisteredevent ure join (select * from core_event ev where ev.owner_NPO_id=n.id) as e ON ure.registered_event_id=e.id and ure.score > '0')) as npo_average_score, "
								+ "(SELECT count(user_id) from (select * from core_userregisteredevent ure join (select * from core_event ev where ev.owner_NPO_id=n.id) as e ON ure.registered_event_id=e.id and ure.score > '0')) as npo_rating_user_num, "
								+ "(select count(user_id) from core_usersubscribednpo where subscribed_NPO_id=n.id) as npo_subscribed_user_count, "
								+ "(select count(id) from core_event where owner_NPO_id=n.id) as event_count, "
								+ "(select id from core_usersubscribednpo where user_id=? and subscribed_NPO_id=n.id) not null as is_subscribed, "
								+ "(select count(user_id) from core_userregisteredevent ure join (select * from core_event ev where ev.owner_NPO_id=n.id) as e ON ure.registered_event_id=e.id) as npo_joined_user_count "
								+ "from core_npo n where n.id=?;",
						array(uid, nid));
	}

	public static Cursor queryNpo(Context c, long nid) {
		return queryNpo(open(c), nid);
	}

	public static Cursor queryNpo(SQLiteDatabase db, long nid) {
		return db
				.rawQuery(
						"select n.id as _id, n.*, "
								+ "(SELECT ifnull(AVG(score),0) from (select * from core_userregisteredevent ure join (select * from core_event ev where ev.owner_NPO_id=n.id) as e ON ure.registered_event_id=e.id and ure.score > '0')) as npo_average_score, "
								+ "(SELECT count(user_id) from (select * from core_userregisteredevent ure join (select * from core_event ev where ev.owner_NPO_id=n.id) as e ON ure.registered_event_id=e.id and ure.score > '0')) as npo_rating_user_num, "
								+ "(select count(user_id) from core_usersubscribednpo where subscribed_NPO_id=n.id) as npo_subscribed_user_count, "
								+ "(select count(id) from core_event where owner_NPO_id=n.id) as event_count, "
								+ "(select count(user_id) from core_userregisteredevent ure join (select * from core_event ev where ev.owner_NPO_id=n.id) as e ON ure.registered_event_id=e.id) as npo_joined_user_count "
								+ "from core_npo n where n.id=?;", fill(nid));
	}

	public static Cursor queryNpoEvents(SQLiteDatabase db, long id) {
		return db
				.rawQuery(
						"select id as _id, e.* from core_event e where owner_npo_id=? order by e.happen_date ASC;",
						fill(id));
	}

	public static Cursor queryNpoEvents(Context c, long id) {
		return queryNpoEvents(open(c), id);
	}

	public static Cursor queryNpoEvents(SQLiteDatabase db, long uid, long id) {
		return db
				.rawQuery(
						"select e.id as _id, e.*, "
								+ "(select id from core_userregisteredevent where registered_event_id=e.id and user_id=?) not null as is_event_registered, "
								+ "(select id from core_userfocusedevent where focused_event_id=e.id and user_id=?) not null as is_event_focused "
								+ " from core_event e where owner_npo_id=? order by e.happen_date DESC;",
						array(uid, uid, id));
	}

	public static Cursor queryNpoEvents(Context c, long uid, long id) {
		return queryNpoEvents(open(c), uid, id);
	}

	public static Cursor queryEvent(SQLiteDatabase db, long userId, long eventId) {
		return db
				.rawQuery(
						"select e.id as _id, e.*, n.name as npo_name, "
								+ "(SELECT ifnull(AVG(score),0) from (select * from core_userregisteredevent ure join (select * from core_event ev where ev.id=?) as e ON ure.registered_event_id=e.id and ure.score > '0')) as event_average_score, "
								+ "(SELECT count(user_id) from (select * from core_userregisteredevent ure join (select * from core_event ev where ev.id=?) as e ON ure.registered_event_id=e.id and ure.score > '0')) as event_rating_user_num, "
								+ "(select id from core_userfocusedevent where focused_event_id=e.id and user_id=?) not null as is_event_focused "
								+ "from core_event e, core_npo n "
								+ "where n.id=e.owner_NPO_id and e.id=?;",
						array(eventId, eventId, userId, eventId));
	}

	public static Cursor queryEvent(Context c, long userId, long eventId) {
		return queryEvent(open(c), userId, eventId);
	}

	public static Cursor queryEvent(SQLiteDatabase db, long eventId) {
		return db
				.rawQuery(
						"select e.id as _id, e.*, n.name as npo_name, "
								+ "(SELECT ifnull(AVG(score),0) from (select * from core_userregisteredevent ure join (select * from core_event ev where ev.id=?) as e ON ure.registered_event_id=e.id and ure.score > '0')) as event_average_score, "
								+ "(SELECT count(user_id) from (select * from core_userregisteredevent ure join (select * from core_event ev where ev.id=?) as e ON ure.registered_event_id=e.id and ure.score > '0')) as event_rating_user_num "
								+ "from core_event e, core_npo n "
								+ "where n.id=e.owner_NPO_id and e.id=?;",
						array(eventId, eventId, eventId));
	}

	public static Cursor queryEvent(Context c, long eventId) {
		return queryEvent(open(c), eventId);
	}

	public static Cursor queryEvents(Context c) {
		return queryEvents(open(c));
	}

	public static Cursor queryEvents(SQLiteDatabase db) {
		return db
				.rawQuery(
						"select e.id as _id, e.*, n.name as npo_name "
								+ "from core_event e, core_npo n "
								+ "where n.id=e.owner_NPO_id order by e.happen_date ASC;",
						null);
	}

	public static Cursor queryEventSkills(Context c, long id) {
		return queryEventSkills(open(c), id);
	}

	public static Cursor queryEventSkills(SQLiteDatabase db, long id) {
		return db
				.rawQuery(
						"select s.id as _id, s.* from core_skillgroup s where s.owner_event_id=?;",
						fill(id));
	}

	public static final SimpleDateFormat DATETIME_FORMATTER = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.US);
	public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(
			"yyyy-MM-dd", Locale.US);

	public static void insert(SQLiteDatabase db,
			Pair<String, Map<String, Class<?>>> def, JsonArray data)
			throws ParseException {
		Map<String, Class<?>> coltypes = def.second;
		String table = def.first;
		for (JsonElement e : data) {
			JsonObject entry = e.getAsJsonObject().get("fields")
					.getAsJsonObject();
			Set<Entry<String, JsonElement>> cols = entry.entrySet();
			ContentValues cv = new ContentValues();
			for (Entry<String, JsonElement> col : cols) {
				String cname = col.getKey();
				Class<?> type = coltypes.get(cname);
				if (type == null) {
					// django ORM automatically appends _id to foreign key
					// fields
					cname += "_id";
					type = coltypes.get(cname);
					if (type == null) {
						throw new RuntimeException("undefined type for column "
								+ cname + " in table " + table);
					}
				}
				JsonElement colvalue = col.getValue();
				if (String.class.equals(type)) {
					cv.put(cname, colvalue.getAsString());
				} else if (Double.class.equals(type)) {
					cv.put(cname, colvalue.getAsDouble());
				} else if (Date.class.equals(type)) {

					cv.put(cname, DATETIME_FORMATTER.format(Timestamp
							.valueOf(colvalue.getAsString().replace('T', ' ')
									.replace("Z", ""))));

				} else if (Long.class.equals(type)) {
					cv.put(cname, colvalue.getAsLong());
				} else if (Boolean.class.equals(type)) {
					cv.put(cname, colvalue.getAsBoolean());
				}
			}
			db.insert(table, null, cv);
		}
	}

	public static void purge(SQLiteDatabase db, String table) {
		db.execSQL("delete from " + table + ";");
	}
}

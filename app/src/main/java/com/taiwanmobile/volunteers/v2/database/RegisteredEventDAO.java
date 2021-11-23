package com.taiwanmobile.volunteers.v2.database;

import java.sql.Date;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.taiwanmobile.volunteers.v2.MainActivity;

public class RegisteredEventDAO {
	private final static String TAG = "RegisteredEventDAO";

	public static final String DATABASE_TABLE_NAME = "registered_event";
	public static final String DATABASE_COLUMN_ID = "_id";
	public static final String DATABASE_COLUMN_REGISTERED_EVENT_ID = "registeredEvent_id";
	public static final String DATABASE_COLUMN_IS_JOINED = "isJoined";
	public static final String DATABASE_COLUMN_IS_LEAVED = "isLeaved";

	static final String JSON_GET_KEY_ID = "id";
	static final String JSON_GET_KEY_REGISTERED_EVENT = "registered_event";
	static final String JSON_GET_KEY_IS_JOINED = "isJoined";
	static final String JSON_GET_KEY_IS_LEAVED = "isLeaved";

	@DatabaseField(columnName = "_id", id = true)
	Long id;
	@DatabaseField(foreign = true, foreignAutoRefresh = true)
	EventDAO registeredEvent;
	@DatabaseField
	public Boolean isJoined;
	@DatabaseField
	public Boolean isLeaved;

	RegisteredEventDAO() {
	}


	public RegisteredEventDAO(String uid, long eventId, boolean isJoined, boolean isLeaved){

//		this.id = uid;
		EventDAO dao = new EventDAO();
		dao.id = eventId;
		this.registeredEvent = new EventDAO();
		this.isJoined = isJoined;
		this.isLeaved = isLeaved;
	}
	public RegisteredEventDAO(JSONObject obj) throws JSONException {
		this.id = obj.getLong(JSON_GET_KEY_ID);
		this.registeredEvent = new EventDAO(
				obj.getJSONObject(JSON_GET_KEY_REGISTERED_EVENT));
		this.isJoined = obj.getBoolean(JSON_GET_KEY_IS_JOINED);
		this.isLeaved = obj.getBoolean(JSON_GET_KEY_IS_LEAVED);
		//Log.d(TAG,"gengqiang->RegisteredEventDAO(): JSONObject= "+obj.toString());
		Log.d(TAG,"gengqiang->RegisteredEventDAO(): this.isJoined= "+this.isJoined);
		Log.d(TAG,"gengqiang->RegisteredEventDAO(): this.isLeaved= "+this.isLeaved);
	}

	public void save() {
		try {
			MainActivity.MyDbHelper.getEventDAO().createOrUpdate(
					registeredEvent);
			MainActivity.MyDbHelper.getRegisteredEventDAO()
					.createOrUpdate(this);
		} catch (SQLException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	public static Boolean isUserRegistered(long eventId) throws SQLException {
		List<RegisteredEventDAO> list = MainActivity.MyDbHelper
				.getRegisteredEventDAO().queryForEq(
						DATABASE_COLUMN_REGISTERED_EVENT_ID, eventId);
		if (list.size() == 1) {
			return true;
		} else {
			return false;
		}
	}

	public static Boolean isUserJoined(long eventId) throws SQLException {
		List<RegisteredEventDAO> list = MainActivity.MyDbHelper
				.getRegisteredEventDAO().queryForEq(
						DATABASE_COLUMN_REGISTERED_EVENT_ID, eventId);
		if (list.size() == 1 && list.get(0).isJoined) {
			return true;
		} else {
			return false;
		}
	}

	public static Boolean isUserSignouted(long eventId) throws SQLException {
		List<RegisteredEventDAO> list = MainActivity.MyDbHelper
				.getRegisteredEventDAO().queryForEq(
						DATABASE_COLUMN_REGISTERED_EVENT_ID, eventId);
		if (list.size() == 1 && list.get(0).isLeaved) {
			return true;
		} else {
			return false;
		}
	}

    private static boolean isYearInRange(int thisYear, int startYear, int endYear) {
        return !(startYear != 0 && startYear > thisYear) && !(endYear != 0 && endYear < thisYear);
    }

	public static Integer getUserEventHour(int startYear, int endYear) throws SQLException {
        List<RegisteredEventDAO> list = MainActivity.MyDbHelper
                .getRegisteredEventDAO().queryForAll();
        int event_hour = 0;
        for (RegisteredEventDAO dao : list) {
            if (dao.isJoined) {
                int thisYear = Integer.valueOf(dao.registeredEvent.closeDate.substring(0,4));

                Log.d(TAG, "ths event ("+thisYear+"): " + dao.registeredEvent.subject + ", " + dao.registeredEvent.happenDate+ ", " + dao.registeredEvent.closeDate);
                if(!isYearInRange(thisYear, startYear, endYear)){
                    continue;
                }
                event_hour += dao.registeredEvent.eventHour;
            }
        }
        return event_hour;
	}


    public static Integer getUserEventHour() throws SQLException {
		return getUserEventHour(0, 0);
	}


    public static Integer getUserGeneralEventHour(int startYear, int endYear) throws SQLException {
        List<RegisteredEventDAO> list = MainActivity.MyDbHelper
                .getRegisteredEventDAO().queryForAll();
        int event_hour = 0;
        for (RegisteredEventDAO dao : list) {
            if (dao.isJoined) {
                Log.d(TAG, "ths event: " + dao.registeredEvent.subject);

                int thisYear = Integer.valueOf(dao.registeredEvent.closeDate.substring(0,4));
                if(!isYearInRange(thisYear, startYear, endYear)){
                    continue;
                }
                event_hour += dao.registeredEvent.eventHour;
            }
        }
        return event_hour;
    }


    public static Integer getUserGeneralEventHour() throws SQLException {
        return getUserGeneralEventHour(0, 0);
    }


    public static Integer getUserEnterpriseEventHour(int startYear, int endYear) throws SQLException {
        List<RegisteredEventDAO> list = MainActivity.MyDbHelper
                .getRegisteredEventDAO().queryForAll();
        int event_hour = 0;
        for (RegisteredEventDAO dao : list) {
            if (dao.isJoined) {
                Log.d(TAG, "ths event: " + dao.registeredEvent.subject);
                int thisYear = Integer.valueOf(dao.registeredEvent.closeDate.substring(0,4));
                if(!isYearInRange(thisYear, startYear, endYear)){
                    continue;
                }
                event_hour += dao.registeredEvent.eventHour;
            }
        }
        return event_hour;
    }

    public static Integer getUserEnterpriseEventHour() throws SQLException {
        return getUserEnterpriseEventHour(0, 0);
    }


	public static Integer getUserJoinedEventNumber() throws SQLException {
		return MainActivity.MyDbHelper.getRegisteredEventDAO()
				.queryForEq(DATABASE_COLUMN_IS_JOINED, true).size();
	}

	public static Boolean deleteByEventId(long id) throws SQLException {
		List<RegisteredEventDAO> list = MainActivity.MyDbHelper
				.getRegisteredEventDAO().queryForEq(
						DATABASE_COLUMN_REGISTERED_EVENT_ID, id);
		if (list.size() == 1) {
			MainActivity.MyDbHelper.getRegisteredEventDAO().delete(list.get(0));
		}
		return true;
	}

	public static Cursor queryJoinedEvents() {
		String sql = "select e.* from eventdao e, registeredeventdao r "
				+ "where e._id=r.registeredEvent_id and r.isJoined=1 order by isUrgent DESC, isShort DESC, e.happenDate DESC;";
		//FIH-modify fix ���ڰѥ[�L�����ʡ��C���S�ȧY�ϳ��L�W ����and r.isJoined=1 isJoined���ӥN����W��ù�ڰѥ[�L�o�Ӭ��ʡA�ڻ{��android app�o�̪��B�z�ɹ諸�A��web�ݧ�u�n���L�W���N�C�X�ӡA�ҥH���o�˰��a�C�C�C
		//0820 ��s�A�P�Ȥ�T�{�A�HAPP�ݬ���AWeb�|�ק�
		//String sql = "select e.* from eventdao e, registeredeventdao r "
		//		+ "where e._id=r.registeredEvent_id order by isUrgent DESC, isShort DESC, e.happenDate DESC;";
		return MainActivity.MyDbHelper.getReadableDatabase()
				.rawQuery(sql, null);
	}

	// Return event that user register and is volunteer but not join
	public static Cursor queryNonJoinedEvents() {
		String sql = "select e.* from eventdao e, registeredeventdao r "
				+ "where e._id=r.registeredEvent_id and e.isVolunteer=1 and r.isJoined=0 order by isUrgent DESC, isShort DESC, e.happenDate DESC;";
		return MainActivity.MyDbHelper.getReadableDatabase()
				.rawQuery(sql, null);
	}

    // Return event that not expire and not join and is registered and is volunteer
    public static Cursor queryNonJoinedAndNotExpiredEvents(){
        String sql = "select e.* from eventdao e, registeredeventdao r "
                + "where e._id=r.registeredEvent_id and e.isVolunteer=1 and r.isJoined=0 and e.closeDate > datetime('now') order by isUrgent DESC, isShort DESC, e.happenDate DESC;";
        return MainActivity.MyDbHelper.getReadableDatabase()
                .rawQuery(sql, null);
    }

	public static Cursor queryDonatedItemEvents() {
		String sql = "select e.* from eventdao e, registeredeventdao r "
				+ "where e._id=r.registeredEvent_id and e.isVolunteer=0 order by isUrgent DESC, isShort DESC, e.happenDate DESC;";
		return MainActivity.MyDbHelper.getReadableDatabase()
				.rawQuery(sql, null);
	}

}

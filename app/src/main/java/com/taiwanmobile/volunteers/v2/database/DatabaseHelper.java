package com.taiwanmobile.volunteers.v2.database;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	private Context context;
	private static final String TAG = DatabaseHelper.class.getCanonicalName();

	// any time you make changes to your database objects, you may have to
	// increase the database version
	private static final int DATABASE_VERSION = 10;
	public static final String DATABASE_NAME = "twmf_orm_v" + DATABASE_VERSION
			+ ".db";

	private static Dao<UserAccountDAO, Integer> useraccountDao;

	public Dao<UserAccountDAO, Integer> getUserAccountDAO() throws SQLException {
		if (useraccountDao == null) {
			useraccountDao = DaoManager.createDao(connectionSource,
					UserAccountDAO.class);
		}
		return useraccountDao;
	}

	private static Dao<UserLicenseImageDAO, Integer> userLicenseImageDAO;

	public Dao<UserLicenseImageDAO, Integer> getUserLicenseImageDAO() throws SQLException {
		if(userLicenseImageDAO == null){
			userLicenseImageDAO = DaoManager.createDao(connectionSource,
					UserLicenseImageDAO.class);
		}
		return userLicenseImageDAO;
	}

	private static Dao<NpoDAO, Integer> npoDao;

	public Dao<NpoDAO, Integer> getNpoDAO() throws SQLException {
		if (npoDao == null) {
			npoDao = DaoManager.createDao(connectionSource, NpoDAO.class);
		}
		return npoDao;
	}

	private static Dao<EventDAO, Integer> eventDao;

	public Dao<EventDAO, Integer> getEventDAO() throws SQLException {
		if (eventDao == null) {
			eventDao = DaoManager.createDao(connectionSource, EventDAO.class);
		}
		return eventDao;
	}

	private static Dao<SubscribedNpoDAO, Integer> subscribedNpoDAO;

	public Dao<SubscribedNpoDAO, Integer> getSubscribedNpoDAO()
			throws SQLException {
		if (subscribedNpoDAO == null) {
			subscribedNpoDAO = DaoManager.createDao(connectionSource,
					SubscribedNpoDAO.class);
		}
		return subscribedNpoDAO;
	}

	private static Dao<RegisteredEventDAO, Integer> registeredEventDAO;

	public Dao<RegisteredEventDAO, Integer> getRegisteredEventDAO()
			throws SQLException {
		if (registeredEventDAO == null) {
			registeredEventDAO = DaoManager.createDao(connectionSource,
					RegisteredEventDAO.class);
		}
		return registeredEventDAO;
	}

	private static Dao<FocusedEventDAO, Integer> focusedEventDAO;

	public Dao<FocusedEventDAO, Integer> getFocusedEventDAO()
			throws SQLException {
		if (focusedEventDAO == null) {
			focusedEventDAO = DaoManager.createDao(connectionSource,
					FocusedEventDAO.class);
		}
		return focusedEventDAO;
	}

	private static Dao<SkillGroupDAO, Integer> skillGroupDAO;

	public Dao<SkillGroupDAO, Integer> getSkillGroupDAO() throws SQLException {
		if (skillGroupDAO == null) {
			skillGroupDAO = DaoManager.createDao(connectionSource,
					SkillGroupDAO.class);
		}
		return skillGroupDAO;
	}

	private static Dao<ReplyDAO, Integer> replyDAO;

	public Dao<ReplyDAO, Integer> getReplyDAO() throws SQLException {
		if (replyDAO == null) {
			replyDAO = DaoManager.createDao(connectionSource, ReplyDAO.class);
		}
		return replyDAO;
	}

	private static Dao<EventResultImageDAO, Integer> eventResultImageDAO;

	public Dao<EventResultImageDAO, Integer> getEventResultImageDAO()
			throws SQLException {
		if (eventResultImageDAO == null) {
			eventResultImageDAO = DaoManager.createDao(connectionSource,
					EventResultImageDAO.class);
		}
		return eventResultImageDAO;
	}

    private static Dao<DonationNpoDAO, Integer> donationNpoDAO;

    public Dao<DonationNpoDAO, Integer> getDonationNpoDAO() throws SQLException {
        if (donationNpoDAO == null) {
            donationNpoDAO = DaoManager.createDao(connectionSource,
                    DonationNpoDAO.class);
        }
        return donationNpoDAO;
    }


    private static Dao<CooperationNpoDAO, Integer> cooperationNpoDAO;

    public Dao<CooperationNpoDAO, Integer> getCooperationNpoDAO() throws SQLException {
        if (cooperationNpoDAO == null) {
            cooperationNpoDAO = DaoManager.createDao(connectionSource,
                    CooperationNpoDAO.class);
        }
        return cooperationNpoDAO;
    }



	/*
	 * when add a model, remember to : 1. add a Dao<> instance 2. create model
	 * table in OnCreate() 3. set Dao instance to null in OnDestroy()
	 */

	public static String getDbPath(Context context) {
		if (BackendContract.DEBUG) {
			return context.getExternalFilesDir(null) + "/" + DATABASE_NAME;
		} else {
			return DATABASE_NAME;
		}
	}

	public static File getDbFilePath(Context c) {
		// return c.getDatabasePath(Db.DATABASE_NAME);
		if (BackendContract.DEBUG) {
			return new File(c.getExternalFilesDir(null), DATABASE_NAME);
		} else {
			return c.getDatabasePath(DATABASE_NAME);
		}
	}

	public DatabaseHelper(Context context) {
		super(context, getDbPath(context), null, DATABASE_VERSION);
		this.context = context;
	}

	/**
	 * This is called when the database is first created. Usually you should
	 * call createTable statements here to create the tables that will store
	 * your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			// Log.e(TAG, "onCreate");

			// no relations inside tables
			TableUtils.createTable(connectionSource, UserAccountDAO.class);
			TableUtils.createTable(connectionSource, NpoDAO.class);
			TableUtils.createTable(connectionSource, DonationNpoDAO.class);
			// relations inside tables
			TableUtils.createTable(connectionSource, UserLicenseImageDAO.class);
			TableUtils.createTable(connectionSource, EventDAO.class);
			TableUtils.createTable(connectionSource, SubscribedNpoDAO.class);
			TableUtils.createTable(connectionSource, RegisteredEventDAO.class);
			TableUtils.createTable(connectionSource, FocusedEventDAO.class);
			TableUtils.createTable(connectionSource, SkillGroupDAO.class);
			TableUtils.createTable(connectionSource, ReplyDAO.class);
            TableUtils.createTable(connectionSource, EventResultImageDAO.class);
            TableUtils.createTable(connectionSource, CooperationNpoDAO.class);
		} catch (SQLException e) {
			Log.e(TAG, "Can't create database\n" + Log.getStackTraceString(e));
		}
	}

	/**
	 * This is called when your application is upgraded and it has a higher
	 * version number. This allows you to adjust the various data to match the
	 * new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {
		try {
			MyPreferenceManager.clearCredentials(context);
			MyPreferenceManager.clearInitialzed(context);

			// no relations inside tables
			TableUtils.dropTable(connectionSource, UserAccountDAO.class, true);
			TableUtils.dropTable(connectionSource, NpoDAO.class, true);
			TableUtils.dropTable(connectionSource, DonationNpoDAO.class, true);
			// relations inside tables
			TableUtils.dropTable(connectionSource, UserLicenseImageDAO.class, true);
			TableUtils.dropTable(connectionSource, EventDAO.class, true);
			TableUtils.dropTable(connectionSource, SubscribedNpoDAO.class, true);
			TableUtils.dropTable(connectionSource, RegisteredEventDAO.class, true);
			TableUtils.dropTable(connectionSource, FocusedEventDAO.class, true);
			TableUtils.dropTable(connectionSource, SkillGroupDAO.class, true);
			TableUtils.dropTable(connectionSource, ReplyDAO.class, true);
			TableUtils.dropTable(connectionSource, EventResultImageDAO.class, true);
			TableUtils.dropTable(connectionSource, CooperationNpoDAO.class, true);
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		super.close();
		useraccountDao = null;
		userLicenseImageDAO = null;
		npoDao = null;
		eventDao = null;
		subscribedNpoDAO = null;
		registeredEventDAO = null;
		focusedEventDAO = null;
		skillGroupDAO = null;
		replyDAO = null;
		eventResultImageDAO = null;
		donationNpoDAO = null;
        cooperationNpoDAO = null;
	}

	public static void removeDbData(Context c) {
		c.deleteDatabase(getDbPath(c));
	}

	public static Boolean hasThumbnail(String url) {
		if (url.length() > 5) { // not empty nor "null"
			return true;
		} else {
			return false;
		}
	}


    public class FieldContentException extends Exception {

		FieldContentException(String detailMessage) {
			super(detailMessage);
		}

		private static final long serialVersionUID = -913410543392661502L;
	}

	public static final SimpleDateFormat DATETIME_FORMATTER = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.US);

	public static String[] fill(long v) {
		return fill(1, v);
	}

	public static String[] fill(int len, long v) {
		String[] ret = new String[len];
		Arrays.fill(ret, String.valueOf(v));
		return ret;
	}

	@Deprecated
	private static String[] array(long... vals) {
		ArrayList<String> a = new ArrayList<String>();
		for (long v : vals) {
			a.add(String.valueOf(v));
		}
		return a.toArray(new String[0]);
	}
}

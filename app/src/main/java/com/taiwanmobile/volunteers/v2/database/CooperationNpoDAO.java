package com.taiwanmobile.volunteers.v2.database;

import android.database.Cursor;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.taiwanmobile.volunteers.v2.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by pichu on 西元2016/11/16.
 */
public class CooperationNpoDAO {

    private final String TAG = getClass().getSimpleName();

    static final String JSON_GET_KEY_ID = "id";
    static final String JSON_GET_KEY_NAME = "name";

    @DatabaseField(columnName = "_id", id = true)
    public Long id;
    @DatabaseField
    public String name;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    EventDAO event;

    CooperationNpoDAO() {
    }

    public CooperationNpoDAO(JSONObject obj, EventDAO eventObject) throws JSONException {
        this.id = obj.getLong(JSON_GET_KEY_ID);
        this.name = obj.getString(JSON_GET_KEY_NAME);
        this.event = eventObject;
    }

//    public void save() {
//        try {
//            MainActivity.MyDbHelper.getCooperationNpoDAO().createOrUpdate(this);
//        } catch (SQLException e) {
//            Log.e(TAG, Log.getStackTraceString(e));
//        }
//    }

}

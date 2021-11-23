package com.taiwanmobile.volunteers.v2.api;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.j256.ormlite.misc.TransactionManager;
import com.taiwanmobile.volunteers.R;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.database.RegisteredEventDAO;
import com.taiwanmobile.volunteers.v2.utils.AsyncTaskWithDialogs;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;
import com.taiwanmobile.volunteers.v2.utils.FragHelper;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.concurrent.Callable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by pichu on 西元2018/4/18.
 */

public class TTVolunteerQueryRegisterTask extends AsyncTaskWithDialogs<Void, Void> implements
        OnDismissListener  {
    private final String TAG = getClass().getSimpleName();

    Context mContext;
    // private final EventDAO mEventObject;
    String eventUid;
    String url;
    Fragment mEventDetailFragment;

    public int returnStatusCode = 0;
    public String reasonString = "";

    public TTVolunteerQueryRegisterTask(Context c, Fragment pFrag, String uid) {
        super(c);
        mContext = c;
        mEventDetailFragment = pFrag;
        eventUid = uid;
        url = BackendContract.V2_TTVOLUNTEER_REGISTER_STATUS_URL + eventUid + "/";
        setProgressDialog(DialogFactory.createBusyDialog(mContext, "查詢狀態中"));
        setSuccessDialog(DialogFactory.createDummyDialog(mContext, this));
        setFailDialog(DialogFactory.createDummyDialog(mContext, this));
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Token " + MyPreferenceManager.getAuthToken(mContext))
                    .build();

            Response response = client.newCall(request).execute();

            returnStatusCode = response.code();
            ResponseBody body = response.body();

            if (body == null) {
                Log.e(TAG, "server response error");
                return false;
            }

            final String responseString = body.string();


            if (returnStatusCode != 200) {
                Log.e(TAG, responseString);
                return false;
            }
            JSONObject object = new JSONObject("{\"s\":"+responseString+"}");
            reasonString = object.getString("s");
            return true;
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return false;
    }

    @Override
    public void onDismiss(DialogInterface arg0) {
        if (returnStatusCode == 200) {
            View view = mEventDetailFragment.getView();
            if(view == null){
                return;
            }
            TextView btn = view.findViewById(R.id.fragment_event_detail_tv_join);
            btn.setText(reasonString);
        } else if (returnStatusCode == 404) {
            View view = mEventDetailFragment.getView();
            if(view == null){
                return;
            }
            TextView btn = view.findViewById(R.id.fragment_event_detail_tv_join);
            btn.setText("XXXXX");
        } else if (returnStatusCode == 401) {
            MyPreferenceManager.ForceLogout((Activity) mContext);
        } else {
            new DialogFactory.TitleMessageDialog(mContext, "報到失敗",
                    "請確認手機上網功能已開啟").show();
        }
    }
}

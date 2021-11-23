package com.taiwanmobile.volunteers.v1.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.j256.ormlite.misc.TransactionManager;
import com.taiwanmobile.volunteers.v1.util.WebServiceParams;
import com.taiwanmobile.volunteers.v1.util.WebServiceUtil;
import com.taiwanmobile.volunteers.v2.BackendContract;
import com.taiwanmobile.volunteers.v2.MainActivity;
import com.taiwanmobile.volunteers.v2.MyPreferenceManager;
import com.taiwanmobile.volunteers.v2.database.RegisteredEventDAO;
import com.taiwanmobile.volunteers.v2.loginview.ForgotPasswordFragment;
import com.taiwanmobile.volunteers.v2.utils.DialogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class V1PWDRestTask extends BaseRequestTask {
    public static final String TAG = "V1PWDRestTask";

    String mEmail;

    public V1PWDRestTask(Context c, String email) {
        super(c);

        mEmail = email;

        setProgressDialog(DialogFactory.createBusyDialog(c,
                "傳送中"));
        setSuccessDialog(DialogFactory.createDummyDialog(c,
                this));
        setFailDialog(DialogFactory.createDummyDialog(c, this));
    }


    private Request buildRequest(){

        String accessToken = MyPreferenceManager.getAuthToken(mContext);


        String url = BackendContract.V1_RESET_PWD;
        Log.d(TAG, "set url = " + url);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", mEmail);
        jsonObject.addProperty("accessPage", BackendContract.DEPLOYMENT_URL + "/user/resetPassword");


        String jsonStr = jsonObject.toString();

        Log.d(TAG, "set requestBody = " + jsonStr);

        MediaType mediaType = MediaType.parse(WebServiceParams.CommonValues.Headers_Content_Type_APPLICATION_JSON);
        RequestBody requestBody = RequestBody.create(mediaType, jsonStr);

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(requestBody);


        Map<String, String> headers = WebServiceUtil.getWebServiceHeaders(mContext,
                WebServiceParams.CommonValues.Headers_Content_Type_APPLICATION_JSON, accessToken);

        Set<String> keySets = headers.keySet();
        for(String key : keySets){
            String value = headers.get(key);
            Log.d(TAG, "addHeader, key = " + key + ", value = "+ value);
            requestBuilder.addHeader(key, value);
        }

        return requestBuilder.build();

    }

    @Override
    public Boolean doInBackground(Void... arg0) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = buildRequest();

            Response response = client.newCall(request).execute();

            mStatusCode = response.code();
            Log.e(TAG, "returnStatusCode = "+mStatusCode);

            ResponseBody body = response.body();

            if (body == null) {
                Log.e(TAG, "server response error");
                return false;
            }

            final String responseString = body.string();

            Log.e(TAG, "Response = "+responseString);
            if (mStatusCode != 200) {
                sendResult(ForgotPasswordFragment.MSG_ACCOUNT_NOT_FOUND);
                return false;
            }


            JSONObject jsonObject = new JSONObject(responseString);

            if(!jsonObject.has("error")){
                sendResult(ForgotPasswordFragment.MSG_SUCCESS);
                return true;
            }else{
                JSONArray errors = jsonObject.getJSONArray("error");
                JSONObject error = errors.getJSONObject(0);
                mError = error.getString("error");
                sendResult(ForgotPasswordFragment.MSG_ERROR, mError);
            }

            return false;


        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            sendResult(ForgotPasswordFragment.MSG_ACCOUNT_NOT_FOUND);
        }
        return false;
    }

}

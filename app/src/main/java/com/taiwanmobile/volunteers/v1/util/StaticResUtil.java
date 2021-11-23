package com.taiwanmobile.volunteers.v1.util;

import android.util.Log;

import com.taiwanmobile.volunteers.v2.BackendContract;

public class StaticResUtil {

    public static final String TAG = "StaticResUtil";


    public static String checkUrl(String imageFilepath, boolean isStatic, String obj){
        Log.d(TAG + "_" + obj, "checkUrl-> " +  imageFilepath);

        if(imageFilepath.startsWith("http")){
            return imageFilepath;
        }
        String modifiedUrl = (isStatic ? BackendContract.STATIC_RESOURCE_URL_STATIC :BackendContract.STATIC_RESOURCE_URL_BASE) + imageFilepath;
        modifiedUrl = modifiedUrl.replace("uploads", "resources");
        Log.d(TAG + "_" + obj, "modified Url -> " + modifiedUrl);
        return modifiedUrl;

    }

}
